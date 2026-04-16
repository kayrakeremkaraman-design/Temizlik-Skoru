package com.example.tidyai.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tidyai.data.remote.model.TidyAnalysis
import com.example.tidyai.data.repository.HistoryRepository
import com.example.tidyai.data.repository.SettingsRepository
import com.example.tidyai.data.repository.TidyAiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

sealed class AnalyzeState {
    object Idle : AnalyzeState()
    object Loading : AnalyzeState()
    data class Success(val result: TidyAnalysis) : AnalyzeState()
    data class Error(val message: String) : AnalyzeState()
}

@HiltViewModel
class AnalyzeViewModel @Inject constructor(
    private val repository: TidyAiRepository,
    private val historyRepository: HistoryRepository,
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _analyzeState = MutableStateFlow<AnalyzeState>(AnalyzeState.Idle)
    val analyzeState: StateFlow<AnalyzeState> = _analyzeState.asStateFlow()

    // Gamification: Completed suggestions tracking per session
    private val _completedSuggestions = MutableStateFlow<Set<Int>>(emptySet())
    val completedSuggestions: StateFlow<Set<Int>> = _completedSuggestions.asStateFlow()

    private val _sessionPoints = MutableStateFlow(0)
    val sessionPoints: StateFlow<Int> = _sessionPoints.asStateFlow()

    /** İmge doğrulama hatasi olduğunda çağrılacak opsiyonel callback */
    private var onQuotaRefund: (() -> Unit)? = null

    fun analyzeImage(uri: Uri, spaceType: String, useMock: Boolean = false, onQuotaRefund: (() -> Unit)? = null) {
        this.onQuotaRefund = onQuotaRefund
        _analyzeState.value = AnalyzeState.Loading
        _completedSuggestions.value = emptySet()
        _sessionPoints.value = 0
        viewModelScope.launch {
            try {
                val base64 = withContext(Dispatchers.IO) {
                    val bitmap = getBitmapFromUri(context, uri)
                        ?: throw Exception("Resim yüklenemedi. Lütfen tekrar seçin.")

                    // Görsel kalite kontrolü
                    validateImage(bitmap)

                    val resizedBitmap = resizeBitmap(bitmap, 512)
                    encodeBitmapToBase64(resizedBitmap)
                }

                val apiKey = settingsRepository.apiKey.first()
                val result = repository.analyzeSpace(base64, spaceType, useMock, apiKey)

                if (result.isSuccess) {
                    val tidyResult = result.getOrThrow()
                    _analyzeState.value = AnalyzeState.Success(tidyResult)
                    historyRepository.saveResult(tidyResult, uri.toString())
                } else {
                    _analyzeState.value = AnalyzeState.Error(result.exceptionOrNull()?.message ?: "Bilinmeyen bir hata oluştu")
                }
            } catch (e: Exception) {
                // Görsel kalite hatası ise hakkı geri ver
                val isImageValidationError = e.message?.startsWith("📷") == true
                    || e.message?.startsWith("🖼️") == true
                    || e.message?.contains("karanlık") == true
                    || e.message?.contains("yeterince içerik") == true
                if (isImageValidationError) {
                    this@AnalyzeViewModel.onQuotaRefund?.invoke()
                }
                _analyzeState.value = AnalyzeState.Error(e.message ?: "İşlem sırasında beklenmeyen bir hata oluştu")
            }
        }
    }

    fun completeSuggestion(index: Int, gain: Int) {
        val current = _completedSuggestions.value
        if (index !in current) {
            _completedSuggestions.value = current + index
            _sessionPoints.value += gain
        }
    }

    /**
     * Görselin analiz edilmeye değer olup olmadığını kontrol eder.
     * - Ortalama parlaklık çok düşükse (siyah/karanlık) → hata fırlatır
     * - Renk çeşitliliği çok düşükse (tamamen tek renk) → hata fırlatır
     */
    private fun validateImage(bitmap: Bitmap) {
        val sampleSize = 40  // örnekleme ızgarası
        val stepX = maxOf(1, bitmap.width  / sampleSize)
        val stepY = maxOf(1, bitmap.height / sampleSize)

        var totalBrightness = 0L
        var pixelCount      = 0
        val brightnessValues = mutableListOf<Int>()

        var x = 0
        while (x < bitmap.width) {
            var y = 0
            while (y < bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr  8) and 0xFF
                val b =  pixel        and 0xFF
                // ITU-R BT.601 parlaklık formülü
                val brightness = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                totalBrightness += brightness
                brightnessValues += brightness
                pixelCount++
                y += stepY
            }
            x += stepX
        }

        if (pixelCount == 0) return

        val meanBrightness = totalBrightness.toDouble() / pixelCount

        // Çok karanlık görsel kontrolü (ortalama parlaklık < 15)
        if (meanBrightness < 15) {
            throw Exception("📷 Fotoğraf çok karanlık görünüyor. Işıklı bir ortamda tekrar çekmeyi dene!")
        }

        // Tek renk / boş görsel kontrolü: standart sapma çok düşükse
        val variance = brightnessValues.map { b ->
            val diff = b - meanBrightness
            diff * diff
        }.average()
        val stdDev = Math.sqrt(variance)

        if (stdDev < 8.0) {
            throw Exception("🖼️ Fotoğraf analiz için yeterince içerik barındırmıyor. Gerçek bir oda fotoğrafı çekmeyi dene!")
        }
    }

    private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun resetState() {
        _analyzeState.value = AnalyzeState.Idle
        _completedSuggestions.value = emptySet()
        _sessionPoints.value = 0
    }
}

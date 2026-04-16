package com.example.tidyai.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tidyai.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Ücretsiz analiz limiti */
const val FREE_QUERY_LIMIT = 2

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _selectedSpaceType = MutableStateFlow("Yatak Odası")
    val selectedSpaceType: StateFlow<String> = _selectedSpaceType.asStateFlow()

    /** DataStore'dan okunan kalıcı sorgu sayacı */
    val queryCount: StateFlow<Int> = settingsRepository.queryCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0
        )

    private val _showAdDialog = MutableStateFlow(false)
    val showAdDialog: StateFlow<Boolean> = _showAdDialog.asStateFlow()

    fun setImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun setSpaceType(type: String) {
        _selectedSpaceType.value = type
    }

    /**
     * Analiz başlatmaya çalış.
     * - limit dolmadıysa sayacı artır → true döner (analiz başlasın)
     * - limit dolduysa reklam dialog'u aç → false döner
     */
    fun tryStartAnalysis(): Boolean {
        return if (queryCount.value < FREE_QUERY_LIMIT) {
            viewModelScope.launch { settingsRepository.incrementQueryCount() }
            true
        } else {
            _showAdDialog.value = true
            false
        }
    }

    /** Reklam izlendi → sayacı sıfırla */
    fun onAdWatched() {
        viewModelScope.launch { settingsRepository.resetQueryCount() }
        _showAdDialog.value = false
    }

    /** Görsel hatalıydı → son sorguyu geri al (sayacı eksilt) */
    fun refundQuery() {
        viewModelScope.launch { settingsRepository.decrementQueryCount() }
    }

    fun dismissAdDialog() {
        _showAdDialog.value = false
    }

    val spaceTypes = listOf(
        "Yatak Odası",
        "Oturma Odası",
        "Mutfak",
        "Banyo",
        "Çalışma Odası",
        "Garaj",
        "Diğer"
    )
}

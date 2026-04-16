package com.example.tidyai.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tidyai.ui.theme.*
import com.example.tidyai.viewmodel.AnalyzeState
import kotlinx.coroutines.delay

private val loadingMessages = listOf(
    "Yapay zeka odanı inceliyor... 🔍",
    "Boyutlar hesaplanıyor... 📊",
    "Bölgeler taranıyor... 🗺️",
    "Sorunlar tespit ediliyor... 🧹",
    "Öneriler hazırlanıyor... 💡",
    "Skor belirleniyor... ✨"
)

@Composable
fun AnalyzingScreen(
    analyzeState: AnalyzeState,
    onAnalysisComplete: () -> Unit,
    onError: () -> Unit,
    onGoHome: () -> Unit = onError  // varsayılan: hata ile aynı davranış
) {
    LaunchedEffect(analyzeState) {
        if (analyzeState is AnalyzeState.Success) {
            onAnalysisComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, DarkSurface, DarkBackground)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        when (analyzeState) {
            is AnalyzeState.Loading -> {
                LoadingContent(onGoHome = onGoHome)
            }
            is AnalyzeState.Error -> {
                ErrorContent(
                    message   = analyzeState.message,
                    onGoBack  = onError,
                    onGoHome  = onGoHome
                )
            }
            else -> {}
        }
    }
}

// ── Yükleme içeriği ──────────────────────────────────────────────────────────

@Composable
private fun LoadingContent(onGoHome: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "loadingAnim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 360f,
        animationSpec = infiniteRepeatable(
            animation  = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue  = 1.1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    var messageIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            messageIndex = (messageIndex + 1) % loadingMessages.size
        }
    }

    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier        = Modifier.scale(pulseScale),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector      = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                modifier         = Modifier
                    .size(120.dp)
                    .alpha(0.2f)
                    .scale(1.5f)
                    .rotate(rotation),
                tint = TidyPrimary
            )
            Icon(
                imageVector      = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                modifier         = Modifier
                    .size(80.dp)
                    .rotate(rotation),
                tint = TidyPrimary
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text      = "Analiz Ediliyor",
            style     = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color     = TextWhite
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text      = loadingMessages[messageIndex],
            style     = MaterialTheme.typography.bodyLarge,
            color     = TidyPrimary,
            textAlign = TextAlign.Center,
            fontSize  = 16.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        LinearProgressIndicator(
            modifier  = Modifier
                .fillMaxWidth(0.7f)
                .height(4.dp),
            color      = TidyPrimary,
            trackColor = DarkSurfaceVariant,
            strokeCap  = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Hafif "Ana Menü" linki — analiz sürerken iptal için
        HomeChip(onClick = onGoHome)
    }
}

// ── Hata içeriği ─────────────────────────────────────────────────────────────

@Composable
private fun ErrorContent(
    message  : String,
    onGoBack : () -> Unit,
    onGoHome : () -> Unit
) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Emoji
        Text(text = "😔", fontSize = 64.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text       = "Analiz Başarısız",
            style      = MaterialTheme.typography.headlineMedium,
            color      = ErrorRed,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Hata mesajı — şeffaf kart içinde
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(GlassWhite)
                .padding(16.dp)
        ) {
            Text(
                text      = message,
                style     = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color     = TextWhiteSecondary,
                lineHeight = 20.sp,
                modifier  = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tekrar dene butonu
        Button(
            onClick  = onGoBack,
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor   = DarkBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(
                Icons.Rounded.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tekrar Dene", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Ana menü — ince, dikkat çekmeyen
        HomeChip(onClick = onGoHome)
    }
}

// ── Paylaşılan küçük "Ana Menüye Dön" bileşeni ───────────────────────────────

@Composable
private fun HomeChip(onClick: () -> Unit) {
    TextButton(
        onClick  = onClick,
        modifier = Modifier.alpha(0.65f)
    ) {
        Icon(
            Icons.Rounded.Home,
            contentDescription = null,
            tint     = TextWhiteSecondary,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text   = "Ana Menüye Dön",
            color  = TextWhiteSecondary,
            style  = MaterialTheme.typography.bodySmall
        )
    }
}

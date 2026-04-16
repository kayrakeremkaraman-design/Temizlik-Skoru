package com.example.tidyai.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tidyai.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        visible = true
        delay(2500L)
        onTimeout()
    }

    // Pulse animation for the icon
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Fade-in animation
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "fadeIn"
    )

    // Scale-in animation
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.6f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "scaleIn"
    )

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
        Column(
            modifier = Modifier
                .alpha(alpha)
                .scale(scale),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pulsating icon
            Box(
                modifier = Modifier.scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                // Glow effect
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .alpha(0.3f)
                        .scale(1.4f),
                    tint = TidyPrimary
                )
                // Main icon
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = TidyPrimary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Temizlik Skoru",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 34.sp
                ),
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "AI ile odanı analiz et ✨",
                style = MaterialTheme.typography.bodyLarge,
                color = TextWhiteSecondary
            )
        }
    }
}

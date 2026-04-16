package com.example.tidyai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tidyai.ui.components.GlassCard
import com.example.tidyai.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val emoji: String,
    val icon: ImageVector,
    val title: String,
    val description: String,
    val gradientColors: List<Color>
)

private val pages = listOf(
    OnboardingPage(
        emoji = "📸",
        icon = Icons.Rounded.CameraAlt,
        title = "Fotoğraf Çek",
        description = "Odanın fotoğrafını çek veya galeriden seç. Yapay zeka geri kalanı halleder!",
        gradientColors = listOf(GradientPrimaryStart, GradientPrimaryEnd)
    ),
    OnboardingPage(
        emoji = "🧠",
        icon = Icons.Rounded.AutoAwesome,
        title = "AI Analizi",
        description = "Yapay zeka odanı saniyeler içinde analiz eder. Temizlik, düzen, ferahlık gibi boyutları tek tek inceler.",
        gradientColors = listOf(GradientAccentStart, GradientAccentEnd)
    ),
    OnboardingPage(
        emoji = "🎯",
        icon = Icons.Rounded.EmojiEvents,
        title = "Puan Kazan",
        description = "Önerileri tamamla, puan topla! Her küçük adım seni daha düzenli bir yaşama götürür.",
        gradientColors = listOf(Color(0xFFFFD93D), Color(0xFFFF9F43))
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, DarkSurface)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(pages[page])
            }

            // Dots indicator
            Row(
                modifier = Modifier.padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(
                                width = if (pagerState.currentPage == index) 28.dp else 10.dp,
                                height = 10.dp
                            )
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index) TidyPrimary
                                else GlassBorder
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button
            if (pagerState.currentPage == pages.lastIndex) {
                Button(
                    onClick = onFinish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TidyPrimary,
                        contentColor = DarkBackground
                    )
                ) {
                    Text(
                        "Başlayalım! 🚀",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GlassWhite,
                        contentColor = TextWhite
                    )
                ) {
                    Text(
                        "Devam →",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (pagerState.currentPage < pages.lastIndex) {
                TextButton(onClick = onFinish) {
                    Text(
                        "Atla",
                        color = TextWhiteTertiary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically { -40 }
        ) {
            GlassCard(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Big emoji
                    Text(
                        text = page.emoji,
                        fontSize = 72.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Icon with gradient background
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(page.gradientColors)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = page.icon,
                            contentDescription = null,
                            tint = DarkBackground,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = TextWhite,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = page.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextWhiteSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}

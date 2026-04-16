package com.example.tidyai.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tidyai.ui.components.*
import com.example.tidyai.ui.theme.*
import com.example.tidyai.viewmodel.AnalyzeState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    state: AnalyzeState,
    originalImageUri: android.net.Uri?,
    completedSuggestions: Set<Int>,
    sessionPoints: Int,
    onCompleteSuggestion: (Int, Int) -> Unit,
    onRestart: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, DarkSurface, DarkBackground)
                )
            )
    ) {
        if (state is AnalyzeState.Success) {
            val result = state.result
            val showConfetti = completedSuggestions.isNotEmpty()

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Top Bar ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Analiz Sonucu",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        if (sessionPoints > 0) {
                            AnimatedPointsCounter(points = sessionPoints)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ██ 1. HERO: Score Ring ██
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn() + scaleIn(initialScale = 0.8f)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ScoreRing(score = result.score)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = scoreLabel(result.score),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = scoreColor(result.score)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ██ 2. PHOTO ██
                    if (originalImageUri != null) {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn() + expandVertically()
                        ) {
                            AsyncImage(
                                model = originalImageUri,
                                contentDescription = "Analiz edilen oda",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(20.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // ██ 3. SUMMARY ██
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn() + slideInVertically { it / 3 }
                    ) {
                        GlassCard {
                            Text(
                                text = "📝 Özet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TidyPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = result.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextWhiteSecondary,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ██ 4. POTENTIAL MESSAGE (Motivasyon) ██
                    if (!result.potentialMessage.isNullOrBlank()) {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn() + slideInVertically { it / 3 }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                GradientPrimaryStart.copy(alpha = 0.2f),
                                                GradientPrimaryEnd.copy(alpha = 0.2f)
                                            )
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "💪 ${result.potentialMessage}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = TidyPrimary,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // ██ 5. DIMENSIONS ██
                    if (result.dimensions.isNotEmpty()) {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn()
                        ) {
                            Column {
                                SectionHeader(title = "Boyutlar", emoji = "📊")
                                Spacer(modifier = Modifier.height(16.dp))
                                GlassCard {
                                    result.dimensions.forEachIndexed { index, dim ->
                                        DimensionBar(
                                            dimension = dim,
                                            delay = index * 200
                                        )
                                        if (index < result.dimensions.lastIndex) {
                                            Spacer(modifier = Modifier.height(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // ██ 6. ZONES ██
                    if (result.zones.isNotEmpty()) {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn()
                        ) {
                            Column {
                                SectionHeader(title = "Bölgeler", emoji = "🗺️")
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    result.zones.forEach { zone ->
                                        ZoneScoreChip(
                                            zone = zone,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // ██ 7. ISSUES ██
                    if (result.issues.isNotEmpty()) {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn()
                        ) {
                            Column {
                                SectionHeader(title = "Sorunlar", emoji = "⚠️")
                                Spacer(modifier = Modifier.height(16.dp))
                                result.issues.forEach { issue ->
                                    IssueBadge(issue = issue)
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // ██ 8. POSITIVES ██
                    if (result.positives.isNotEmpty()) {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn()
                        ) {
                            Column {
                                SectionHeader(title = "İyi Noktalar", emoji = "✅")
                                Spacer(modifier = Modifier.height(16.dp))
                                GlassCard {
                                    result.positives.forEach { positive ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "✅",
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = positive,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = SuccessGreen
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // ██ 9. SUGGESTIONS (Yaptıklarım) ██
                    if (result.suggestions.isNotEmpty()) {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn()
                        ) {
                            Column {
                                SectionHeader(title = "Yaptıklarım", emoji = "🎯")
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tamamladığına tıkla, puan kazan!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextWhiteTertiary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                result.suggestions.forEachIndexed { index, suggestion ->
                                    SuggestionCard(
                                        suggestion = suggestion,
                                        index = index,
                                        isCompleted = index in completedSuggestions,
                                        onComplete = onCompleteSuggestion
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // ██ 10. CTA ██
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onRestart,
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
                            "Yeni Analiz 📸",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }

                // Confetti overlay
                if (showConfetti) {
                    ConfettiOverlay(
                        trigger = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

private fun scoreLabel(score: Int): String = when {
    score >= 85 -> "Harika! 🌟"
    score >= 70 -> "İyi Durumda 😊"
    score >= 50 -> "Fena Değil 😐"
    score >= 30 -> "İyileştirmeli 😬"
    else -> "Acil Müdahale! 😱"
}

package com.example.tidyai.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tidyai.data.remote.model.IssueSeverity
import com.example.tidyai.data.remote.model.ScoreDimension
import com.example.tidyai.data.remote.model.ScoreIssue
import com.example.tidyai.data.remote.model.ScoreSuggestion
import com.example.tidyai.data.remote.model.ScoreZone
import com.example.tidyai.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

// ═══════════════════════════════════════════════════
// GLASS CARD
// ═══════════════════════════════════════════════════

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    val cardModifier = modifier
        .fillMaxWidth()
        .clip(shape)
        .background(GlassWhite)
        .border(1.dp, GlassBorder, shape)
        .then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        )
        .padding(20.dp)

    Column(modifier = cardModifier, content = content)
}

// ═══════════════════════════════════════════════════
// SCORE RING (Büyük Dairesel Skor)
// ═══════════════════════════════════════════════════

@Composable
fun ScoreRing(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    strokeWidth: Dp = 14.dp
) {
    var animTriggered by remember { mutableStateOf(false) }
    val animProgress by animateFloatAsState(
        targetValue = if (animTriggered) score / 100f else 0f,
        animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
        label = "scoreRing"
    )
    val displayScore = (animProgress * 100).toInt()
    val color = scoreColor(score)

    LaunchedEffect(Unit) {
        delay(300)
        animTriggered = true
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val arcSize = this.size.minDimension
            val stroke = strokeWidth.toPx()

            // Track
            drawArc(
                color = DarkSurfaceVariant,
                startAngle = -225f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(arcSize - stroke, arcSize - stroke)
            )

            // Progress
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(color.copy(alpha = 0.6f), color),
                ),
                startAngle = -225f,
                sweepAngle = 270f * animProgress,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(arcSize - stroke, arcSize - stroke)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$displayScore",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 52.sp
                ),
                color = color
            )
            Text(
                text = scoreEmoji(score),
                fontSize = 28.sp
            )
        }
    }
}

private fun scoreEmoji(score: Int): String = when {
    score >= 85 -> "🌟"
    score >= 70 -> "😊"
    score >= 50 -> "😐"
    score >= 30 -> "😬"
    else -> "😱"
}

// ═══════════════════════════════════════════════════
// DIMENSION BAR (Boyut Çubuğu)
// ═══════════════════════════════════════════════════

@Composable
fun DimensionBar(
    dimension: ScoreDimension,
    modifier: Modifier = Modifier,
    delay: Int = 0
) {
    var animTriggered by remember { mutableStateOf(false) }
    val animProgress by animateFloatAsState(
        targetValue = if (animTriggered) dimension.value / 100f else 0f,
        animationSpec = tween(
            durationMillis = 1200,
            delayMillis = delay,
            easing = FastOutSlowInEasing
        ),
        label = "dimensionBar"
    )

    LaunchedEffect(Unit) { animTriggered = true }

    val color = scoreColor(dimension.value)
    val icon = mapSfSymbolToIcon(dimension.icon)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = dimension.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "${(animProgress * 100).toInt()}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DarkSurfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(color.copy(alpha = 0.7f), color)
                        )
                    )
            )
        }
    }
}

// ═══════════════════════════════════════════════════
// ISSUE BADGE
// ═══════════════════════════════════════════════════

@Composable
fun IssueBadge(
    issue: ScoreIssue,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, emoji) = when (issue.severity) {
        IssueSeverity.HIGH -> Triple(SeverityHigh.copy(alpha = 0.15f), SeverityHigh, "🔴")
        IssueSeverity.MEDIUM -> Triple(SeverityMedium.copy(alpha = 0.15f), SeverityMedium, "🟡")
        IssueSeverity.LOW -> Triple(SeverityLow.copy(alpha = 0.15f), SeverityLow, "🟢")
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = emoji, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = issue.text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            if (!issue.zone.isNullOrBlank()) {
                Text(
                    text = "📍 ${issue.zone}",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// SUGGESTION CARD (Yaptıklarım — Tıklanabilir)
// ═══════════════════════════════════════════════════

@Composable
fun SuggestionCard(
    suggestion: ScoreSuggestion,
    index: Int,
    isCompleted: Boolean,
    onComplete: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "suggScale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isCompleted) SuccessGreen.copy(alpha = 0.15f)
                else GlassWhite
            )
            .border(
                1.dp,
                if (isCompleted) SuccessGreen.copy(alpha = 0.3f) else GlassBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable(enabled = !isCompleted) { onComplete(index, suggestion.gain) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox circle
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (isCompleted) SuccessGreen
                    else Color.Transparent
                )
                .border(
                    2.dp,
                    if (isCompleted) SuccessGreen else GlassBorder,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = DarkBackground,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = suggestion.text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (isCompleted)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.onSurface,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "⏱ ${suggestion.effort}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Points badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isCompleted) SuccessGreen.copy(alpha = 0.2f)
                    else TidyPrimary.copy(alpha = 0.15f)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "+${suggestion.gain}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isCompleted) SuccessGreen else TidyPrimary
            )
        }
    }
}

// ═══════════════════════════════════════════════════
// ZONE SCORE CHIP
// ═══════════════════════════════════════════════════

@Composable
fun ZoneScoreChip(
    zone: ScoreZone,
    modifier: Modifier = Modifier
) {
    val color = scoreColor(zone.score)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GlassWhite)
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${zone.score}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = zone.area,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = zone.note,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ═══════════════════════════════════════════════════
// ACTION CARD (Ana ekran butonları)
// ═══════════════════════════════════════════════════

@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(GradientPrimaryStart, GradientPrimaryEnd)
) {
    val shape = RoundedCornerShape(20.dp)
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = gradientColors.map { it.copy(alpha = 0.15f) }
                    )
                )
                .border(1.dp, gradientColors[0].copy(alpha = 0.3f), shape)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DarkBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// SECTION HEADER (Bölüm Başlığı)
// ═══════════════════════════════════════════════════

@Composable
fun SectionHeader(
    title: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// ═══════════════════════════════════════════════════
// CONFETTI EFFECT
// ═══════════════════════════════════════════════════

@Composable
fun ConfettiOverlay(
    trigger: Boolean,
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val particles = remember { List(30) { ConfettiParticle() } }
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiProgress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val x = particle.x * size.width
            val y = (particle.y + progress * particle.speed) % 1f * size.height
            drawCircle(
                color = particle.color,
                radius = particle.radius,
                center = Offset(x, y)
            )
        }
    }
}

private data class ConfettiParticle(
    val x: Float = Random.nextFloat(),
    val y: Float = Random.nextFloat(),
    val speed: Float = 0.3f + Random.nextFloat() * 0.7f,
    val radius: Float = 3f + Random.nextFloat() * 5f,
    val color: Color = listOf(
        Color(0xFFFF6B6B), Color(0xFFFFD93D), Color(0xFF6BCB77),
        Color(0xFF4D96FF), Color(0xFFA66CFF), Color(0xFFFF9F43)
    ).random()
)

// ═══════════════════════════════════════════════════
// ANIMATED COUNTER
// ═══════════════════════════════════════════════════

@Composable
fun AnimatedPointsCounter(
    points: Int,
    modifier: Modifier = Modifier
) {
    val animatedPoints by animateIntAsState(
        targetValue = points,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "pointsCounter"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(GradientPrimaryStart.copy(alpha = 0.2f), GradientPrimaryEnd.copy(alpha = 0.2f))
                )
            )
            .border(1.dp, TidyPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "⭐", fontSize = 16.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "+$animatedPoints",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TidyPrimary
        )
    }
}

// ═══════════════════════════════════════════════════
// HELPER: SF Symbol → Material Icon mapping
// ═══════════════════════════════════════════════════

fun mapSfSymbolToIcon(sfSymbol: String?): ImageVector = when (sfSymbol) {
    "sparkles" -> Icons.Rounded.AutoAwesome
    "wind" -> Icons.Rounded.Air
    "sun.max" -> Icons.Rounded.WbSunny
    "leaf.fill" -> Icons.Rounded.Eco
    "square.grid.3x3" -> Icons.Rounded.GridView
    "paintbrush" -> Icons.Rounded.Brush
    "eye" -> Icons.Rounded.Visibility
    "figure.walk" -> Icons.Rounded.DirectionsWalk
    "lightbulb" -> Icons.Rounded.Lightbulb
    "star.fill" -> Icons.Rounded.Star
    "heart.fill" -> Icons.Rounded.Favorite
    "house.fill" -> Icons.Rounded.Home
    else -> Icons.Rounded.AutoAwesome
}

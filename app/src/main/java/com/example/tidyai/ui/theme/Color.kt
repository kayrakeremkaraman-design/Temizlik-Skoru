package com.example.tidyai.ui.theme

import androidx.compose.ui.graphics.Color

// ── Primary Palette ──
val TidyPrimary = Color(0xFF00DCAF)          // Neon mint yeşili
val TidyPrimaryVariant = Color(0xFF00B894)   // Koyu mint
val TidySecondary = Color(0xFF6C5CE7)        // Elektrik mor
val TidyAccent = Color(0xFF00D2FF)           // Parlak cyan

// ── Background (Dark) ──
val DarkBackground = Color(0xFF0A0E21)       // Çok koyu lacivert
val DarkSurface = Color(0xFF151A30)          // Lacivert kart
val DarkSurfaceVariant = Color(0xFF1E2545)   // Açık lacivert
val DarkElevated = Color(0xFF232B4A)         // Yükseltilmiş yüzey

// ── Background (Light) ──
val LightBackground = Color(0xFFF0F4F8)      // Soğuk beyaz
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFE8ECF1)
val LightElevated = Color(0xFFF5F7FA)

// ── Text ──
val TextWhite = Color(0xFFF5F5F5)
val TextWhiteSecondary = Color(0xB3F5F5F5)   // 70% alpha
val TextWhiteTertiary = Color(0x66F5F5F5)    // 40% alpha
val TextDark = Color(0xFF1A1A2E)
val TextDarkSecondary = Color(0xFF6B7280)

// ── Score Colors ──
val ScoreCritical = Color(0xFFFF6B6B)        // 0-29
val ScoreLow = Color(0xFFFF9F43)             // 30-49
val ScoreMedium = Color(0xFFFECA57)          // 50-69
val ScoreGood = Color(0xFF00DCAF)            // 70-84
val ScoreExcellent = Color(0xFFFFD700)       // 85-100

// ── Severity Colors ──
val SeverityHigh = Color(0xFFFF6B6B)
val SeverityMedium = Color(0xFFFF9F43)
val SeverityLow = Color(0xFFFECA57)

// ── Glass ──
val GlassWhite = Color(0x14FFFFFF)           // 8% white
val GlassBorder = Color(0x26FFFFFF)          // 15% white
val GlassWhiteLight = Color(0x0DFFFFFF)      // 5% white

// ── Gradients (start/end pairs) ──
val GradientPrimaryStart = Color(0xFF00DCAF)
val GradientPrimaryEnd = Color(0xFF00D2FF)
val GradientAccentStart = Color(0xFF6C5CE7)
val GradientAccentEnd = Color(0xFFA29BFE)
val GradientDangerStart = Color(0xFFFF6B6B)
val GradientDangerEnd = Color(0xFFEE5A24)

// ── Status ──
val ErrorRed = Color(0xFFFF6B6B)
val SuccessGreen = Color(0xFF00DCAF)
val WarningOrange = Color(0xFFFF9F43)

fun scoreColor(score: Int): Color = when {
    score >= 85 -> ScoreExcellent
    score >= 70 -> ScoreGood
    score >= 50 -> ScoreMedium
    score >= 30 -> ScoreLow
    else -> ScoreCritical
}

fun severityColor(severity: String): Color = when (severity.lowercase()) {
    "high" -> SeverityHigh
    "medium" -> SeverityMedium
    "low" -> SeverityLow
    else -> SeverityMedium
}

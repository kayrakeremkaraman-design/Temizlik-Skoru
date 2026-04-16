package com.example.tidyai.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.tidyai.data.repository.AppTheme

// ── Tema renk paletleri ──────────────────────────────────────────────────────

private fun darkScheme() = darkColorScheme(
    primary              = Color(0xFF00DCAF),
    onPrimary            = Color(0xFF0A0E21),
    primaryContainer     = Color(0xFF1E2545),
    onPrimaryContainer   = Color(0xFF00DCAF),
    secondary            = Color(0xFF6C5CE7),
    onSecondary          = Color.White,
    background           = Color(0xFF0A0E21),
    onBackground         = Color(0xFFF5F5F5),
    surface              = Color(0xFF151A30),
    onSurface            = Color(0xFFF5F5F5),
    surfaceVariant       = Color(0xFF1E2545),
    onSurfaceVariant     = Color(0xB3F5F5F5),
    outline              = Color(0x26FFFFFF),
    error                = Color(0xFFFF6B6B),
    onError              = Color.White
)

private fun mintScheme() = darkColorScheme(
    primary              = Color(0xFF00E676),
    onPrimary            = Color(0xFF00220F),
    primaryContainer     = Color(0xFF0A2E1A),
    onPrimaryContainer   = Color(0xFF00E676),
    secondary            = Color(0xFF1DE9B6),
    onSecondary          = Color.White,
    background           = Color(0xFF071A10),
    onBackground         = Color(0xFFF5F5F5),
    surface              = Color(0xFF0D2B1A),
    onSurface            = Color(0xFFF5F5F5),
    surfaceVariant       = Color(0xFF143D25),
    onSurfaceVariant     = Color(0xB3F5F5F5),
    outline              = Color(0x3300E676),
    error                = Color(0xFFFF6B6B),
    onError              = Color.White
)

private fun oceanScheme() = darkColorScheme(
    primary              = Color(0xFF40C4FF),
    onPrimary            = Color(0xFF001829),
    primaryContainer     = Color(0xFF0A1E3A),
    onPrimaryContainer   = Color(0xFF40C4FF),
    secondary            = Color(0xFF00B0FF),
    onSecondary          = Color.White,
    background           = Color(0xFF060F1E),
    onBackground         = Color(0xFFF5F5F5),
    surface              = Color(0xFF0C1A30),
    onSurface            = Color(0xFFF5F5F5),
    surfaceVariant       = Color(0xFF102040),
    onSurfaceVariant     = Color(0xB3F5F5F5),
    outline              = Color(0x3340C4FF),
    error                = Color(0xFFFF6B6B),
    onError              = Color.White
)

private fun sunsetScheme() = darkColorScheme(
    primary              = Color(0xFFFF6E4A),
    onPrimary            = Color(0xFF2A0E07),
    primaryContainer     = Color(0xFF3D1000),
    onPrimaryContainer   = Color(0xFFFF6E4A),
    secondary            = Color(0xFFFFB74D),
    onSecondary          = Color(0xFF1A0E00),
    background           = Color(0xFF1A0800),
    onBackground         = Color(0xFFF5F5F5),
    surface              = Color(0xFF2A1000),
    onSurface            = Color(0xFFF5F5F5),
    surfaceVariant       = Color(0xFF3D1800),
    onSurfaceVariant     = Color(0xB3F5F5F5),
    outline              = Color(0x33FF6E4A),
    error                = Color(0xFFFF6B6B),
    onError              = Color.White
)

private fun purpleScheme() = darkColorScheme(
    primary              = Color(0xFFCE93D8),
    onPrimary            = Color(0xFF1A0029),
    primaryContainer     = Color(0xFF2D0040),
    onPrimaryContainer   = Color(0xFFCE93D8),
    secondary            = Color(0xFF9C27B0),
    onSecondary          = Color.White,
    background           = Color(0xFF0D0014),
    onBackground         = Color(0xFFF5F5F5),
    surface              = Color(0xFF180020),
    onSurface            = Color(0xFFF5F5F5),
    surfaceVariant       = Color(0xFF250030),
    onSurfaceVariant     = Color(0xB3F5F5F5),
    outline              = Color(0x33CE93D8),
    error                = Color(0xFFFF6B6B),
    onError              = Color.White
)

// ── Tema arka plan gradyan renkleri (tüm ekranlarda kullanılır) ──────────────

data class TidyThemeColors(
    val gradientTop: Color,
    val gradientMid: Color,
    val primary: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val glassBorder: Color
)

val LocalTidyTheme = compositionLocalOf {
    TidyThemeColors(
        gradientTop     = Color(0xFF0A0E21),
        gradientMid     = Color(0xFF151A30),
        primary         = Color(0xFF00DCAF),
        surface         = Color(0xFF151A30),
        surfaceVariant  = Color(0xFF1E2545),
        glassBorder     = Color(0x26FFFFFF)
    )
}

private fun themeColors(appTheme: AppTheme) = when (appTheme) {
    AppTheme.DARK -> TidyThemeColors(
        gradientTop    = Color(0xFF0A0E21),
        gradientMid    = Color(0xFF151A30),
        primary        = Color(0xFF00DCAF),
        surface        = Color(0xFF151A30),
        surfaceVariant = Color(0xFF1E2545),
        glassBorder    = Color(0x26FFFFFF)
    )
    AppTheme.MINT -> TidyThemeColors(
        gradientTop    = Color(0xFF071A10),
        gradientMid    = Color(0xFF0D2B1A),
        primary        = Color(0xFF00E676),
        surface        = Color(0xFF0D2B1A),
        surfaceVariant = Color(0xFF143D25),
        glassBorder    = Color(0x3300E676)
    )
    AppTheme.OCEAN -> TidyThemeColors(
        gradientTop    = Color(0xFF060F1E),
        gradientMid    = Color(0xFF0C1A30),
        primary        = Color(0xFF40C4FF),
        surface        = Color(0xFF0C1A30),
        surfaceVariant = Color(0xFF102040),
        glassBorder    = Color(0x3340C4FF)
    )
    AppTheme.SUNSET -> TidyThemeColors(
        gradientTop    = Color(0xFF1A0800),
        gradientMid    = Color(0xFF2A1000),
        primary        = Color(0xFFFF6E4A),
        surface        = Color(0xFF2A1000),
        surfaceVariant = Color(0xFF3D1800),
        glassBorder    = Color(0x33FF6E4A)
    )
    AppTheme.PURPLE -> TidyThemeColors(
        gradientTop    = Color(0xFF0D0014),
        gradientMid    = Color(0xFF180020),
        primary        = Color(0xFFCE93D8),
        surface        = Color(0xFF180020),
        surfaceVariant = Color(0xFF250030),
        glassBorder    = Color(0x33CE93D8)
    )
}

private fun colorScheme(appTheme: AppTheme) = when (appTheme) {
    AppTheme.DARK   -> darkScheme()
    AppTheme.MINT   -> mintScheme()
    AppTheme.OCEAN  -> oceanScheme()
    AppTheme.SUNSET -> sunsetScheme()
    AppTheme.PURPLE -> purpleScheme()
}

// ── Ana tema bileşeni ────────────────────────────────────────────────────────

@Composable
fun TidyAITheme(
    appTheme: AppTheme = AppTheme.DARK,
    content: @Composable () -> Unit
) {
    val tidyColors = themeColors(appTheme)
    val colorScheme = colorScheme(appTheme)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor     = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars     = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    CompositionLocalProvider(LocalTidyTheme provides tidyColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            content     = content
        )
    }
}

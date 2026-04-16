package com.example.tidyai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tidyai.data.repository.AppTheme
import com.example.tidyai.ui.components.GlassCard
import com.example.tidyai.ui.theme.*
import com.example.tidyai.viewmodel.SettingsViewModel

private val themePreviewColors: Map<AppTheme, List<Color>> = mapOf(
    AppTheme.DARK   to listOf(Color(0xFF0A0E21), Color(0xFF151A30)),
    AppTheme.MINT   to listOf(Color(0xFF071A10), Color(0xFF143D25)),
    AppTheme.OCEAN  to listOf(Color(0xFF060F1E), Color(0xFF102040)),
    AppTheme.SUNSET to listOf(Color(0xFF1A0800), Color(0xFF3D1800)),
    AppTheme.PURPLE to listOf(Color(0xFF0D0014), Color(0xFF250030))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val currentTheme by viewModel.appTheme.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Top Bar ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = "Geri",
                        tint = TextWhite
                    )
                }
                Text(
                    text = "Ayarlar",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(8.dp))

                // ── Tema Seçimi ──
                GlassCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Uygulama Teması",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTheme.entries.forEach { theme ->
                        ThemeOption(
                            theme      = theme,
                            isSelected = theme == currentTheme,
                            onSelect   = { viewModel.saveTheme(theme) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Hakkında ──
                GlassCard {
                    Text(
                        "Uygulama Hakkında",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InfoRow("Uygulama", "Temizlik Dağınıklık Skoru")
                    InfoRow("Sürüm", "1.0.0")
                    InfoRow("Geliştirici", "Kayra Kerem Karaman")
                    InfoRow("AI", "OpenRouter — Gemini 2.5 Flash Lite")

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Fotoğraflarınız sadece analiz için gönderilir ve cihazınızda tutulur. Gizliliğiniz bizim önceliğimizdir. 🔒",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextWhiteTertiary,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
}

@Composable
private fun ThemeOption(
    theme: AppTheme,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val preview  = themePreviewColors[theme] ?: listOf(DarkBackground, DarkSurface)
    val accent   = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width  = if (isSelected) 2.dp else 1.dp,
                color  = if (isSelected) accent else GlassBorder,
                shape  = RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.horizontalGradient(preview))
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text       = "${theme.emoji} ${theme.displayName}",
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color      = if (isSelected) accent else TextWhite,
            modifier   = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                Icons.Rounded.Check,
                contentDescription = null,
                tint               = accent,
                modifier           = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextWhiteSecondary)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = TextWhite)
    }
}

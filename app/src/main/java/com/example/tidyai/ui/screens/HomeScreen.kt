package com.example.tidyai.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.tidyai.ui.components.ActionCard
import com.example.tidyai.ui.components.GlassCard
import com.example.tidyai.ui.theme.*
import com.example.tidyai.viewmodel.FREE_QUERY_LIMIT
import java.io.File

private fun createTempImageUri(context: Context): Uri {
    val imageFile = File(context.cacheDir, "images").apply { mkdirs() }
    val tempFile = File.createTempFile("tidy_camera_", ".jpg", imageFile)
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedImageUri: Uri?,
    spaceType: String,
    spaceTypes: List<String>,
    queryCount: Int,
    showAdDialog: Boolean,
    onImageSelected: (Uri?) -> Unit,
    onSpaceTypeSelected: (String) -> Unit,
    onStartAnalysis: () -> Unit,     // ViewModel'den tryStartAnalysis() çağrılacak
    onAdWatched: () -> Unit,
    onAdDismiss: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var showSpaceTypeDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> onImageSelected(uri) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            onImageSelected(tempCameraUri)
        }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Top Bar ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Temizlik Skoru ✨",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Odanı analiz et, puan kazan!",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextWhiteSecondary
                    )
                }
                Row {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            Icons.Rounded.History,
                            contentDescription = "Geçmiş",
                            tint = TextWhiteSecondary
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Rounded.Settings,
                            contentDescription = "Ayarlar",
                            tint = TextWhiteSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Sorgu Hakkı Göstergesi ──
            QueryCounterBadge(queryCount = queryCount, limit = FREE_QUERY_LIMIT)

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedImageUri != null) {
                // ── Image Preview ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Seçilen fotoğraf",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Space type selector
                GlassCard(onClick = { showSpaceTypeDialog = true }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Rounded.Home,
                                contentDescription = null,
                                tint = TidyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = spaceType,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                        }
                        Icon(
                            Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextWhiteSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Analyze button
                Button(
                    onClick = onStartAnalysis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TidyPrimary,
                        contentColor = DarkBackground
                    )
                ) {
                    Icon(
                        Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Analiz Et 🔍",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = { onImageSelected(null) }) {
                    Text(
                        "Fotoğrafı Değiştir",
                        color = ErrorRed.copy(alpha = 0.8f)
                    )
                }

            } else {
                // ── Empty State ──
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "🏠",
                    fontSize = 64.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Odanı Analiz Edelim!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Fotoğraf çek veya galeriden seç,\nYapay zeka gerisini halleder.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextWhiteSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                ActionCard(
                    title = "Fotoğraf Çek",
                    subtitle = "Kamerayı kullan",
                    icon = Icons.Rounded.CameraAlt,
                    onClick = {
                        val uri = createTempImageUri(context)
                        tempCameraUri = uri
                        cameraLauncher.launch(uri)
                    },
                    gradientColors = listOf(GradientPrimaryStart, GradientPrimaryEnd)
                )

                Spacer(modifier = Modifier.height(14.dp))

                ActionCard(
                    title = "Galeriden Seç",
                    subtitle = "Mevcut bir fotoğraf seç",
                    icon = Icons.Rounded.PhotoLibrary,
                    onClick = { galleryLauncher.launch("image/*") },
                    gradientColors = listOf(GradientAccentStart, GradientAccentEnd)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }

    // ── Space Type Dialog ──
    if (showSpaceTypeDialog) {
        AlertDialog(
            onDismissRequest = { showSpaceTypeDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    "Oda Tipi Seçin",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Column {
                    spaceTypes.forEach { type ->
                        TextButton(
                            onClick = {
                                onSpaceTypeSelected(type)
                                showSpaceTypeDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                type,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth(),
                                color = if (type == spaceType) TidyPrimary else TextWhite,
                                fontWeight = if (type == spaceType) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // ── Reklam Dialog ──
    AnimatedVisibility(
        visible = showAdDialog,
        enter = fadeIn() + scaleIn(initialScale = 0.9f),
        exit = fadeOut() + scaleOut(targetScale = 0.9f)
    ) {
        AdWatchDialog(
            onWatch = onAdWatched,
            onDismiss = onAdDismiss
        )
    }
}

// ── Sorgu sayacı rozeti ──
@Composable
private fun QueryCounterBadge(queryCount: Int, limit: Int) {
    val remaining = limit - queryCount
    val color = when {
        remaining > 1 -> TidyPrimary
        remaining == 1 -> Color(0xFFFFC107)
        else -> ErrorRed
    }

    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.Analytics,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Ücretsiz Sorgu Hakkı",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )
                    Text(
                        text = if (remaining > 0) "Kalan: $remaining / $limit analiz" else "Limitin doldu — reklam izle!",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (remaining > 0) TextWhiteSecondary else ErrorRed
                    )
                }
            }
            // Küçük progress dots
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(limit) { index ->
                    val filled = index < queryCount
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (filled) color.copy(alpha = 0.3f) else color)
                    )
                }
            }
        }
    }
}

// ── Reklam izleme dialog'u ──
@Composable
private fun AdWatchDialog(
    onWatch: () -> Unit,
    onDismiss: () -> Unit
) {
    var watching by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var done by remember { mutableStateOf(false) }

    // Reklam sayaç animasyonu (5 saniye)
    LaunchedEffect(watching) {
        if (watching) {
            val steps = 100
            repeat(steps) {
                kotlinx.coroutines.delay(50L)
                progress = (it + 1) / steps.toFloat()
            }
            done = true
        }
    }

    Dialog(
        onDismissRequest = { if (!watching) onDismiss() },
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // İkon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(TidyPrimary.copy(alpha = 0.3f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (done) Icons.Rounded.CheckCircle else Icons.Rounded.PlayCircle,
                        contentDescription = null,
                        tint = if (done) TidyPrimary else Color(0xFFFFC107),
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (done) "Teşekkürler! 🎉" else "Daha fazla analiz için",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (done)
                        "Hakkın yenilendi. Tekrar 2 ücretsiz analizin var!"
                    else
                        "Ücretsiz analiz limitine ulaştın.\nKısa bir reklam izle ve $FREE_QUERY_LIMIT hakkını geri kazan! 🚀",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhiteSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (watching && !done) {
                    // Progress bar simülasyonu
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50)),
                        color = TidyPrimary,
                        trackColor = DarkSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Reklam izleniyor... ${(progress * 5).toInt() + 1}/5 sn",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextWhiteTertiary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (done) {
                    Button(
                        onClick = onWatch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TidyPrimary,
                            contentColor = DarkBackground
                        )
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analiz Yapmaya Devam Et", fontWeight = FontWeight.Bold)
                    }
                } else if (!watching) {
                    Button(
                        onClick = { watching = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFC107),
                            contentColor = DarkBackground
                        )
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reklamı İzle (5 sn)", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = onDismiss) {
                        Text(
                            "Vazgeç",
                            color = TextWhiteTertiary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

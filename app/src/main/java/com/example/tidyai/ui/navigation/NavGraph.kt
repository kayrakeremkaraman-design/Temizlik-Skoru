package com.example.tidyai.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tidyai.ui.screens.*
import com.example.tidyai.viewmodel.AnalyzeViewModel
import com.example.tidyai.viewmodel.HomeViewModel
import com.example.tidyai.viewmodel.SettingsViewModel

sealed class Screen(val route: String) {
    object Splash      : Screen("splash")
    object Onboarding  : Screen("onboarding")
    object Home        : Screen("home")
    object Analyzing   : Screen("analyzing")
    object Result      : Screen("result")
    object History     : Screen("history")
    object Settings    : Screen("settings")
}

@Composable
fun TidyAiNavHost(
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    analyzeViewModel: AnalyzeViewModel = hiltViewModel(),
    historyViewModel: com.example.tidyai.viewmodel.HistoryViewModel = hiltViewModel(),
    // MainActivity'den geliyor, zaten oluşturulmuş
    settingsViewModel: SettingsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition  = { fadeOut(animationSpec = tween(300)) }
    ) {

        // ── Splash: onboarding görüldü mü kontrol et ──────────────────────
        composable(Screen.Splash.route) {
            val hasSeenOnboarding by settingsViewModel.hasSeenOnboarding.collectAsState()

            SplashScreen {
                if (hasSeenOnboarding) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
        }

        // ── Onboarding: bitince flag set et ──────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen {
                settingsViewModel.markOnboardingSeen()
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            }
        }

        // ── Home ──────────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            val selectedImage  by homeViewModel.selectedImageUri.collectAsState()
            val spaceType      by homeViewModel.selectedSpaceType.collectAsState()
            val queryCount     by homeViewModel.queryCount.collectAsState()
            val showAdDialog   by homeViewModel.showAdDialog.collectAsState()

            HomeScreen(
                selectedImageUri  = selectedImage,
                spaceType         = spaceType,
                spaceTypes        = homeViewModel.spaceTypes,
                queryCount        = queryCount,
                showAdDialog      = showAdDialog,
                onImageSelected   = { homeViewModel.setImageUri(it) },
                onSpaceTypeSelected = { homeViewModel.setSpaceType(it) },
                onStartAnalysis   = {
                    if (selectedImage != null) {
                        val canGo = homeViewModel.tryStartAnalysis()
                        if (canGo) {
                            analyzeViewModel.analyzeImage(
                                uri          = selectedImage!!,
                                spaceType    = spaceType,
                                onQuotaRefund = { homeViewModel.refundQuery() }
                            )
                            navController.navigate(Screen.Analyzing.route)
                        }
                    }
                },
                onAdWatched  = { homeViewModel.onAdWatched() },
                onAdDismiss  = { homeViewModel.dismissAdDialog() },
                onNavigateToHistory  = { navController.navigate(Screen.History.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        // ── Analyzing ─────────────────────────────────────────────────────
        composable(Screen.Analyzing.route) {
            AnalyzingScreen(
                analyzeState = analyzeViewModel.analyzeState.collectAsState().value,
                onAnalysisComplete = {
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Analyzing.route) { inclusive = true }
                    }
                },
                onError = { navController.popBackStack() },
                onGoHome = {
                    analyzeViewModel.resetState()
                    homeViewModel.setImageUri(null)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Result ────────────────────────────────────────────────────────
        composable(Screen.Result.route) {
            val analyzeState         by analyzeViewModel.analyzeState.collectAsState()
            val selectedImage        by homeViewModel.selectedImageUri.collectAsState()
            val completedSuggestions by analyzeViewModel.completedSuggestions.collectAsState()
            val sessionPoints        by analyzeViewModel.sessionPoints.collectAsState()

            ResultScreen(
                state                = analyzeState,
                originalImageUri     = selectedImage,
                completedSuggestions = completedSuggestions,
                sessionPoints        = sessionPoints,
                onCompleteSuggestion = { index, gain ->
                    analyzeViewModel.completeSuggestion(index, gain)
                },
                onRestart = {
                    homeViewModel.setImageUri(null)
                    analyzeViewModel.resetState()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // ── History ───────────────────────────────────────────────────────
        composable(Screen.History.route) {
            HistoryScreen(
                viewModel      = historyViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Settings ──────────────────────────────────────────────────────
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel      = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

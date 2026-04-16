package com.example.tidyai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.example.tidyai.ui.navigation.TidyAiNavHost
import com.example.tidyai.ui.theme.TidyAITheme
import com.example.tidyai.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val appTheme by settingsViewModel.appTheme.collectAsState()

            TidyAITheme(appTheme = appTheme) {
                TidyAiNavHost(settingsViewModel = settingsViewModel)
            }
        }
    }
}

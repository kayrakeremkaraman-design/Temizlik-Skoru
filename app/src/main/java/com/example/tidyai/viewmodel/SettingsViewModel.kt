package com.example.tidyai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tidyai.data.repository.AppTheme
import com.example.tidyai.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val apiKey: StateFlow<String?> = settingsRepository.apiKey
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val appTheme: StateFlow<AppTheme> = settingsRepository.appTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.DARK
        )

    val hasSeenOnboarding: StateFlow<Boolean> = settingsRepository.hasSeenOnboarding
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,  // Splash ekranında hemen lazım
            initialValue = false
        )

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            settingsRepository.saveApiKey(key)
        }
    }

    fun saveTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.saveTheme(theme)
        }
    }

    fun markOnboardingSeen() {
        viewModelScope.launch {
            settingsRepository.markOnboardingSeen()
        }
    }
}

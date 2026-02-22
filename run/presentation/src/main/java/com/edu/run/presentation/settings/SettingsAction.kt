package com.edu.run.presentation.settings

sealed interface SettingsAction {
    data object OnBackClick: SettingsAction
}
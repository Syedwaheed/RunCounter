package com.edu.run.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel : ViewModel() {

    var state by mutableStateOf(SettingsState())
        private set

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.OnBackClick -> Unit
        }
    }

}
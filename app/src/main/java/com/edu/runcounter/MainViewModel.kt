package com.edu.runcounter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.core.domain.SessionStorage
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionStorge: SessionStorage
): ViewModel() {
    var state by mutableStateOf(MainState ())
        private set

    init{
        viewModelScope.launch{
            state = state.copy(isCheckingAuth = true)
            state = state.copy(
                isLoggedIn = sessionStorge.get() != null
            )
            state = state.copy(isCheckingAuth = false)
        }
    }
}
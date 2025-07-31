package com.edu.auth.presentation.register

import com.edu.core.presentation.ui.UiText

sealed interface RegisterEvent {
    data object RegisterSuccess: RegisterEvent
    data class Error(val error: UiText): RegisterEvent

}
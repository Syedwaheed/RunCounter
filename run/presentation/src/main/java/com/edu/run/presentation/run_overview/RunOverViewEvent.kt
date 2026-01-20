package com.edu.run.presentation.run_overview

sealed interface RunOverViewEvent {
    data object LogoutSuccess : RunOverViewEvent
}
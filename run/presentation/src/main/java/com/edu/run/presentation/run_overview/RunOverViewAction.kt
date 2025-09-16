package com.edu.run.presentation.run_overview

sealed interface RunOverViewAction {
    data object OnStartClick: RunOverViewAction
    data object OnLogoutClick: RunOverViewAction
    data object OnAnalyticClick: RunOverViewAction
}
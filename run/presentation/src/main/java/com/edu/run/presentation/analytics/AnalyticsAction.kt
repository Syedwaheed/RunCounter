package com.edu.run.presentation.analytics

sealed interface AnalyticsAction {
    data object OnBackClick : AnalyticsAction
}
package com.edu.run.presentation.analytics

data class AnalyticsState(
    val totalDistanceRun: String = "",
    val totalTimeRun: String = "",
    val fastestEverRun: String = "",
    val avgDistance: String = "",
    val avgPace: String = "",
    val totalRuns: Int = 0
)
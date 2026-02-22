package com.edu.run.presentation.run_overview.model

data class RunUI(
    val id: String,
    val duration: String,
    val dateTime: String,
    val distance: String,
    val avgSpeed: String,
    val maxSpeed: String,
    val pace: String,
    val totalElevation: String,
    val mapPictureUrl: String?,
    val goalId: String? = null,
    val goalName: String? = null,
)

package com.edu.goal.presentation.model

data class GoalUI(
    val id: String,
    val name: String,
    val targetDistanceMeters: String,
    val currentDistanceMeters: String,
    val progressPercentage: Float,
    val endDate: String,
    val isExpired: Boolean
)
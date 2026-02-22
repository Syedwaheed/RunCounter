package com.edu.goal.domain

sealed interface GoalValidationResult {
    data object Success: GoalValidationResult
    data class Error(val message: String) : GoalValidationResult
}
package com.edu.goal.domain

import java.time.LocalDate

interface GoalValidator {
    fun validateEndDate(date: LocalDate?) : GoalValidationResult
    fun validateTarget(target: String): GoalValidationResult
    fun validateName(name: String): GoalValidationResult
}
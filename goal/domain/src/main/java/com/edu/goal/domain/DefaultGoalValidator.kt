package com.edu.goal.domain

import java.time.LocalDate

class DefaultGoalValidator : GoalValidator {
    override fun validateEndDate(date: LocalDate?) : GoalValidationResult{
        return when{
            date == null -> GoalValidationResult.Error("End date cannot be empty")
            date.isBefore(LocalDate.now()) -> GoalValidationResult.Error("End date must be in future")
            else -> GoalValidationResult.Success
        }
    }

    override fun validateTarget(target: String): GoalValidationResult{
        return when{
            target.isBlank() -> GoalValidationResult.Error("Target cannot be empty")
            target.toDoubleOrNull() == null -> GoalValidationResult.Error("Target must be a number")
            target.toDouble() <= 0 -> GoalValidationResult.Error("Target must be greater than 0")
            else -> GoalValidationResult.Success
        }
    }

    override fun validateName(name: String): GoalValidationResult{
        return when{
            name.isBlank() -> GoalValidationResult.Error("Name cannot be empty")
            name.length < 3 -> GoalValidationResult.Error("Name must be at least 3 characters")
            else -> GoalValidationResult.Success
        }
    }
}
package com.edu.goal.presentation.mappers

import com.edu.core.presentation.ui.toFormattedKm
import com.edu.goal.domain.Goal
import com.edu.goal.presentation.model.GoalUI
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun Goal.toGoalUI(): GoalUI{
   return GoalUI(
        id = id,
        name = name,
        targetDistanceMeters = (targetDistanceMeters / 1000.0).toFormattedKm(),
        currentDistanceMeters = (currentDistanceMeters / 1000.0).toFormattedKm(),
       progressPercentage = progressPercentage,
        endDate = DateTimeFormatter.ofPattern("MMM dd,yyyy").format(endDate),
        isExpired = isExpired
    )

}

fun LocalDate.toZonedDateTime(): ZonedDateTime{
    return this.atTime(23,59,59).atZone(ZoneId.systemDefault())
}



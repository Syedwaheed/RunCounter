package com.edu.goal.data.mappers

import com.edu.core.database.entity.GoalEntity
import com.edu.goal.domain.Goal
import java.time.ZonedDateTime

fun GoalEntity.toGoal(): Goal {
    val startDate = ZonedDateTime.parse(this.startDate)
    val endDate = ZonedDateTime.parse(this.endDate)
    return Goal(
        id = id,
        name = name,
        targetDistanceMeters = targetDistanceMeters,
        currentDistanceMeters = currentDistanceMeters,
        startDate = startDate,
        endDate = endDate,
        isCompleted = isCompleted
    )
}

fun Goal.toGoalEntity(): GoalEntity {
    return GoalEntity(
        id = id,
        name = name,
        targetDistanceMeters = targetDistanceMeters,
        currentDistanceMeters = currentDistanceMeters,
        startDate = startDate.toString(),
        endDate = endDate.toString(),
        isCompleted = isCompleted
    )
}

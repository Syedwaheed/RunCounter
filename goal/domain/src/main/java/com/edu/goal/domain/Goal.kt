package com.edu.goal.domain

import java.time.ZonedDateTime

data class Goal(
    val id: String,
    val name: String,
    val targetDistanceMeters: Long,
    val currentDistanceMeters: Long = 0L,
    val startDate: ZonedDateTime = ZonedDateTime.now(),
    val endDate: ZonedDateTime,
    val isCompleted: Boolean = false
) {
    val progressPercentage: Float
        get() = if (targetDistanceMeters > 0) {
            (currentDistanceMeters.toFloat() / targetDistanceMeters.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val remainingDistanceMeters: Long
        get() = (targetDistanceMeters - currentDistanceMeters).coerceAtLeast(0L)

    val isExpired: Boolean
        get() = ZonedDateTime.now().isAfter(endDate) && !isCompleted
}

package com.edu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity
data class GoalEntity(
    @PrimaryKey val id: String,
    val name: String,
    val targetDistanceMeters: Long,
    val currentDistanceMeters: Long,
    val startDate: String,
    val endDate: String,
    val isCompleted: Boolean,
    val userId: String
)

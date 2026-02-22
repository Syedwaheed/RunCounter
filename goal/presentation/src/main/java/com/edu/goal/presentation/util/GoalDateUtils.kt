package com.edu.goal.presentation.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object GoalDateUtils {

    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd,yyyy")

    fun calculateDaysRemaining(endDateStr: String): Int {
        return try {
            val endDate = LocalDate.parse(endDateStr, dateFormatter)
            val today = LocalDate.now()
            ChronoUnit.DAYS.between(today, endDate).toInt()
        } catch (e: Exception) {
            -1
        }
    }

    fun isExpired(endDateStr: String): Boolean {
        return calculateDaysRemaining(endDateStr) < 0
    }

    fun formatDate(date: LocalDate): String {
        return dateFormatter.format(date)
    }
}
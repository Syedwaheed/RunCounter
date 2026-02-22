package com.edu.goal.domain

import java.time.LocalDate

interface DateFormatter {
    fun formatDate(date: LocalDate): String
}
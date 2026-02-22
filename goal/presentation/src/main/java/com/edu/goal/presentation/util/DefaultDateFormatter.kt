package com.edu.goal.presentation.util

import com.edu.goal.domain.DateFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DefaultDateFormatter : DateFormatter {
    private val displayFormatter = DateTimeFormatter.ofPattern("MMM dd,yyyy")
    override fun formatDate(date: LocalDate): String {
        return displayFormatter.format(date)
    }

}
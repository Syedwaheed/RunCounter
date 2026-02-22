package com.edu.goal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.core.presentation.designsystem.RunCounterCyan
import com.edu.core.presentation.designsystem.RunCounterCyan10

@Composable
fun DaysRemainingChip(
    days: Int,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = getChipColors(days)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = formatDaysText(days),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

private fun getChipColors(days: Int): Pair<Color, Color> {
    return when {
        days <= 3 -> Color(0xFFEF5350).copy(alpha = 0.15f) to Color(0xFFEF5350)
        days <= 7 -> Color(0xFFFFA726).copy(alpha = 0.15f) to Color(0xFFFFA726)
        else -> RunCounterCyan10 to RunCounterCyan
    }
}

private fun formatDaysText(days: Int): String {
    return if (days == 1) "$days day left" else "$days days left"
}
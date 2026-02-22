package com.edu.goal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.edu.core.presentation.designsystem.RunCounterTheme

@Composable
fun CircularProgressIndicator(
    progress: Float,
    progressColor: Color,
    trackColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 70.dp,
    strokeWidth: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .drawBehind {
                val strokePx = strokeWidth.toPx()
                val radius = (size.toPx() - strokePx) / 2

                drawCircle(
                    color = trackColor,
                    radius = radius,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )

                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round),
                    topLeft = Offset(strokePx / 2, strokePx / 2),
                    size = Size(size.toPx() - strokePx, size.toPx() - strokePx)
                )
            }
    )
}

@Composable
fun GradientLinearProgressBar(
    progress: Float,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    cornerRadius: Dp = 6.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(12.dp)
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    brush = Brush.horizontalGradient(gradientColors)
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CircularProgressIndicatorPreview() {
    RunCounterTheme {
        CircularProgressIndicator(
            progress = 0.75f,
            progressColor = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
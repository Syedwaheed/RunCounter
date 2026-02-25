package com.edu.goal.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edu.core.presentation.designsystem.CalendarIcon
import com.edu.core.presentation.designsystem.DeleteIcon
import com.edu.core.presentation.designsystem.RunCounterCyan
import com.edu.core.presentation.designsystem.RunCounterTheme
import com.edu.goal.presentation.R
import com.edu.goal.presentation.model.GoalUI
import com.edu.goal.presentation.util.GoalDateUtils
import kotlinx.coroutines.delay

@Composable
fun AnimatedGoalCard(
    goal: GoalUI,
    index: Int,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
    showSwipeHint: Boolean = false
) {
    var visible by remember { mutableStateOf(false) }
    var hasBeenVisible by remember { mutableStateOf(false) }

    // Hint animation offset
    val hintOffset = remember { Animatable(0f) }

    val dismissState = remember {
        SwipeToDismissBoxState(
            initialValue = SwipeToDismissBoxValue.Settled,
            positionalThreshold = { totalDistance -> totalDistance * 0.5f }
        )
    }

    // Handle dismissal when state changes to EndToStart
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            visible = false
        }
    }

    LaunchedEffect(Unit) {
        delay(index * 100L)
        visible = true
        hasBeenVisible = true
    }

    // Swipe hint animation - only for first card when enabled
    LaunchedEffect(visible, showSwipeHint) {
        if (visible && showSwipeHint && index == 0) {
            delay(600) // Wait for entry animation to complete
            repeat(2) {
                hintOffset.animateTo(
                    targetValue = -100f,
                    animationSpec = tween(300)
                )
                delay(150)
                hintOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(300)
                )
                delay(100)
            }
        }
    }

    LaunchedEffect(visible) {
        if (!visible && hasBeenVisible) {
            onDelete(goal.id)
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(300)
        ) + slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            initialOffsetY = { it / 2 }
        )
    ) {
        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = false,  // Only allow swipe from right to left
            enableDismissFromEndToStart = true,
            backgroundContent = {
                // Show background when actively swiping OR during hint animation
                val swipeOffset = runCatching {
                    dismissState.requireOffset()
                }.getOrDefault(0f)
                val showDelete = swipeOffset < 0f || hintOffset.value < 0f
                DeleteBackground(showDelete)
            },
            content = {
                GoalCard(
                    goal = goal,
                    onDelete = onDelete,
                    modifier = modifier.offset { IntOffset(hintOffset.value.toInt(), 0) }
                )
            }
        )
    }


}

@Composable
private fun DeleteBackground(
    showDelete: Boolean
) {
    val color = if (showDelete) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        if (showDelete) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.delete),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Icon(
                    imageVector = DeleteIcon,
                    contentDescription = stringResource(id = R.string.delete),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}


@Composable
fun GoalCard(
    goal: GoalUI,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(goal.progressPercentage) {
        animatedProgress.animateTo(
            targetValue = goal.progressPercentage,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
    }

    val progressColor = getProgressColor(goal.progressPercentage)
    val gradientColors = getProgressGradientColors(goal.progressPercentage)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            GoalCardHeader(
                goal = goal,
                animatedProgress = animatedProgress.value,
                progressColor = progressColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            GradientLinearProgressBar(
                progress = animatedProgress.value,
                gradientColors = gradientColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            GoalCardFooter(
                goal = goal,
                progressColor = progressColor,
                onDelete = onDelete
            )

            if (goal.progressPercentage >= 1f) {
                Spacer(modifier = Modifier.height(12.dp))
                GoalCompletedBanner()
            }
        }
    }
}

@Composable
private fun GoalCardHeader(
    goal: GoalUI,
    animatedProgress: Float,
    progressColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = goal.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = CalendarIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = goal.endDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!goal.isExpired) {
                    val daysRemaining = GoalDateUtils.calculateDaysRemaining(goal.endDate)
                    if (daysRemaining >= 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        DaysRemainingChip(days = daysRemaining)
                    }
                }
            }
        }

        Box(
            modifier = Modifier.size(70.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = animatedProgress,
                progressColor = progressColor,
                trackColor = progressColor.copy(alpha = 0.2f)
            )
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = progressColor
            )
        }
    }
}

@Composable
private fun GoalCardFooter(
    goal: GoalUI,
    progressColor: Color,
    onDelete: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.current),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = goal.currentDistanceMeters,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = progressColor
            )
        }

        /*   Icon(
               modifier = Modifier
                   .size(32.dp)
                   .clip(CircleShape)
                   .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                   .clickable(onClick = { onDelete(goal.id) })
                   .padding(6.dp),
               imageVector = DeleteIcon,
               contentDescription = "Delete",
               tint = MaterialTheme.colorScheme.error
           )*/

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(id = R.string.target),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = goal.targetDistanceMeters,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun GoalCompletedBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF4CAF50).copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.goal_completed),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF4CAF50)
        )
    }
}

private fun getProgressColor(progress: Float): Color {
    return when {
        progress >= 1f -> Color(0xFF4CAF50)
        progress >= 0.7f -> RunCounterCyan
        progress >= 0.4f -> Color(0xFFFFA726)
        else -> Color(0xFFEF5350)
    }
}

private fun getProgressGradientColors(progress: Float): List<Color> {
    return when {
        progress >= 1f -> listOf(Color(0xFF4CAF50), Color(0xFF81C784))
        progress >= 0.7f -> listOf(RunCounterCyan, Color(0xFF4DD0E1))
        progress >= 0.4f -> listOf(Color(0xFFFFA726), Color(0xFFFFCC80))
        else -> listOf(Color(0xFFEF5350), Color(0xFFEF9A9A))
    }
}


@Preview(showBackground = true)
@Composable
private fun GoalCardPreview() {
    RunCounterTheme {
        GoalCard(
            goal = GoalUI(
                id = "12",
                name = "Marathon Training",
                targetDistanceMeters = "42 km",
                currentDistanceMeters = "21 km",
                progressPercentage = 0.5f,
                endDate = "Mar 15,2026",
                isExpired = false
            ),
            onDelete = {}
        )
    }
}
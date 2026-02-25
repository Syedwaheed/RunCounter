package com.edu.goal.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edu.core.presentation.designsystem.RunCounterCyan
import com.edu.core.presentation.designsystem.RunCounterCyan30
import com.edu.core.presentation.designsystem.RunCounterTheme
import com.edu.core.presentation.designsystem.components.GradientBackground
import com.edu.goal.presentation.components.AddGoalSheetContent
import com.edu.goal.presentation.components.AnimatedGoalCard
import com.edu.goal.presentation.components.EmptyGoalsState
import com.edu.goal.presentation.model.GoalUI
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreenRoot(
    showAddGoalSheet: Boolean,
    onDismissAddGoalSheet: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GoalViewModel = koinViewModel()
) {
    GoalScreen(
        modifier = modifier,
        state = viewModel.state,
        showAddGoalSheet = showAddGoalSheet,
        onDismissAddGoalSheet = onDismissAddGoalSheet,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(
    state: GoalState,
    showAddGoalSheet: Boolean,
    onDismissAddGoalSheet: () -> Unit,
    onAction: (GoalAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Sync sheet visibility with boolean state
    LaunchedEffect(showAddGoalSheet) {
        if (showAddGoalSheet) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    // Dismiss sheet when goal is saved
    LaunchedEffect(state.goalSaved) {
        if (state.goalSaved) {
            sheetState.hide()
            onDismissAddGoalSheet()
            onAction(GoalAction.OnGoalSavedHandled)
        }
    }

    if (showAddGoalSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    onDismissAddGoalSheet()
                }
            },
            sheetState = sheetState,
            modifier = Modifier.fillMaxWidth()
        ) {
            AddGoalSheetContent(
                state = state,
                onAction = onAction,
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
                        onDismissAddGoalSheet()
                    }
                }
            )
        }
    }

    GradientBackground {
        Box(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = RunCounterCyan,
                        trackColor = RunCounterCyan30
                    )
                }
                state.goals.isEmpty() -> {
                    EmptyGoalsState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    GoalList(
                        goals = state.goals,
                        onDeleteGoal = { id ->
                            onAction(GoalAction.OnDeleteGoalClick(id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalList(
    goals: List<GoalUI>,
    onDeleteGoal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(
            items = goals,
            key = { _, goal -> goal.id }
        ) { index, goal ->
            AnimatedGoalCard(
                goal = goal,
                index = index,
                onDelete = onDeleteGoal,
                showSwipeHint = true
            )
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GoalScreenPreview() {
    RunCounterTheme {
        GoalScreen(
            state = GoalState(
                goals = persistentListOf(
                    GoalUI(
                        id = "12",
                        name = "Marathon Training",
                        targetDistanceMeters = "42 km",
                        currentDistanceMeters = "21 km",
                        progressPercentage = 0.5f,
                        endDate = "Mar 15,2026",
                        isExpired = false
                    ),
                    GoalUI(
                        id = "123",
                        name = "Weekly Goal",
                        targetDistanceMeters = "10 km",
                        currentDistanceMeters = "8.5 km",
                        progressPercentage = 0.85f,
                        endDate = "Feb 28,2026",
                        isExpired = false
                    ),
                    GoalUI(
                        id = "1234",
                        name = "Completed Goal",
                        targetDistanceMeters = "5 km",
                        currentDistanceMeters = "5 km",
                        progressPercentage = 1f,
                        endDate = "Feb 20,2026",
                        isExpired = false
                    )
                ),
                isLoading = false
            ),
            onAction = {},
            showAddGoalSheet = false,
            onDismissAddGoalSheet = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    RunCounterTheme {
        GoalScreen(
            state = GoalState(
                goals = persistentListOf(),
                isLoading = false
            ),
            onAction = {},
            showAddGoalSheet = false,
            onDismissAddGoalSheet = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddGoalSheetPreview() {
    RunCounterTheme {
        AddGoalSheetContent(
            state = GoalState(),
            onAction = {},
            onDismiss = {}
        )
    }
}
package com.edu.run.presentation.run_overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edu.core.presentation.designsystem.RunCounterTheme
import com.edu.core.presentation.ui.ObserveAsEvent
import com.edu.run.presentation.run_overview.components.RunListItem
import com.edu.run.presentation.run_overview.components.StatsHeader
import com.edu.run.presentation.run_overview.model.RunUI
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverViewScreenRoot(
    modifier: Modifier = Modifier,
    contentPaddingValues: PaddingValues,
    onLogout: () -> Unit,
    onRunClick: (runId: String) -> Unit,
    listState: LazyListState,
    viewModel: RunOverviewViewModel = koinViewModel()
) {
    // Refresh runs when screen appears (handles ViewModel reuse across sessions)
    LaunchedEffect(Unit) {
        viewModel.onAction(RunOverViewAction.OnScreenResume)
    }

    ObserveAsEvent(flow = viewModel.events) { event ->
        when (event) {
            RunOverViewEvent.LogoutSuccess -> onLogout()
        }
    }
    RunOverViewContent(
        modifier = modifier,
        contentPaddingValues = contentPaddingValues,
        state = viewModel.state,
        onAction = { action ->
            viewModel.onAction(action)
        },
        onRunClick = onRunClick,
        listState = listState
    )
}

@Composable
fun RunOverViewContent(
    modifier: Modifier = Modifier,
    contentPaddingValues: PaddingValues = PaddingValues(),
    state: RunOverViewState,
    onAction: (RunOverViewAction) -> Unit,
    onRunClick: (runId: String) -> Unit,
    listState: LazyListState
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            state = listState,
            contentPadding = contentPaddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Header with entry animation
            item {
                var isVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { isVisible = true }

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -30 })
                ) {
                    StatsHeader(
                        totalRuns = state.totalRuns,
                        totalDistance = state.totalDistance,
                        totalDuration = state.totalDuration,
                        thisWeekRuns = state.thisWeekRuns,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                }
            }

            // Run cards with staggered animation
            itemsIndexed(
                items = state.runs,
                key = { _, run -> run.id }
            ) { index, run ->
                RunListItem(
                    modifier = Modifier.animateItem(),
                    runUI = run,
                    onDeleteClick = {
                        onAction(RunOverViewAction.DeleteRun(run))
                    },
                    onClick = {
                        onRunClick(run.id)
                    },
                    animationDelay = 50 + (index * 60)
                )
            }

            // Bottom padding for FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080707)
@Composable
private fun RunOverViewContentPreview() {
    RunCounterTheme {
        RunOverViewContent(
            state = RunOverViewState(
                runs = listOf(
                    RunUI(
                        id = "1",
                        duration = "00:45:30",
                        dateTime = "Mar 5, 2026 - 6:30 AM",
                        dateTimeEpoch = 0L,
                        distance = "8.5 km",
                        avgSpeed = "11.2 km/h",
                        maxSpeed = "14.8 km/h",
                        pace = "5:21 /km",
                        totalElevation = "145 m",
                        mapPictureUrl = null,
                        goalName = "Marathon Prep"
                    ),
                    RunUI(
                        id = "2",
                        duration = "00:32:15",
                        dateTime = "Mar 4, 2026 - 7:15 AM",
                        dateTimeEpoch = 0L,
                        distance = "5.8 km",
                        avgSpeed = "10.8 km/h",
                        maxSpeed = "13.5 km/h",
                        pace = "5:33 /km",
                        totalElevation = "62 m",
                        mapPictureUrl = null
                    ),
                    RunUI(
                        id = "3",
                        duration = "01:12:45",
                        dateTime = "Mar 2, 2026 - 5:45 AM",
                        dateTimeEpoch = 0L,
                        distance = "12.3 km",
                        avgSpeed = "10.2 km/h",
                        maxSpeed = "12.8 km/h",
                        pace = "5:52 /km",
                        totalElevation = "210 m",
                        mapPictureUrl = null,
                        goalName = "Long Run"
                    )
                ),
                totalRuns = 3,
                totalDistance = "26.6 km",
                totalDuration = "2h 30m",
                thisWeekRuns = 2
            ),
            onAction = {},
            onRunClick = {},
            listState = rememberLazyListState()
        )
    }
}

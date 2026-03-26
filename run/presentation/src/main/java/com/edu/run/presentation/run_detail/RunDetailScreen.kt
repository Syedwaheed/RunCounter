package com.edu.run.presentation.run_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.edu.core.presentation.designsystem.CalendarIcon
import com.edu.core.presentation.designsystem.DeleteIcon
import com.edu.core.presentation.designsystem.GoalIcon
import com.edu.core.presentation.designsystem.RunCounterTheme
import com.edu.core.presentation.designsystem.RunOutlinedIcon
import com.edu.core.presentation.designsystem.SurfaceCard
import com.edu.core.presentation.designsystem.components.RunCounterScaffold
import com.edu.core.presentation.designsystem.components.RunCounterToolbar
import com.edu.core.presentation.designsystem.components.util.DropDownItem
import com.edu.core.presentation.ui.ObserveAsEvent
import com.edu.run.presentation.R
import com.edu.run.presentation.run_overview.model.RunUI

@Composable
fun RunDetailScreenRoot(
    onBack: () -> Unit,
    viewModel: RunDetailViewModel
) {
    // Observe one-time events
    ObserveAsEvent(flow = viewModel.events) { event ->
        when (event) {
            RunDetailEvent.NavigateBack -> onBack()
            RunDetailEvent.RunDeleted -> onBack()
        }
    }

    RunDetailScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RunDetailScreen(
    state: RunDetailState,
    onAction: (RunDetailAction) -> Unit
) {
    RunCounterScaffold(
        topAppBar = {
            RunCounterToolbar(
                showBackButton = true,  // This shows the back arrow
                title = stringResource(R.string.run_details),
                onBackClick = {
                    // This connects toolbar back to our action
                    onAction(RunDetailAction.OnBackClick)
                },
                menuItems = listOf(
                    DropDownItem(
                        icon = DeleteIcon,
                        title = stringResource(R.string.delete)
                    )
                ),
                onMenuItemClick = { index ->
                    if (index == 0) {
                        onAction(RunDetailAction.OnDeleteClick)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.run != null) {
            RunDetailContent(
                run = state.run,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun RunDetailContent(
    run: RunUI,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Map image
        SubcomposeAsyncImage(
            model = run.mapPictureUrl,
            contentDescription = stringResource(R.string.run_map),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .clip(RoundedCornerShape(16.dp)),
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SurfaceCard),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SurfaceCard),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = RunOutlinedIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        )

        // Duration card
        StatCard(
            title = stringResource(R.string.total_running_time),
            value = run.duration,
            icon = { Icon(RunOutlinedIcon, null, tint = MaterialTheme.colorScheme.primary) }
        )

        // Date
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = CalendarIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Text(
                text = run.dateTime,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Goal if present
        run.goalName?.let { goalName ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = GoalIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                Text(
                    text = goalName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Stats grid
        Text(
            text = stringResource(R.string.statistics),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = stringResource(R.string.distance),
                value = run.distance,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = stringResource(R.string.pace),
                value = run.pace,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = stringResource(R.string.avg_speed),
                value = run.avgSpeed,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = stringResource(R.string.max_speed),
                value = run.maxSpeed,
                modifier = Modifier.weight(1f)
            )
        }

        StatCard(
            title = stringResource(R.string.total_elevation),
            value = run.totalElevation
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .padding(16.dp)
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080707)
@Composable
private fun RunDetailScreenPreview() {
    RunCounterTheme {
        RunDetailScreen(
            state = RunDetailState(
                run = RunUI(
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
                isLoading = false
            ),
            onAction = {}
        )
    }
}

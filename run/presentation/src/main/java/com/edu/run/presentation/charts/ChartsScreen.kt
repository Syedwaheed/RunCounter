package com.edu.run.presentation.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.core.presentation.designsystem.R
import com.edu.core.presentation.designsystem.RunCounterTheme
import com.edu.run.presentation.charts.mappers.toCartesianValues
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChartsScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: ChartsScreenViewModel = koinViewModel()
) {


    ChartsScreenScreen(
        modifier = modifier,
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ChartsScreenScreen(
    modifier: Modifier = Modifier,
    state: ChartsScreenState,
    onAction: (ChartsScreenAction) -> Unit
) {
    val distanceModelProducer = remember { CartesianChartModelProducer() }
    val speedModelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(state.distancePerRun) {
        if (state.distancePerRun.isNotEmpty()) {
            distanceModelProducer.runTransaction {
                lineSeries {
                    series(state.distancePerRun)
                }
            }
        }
    }
    LaunchedEffect(state.pacePerRun) {
        if (state.pacePerRun.isNotEmpty()) {
            speedModelProducer.runTransaction {
                columnSeries {
                    series(state.pacePerRun)
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Goal Filter Chips
        if (state.goalProgressData.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = state.selectedGoalId == null,
                        onClick = { onAction(ChartsScreenAction.ClearGoalFilter) },
                        label = { Text("All Runs") }
                    )
                }
                items(state.goalProgressData) { goal ->
                    FilterChip(
                        selected = state.selectedGoalId == goal.goalId,
                        onClick = { onAction(ChartsScreenAction.SelectGoal(goal.goalId)) },
                        label = { Text(goal.goalName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (goal.isCompleted) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.secondaryContainer
                            }
                        )
                    )
                }
            }
        }

        // Goal Progress Section
        if (state.goalProgressData.isNotEmpty()) {
            ChartCard(
                title = stringResource(R.string.goal_progress),
                subtitle = stringResource(R.string.distance_towards_goals)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.goalProgressData.forEach { goal ->
                        GoalProgressItem(goal = goal)
                    }
                }
            }
        }

        val axisLabelColor = MaterialTheme.colorScheme.onSurface
        val axisLabel = rememberAxisLabelComponent(
            style = TextStyle(
                color = axisLabelColor,
                fontSize = 10.sp
            )
        )
        if (state.distancePerRun.isNotEmpty()) {
            ChartCard(
                title = stringResource(R.string.distance_per_run),
                subtitle = if (state.selectedGoalId != null) {
                    stringResource(R.string.filtered_by_goal)
                } else {
                    stringResource(id = R.string.km_per_run)
                }
            ) {
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberLineCartesianLayer(),
                        startAxis = VerticalAxis.rememberStart(
                            label = axisLabel
                        ),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            label = axisLabel,
                            valueFormatter = state.toCartesianValues()
                        )
                    ),
                    modelProducer = distanceModelProducer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
        if (state.pacePerRun.isNotEmpty()) {
            ChartCard(
                title = stringResource(R.string.speed_per_run),
                subtitle = stringResource(id = R.string.km_per_hour)
            ) {
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberColumnCartesianLayer(),
                        startAxis = VerticalAxis.rememberStart(
                            label = axisLabel
                        ),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            label = axisLabel,
                            valueFormatter = state.toCartesianValues()
                        )
                    ),
                    modelProducer = speedModelProducer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
        // Show message when no runs for selected goal filter
        if (state.distancePerRun.isEmpty() && state.selectedGoalId != null && !state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.no_runs_for_goal),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        // Show message when no data at all
        if (state.distancePerRun.isEmpty() && state.goalProgressData.isEmpty() && !state.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.no_data),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun GoalProgressItem(
    goal: GoalProgressData,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = goal.goalName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (goal.isCompleted) {
                    stringResource(R.string.completed)
                } else if (goal.isExpired) {
                    stringResource(R.string.expired)
                } else {
                    "${String.format("%.1f", goal.currentDistanceKm)} / ${String.format("%.1f", goal.targetDistanceKm)} km"
                },
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    goal.isCompleted -> MaterialTheme.colorScheme.primary
                    goal.isExpired -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { goal.progressPercentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = when {
                goal.isCompleted -> MaterialTheme.colorScheme.primary
                goal.isExpired -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.secondary
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = "${(goal.progressPercentage * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun ChartCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun Preview() {
    RunCounterTheme {
        ChartsScreenScreen(
            state = ChartsScreenState(),
            onAction = {},
        )

    }
}
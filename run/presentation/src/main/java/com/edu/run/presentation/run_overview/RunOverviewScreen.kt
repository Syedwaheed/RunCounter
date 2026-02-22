package com.edu.run.presentation.run_overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edu.core.presentation.designsystem.RunCounterTheme
import com.edu.core.presentation.ui.ObserveAsEvent
import com.edu.run.presentation.run_overview.components.RunListItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverViewScreenRoot(
    modifier: Modifier = Modifier,
    contentPaddingValues: PaddingValues,
    onLogout: () -> Unit,
    listState: LazyListState,
    viewModel: RunOverviewViewModel = koinViewModel()
) {
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
            when (action) {
                else -> Unit
            }
        },
        listState = listState
    )
}

/*@Composable
fun RunOverviewScreenRoot(
    modifier: Modifier = Modifier,
    onStartRunClick: () -> Unit,
    onLogout: () -> Unit,
    onAnalyticsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: RunOverviewViewModel = koinViewModel()
) {
    ObserveAsEvent(flow = viewModel.events) { event ->
        when (event) {
            RunOverViewEvent.LogoutSuccess -> onLogout()
        }
    }

    RunOverviewScreen(
        modifier = modifier,
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                RunOverViewAction.OnStartClick -> onStartRunClick()
                RunOverViewAction.OnAnalyticClick -> onAnalyticsClick()
                RunOverViewAction.OnSettingClick -> onSettingsClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunOverviewScreen(
    modifier: Modifier = Modifier,
    onAction: (RunOverViewAction) -> Unit,
    state: RunOverViewState,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )
    RunCounterScaffold(
        topAppBar = {
            RunCounterToolbar(
                showBackButton = false,
                title = stringResource(id = R.string.run_counter),
                scrollBehaviour = scrollBehavior,
                menuItems = listOf(
                    DropDownItem(
                        icon = AnalyticsIcon,
                        title = stringResource(id = R.string.analytics)
                    ),
                    DropDownItem(
                        icon = PersonIcon,
                        title = stringResource(id = R.string.settings)
                    ),
                    DropDownItem(
                        icon = LogoutIcon,
                        title = stringResource(id = R.string.logout)
                    )
                ),
                onMenuItemClick = { index ->
                    when (index) {
                        0 -> onAction(RunOverViewAction.OnAnalyticClick)
                        1 -> onAction(RunOverViewAction.OnSettingClick)
                        2 -> onAction(RunOverViewAction.OnLogoutClick)
                    }
                },
                startContent = {
                    Icon(
                        imageVector = LogoIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            RunCounterFloatingActionButton(
                icon = RunIcon,
                onClick = {
                    onAction(RunOverViewAction.OnStartClick)
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(horizontal = 16.dp),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = state.runs,
                key = { it.id }
            ) {
                RunListItem(
                    runUI = it,
                    onDeleteClick = {
                        onAction(RunOverViewAction.DeleteRun(it))
                    },
                    modifier = Modifier
                        .animateItem()
                )
            }
        }
    }
}*/

@Composable
fun RunOverViewContent(
    modifier: Modifier = Modifier,
    contentPaddingValues: PaddingValues = PaddingValues(),
    state: RunOverViewState,
    onAction: (RunOverViewAction) -> Unit,
    listState: LazyListState
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            state = listState,
            contentPadding = contentPaddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = state.runs,
                key = { it.id }
            ) { run ->
                RunListItem(
                    modifier = modifier.animateItem(),
                    runUI = run,
                    onDeleteClick = {
                        onAction(RunOverViewAction.DeleteRun(run))
                    }
                )
            }
        }
    }
}
@Preview
@Composable
private fun RunOverviewScreenPreview() {
    RunCounterTheme {
        RunOverViewContent(
            state = RunOverViewState(),
            onAction = {},
            listState = rememberLazyListState()
        )
    }
}


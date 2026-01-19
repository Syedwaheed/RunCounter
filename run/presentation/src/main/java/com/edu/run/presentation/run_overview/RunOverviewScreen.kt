package com.edu.run.presentation.run_overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edu.core.presentation.designsystem.AnalyticsIcon
import com.edu.core.presentation.designsystem.LogoIcon
import com.edu.core.presentation.designsystem.LogoutIcon
import com.edu.core.presentation.designsystem.RunCounterTheme
import com.edu.core.presentation.designsystem.RunIcon
import com.edu.core.presentation.designsystem.components.RunCounterFloatingActionButton
import com.edu.core.presentation.designsystem.components.RunCounterScaffold
import com.edu.core.presentation.designsystem.components.RunCounterToolbar
import com.edu.core.presentation.designsystem.components.util.DropDownItem
import com.edu.core.presentation.ui.ObserveAsEvent
import com.edu.run.presentation.R
import com.edu.run.presentation.run_overview.components.RunListItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    onStartRunClick:() -> Unit,
    onLogout: () -> Unit,
    onAnalyticsClick: () -> Unit,
    viewModel: RunOverviewViewModel = koinViewModel()
) {
    ObserveAsEvent(flow = viewModel.events) { event ->
        when (event) {
            RunOverViewEvent.LogoutSuccess -> onLogout()
        }
    }

    RunOverviewScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action){
                is RunOverViewAction.OnStartClick -> onStartRunClick()
                is RunOverViewAction.OnAnalyticClick -> onAnalyticsClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunOverviewScreen(
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
                        icon = LogoutIcon,
                        title = stringResource(id = R.string.logout)
                    )
                ),
                onMenuItemClick = { index ->
                    when(index){
                        0 -> onAction(RunOverViewAction.OnAnalyticClick)
                        1 -> onAction(RunOverViewAction.OnLogoutClick)
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
    ) {padding ->
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
                key ={ it.id}
            ){
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
}

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    RunCounterTheme {
        RunOverviewScreen(
            state = RunOverViewState(),
            onAction = {},
        )
    }
}
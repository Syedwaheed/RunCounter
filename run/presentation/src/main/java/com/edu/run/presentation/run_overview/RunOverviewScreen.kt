package com.edu.run.presentation.run_overview

import android.R.attr.action
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edu.core.presentation.designsystem.AnalyticsIcon
import com.edu.core.presentation.designsystem.LogoIcon
import com.edu.core.presentation.designsystem.LogoutIcon
import com.edu.core.presentation.designsystem.RunCounterTheme
import com.edu.core.presentation.designsystem.RunIcon
import com.edu.core.presentation.designsystem.components.RunCounterFloatingActionButton
import com.edu.core.presentation.designsystem.components.RunCounterScaffold
import com.edu.core.presentation.designsystem.components.RunCounterToolbar
import com.edu.core.presentation.designsystem.components.util.DropDownItem
import com.edu.run.presentation.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    onStartRunClick:() -> Unit,
    viewModel: RunOverviewViewModel = koinViewModel()
) {

    RunOverviewScreen(
        onAction = { action ->
            when(action){
                is RunOverViewAction.OnStartClick -> onStartRunClick()
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
    ) { }
}

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    RunCounterTheme {
        RunOverviewScreen(
            onAction = {}
        )
    }
}
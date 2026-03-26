package com.edu.runcounter.dashboard

import android.app.ProgressDialog.show
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.edu.core.presentation.designsystem.GoalIcon
import com.edu.core.presentation.designsystem.LogoutIcon
import com.edu.core.presentation.designsystem.PersonIcon
import com.edu.core.presentation.designsystem.RunCounterTheme
import com.edu.core.presentation.designsystem.components.BottomNavItem
import com.edu.core.presentation.designsystem.components.GradientBackground
import com.edu.core.presentation.designsystem.components.RunCounterBottomNavigation
import com.edu.core.presentation.designsystem.components.RunCounterFloatingActionButton
import com.edu.core.presentation.designsystem.components.RunCounterToolbar
import com.edu.core.presentation.designsystem.components.util.DropDownItem
import com.edu.goal.presentation.GoalScreenRoot
import com.edu.run.presentation.R
import com.edu.run.presentation.analytics.AnalyticsScreenRoot
import com.edu.run.presentation.charts.ChartsScreenRoot
import com.edu.run.presentation.run_overview.RunOverViewScreenRoot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashBoardScreen(
    modifier: Modifier = Modifier,
    onStartRunClick: () -> Unit,
    onLogOutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onRunClick: (runId: String) -> Unit = {},
) {

    var selectedTab by rememberSaveable { mutableStateOf(BottomNavItem.HOME) }
    var showAddGoalSheet by rememberSaveable { mutableStateOf(false)}
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(state = topAppBarState)
    val listState = rememberLazyListState()
    val toolbarElevated by remember(listState){
        derivedStateOf {
            listState.firstVisibleItemIndex >0 || listState.firstVisibleItemScrollOffset >0
        }
    }
    GradientBackground(

    )
    {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
,            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0f),
            topBar = {
                
                RunCounterToolbar(
                    showBackButton = false,
                    title = stringResource(R.string.run_counter),
                    menuItems = listOf(
                        DropDownItem(
                            icon = LogoutIcon,
                            title = stringResource(id = R.string.logout)
                        ),
                        DropDownItem(
                            icon = PersonIcon,
                            title = stringResource(id = R.string.settings)
                        )
                    ),
                    onMenuItemClick = { index ->
                        when (index) {
                            0 -> onLogOutClick()
                            1 -> onSettingsClick()
                        }

                    },
                    scrollBehaviour = scrollBehavior,
                )
            },
            bottomBar = {
                RunCounterBottomNavigation(
                    selectedItem = selectedTab,
                    onNavigationItemSelected = { navItemSelected ->
                        selectedTab = navItemSelected
                    },
                    onStartClick = onStartRunClick
                )
            },
            floatingActionButton = {
                if(selectedTab == BottomNavItem.Goals){
                    RunCounterFloatingActionButton(
                        icon = GoalIcon,
                        onClick = {showAddGoalSheet = true}
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End
            ) { paddingValues ->
            when (selectedTab) {

                BottomNavItem.HOME -> RunOverViewScreenRoot(
                    modifier = Modifier,
                    onLogout = onLogOutClick,
                    listState = listState,
                    contentPaddingValues = paddingValues,
                    onRunClick = onRunClick
                )

                BottomNavItem.ANALYTICS -> AnalyticsScreenRoot(
                    modifier = Modifier.padding(paddingValues)
                )

                BottomNavItem.Goals -> GoalScreenRoot(
                    modifier = Modifier
                        .padding(paddingValues),
                    showAddGoalSheet = showAddGoalSheet,
                    onDismissAddGoalSheet = { showAddGoalSheet = false }
                )

                BottomNavItem.RUN -> Unit
                BottomNavItem.CHARTS -> ChartsScreenRoot(
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
private fun MainDashBoardScreenPreview() {
    RunCounterTheme {
        MainDashBoardScreen(
            onStartRunClick = {},
            onLogOutClick = {},
            onSettingsClick = {},
            onRunClick = {}
        )
    }
}
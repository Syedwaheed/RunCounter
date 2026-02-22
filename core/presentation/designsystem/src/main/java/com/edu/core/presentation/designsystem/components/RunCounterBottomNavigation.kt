package com.edu.core.presentation.designsystem.components

import android.R.attr.y
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edu.core.presentation.designsystem.AnalyticsIcon
import com.edu.core.presentation.designsystem.ChartIcon
import com.edu.core.presentation.designsystem.GoalIcon
import com.edu.core.presentation.designsystem.HomeIcon
import com.edu.core.presentation.designsystem.PersonIcon
import com.edu.core.presentation.designsystem.RunCounterTheme
import com.edu.core.presentation.designsystem.RunIcon

enum class BottomNavItem(
    val label: String,
    val isCentered: Boolean = false
){

    HOME("Home"),
    CHARTS("charts"),
    RUN("Run", isCentered = true),
    ANALYTICS("Analytics"),
    Goals("Goals");

    val icon: ImageVector
        @Composable
        get() = when(this){
            HOME -> HomeIcon
            CHARTS -> ChartIcon
            RUN -> RunIcon
            ANALYTICS -> AnalyticsIcon
            Goals -> GoalIcon
        }
}

@Composable
fun RunCounterBottomNavigation(
    modifier: Modifier = Modifier,
    selectedItem: BottomNavItem,
    onNavigationItemSelected: (BottomNavItem) -> Unit,
    onStartClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            BottomNavItem.entries.forEach { item ->
                if (item.isCentered) {
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = {},
                        label = {},
                        enabled = false,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                } else {
                    NavigationBarItem(
                        selected = selectedItem == item,
                        onClick = { onNavigationItemSelected(item) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(
                                text = item.label
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .layout{measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width,placeable.height){
                        placeable.place(0,-32.dp.roundToPx())
                    }
                }
                .size(64.dp)
                .shadow(8.dp,CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = onStartClick),
            contentAlignment = Alignment.Center
        ){
            Icon(
                imageVector = RunIcon,
                contentDescription = "Start Run",
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RunCounterBottomNavigationPreview() {
    RunCounterTheme {
        RunCounterBottomNavigation(
            selectedItem = BottomNavItem.HOME,
            onNavigationItemSelected = {},
            onStartClick = {}
        )
    }
}
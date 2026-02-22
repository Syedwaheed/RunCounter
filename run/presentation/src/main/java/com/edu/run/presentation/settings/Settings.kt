package com.edu.run.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edu.core.presentation.designsystem.RunCounterTheme
import com.edu.core.presentation.designsystem.components.RunCounterScaffold
import com.edu.core.presentation.designsystem.components.RunCounterToolbar
import com.edu.run.presentation.R
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoot(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {

    SettingsScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                SettingsAction.OnBackClick -> onBackClick()
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
) {
    RunCounterScaffold(
        topAppBar = {
            RunCounterToolbar(
                showBackButton = true,
                title = stringResource(R.string.settings),
                onBackClick = { onAction(SettingsAction.OnBackClick) }
            )
        }
    ) { paddingValues ->
        SettingContent(
            modifier = Modifier.padding(paddingValues),
            state = state
        )
    }

}

@Composable
private fun SettingContent(
    modifier: Modifier = Modifier,
    state: SettingsState
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {
        SettingsCard(
            title = stringResource(id = R.string.unit),
            value = state.distanceUnit
        )
        Spacer(modifier = Modifier.height(16.dp))
        SettingsCard(
            title = stringResource(id = R.string.lang),
            value = state.language
        )
    }
}

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RunCounterTheme {
        SettingContent(
            state = SettingsState()
        )
    }
}
package com.edu.goal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.edu.core.presentation.designsystem.CalendarIcon
import com.edu.core.presentation.designsystem.components.RunCounterActionButton
import com.edu.core.presentation.designsystem.components.RunCounterDateTimeField
import com.edu.core.presentation.designsystem.components.RunCounterTextField
import com.edu.goal.presentation.GoalAction
import com.edu.goal.presentation.GoalState
import com.edu.goal.presentation.R
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalSheetContent(
    state: GoalState,
    onAction: (GoalAction) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RectangleShape
            )
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.create_new_goal),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        RunCounterTextField(
            state = state.nameState,
            startIcon = null,
            endIcon = null,
            hint = stringResource(id = R.string.goal_name),
            title = stringResource(id = R.string.goal_name),
            modifier = Modifier,
            keyboardType = KeyboardType.Text,
            error = state.nameError
        )

        RunCounterTextField(
            state = state.targetState,
            startIcon = null,
            endIcon = null,
            hint = stringResource(id = R.string.goal_target_hint),
            title = stringResource(id = R.string.goal_target),
            modifier = Modifier,
            keyboardType = KeyboardType.Number,
            error = state.targetError
        )

        RunCounterDateTimeField(
            value = state.formattedGoalEndDate,
            modifier = Modifier,
            onClick = { onAction(GoalAction.ShowDatePickerDialog) },
            startIcon = CalendarIcon,
            hint = stringResource(id = R.string.select_date),
            title = stringResource(R.string.end_date),
            error = state.dateError
        )

        Spacer(modifier = Modifier.height(8.dp))

        RunCounterActionButton(
            text = stringResource(R.string.save),
            isLoading = state.isSaving,
            modifier = Modifier.fillMaxWidth(),
        ) {
            onAction(GoalAction.OnSaveGoalClick)
        }

        if (state.showDatePicker) {
            GoalDatePicker(
                onDateSelected = { date -> onAction(GoalAction.OnDateSelected(date)) },
                onDismiss = { onAction(GoalAction.HideDatePickerDialog) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDatePicker(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(date)
                    }
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
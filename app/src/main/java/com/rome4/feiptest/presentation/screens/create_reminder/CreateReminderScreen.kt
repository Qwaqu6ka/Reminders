package com.rome4.feiptest.presentation.screens.create_reminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rome4.feiptest.R
import com.rome4.feiptest.presentation.common_components.ScreenWithToolbar
import com.rome4.feiptest.presentation.common_components.observeWithLifecycle
import com.rome4.feiptest.presentation.models.UIClient
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CreateReminderScreen(
    navigateBack: () -> Unit,
    navigateToChooseClientScreen: () -> Unit,
    navigateToListScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateReminderViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    viewModel.sideEffectFlow.observeWithLifecycle {
        when (it) {
            CreateReminderViewModel.SideEffect.NavigateToList -> navigateToListScreen()
        }
    }

    val showDatePickerDialog = remember { mutableStateOf(false) }
    DatePickerDialog(showDialog = showDatePickerDialog, onDateChange = viewModel::onDateChange)

    val showTimePickerDialog = remember { mutableStateOf(false) }
    TimePickerDialog(showDialog = showTimePickerDialog, onTimeChange = viewModel::onTimeChange)

    ScreenWithToolbar(
        modifier = modifier,
        navigateBack = navigateBack,
        title = stringResource(R.string.creation_of_reminder)
    ) { scaffoldPadding ->

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(scaffoldPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = state.title,
                label = {
                    Text(text = stringResource(R.string.title))
                },
                onValueChange = viewModel::onTitleChange,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val dateTimeFormat = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
                OutlinedTextField(
                    value = state.date?.format(dateTimeFormat) ?: "",
                    label = {
                        Text(text = stringResource(R.string.date))
                    },
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    enabled = false
                )
                FloatingActionButton(onClick = { showDatePickerDialog.value = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val dateTimeFormat = remember { DateTimeFormatter.ofPattern("HH:mm") }
                OutlinedTextField(
                    value = state.time?.format(dateTimeFormat) ?: "",
                    label = {
                        Text(text = stringResource(R.string.time))
                    },
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    enabled = false
                )
                FloatingActionButton(onClick = { showTimePickerDialog.value = true }) {
                    Icon(painter = painterResource(R.drawable.ic_clock), contentDescription = null)
                }
            }
            ClientBlock(
                chooseClient = navigateToChooseClientScreen,
                client = state.client,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = viewModel::saveReminder,
                enabled = state.createButtonEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.create),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (state.deleteButtonAvailable) {
                Button(
                    onClick = viewModel::deleteReminder,
                    enabled = state.createButtonEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    showDialog: MutableState<Boolean>,
    onDateChange: (LocalDate) -> Unit,
) {
    val datePickerState = rememberDatePickerState()
    if (showDialog.value) {
        DatePickerDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDateMillis = datePickerState.selectedDateMillis
                        if (selectedDateMillis != null) {
                            val instant = Instant.ofEpochMilli(selectedDateMillis)
                                .atZone(ZoneId.systemDefault())
                            val date = instant.toLocalDate()
                            onDateChange(date)
                            showDialog.value = false
                        }
                    }
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    showDialog: MutableState<Boolean>,
    onTimeChange: (LocalTime) -> Unit,
) {
    val timePickerState = rememberTimePickerState()
    if (showDialog.value) {
        DatePickerDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val time = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        onTimeChange(time)
                        showDialog.value = false
                    }
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
private fun ClientBlock(
    chooseClient: () -> Unit,
    client: UIClient?,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier, shape = RoundedCornerShape(10.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(15.dp)
        ) {
            if (client == null) {
                Text(
                    text = stringResource(R.string.client_not_chosen),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = client.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = client.email,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(onClick = chooseClient) {
                Text(text = stringResource(R.string.choose_client))
            }
        }
    }
}

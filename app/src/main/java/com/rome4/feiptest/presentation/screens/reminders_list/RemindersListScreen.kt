package com.rome4.feiptest.presentation.screens.reminders_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rome4.feiptest.R
import com.rome4.feiptest.presentation.async_result.SimpleResultRenderer
import com.rome4.feiptest.presentation.common_components.ScreenWithToolbar
import com.rome4.feiptest.presentation.models.UIClient
import com.rome4.feiptest.presentation.models.UIReminder
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

@Composable
fun RemindersListScreen(
    navigateToCreateReminderScreen: (id: Int?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RemindersListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    ScreenWithToolbar(
        modifier = modifier,
        title = stringResource(R.string.reminders),
        fab = {
            FloatingActionButton(onClick = { navigateToCreateReminderScreen(null) }) {
                Icon(Icons.Filled.Add, null)
            }
        },
    ) { scaffoldPadding ->

        SimpleResultRenderer(
            result = state.reminders,
            onTryAgain = viewModel::reloadReminders,
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize()
        ) { reminders ->
            SimpleResultRenderer(
                result = state.clients,
                onTryAgain = viewModel::reloadClients,
                modifier = Modifier.fillMaxSize()
            ) { clients ->
                if (reminders.isNotEmpty()) {
                    LazyColumn {
                        items(items = reminders, key = { it.id }) { reminder ->
                            ReminderCard(
                                reminder = reminder,
                                client = clients.first { it.id == reminder.clientId },
                                onClick = { navigateToCreateReminderScreen(reminder.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.list_is_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(
    reminder: UIReminder,
    onClick: () -> Unit,
    client: UIClient,
    modifier: Modifier = Modifier
) {
    val datetime = remember(reminder) {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        reminder.datetime.format(dateTimeFormatter)
    }
    Surface(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            AsyncImage(
                model = client.photoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(3f)
                    .fillMaxSize()
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.weight(6f)
            ) {
                Text(text = reminder.title, style = MaterialTheme.typography.bodyLarge)
                Text(text = datetime, style = MaterialTheme.typography.bodySmall)
                Text(text = client.name, style = MaterialTheme.typography.bodyMedium)
                Text(text = client.email, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
package com.rome4.feiptest.presentation.screens.choose_client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChooseClientScreen(
    navigateBackWithClientId: (String) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChooseClientViewModel = koinViewModel(),
) {
    val clientsResult by viewModel.clients.collectAsState()

    ScreenWithToolbar(
        modifier = modifier,
        title = stringResource(R.string.client_choosing),
        navigateBack = navigateBack,
    ) { scaffoldPadding ->
        SimpleResultRenderer(
            result = clientsResult,
            onTryAgain = viewModel::reloadClients,
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) { clients ->
            if (clients.isNotEmpty()) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(items = clients, key = { it.id }) { client ->
                        ClientCard(
                            client = client,
                            onClick = { navigateBackWithClientId(client.id) },
                            modifier = Modifier.padding(horizontal = 20.dp)
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

@Composable
private fun ClientCard(client: UIClient, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(15.dp)
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
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(7f)
            ) {
                Text(text = client.name, style = MaterialTheme.typography.bodyMedium)
                Text(text = client.email, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
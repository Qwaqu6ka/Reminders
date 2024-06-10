package com.rome4.feiptest.presentation.screens.reminders_list

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rome4.feiptest.data.repositoies.RemindersRepository
import com.rome4.feiptest.presentation.async_result.AsyncResult
import com.rome4.feiptest.presentation.async_result.MutableFlowResult
import com.rome4.feiptest.presentation.models.UIClient
import com.rome4.feiptest.presentation.models.UIReminder
import com.rome4.feiptest.presentation.models.mapToUI
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RemindersListViewModel(
    private val remindersRepository: RemindersRepository,
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        reminders.value = AsyncResult.ErrorResult(throwable)
        clients.value = AsyncResult.ErrorResult(throwable)
    }

    private var loadRemindersJob: Job? = null
    private var loadClientsJob: Job? = null

    private val reminders: MutableFlowResult<ImmutableList<UIReminder>> =
        MutableStateFlow(AsyncResult.PendingResult())

    private val clients: MutableFlowResult<ImmutableList<UIClient>> =
        MutableStateFlow(AsyncResult.PendingResult())

    val state = combine(reminders, clients, ::mergeSources)
        .stateIn(viewModelScope, SharingStarted.Eagerly, RemindersListScreenState())

    private fun mergeSources(
        reminders: AsyncResult<ImmutableList<UIReminder>>,
        clients: AsyncResult<ImmutableList<UIClient>>
    ): RemindersListScreenState {
        return RemindersListScreenState(
            reminders = reminders,
            clients = clients
        )
    }

    init {
        loadReminders()
    }

    fun reloadReminders() = loadReminders()

    fun reloadClients() = loadClients()

    private fun loadReminders() {
        loadRemindersJob?.cancel()
        loadRemindersJob = viewModelScope.launch(exceptionHandler) {
            reminders.value = AsyncResult.SuccessResult(
                remindersRepository.getReminders()
                    .map { it.mapToUI() }
                    .toImmutableList()
            )
            loadClients()
        }
    }

    private fun loadClients() {
        loadClientsJob?.cancel()
        loadClientsJob = viewModelScope.launch(exceptionHandler) {
            clients.value = AsyncResult.SuccessResult(
                remindersRepository.getClients()
                    .map { it.mapToUI() }
                    .toImmutableList()
            )
        }
    }

    @Immutable
    data class RemindersListScreenState(
        val clients: AsyncResult<ImmutableList<UIClient>> = AsyncResult.PendingResult(),
        val reminders: AsyncResult<ImmutableList<UIReminder>> = AsyncResult.PendingResult(),
    )
}
package com.rome4.feiptest.presentation.screens.choose_client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rome4.feiptest.data.repositoies.RemindersRepository
import com.rome4.feiptest.presentation.async_result.AsyncResult
import com.rome4.feiptest.presentation.async_result.FlowResult
import com.rome4.feiptest.presentation.async_result.MutableFlowResult
import com.rome4.feiptest.presentation.models.UIClient
import com.rome4.feiptest.presentation.models.mapToUI
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChooseClientViewModel(
    private val remindersRepository: RemindersRepository,
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _clients.value = AsyncResult.ErrorResult(throwable)
    }

    private var getClientsJob: Job? = null

    private val _clients: MutableFlowResult<ImmutableList<UIClient>> =
        MutableStateFlow(AsyncResult.PendingResult())
    val clients: FlowResult<ImmutableList<UIClient>> = _clients

    init {
        getClients()
    }

    private fun getClients() {
        getClientsJob?.cancel()
        getClientsJob = viewModelScope.launch(exceptionHandler) {
            _clients.value = AsyncResult.SuccessResult(
                remindersRepository.getClients(forceUpdate = true)
                    .map { it.mapToUI() }
                    .toImmutableList()
            )
        }
    }

    fun reloadClients() = getClients()
}
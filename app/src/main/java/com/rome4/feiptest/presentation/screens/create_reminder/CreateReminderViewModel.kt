package com.rome4.feiptest.presentation.screens.create_reminder

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rome4.feiptest.data.models.Reminder
import com.rome4.feiptest.data.repositoies.RemindersRepository
import com.rome4.feiptest.presentation.models.UIClient
import com.rome4.feiptest.presentation.models.mapToUI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CreateReminderViewModel(
    private val remindersRepository: RemindersRepository,
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        // TODO()
    }

    private var createReminderJob: Job? = null

    private val _sideEffectFlow = MutableSharedFlow<SideEffect>()
    val sideEffectFlow: SharedFlow<SideEffect> = _sideEffectFlow

    private val clientId = MutableStateFlow<String?>(null)
    private val client = clientId.map { id ->
        id?.let {
            remindersRepository.getClientById(id = it).mapToUI()
        }
    }
    private val title = MutableStateFlow("")
    private val date = MutableStateFlow<LocalDate?>(null)
    private val time = MutableStateFlow<LocalTime?>(null)

    val state = combine(title, client, date, time, ::mergeSources)
        .stateIn(viewModelScope, SharingStarted.Eagerly, State())

    private fun mergeSources(
        title: String,
        client: UIClient?,
        date: LocalDate?,
        time: LocalTime?
    ): State {
        return State(
            title = title,
            client = client,
            date = date,
            time = time
        )
    }

    fun onTitleChange(value: String) {
        title.value = value
    }

    fun onDateChange(value: LocalDate) {
        date.value = value
    }

    fun onTimeChange(value: LocalTime) {
        time.value = value
    }

    fun updateClientId(value: String?) {
        clientId.value = value
    }

    fun createReminder() {
        createReminderJob?.cancel()
        createReminderJob = viewModelScope.launch(exceptionHandler) {
            val clientId = clientId.value
            val date = date.value
            val time = time.value
            val title = title.value
            if (clientId == null || title.isBlank() || date == null)
                return@launch

            val dateTime = if (time != null) {
                LocalDateTime.of(date, time)
            } else {
                LocalDateTime.of(date, LocalTime.of(9, 0))
            }
            remindersRepository.addReminder(
                Reminder(
                    clientId = clientId,
                    date = dateTime,
                    title = title
                )
            )
            _sideEffectFlow.emit(SideEffect.NavigateToList)
        }
    }

    @Immutable
    data class State(
        val title: String = "",
        val client: UIClient? = null,
        val date: LocalDate? = null,
        val time: LocalTime? = null
    ) {
        val createButtonEnabled: Boolean = title.isNotBlank() && client != null && date != null
    }

    @Immutable
    sealed interface SideEffect {
        data object NavigateToList : SideEffect
    }

    companion object {
        const val ARG_CLIENT = "client"
    }
}
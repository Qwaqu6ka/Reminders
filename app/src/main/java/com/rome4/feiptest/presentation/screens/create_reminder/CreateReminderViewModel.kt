package com.rome4.feiptest.presentation.screens.create_reminder

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rome4.feiptest.data.models.Reminder
import com.rome4.feiptest.data.repositoies.RemindersRepository
import com.rome4.feiptest.presentation.ARG_REMINDER_ID
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        // TODO()
    }

    private val reminderId: Int? = savedStateHandle.get<String?>(ARG_REMINDER_ID)?.toInt()

    private var saveReminderJob: Job? = null
    private var deleteReminderJob: Job? = null
    private var gerReminderJob: Job? = null

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
            time = time,
            deleteButtonAvailable = reminderId != null,
        )
    }

    init {
        getReminder()
    }

    private fun getReminder() {
        if (reminderId != null) {
            gerReminderJob?.cancel()
            gerReminderJob = viewModelScope.launch(exceptionHandler) {
                val reminder = remindersRepository.gerReminderById(reminderId)
                title.value = reminder.title
                date.value = reminder.datetime.toLocalDate()
                time.value = reminder.datetime.toLocalTime()
                clientId.value = reminder.clientId
            }
        }
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

    fun saveReminder() {
        saveReminderJob?.cancel()
        saveReminderJob = viewModelScope.launch(exceptionHandler) {
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
            remindersRepository.saveReminder(
                Reminder(
                    id = reminderId ?: 0,
                    clientId = clientId,
                    datetime = dateTime,
                    title = title
                )
            )
            _sideEffectFlow.emit(SideEffect.NavigateToList)
        }
    }

    fun deleteReminder() {
        if (reminderId == null) return
        deleteReminderJob?.cancel()
        deleteReminderJob = viewModelScope.launch(exceptionHandler) {
            remindersRepository.deleteReminder(reminderId)
            _sideEffectFlow.emit(SideEffect.NavigateToList)
        }
    }

    @Immutable
    data class State(
        val title: String = "",
        val client: UIClient? = null,
        val date: LocalDate? = null,
        val time: LocalTime? = null,
        val deleteButtonAvailable: Boolean = false,
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
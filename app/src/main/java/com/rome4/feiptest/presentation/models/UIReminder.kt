package com.rome4.feiptest.presentation.models

import androidx.compose.runtime.Immutable
import com.rome4.feiptest.data.models.Reminder
import java.time.LocalDateTime

@Immutable
data class UIReminder(
    val id: Int,
    val title: String,
    val datetime: LocalDateTime,
    val clientId: String,
)

fun Reminder.mapToUI() = UIReminder(
    id = id,
    title = title,
    datetime = datetime,
    clientId = clientId,
)

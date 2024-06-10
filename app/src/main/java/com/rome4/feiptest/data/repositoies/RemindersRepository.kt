package com.rome4.feiptest.data.repositoies

import com.rome4.feiptest.data.models.Client
import com.rome4.feiptest.data.models.Reminder

interface RemindersRepository {
    suspend fun getReminders(): List<Reminder>
    suspend fun addReminder(reminder: Reminder)
    suspend fun getClients(forceUpdate: Boolean = false): List<Client>
    suspend fun getClientById(id: String): Client
}
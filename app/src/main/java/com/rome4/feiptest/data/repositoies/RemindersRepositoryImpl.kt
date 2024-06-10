package com.rome4.feiptest.data.repositoies

import com.rome4.feiptest.data.models.Client
import com.rome4.feiptest.data.models.Reminder
import com.rome4.feiptest.data.sources.ClientsSource
import com.rome4.feiptest.data.sources.room.AppDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RemindersRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val clientsSource: ClientsSource,
    private val room: AppDatabase
) : RemindersRepository {

    private var clients = listOf<Client>()

    override suspend fun getReminders(): List<Reminder> = withContext(ioDispatcher) {
        room.remindersDao().getReminders()
    }

    override suspend fun addReminder(reminder: Reminder) = withContext(ioDispatcher) {
        room.remindersDao().addReminder(reminder)
    }

    override suspend fun getClients(forceUpdate: Boolean): List<Client> =
        withContext(ioDispatcher) {
            if (clients.isEmpty() || forceUpdate) {
                loadClients()
            }
            clients
        }

    override suspend fun getClientById(id: String): Client {
        if (clients.isEmpty()) {
            loadClients()
        }
        return clients.first { it.id == id }
    }

    private suspend fun loadClients() = withContext(ioDispatcher) {
        clients = clientsSource.getClients().mapToListOfClients()
    }
}
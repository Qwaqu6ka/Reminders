package com.rome4.feiptest.data.sources.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rome4.feiptest.data.models.Reminder

@Dao
interface RemindersDao {

    @Query("SELECT * FROM reminders")
    suspend fun getReminders(): List<Reminder>

    @Insert
    suspend fun addReminder(reminder: Reminder)
}
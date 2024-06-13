package com.rome4.feiptest.data.sources.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rome4.feiptest.data.models.Reminder

@Dao
interface RemindersDao {

    @Query("SELECT * FROM reminders")
    suspend fun getReminders(): List<Reminder>

    @Query("SELECT * FROM reminders WHERE id=:id")
    suspend fun getReminder(id: Int): Reminder

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addReminder(reminder: Reminder)

    @Query("DELETE FROM reminders WHERE id=:id")
    suspend fun deleteReminder(id: Int)
}
package com.rome4.feiptest.data.sources.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.rome4.feiptest.data.models.Reminder
import java.time.LocalDateTime
import java.time.ZoneOffset

@Database(entities = [Reminder::class], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun remindersDao(): RemindersDao
}

class LocalDateTimeConverter {
    @TypeConverter
    fun fromLong(value: Long): LocalDateTime =
        LocalDateTime.ofEpochSecond(value, 0, ZoneOffset.UTC)

    @TypeConverter
    fun dateToLong(dateTime: LocalDateTime): Long =
        dateTime.toEpochSecond(ZoneOffset.UTC)
}
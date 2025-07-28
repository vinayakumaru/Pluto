package com.example.pluto.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.pluto.data.model.Account
import com.example.pluto.data.model.Transaction
import java.util.Date

/**
 * The Room database for the application.
 * Defines the tables (entities) and the DAO to access them.
 */
@Database(entities = [Transaction::class, Account::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
}

/**
 * Type converters to allow Room to reference complex data types.
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
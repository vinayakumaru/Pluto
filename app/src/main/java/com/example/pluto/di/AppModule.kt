package com.example.pluto.di

import android.app.Application
import androidx.room.Room
import com.example.pluto.data.local.AppDatabase
import com.example.pluto.data.local.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Provides dependencies for the entire app lifecycle
object AppModule {

    /**
     * Provides a singleton instance of the AppDatabase.
     * @param app The application context.
     * @return The singleton AppDatabase instance.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "pluto_db" // Name of your database file
        ).build()
    }

    /**
     * Provides an instance of TransactionDao.
     * @param db The AppDatabase instance provided by Hilt.
     * @return An instance of TransactionDao.
     */
    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao {
        return db.transactionDao()
    }
}
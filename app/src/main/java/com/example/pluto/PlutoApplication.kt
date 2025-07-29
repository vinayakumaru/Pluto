package com.example.pluto

import android.app.Application
import androidx.room.Room
import com.example.pluto.data.local.AppDatabase
import com.example.pluto.data.repository.TransactionRepository

// Container for our dependencies
interface AppContainer {
    val transactionRepository: TransactionRepository
}

class DefaultAppContainer(private val application: Application) : AppContainer {
    // Lazily create the database and repository so they are only made when first needed
    private val db: AppDatabase by lazy {
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "pluto_db"
        ).build()
    }

    override val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(db.transactionDao())
    }
}

class PlutoApplication : Application() {
    // The container instance will be created once and live for the entire app's lifecycle
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
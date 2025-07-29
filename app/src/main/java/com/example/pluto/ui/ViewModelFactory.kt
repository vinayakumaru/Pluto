package com.example.pluto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.pluto.PlutoApplication
import com.example.pluto.ui.addedit.AddEditTransactionViewModel
import com.example.pluto.ui.transactions.TransactionScreenViewModel

/**
 * A generic ViewModel factory that provides the TransactionRepository to any ViewModel
 * that needs it. This also correctly handles the creation of ViewModels with SavedStateHandle.
 */
val ViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        // Get the application's dependency container
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as PlutoApplication
        val repository = application.container.transactionRepository

        // Create the requested ViewModel
        return when {
            modelClass.isAssignableFrom(TransactionScreenViewModel::class.java) -> {
                TransactionScreenViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddEditTransactionViewModel::class.java) -> {
                val savedStateHandle = extras.createSavedStateHandle()
                AddEditTransactionViewModel(repository, savedStateHandle) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
package com.example.pluto.ui.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pluto.data.model.Account
import com.example.pluto.data.model.Transaction
import com.example.pluto.data.model.TransactionType
import com.example.pluto.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

// Data class to hold the state of the form
data class AddEditScreenUiState(
    val id: Int = 0,
    val title: String = "",
    val amount: String = "",
    val category: String = "",
    val description: String? = null,
    val date: Date = Date(),
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val accounts: List<Account> = emptyList(),
    val selectedAccountId: Int? = null,
    val isEditing: Boolean = false,
    val hasBeenSaved: Boolean = false
)

class AddEditTransactionViewModel(
    private val repository: TransactionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditScreenUiState())
    val uiState = _uiState.asStateFlow()
    private val transactionId: Int? = savedStateHandle.get<String>("transactionId")?.toIntOrNull()

    init {
        viewModelScope.launch {
            // Load all available accounts for the dropdown
            val accounts = repository.getAllAccounts().first()
            _uiState.update { it.copy(accounts = accounts) }

            if (transactionId != null) {
                // Editing mode: Load the existing transaction
                val transaction = repository.getTransactionById(transactionId).first()
                _uiState.update {
                    it.copy(
                        id = transaction.id,
                        title = transaction.title,
                        amount = transaction.amount.toString(),
                        category = transaction.category,
                        description = transaction.description,
                        date = transaction.date,
                        transactionType = transaction.type,
                        selectedAccountId = transaction.accountId,
                        isEditing = true
                    )
                }
            } else {
                // Adding mode: Set the default account if available
                _uiState.update {
                    it.copy(selectedAccountId = accounts.firstOrNull()?.accountId)
                }
            }
        }
    }

    // --- Event Handlers ---

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onAmountChange(newAmount: String) {
        // Allow only valid numbers
        if (newAmount.matches(Regex("^\\d*\\.?\\d*\$"))) {
            _uiState.update { it.copy(amount = newAmount) }
        }
    }

    fun onCategoryChange(newCategory: String) {
        _uiState.update { it.copy(category = newCategory) }
    }

    fun onDescriptionChange(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun onDateChange(newDate: Date) {
        _uiState.update { it.copy(date = newDate) }
    }

    fun onTransactionTypeChange(newType: TransactionType) {
        _uiState.update { it.copy(transactionType = newType) }
    }

    fun onAccountSelected(accountId: Int) {
        _uiState.update { it.copy(selectedAccountId = accountId) }
    }

    fun saveTransaction() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.title.isBlank() || currentState.amount.isBlank() || currentState.selectedAccountId == null) {
                // TODO: Add user feedback for validation error
                return@launch
            }

            val transactionToSave = Transaction(
                id = currentState.id,
                title = currentState.title,
                amount = currentState.amount.toDoubleOrNull() ?: 0.0,
                category = currentState.category,
                description = currentState.description,
                date = currentState.date,
                type = currentState.transactionType,
                accountId = currentState.selectedAccountId
            )

            if (currentState.isEditing) {
                repository.updateTransaction(transactionToSave)
            } else {
                repository.insertTransaction(transactionToSave)
            }
            _uiState.update { it.copy(hasBeenSaved = true) }
        }
    }
}
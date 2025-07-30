package com.example.pluto.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pluto.data.model.Account
import com.example.pluto.data.model.Transaction
import com.example.pluto.data.model.TransactionType
import com.example.pluto.data.model.TransactionWithAccount
import com.example.pluto.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

data class TransactionScreenUiState(
    val currentDate: Date = Date(),
    val transactions: List<TransactionWithAccount> = emptyList(),
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val accounts: List<Account> = emptyList(),
) {
    val monthlyTotal: Double get() = monthlyIncome - monthlyExpense
}

class TransactionScreenViewModel constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getTransactionsForMonth()
    }

    private fun getTransactionsForMonth() {
        val calendar = Calendar.getInstance().apply { time = _uiState.value.currentDate }

        // Set to the first day of the month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = calendar.time

        // Set to the last day of the month
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val endDate = calendar.time

        val transactionsFlow = repository.getTransactionsForAccountByDateRange(startDate, endDate)
        val incomeFlow = repository.getTotalForTypeInDateRange(TransactionType.INCOME, startDate, endDate)
        val expenseFlow = repository.getTotalForTypeInDateRange(TransactionType.EXPENSE, startDate, endDate)

        viewModelScope.launch {
            // Combine the three flows. The block will be executed whenever any of the flows emit a new value.
            combine(transactionsFlow, incomeFlow, expenseFlow) { transactions, income, expense ->
                // Create a new state object with the latest data
                TransactionScreenUiState(
                    currentDate = _uiState.value.currentDate,
                    transactions = transactions,
                    monthlyIncome = income,
                    monthlyExpense = expense,
                    accounts = _uiState.value.accounts,
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }


    fun onPreviousMonthClick() {
        val calendar = Calendar.getInstance().apply { time = _uiState.value.currentDate }
        calendar.add(Calendar.MONTH, -1)
        _uiState.update { it.copy(currentDate = calendar.time) }
        getTransactionsForMonth()
    }

    fun onNextMonthClick() {
        val calendar = Calendar.getInstance().apply { time = _uiState.value.currentDate }
        calendar.add(Calendar.MONTH, 1)
        _uiState.update { it.copy(currentDate = calendar.time) }
        getTransactionsForMonth()
    }

    fun onDeleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
}
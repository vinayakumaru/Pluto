package com.example.pluto.data.repository

import com.example.pluto.data.local.TransactionDao
import com.example.pluto.data.model.Account
import com.example.pluto.data.model.Transaction
import com.example.pluto.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

/**
 * Repository module for handling data operations.
 * This class abstracts the data source (Room DAO) from the ViewModels.
 *
 * @param transactionDao The DAO for transactions and accounts, injected by Hilt.
 */
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {

    // --- Account Operations ---

    fun getAllAccounts(): Flow<List<Account>> {
        return transactionDao.getAllAccounts()
    }

    suspend fun insertAccount(account: Account) {
        transactionDao.insertAccount(account)
    }

    suspend fun updateAccount(account: Account) {
        transactionDao.updateAccount(account)
    }

    suspend fun deleteAccount(account: Account) {
        transactionDao.deleteAccount(account)
    }

    // --- Transaction Operations ---

    fun getTransactionsForAccountByDateRange(accountId: Int, startDate: Date, endDate: Date): Flow<List<Transaction>> {
        return transactionDao.getTransactionsForAccountByDateRange(accountId, startDate, endDate)
    }

    fun getTotalForTypeInDateRange(accountId: Int, type: TransactionType, startDate: Date, endDate: Date): Flow<Double> {
        return transactionDao.getTotalForTypeInDateRange(accountId, type, startDate, endDate)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }
}
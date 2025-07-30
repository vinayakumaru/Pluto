package com.example.pluto.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.pluto.data.model.Account
import com.example.pluto.data.model.Transaction
import com.example.pluto.data.model.TransactionType
import com.example.pluto.data.model.TransactionWithAccount
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TransactionDao {

    // Account-related queries
    @Insert
    suspend fun insertAccount(account: Account)

    @Update
    suspend fun updateAccount(account: Account)

    @Delete
    suspend fun deleteAccount(account: Account)

    @Query("SELECT * FROM accounts")
    fun getAllAccounts(): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE accountId = :accountId")
    fun getAccountById(accountId: Int): Flow<Account>


    // Transaction-related queries
    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionById(id: Int): Flow<Transaction>

    /**
     * Fetches all transactions JOINED with their account for a specific account
     * within a given date range, ordered by date descending.
     */
    @androidx.room.Transaction
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsForAccountByDateRange(
        startDate: Date,
        endDate: Date
    ): Flow<List<TransactionWithAccount>>

    /**
     * Calculates the total for a given transaction type (INCOME or EXPENSE)
     * for a specific account within a date range.
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate")
    fun getTotalForTypeInDateRange(
        type: TransactionType,
        startDate: Date,
        endDate: Date
    ): Flow<Double>
}
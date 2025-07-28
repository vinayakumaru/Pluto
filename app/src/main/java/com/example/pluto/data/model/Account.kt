package com.example.pluto.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user's account (e.g., Cash, Bank, Credit Card).
 *
 * @param accountId The unique identifier for the account.
 * @param name The name of the account (e.g., "Main Bank").
 * @param initialBalance The starting balance of the account.
 */
@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val accountId: Int = 0,
    val name: String,
    val initialBalance: Double = 0.0
)
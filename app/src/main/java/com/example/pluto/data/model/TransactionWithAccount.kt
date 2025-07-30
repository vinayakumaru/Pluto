package com.example.pluto.data.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Represents the relationship between a Transaction and its associated Account.
 * Room will use this to fetch a Transaction and then automatically fetch the
 * corresponding Account.
 */
data class TransactionWithAccount(
    @Embedded
    val transaction: Transaction,

    @Relation(
        parentColumn = "accountId",
        entityColumn = "accountId"
    )
    val account: Account
)
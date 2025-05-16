package com.wolf.envelopebro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val type: TransactionType,
    val envelopeId: String? = null, // null for income transactions
    val description: String,
    val date: Date = Date()
)

enum class TransactionType {
    INCOME,
    ENVELOPE_TRANSFER,
    EXPENSE
} 
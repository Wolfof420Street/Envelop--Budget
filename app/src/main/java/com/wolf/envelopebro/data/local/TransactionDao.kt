package com.wolf.envelopebro.data.local

import androidx.room.*
import com.wolf.envelopebro.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE envelopeId = :envelopeId ORDER BY date DESC")
    fun getTransactionsForEnvelope(envelopeId: String): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE envelopeId = :envelopeId")
    fun getEnvelopeBalance(envelopeId: String): Flow<Double?>
} 
package com.wolf.envelopebro.data.repository

import com.wolf.envelopebro.data.local.EnvelopeDao
import com.wolf.envelopebro.data.local.TransactionDao
import com.wolf.envelopebro.data.model.Envelope
import com.wolf.envelopebro.data.model.Transaction
import com.wolf.envelopebro.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface EnvelopeRepository {
    fun getAllEnvelopes(): Flow<List<Envelope>>
    suspend fun addEnvelope(envelope: Envelope)
    suspend fun updateEnvelope(envelope: Envelope)
    suspend fun deleteEnvelope(envelope: Envelope)
    suspend fun addIncome(amount: Double, description: String)
    suspend fun transferToEnvelope(envelopeId: String, amount: Double, description: String)
    suspend fun spendFromEnvelope(envelopeId: String, amount: Double, description: String)
    fun getTotalIncome(): Flow<Double?>
    fun getEnvelopeBalance(envelopeId: String): Flow<Double?>
    fun getAllTransactions(): Flow<List<Transaction>>
}

@Singleton
class EnvelopeRepositoryImpl @Inject constructor(
    private val envelopeDao: EnvelopeDao,
    private val transactionDao: TransactionDao
) : EnvelopeRepository {

    override fun getAllEnvelopes(): Flow<List<Envelope>> = envelopeDao.getAllEnvelopes()

    override suspend fun addEnvelope(envelope: Envelope) = envelopeDao.insertEnvelope(envelope)

    override suspend fun updateEnvelope(envelope: Envelope) = envelopeDao.updateEnvelope(envelope)

    override suspend fun deleteEnvelope(envelope: Envelope) = envelopeDao.deleteEnvelope(envelope)

    override suspend fun addIncome(amount: Double, description: String) {
        val transaction = Transaction(
            amount = amount,
            type = TransactionType.INCOME,
            description = description
        )
        transactionDao.insertTransaction(transaction)
    }

    override suspend fun transferToEnvelope(envelopeId: String, amount: Double, description: String) {
        val transaction = Transaction(
            amount = -amount,
            type = TransactionType.ENVELOPE_TRANSFER,
            envelopeId = envelopeId,
            description = description
        )
        transactionDao.insertTransaction(transaction)
        envelopeDao.updateBalance(envelopeId, amount)
    }

    override suspend fun spendFromEnvelope(envelopeId: String, amount: Double, description: String) {
        val transaction = Transaction(
            amount = -amount,
            type = TransactionType.EXPENSE,
            envelopeId = envelopeId,
            description = description
        )
        transactionDao.insertTransaction(transaction)
        envelopeDao.updateBalance(envelopeId, -amount)
    }

    override fun getTotalIncome(): Flow<Double?> = transactionDao.getTotalIncome()

    override fun getEnvelopeBalance(envelopeId: String): Flow<Double?> = 
        transactionDao.getEnvelopeBalance(envelopeId)

    override fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()
} 
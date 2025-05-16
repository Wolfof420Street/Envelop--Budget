package com.wolf.envelopebro.data.local

import androidx.room.*
import com.wolf.envelopebro.data.model.Envelope
import kotlinx.coroutines.flow.Flow

@Dao
interface EnvelopeDao {
    @Query("SELECT * FROM envelopes ORDER BY name ASC")
    fun getAllEnvelopes(): Flow<List<Envelope>>

    @Query("SELECT * FROM envelopes WHERE id = :id")
    suspend fun getEnvelopeById(id: String): Envelope?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnvelope(envelope: Envelope)

    @Update
    suspend fun updateEnvelope(envelope: Envelope)

    @Delete
    suspend fun deleteEnvelope(envelope: Envelope)

    @Query("UPDATE envelopes SET balance = balance + :amount WHERE id = :envelopeId")
    suspend fun updateBalance(envelopeId: String, amount: Double)
} 
package com.wolf.envelopebro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wolf.envelopebro.data.model.Envelope
import com.wolf.envelopebro.data.model.Transaction

@Database(
    entities = [Envelope::class, Transaction::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class EnvelopeDatabase : RoomDatabase() {
    abstract fun envelopeDao(): EnvelopeDao
    abstract fun transactionDao(): TransactionDao
} 
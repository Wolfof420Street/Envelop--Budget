package com.wolf.envelopebro.di

import android.content.Context
import androidx.room.Room
import com.wolf.envelopebro.data.local.EnvelopeDatabase
import com.wolf.envelopebro.data.repository.EnvelopeRepository
import com.wolf.envelopebro.data.repository.EnvelopeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideEnvelopeDatabase(
        @ApplicationContext context: Context
    ): EnvelopeDatabase {
        return Room.databaseBuilder(
            context,
            EnvelopeDatabase::class.java,
            "envelope_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideEnvelopeDao(database: EnvelopeDatabase) = database.envelopeDao()

    @Provides
    @Singleton
    fun provideTransactionDao(database: EnvelopeDatabase) = database.transactionDao()

    @Provides
    @Singleton
    fun provideEnvelopeRepository(
        envelopeDao: com.wolf.envelopebro.data.local.EnvelopeDao,
        transactionDao: com.wolf.envelopebro.data.local.TransactionDao
    ): EnvelopeRepository {
        return EnvelopeRepositoryImpl(envelopeDao, transactionDao)
    }
} 
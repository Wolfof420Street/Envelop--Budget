package com.wolf.envelopebro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "envelopes")
data class Envelope(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val balance: Double,
    val color: Int // Color resource ID
) 
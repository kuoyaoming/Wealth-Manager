package com.wealthmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "cash_assets")
data class CashAsset(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val currency: String, // "TWD" or "USD"
    val amount: Double,
    val twdEquivalent: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)
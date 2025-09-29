package com.wealthmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "stock_assets")
data class StockAsset(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val symbol: String, // e.g., "AAPL", "2330.TW"
    val companyName: String,
    val shares: Int,
    val market: String, // "GLOBAL" for all stocks
    val currentPrice: Double = 0.0,
    val originalCurrency: String = "USD", // Default to USD for global stocks
    val twdEquivalent: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)
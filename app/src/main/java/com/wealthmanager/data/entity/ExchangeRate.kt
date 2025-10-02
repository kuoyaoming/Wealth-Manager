package com.wealthmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates")
data class ExchangeRate(
    @PrimaryKey
    val currencyPair: String, // e.g., "USD_TWD"
    val rate: Double,
    val lastUpdated: Long = System.currentTimeMillis(),
)

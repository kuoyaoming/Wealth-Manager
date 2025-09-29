package com.wealthmanager.data.api

// TWSE API Response Models - Updated based on actual API response
data class TwseStockResponse(
    val value: List<TwseStockItem>
)

data class TwseStockItem(
    val Date: String,           // Date
    val Code: String,           // Stock code
    val Name: String,           // Stock name
    val TradeVolume: String,    // Trading volume
    val TradeValue: String,     // Trading value
    val OpeningPrice: String,   // Opening price
    val HighestPrice: String,   // Highest price
    val LowestPrice: String,    // Lowest price
    val ClosingPrice: String,   // Closing price
    val Change: String,         // Price change
    val Transaction: String     // Transaction count
)

// Taiwan stock data model - for internal processing
data class TwseStockData(
    val stockNo: String,        // Stock code
    val stockName: String,      // Stock name
    val tradeVolume: String,    // Trading volume
    val tradeValue: String,     // Trading value
    val open: String,          // Opening price
    val high: String,          // Highest price
    val low: String,           // Lowest price
    val close: String,         // Closing price
    val change: String,        // Price change
    val changePercent: String, // Price change percentage
    val transactionCount: String // Transaction count
)
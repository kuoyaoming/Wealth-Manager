package com.wealthmanager.data.api

import com.google.gson.annotations.SerializedName

// TWSE API Response Models - Updated based on actual API response
data class TwseStockResponse(
    @SerializedName("value") val value: List<TwseStockItem>
)

data class TwseStockItem(
    @SerializedName("Date") val Date: String,
    @SerializedName("Code") val Code: String,
    @SerializedName("Name") val Name: String,
    @SerializedName("TradeVolume") val TradeVolume: String,
    @SerializedName("TradeValue") val TradeValue: String,
    @SerializedName("OpeningPrice") val OpeningPrice: String,
    @SerializedName("HighestPrice") val HighestPrice: String,
    @SerializedName("LowestPrice") val LowestPrice: String,
    @SerializedName("ClosingPrice") val ClosingPrice: String,
    @SerializedName("Change") val Change: String,
    @SerializedName("Transaction") val Transaction: String
)

// Taiwan stock data model - for internal processing
data class TwseStockData(
    val stockNo: String,
    val stockName: String,
    val tradeVolume: String,
    val tradeValue: String,
    val open: String,
    val high: String,
    val low: String,
    val close: String,
    val change: String,
    val changePercent: String,
    val transactionCount: String
)
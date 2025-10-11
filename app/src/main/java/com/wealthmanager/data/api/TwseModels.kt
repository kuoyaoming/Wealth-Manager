package com.wealthmanager.data.api

import com.google.gson.annotations.SerializedName

/**
 * Individual stock item from TWSE API response.
 *
 * @property date Trading date
 * @property code Stock code
 * @property name Stock name
 * @property tradeVolume Trading volume
 * @property tradeValue Trading value
 * @property openingPrice Opening price
 * @property highestPrice Highest price of the day
 * @property lowestPrice Lowest price of the day
 * @property closingPrice Closing price
 * @property change Price change
 * @property transaction Transaction count
 */
data class TwseStockItem(
    @SerializedName("Date") val date: String,
    @SerializedName("Code") val code: String,
    @SerializedName("Name") val name: String,
    @SerializedName("TradeVolume") val tradeVolume: String,
    @SerializedName("TradeValue") val tradeValue: String,
    @SerializedName("OpeningPrice") val openingPrice: String,
    @SerializedName("HighestPrice") val highestPrice: String,
    @SerializedName("LowestPrice") val lowestPrice: String,
    @SerializedName("ClosingPrice") val closingPrice: String,
    @SerializedName("Change") val change: String,
    @SerializedName("Transaction") val transaction: String,
)

/**
 * Taiwan stock data model for internal processing.
 *
 * This model represents processed stock data from TWSE API
 * with normalized field names for internal use.
 *
 * @property stockNo Stock number/code
 * @property stockName Stock name
 * @property tradeVolume Trading volume
 * @property tradeValue Trading value
 * @property open Opening price
 * @property high Highest price of the day
 * @property low Lowest price of the day
 * @property close Closing price
 * @property change Price change amount
 * @property changePercent Price change percentage
 * @property transactionCount Number of transactions
 */
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
    val transactionCount: String,
)

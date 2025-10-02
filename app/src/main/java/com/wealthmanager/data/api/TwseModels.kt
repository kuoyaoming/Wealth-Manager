package com.wealthmanager.data.api

import com.google.gson.annotations.SerializedName

/**
 * Response model for TWSE API stock data.
 *
 * @property value List of stock items from TWSE API
 */
data class TwseStockResponse(
    @SerializedName("value") val value: List<TwseStockItem>
)

/**
 * Individual stock item from TWSE API response.
 *
 * @property Date Trading date
 * @property Code Stock code
 * @property Name Stock name
 * @property TradeVolume Trading volume
 * @property TradeValue Trading value
 * @property OpeningPrice Opening price
 * @property HighestPrice Highest price of the day
 * @property LowestPrice Lowest price of the day
 * @property ClosingPrice Closing price
 * @property Change Price change
 * @property Transaction Transaction count
 */
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
    val transactionCount: String
)

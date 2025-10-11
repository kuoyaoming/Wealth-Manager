package com.wealthmanager.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API interface for Finnhub financial data service.
 *
 * This interface provides methods to access real-time stock quotes and
 * stock search functionality from the Finnhub API.
 */
interface FinnhubApi {
    /**
     * Retrieves real-time stock quote data for a given symbol.
     *
     * @param symbol The stock symbol to get quote for (e.g., "AAPL", "MSFT")
     * @return [FinnhubQuoteResponse] containing current price, change, and other quote data
     */
    @GET("finnhub/quote")
    suspend fun getStockQuote(
        @Query("symbol") symbol: String,
    ): FinnhubQuoteResponse

    /**
     * Searches for stocks matching the given query.
     *
     * @param query The search query string
     * @return [FinnhubSearchResponse] containing matching stock results
     */
    @GET("finnhub/search")
    suspend fun searchStocks(
        @Query("q") query: String,
    ): FinnhubSearchResponse
}

/**
 * Response model for Finnhub stock quote API.
 *
 * @property currentPrice Current price
 * @property change Change amount
 * @property changePercent Change percentage
 * @property highPrice High price of the day
 * @property lowPrice Low price of the day
 * @property openPrice Open price of the day
 * @property previousClosePrice Previous close price
 * @property timestamp Timestamp of the quote
 */
data class FinnhubQuoteResponse(
    @SerializedName("c") val currentPrice: Double,
    @SerializedName("d") val change: Double,
    @SerializedName("dp") val changePercent: Double,
    @SerializedName("h") val highPrice: Double,
    @SerializedName("l") val lowPrice: Double,
    @SerializedName("o") val openPrice: Double,
    @SerializedName("pc") val previousClosePrice: Double,
    @SerializedName("t") val timestamp: Long,
)

/**
 * Response model for Finnhub stock search API.
 *
 * @property count Total number of search results
 * @property result List of matching stock search results
 */
data class FinnhubSearchResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("result") val result: List<FinnhubSearchResult>,
)

/**
 * Individual stock search result from Finnhub API.
 *
 * @property description Full description of the stock
 * @property displaySymbol Display symbol for the stock
 * @property symbol Trading symbol
 * @property type Type of financial instrument
 */
data class FinnhubSearchResult(
    @SerializedName("description") val description: String,
    @SerializedName("displaySymbol") val displaySymbol: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("type") val type: String,
)

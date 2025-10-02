package com.wealthmanager.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API interface for Finnhub financial data service.
 *
 * This interface provides methods to access real-time stock quotes,
 * stock search functionality, and exchange rate data from Finnhub API.
 */
interface FinnhubApi {

    /**
     * Retrieves real-time stock quote data for a given symbol.
     *
     * @param symbol The stock symbol to get quote for (e.g., "AAPL", "MSFT")
     * @return [FinnhubQuoteResponse] containing current price, change, and other quote data
     */
    @GET("quote")
    suspend fun getStockQuote(
        @Query("symbol") symbol: String
    ): FinnhubQuoteResponse

    /**
     * Searches for stocks matching the given query.
     *
     * @param query The search query string
     * @return [FinnhubSearchResponse] containing matching stock results
     */
    @GET("search")
    suspend fun searchStocks(
        @Query("q") query: String
    ): FinnhubSearchResponse

    /**
     * Gets current exchange rates for a base currency.
     *
     * @param baseCurrency The base currency code (defaults to "USD")
     * @return [FinnhubExchangeResponse] containing exchange rate data
     */
    @GET("forex/rates")
    suspend fun getExchangeRate(
        @Query("base") baseCurrency: String = "USD"
    ): FinnhubExchangeResponse
}

/**
 * Response model for Finnhub stock quote API.
 *
 * @property c Current price
 * @property d Change amount
 * @property dp Change percentage
 * @property h High price of the day
 * @property l Low price of the day
 * @property o Open price of the day
 * @property pc Previous close price
 * @property t Timestamp of the quote
 */
data class FinnhubQuoteResponse(
    @SerializedName("c") val c: Double,
    @SerializedName("d") val d: Double,
    @SerializedName("dp") val dp: Double,
    @SerializedName("h") val h: Double,
    @SerializedName("l") val l: Double,
    @SerializedName("o") val o: Double,
    @SerializedName("pc") val pc: Double,
    @SerializedName("t") val t: Long
)

/**
 * Response model for Finnhub stock search API.
 *
 * @property count Total number of search results
 * @property result List of matching stock search results
 */
data class FinnhubSearchResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("result") val result: List<FinnhubSearchResult>
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
    @SerializedName("type") val type: String
)

/**
 * Response model for Finnhub exchange rate API.
 *
 * @property base Base currency code
 * @property quote Quote currency code
 * @property rate Exchange rate from base to quote currency
 */
data class FinnhubExchangeResponse(
    @SerializedName("base") val base: String,
    @SerializedName("quote") val quote: String,
    @SerializedName("rate") val rate: Double
)

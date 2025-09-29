package com.wealthmanager.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface FinnhubApi {
    
    @GET("quote")
    suspend fun getStockQuote(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): FinnhubQuoteResponse
    
    @GET("search")
    suspend fun searchStocks(
        @Query("q") query: String,
        @Query("token") token: String
    ): FinnhubSearchResponse
    
    @GET("forex/rates")
    suspend fun getExchangeRate(
        @Query("base") baseCurrency: String = "USD",
        @Query("token") token: String
    ): FinnhubExchangeResponse
}

// Finnhub API Response Models
data class FinnhubQuoteResponse(
    val c: Double, // Current price
    val d: Double, // Change
    val dp: Double, // Percent change
    val h: Double, // High price of the day
    val l: Double, // Low price of the day
    val o: Double, // Open price of the day
    val pc: Double, // Previous close price
    val t: Long // Timestamp
)

data class FinnhubSearchResponse(
    val count: Int,
    val result: List<FinnhubSearchResult>
)

data class FinnhubSearchResult(
    val description: String,
    val displaySymbol: String,
    val symbol: String,
    val type: String
)

data class FinnhubExchangeResponse(
    val base: String,
    val quote: String,
    val rate: Double
)

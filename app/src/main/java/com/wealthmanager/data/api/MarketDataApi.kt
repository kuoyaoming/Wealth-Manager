package com.wealthmanager.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface MarketDataApi {
    
    @GET("quote")
    suspend fun getStockQuote(
        @Query("symbol") symbol: String,
        @Query("region") region: String = "US"
    ): StockQuoteResponse
    
    @GET("search")
    suspend fun searchStocks(
        @Query("q") query: String,
        @Query("region") region: String = "US"
    ): StockSearchResponse
    
    @GET("currency")
    suspend fun getExchangeRate(
        @Query("from") from: String,
        @Query("to") to: String
    ): ExchangeRateResponse
}

data class StockQuoteResponse(
    val symbol: String,
    val shortName: String,
    val longName: String,
    val regularMarketPrice: Double,
    val regularMarketChange: Double,
    val regularMarketChangePercent: Double,
    val currency: String,
    val marketState: String,
    val exchange: String
)

data class StockSearchResponse(
    val quotes: List<StockSearchItem>
)

data class StockSearchItem(
    val symbol: String,
    val shortName: String,
    val longName: String,
    val exchange: String,
    val marketState: String
)

data class ExchangeRateResponse(
    val from: String,
    val to: String,
    val rate: Double,
    val timestamp: Long
)
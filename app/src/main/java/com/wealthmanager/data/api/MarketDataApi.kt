package com.wealthmanager.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface MarketDataApi {
    
    @GET("v8/finance/quote")
    suspend fun getStockQuote(
        @Query("symbols") symbols: String,
        @Query("region") region: String = "US"
    ): YahooQuoteResponse
    
    @GET("v1/finance/search")
    suspend fun searchStocks(
        @Query("q") query: String,
        @Query("region") region: String = "US"
    ): YahooSearchResponse
    
    @GET("v8/finance/quote")
    suspend fun getExchangeRate(
        @Query("symbols") symbols: String = "USD=X"
    ): YahooQuoteResponse
}

// Yahoo Finance API Response Models
data class YahooQuoteResponse(
    val quoteResponse: QuoteResponse
)

data class QuoteResponse(
    val result: List<YahooQuoteResult>?,
    val error: YahooError?
)

data class YahooQuoteResult(
    val symbol: String,
    val shortName: String?,
    val longName: String?,
    val regularMarketPrice: Double?,
    val regularMarketChange: Double?,
    val regularMarketChangePercent: Double?,
    val currency: String?,
    val marketState: String?,
    val exchange: String?
)

data class YahooSearchResponse(
    val quotes: List<YahooSearchResult>
)

data class YahooSearchResult(
    val symbol: String,
    val shortName: String?,
    val longName: String?,
    val exchange: String?,
    val marketState: String?
)

data class YahooError(
    val code: String,
    val description: String
)

// Legacy models for backward compatibility
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
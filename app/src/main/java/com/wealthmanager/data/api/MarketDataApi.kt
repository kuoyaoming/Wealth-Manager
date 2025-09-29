package com.wealthmanager.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface MarketDataApi {
    
    @GET("query")
    suspend fun getStockQuote(
        @Query("function") function: String = "GLOBAL_QUOTE",
        @Query("symbol") symbol: String,
        @Query("apikey") apikey: String
    ): AlphaVantageQuoteResponse
    
    @GET("query")
    suspend fun searchStocks(
        @Query("function") function: String = "SYMBOL_SEARCH",
        @Query("keywords") keywords: String,
        @Query("apikey") apikey: String
    ): AlphaVantageSearchResponse
    
    @GET("query")
    suspend fun getExchangeRate(
        @Query("function") function: String = "CURRENCY_EXCHANGE_RATE",
        @Query("from_currency") fromCurrency: String = "USD",
        @Query("to_currency") toCurrency: String = "TWD",
        @Query("apikey") apikey: String
    ): AlphaVantageExchangeResponse
}

// Alpha Vantage API Response Models
data class AlphaVantageQuoteResponse(
    @com.google.gson.annotations.SerializedName("Global Quote") val globalQuote: AlphaVantageQuote?,
    @com.google.gson.annotations.SerializedName("Error Message") val errorMessage: String?,
    @com.google.gson.annotations.SerializedName("Note") val note: String?
)

data class AlphaVantageQuote(
    @com.google.gson.annotations.SerializedName("01. symbol") val symbol: String,
    @com.google.gson.annotations.SerializedName("02. open") val open: String,
    @com.google.gson.annotations.SerializedName("03. high") val high: String,
    @com.google.gson.annotations.SerializedName("04. low") val low: String,
    @com.google.gson.annotations.SerializedName("05. price") val price: String,
    @com.google.gson.annotations.SerializedName("06. volume") val volume: String,
    @com.google.gson.annotations.SerializedName("07. latest trading day") val latestTradingDay: String,
    @com.google.gson.annotations.SerializedName("08. previous close") val previousClose: String,
    @com.google.gson.annotations.SerializedName("09. change") val change: String,
    @com.google.gson.annotations.SerializedName("10. change percent") val changePercent: String
)

data class AlphaVantageSearchResponse(
    @com.google.gson.annotations.SerializedName("bestMatches") val bestMatches: List<AlphaVantageSearchResult>?,
    @com.google.gson.annotations.SerializedName("Error Message") val errorMessage: String?,
    @com.google.gson.annotations.SerializedName("Note") val note: String?
)

data class AlphaVantageSearchResult(
    @com.google.gson.annotations.SerializedName("1. symbol") val symbol: String,
    @com.google.gson.annotations.SerializedName("2. name") val name: String,
    @com.google.gson.annotations.SerializedName("3. type") val type: String,
    @com.google.gson.annotations.SerializedName("4. region") val region: String,
    @com.google.gson.annotations.SerializedName("5. marketOpen") val marketOpen: String,
    @com.google.gson.annotations.SerializedName("6. marketClose") val marketClose: String,
    @com.google.gson.annotations.SerializedName("7. timezone") val timezone: String,
    @com.google.gson.annotations.SerializedName("8. currency") val currency: String,
    @com.google.gson.annotations.SerializedName("9. matchScore") val matchScore: String
)

data class AlphaVantageExchangeResponse(
    @com.google.gson.annotations.SerializedName("Realtime Currency Exchange Rate") val exchangeRate: AlphaVantageExchangeRate?,
    @com.google.gson.annotations.SerializedName("Error Message") val errorMessage: String?,
    @com.google.gson.annotations.SerializedName("Note") val note: String?
)

data class AlphaVantageExchangeRate(
    @com.google.gson.annotations.SerializedName("1. From_Currency Code") val fromCurrencyCode: String,
    @com.google.gson.annotations.SerializedName("2. From_Currency Name") val fromCurrencyName: String,
    @com.google.gson.annotations.SerializedName("3. To_Currency Code") val toCurrencyCode: String,
    @com.google.gson.annotations.SerializedName("4. To_Currency Name") val toCurrencyName: String,
    @com.google.gson.annotations.SerializedName("5. Exchange Rate") val exchangeRate: String,
    @com.google.gson.annotations.SerializedName("6. Last Refreshed") val lastRefreshed: String,
    @com.google.gson.annotations.SerializedName("7. Time Zone") val timeZone: String,
    @com.google.gson.annotations.SerializedName("8. Bid Price") val bidPrice: String,
    @com.google.gson.annotations.SerializedName("9. Ask Price") val askPrice: String
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

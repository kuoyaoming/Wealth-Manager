package com.wealthmanager.data.api

import com.google.gson.annotations.SerializedName
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
    @SerializedName("c") val c: Double,
    @SerializedName("d") val d: Double,
    @SerializedName("dp") val dp: Double,
    @SerializedName("h") val h: Double,
    @SerializedName("l") val l: Double,
    @SerializedName("o") val o: Double,
    @SerializedName("pc") val pc: Double,
    @SerializedName("t") val t: Long
)

data class FinnhubSearchResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("result") val result: List<FinnhubSearchResult>
)

data class FinnhubSearchResult(
    @SerializedName("description") val description: String,
    @SerializedName("displaySymbol") val displaySymbol: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("type") val type: String
)

data class FinnhubExchangeResponse(
    @SerializedName("base") val base: String,
    @SerializedName("quote") val quote: String,
    @SerializedName("rate") val rate: Double
)

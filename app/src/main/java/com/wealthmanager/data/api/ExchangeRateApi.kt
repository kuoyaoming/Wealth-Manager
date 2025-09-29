package com.wealthmanager.data.api

import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {
    
    @GET("v6/{apiKey}/latest/{baseCurrency}")
    suspend fun getExchangeRate(
        @Path("apiKey") apiKey: String,
        @Path("baseCurrency") baseCurrency: String
    ): ExchangeRateResponse
}

// ExchangeRate-API Response Models
data class ExchangeRateResponse(
    val result: String,
    val documentation: String,
    val terms_of_use: String,
    val time_last_update_unix: Long,
    val time_last_update_utc: String,
    val time_next_update_unix: Long,
    val time_next_update_utc: String,
    val base_code: String,
    val target_code: String,
    val conversion_rate: Double,
    val conversion_result: Double
)

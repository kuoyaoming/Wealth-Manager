package com.wealthmanager.data.api

import com.google.gson.annotations.SerializedName
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
    @SerializedName("result") val result: String,
    @SerializedName("documentation") val documentation: String,
    @SerializedName("terms_of_use") val terms_of_use: String,
    @SerializedName("time_last_update_unix") val time_last_update_unix: Long,
    @SerializedName("time_last_update_utc") val time_last_update_utc: String,
    @SerializedName("time_next_update_unix") val time_next_update_unix: Long,
    @SerializedName("time_next_update_utc") val time_next_update_utc: String,
    @SerializedName("base_code") val base_code: String,
    @SerializedName("conversion_rates") val conversion_rates: Map<String, Double>
)

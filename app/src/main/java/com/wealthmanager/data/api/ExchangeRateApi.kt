package com.wealthmanager.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API interface for exchange rate data service.
 *
 * This interface provides methods to access real-time exchange rates
 * for currency conversion calculations.
 */
interface ExchangeRateApi {
    /**
     * Retrieves current exchange rates for a base currency.
     *
     * @param baseCurrency Base currency code (e.g., "USD", "TWD")
     * @return [ExchangeRateResponse] containing exchange rate data
     */
    @GET("exchangerate/latest/{baseCurrency}")
    suspend fun getExchangeRate(
        @Path("baseCurrency") baseCurrency: String,
    ): ExchangeRateResponse
}

/**
 * Response model for exchange rate API.
 *
 * @property result API result status
 * @property documentation API documentation URL
 * @property termsOfUse Terms of use URL
 * @property timeLastUpdateUnix Last update timestamp (Unix)
 * @property timeLastUpdateUtc Last update time (UTC)
 * @property timeNextUpdateUnix Next update timestamp (Unix)
 * @property timeNextUpdateUtc Next update time (UTC)
 * @property baseCode Base currency code
 * @property conversionRates Map of currency codes to exchange rates
 */
data class ExchangeRateResponse(
    @SerializedName("result") val result: String,
    @SerializedName("documentation") val documentation: String,
    @SerializedName("terms_of_use") val termsOfUse: String,
    @SerializedName("time_last_update_unix") val timeLastUpdateUnix: Long,
    @SerializedName("time_last_update_utc") val timeLastUpdateUtc: String,
    @SerializedName("time_next_update_unix") val timeNextUpdateUnix: Long,
    @SerializedName("time_next_update_utc") val timeNextUpdateUtc: String,
    @SerializedName("base_code") val baseCode: String,
    @SerializedName("conversion_rates") val conversionRates: Map<String, Double>,
)

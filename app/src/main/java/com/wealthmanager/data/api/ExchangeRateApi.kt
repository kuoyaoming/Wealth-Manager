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
     * @param apiKey API key for authentication
     * @param baseCurrency Base currency code (e.g., "USD", "TWD")
     * @return [ExchangeRateResponse] containing exchange rate data
     */
    @GET("v6/{apiKey}/latest/{baseCurrency}")
    suspend fun getExchangeRate(
        @Path("apiKey") apiKey: String,
        @Path("baseCurrency") baseCurrency: String
    ): ExchangeRateResponse
}

/**
 * Response model for exchange rate API.
 *
 * @property result API result status
 * @property documentation API documentation URL
 * @property terms_of_use Terms of use URL
 * @property time_last_update_unix Last update timestamp (Unix)
 * @property time_last_update_utc Last update time (UTC)
 * @property time_next_update_unix Next update timestamp (Unix)
 * @property time_next_update_utc Next update time (UTC)
 * @property base_code Base currency code
 * @property conversion_rates Map of currency codes to exchange rates
 */
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

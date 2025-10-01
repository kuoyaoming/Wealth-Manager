package com.wealthmanager.data.service

import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.security.KeyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for testing API key functionality
 * Tests both Finnhub and Exchange Rate API keys
 */
@Singleton
class ApiTestService @Inject constructor(
    private val debugLogManager: DebugLogManager,
    private val keyRepository: KeyRepository
) {
    
    data class ApiTestResult(
        val isWorking: Boolean,
        val message: String,
        val apiName: String
    )
    
    /**
     * Test Finnhub API key by making a simple request
     */
    suspend fun testFinnhubApi(): ApiTestResult = withContext(Dispatchers.IO) {
        try {
            debugLogManager.log("API_TEST", "Testing Finnhub API key")
            
            // Check if API key is available (user key overrides BuildConfig)
            val key = keyRepository.getUserFinnhubKey() ?: ""
            if (key.isBlank()) {
                return@withContext ApiTestResult(
                    isWorking = false,
                    message = "API Key not configured",
                    apiName = "Finnhub"
                )
            }
            
            // Make a simple test request to get AAPL quote
            val testUrl = "https://finnhub.io/api/v1/quote?symbol=AAPL&token=$key"
            val response = java.net.URL(testUrl).openConnection().apply {
                connectTimeout = 10000
                readTimeout = 10000
            }.getInputStream().bufferedReader().readText()
            
            // Check if response contains expected data
            if (response.contains("\"c\":") && response.contains("\"d\":")) {
                debugLogManager.log("API_TEST", "Finnhub API test successful")
                ApiTestResult(
                    isWorking = true,
                    message = "API Working - Test successful",
                    apiName = "Finnhub"
                )
            } else {
                debugLogManager.log("API_TEST", "Finnhub API test failed - invalid response")
                ApiTestResult(
                    isWorking = false,
                    message = "API Error - Invalid response",
                    apiName = "Finnhub"
                )
            }
        } catch (e: Exception) {
            debugLogManager.logError("API_TEST: Finnhub API test failed", e)
            ApiTestResult(
                isWorking = false,
                message = "API Error - ${e.message}",
                apiName = "Finnhub"
            )
        }
    }

    /**
     * Test Finnhub API key with an explicitly provided key (used for validate-before-save in settings)
     */
    suspend fun testFinnhubApiWithKey(key: String): ApiTestResult = withContext(Dispatchers.IO) {
        try {
            debugLogManager.log("API_TEST", "Testing Finnhub API key (provided)")
            if (key.isBlank()) {
                return@withContext ApiTestResult(false, "API Key not configured", "Finnhub")
            }
            val testUrl = "https://finnhub.io/api/v1/quote?symbol=AAPL&token=$key"
            val response = java.net.URL(testUrl).openConnection().apply {
                connectTimeout = 10000
                readTimeout = 10000
            }.getInputStream().bufferedReader().readText()
            if (response.contains("\"c\":") && response.contains("\"d\":")) {
                ApiTestResult(true, "API Working - Test successful", "Finnhub")
            } else {
                ApiTestResult(false, "API Error - Invalid response", "Finnhub")
            }
        } catch (e: Exception) {
            debugLogManager.logError("API_TEST: Finnhub provided key test failed", e)
            ApiTestResult(false, "API Error - ${e.message}", "Finnhub")
        }
    }
    
    /**
     * Test Exchange Rate API key by making a simple request
     */
    suspend fun testExchangeRateApi(): ApiTestResult = withContext(Dispatchers.IO) {
        try {
            debugLogManager.log("API_TEST", "Testing Exchange Rate API key")
            
            // Check if API key is available (user key overrides BuildConfig)
            val key = keyRepository.getUserExchangeKey() ?: ""
            if (key.isBlank()) {
                return@withContext ApiTestResult(
                    isWorking = false,
                    message = "API Key not configured",
                    apiName = "Exchange Rate"
                )
            }
            
            // Make a simple test request to get USD to TWD rate
            val testUrl = "https://v6.exchangerate-api.com/v6/$key/latest/USD"
            val response = java.net.URL(testUrl).openConnection().apply {
                connectTimeout = 10000
                readTimeout = 10000
            }.getInputStream().bufferedReader().readText()
            
            // Check if response contains expected data
            if (response.contains("\"result\":\"success\"") && response.contains("\"TWD\"")) {
                debugLogManager.log("API_TEST", "Exchange Rate API test successful")
                ApiTestResult(
                    isWorking = true,
                    message = "API Working - Test successful",
                    apiName = "Exchange Rate"
                )
            } else {
                debugLogManager.log("API_TEST", "Exchange Rate API test failed - invalid response")
                ApiTestResult(
                    isWorking = false,
                    message = "API Error - Invalid response",
                    apiName = "Exchange Rate"
                )
            }
        } catch (e: Exception) {
            debugLogManager.logError("API_TEST: Exchange Rate API test failed", e)
            ApiTestResult(
                isWorking = false,
                message = "API Error - ${e.message}",
                apiName = "Exchange Rate"
            )
        }
    }

    /**
     * Test Exchange Rate API key with an explicitly provided key (used for validate-before-save in settings)
     */
    suspend fun testExchangeRateApiWithKey(key: String): ApiTestResult = withContext(Dispatchers.IO) {
        try {
            debugLogManager.log("API_TEST", "Testing Exchange Rate API key (provided)")
            if (key.isBlank()) {
                return@withContext ApiTestResult(false, "API Key not configured", "Exchange Rate")
            }
            val testUrl = "https://v6.exchangerate-api.com/v6/$key/latest/USD"
            val response = java.net.URL(testUrl).openConnection().apply {
                connectTimeout = 10000
                readTimeout = 10000
            }.getInputStream().bufferedReader().readText()
            if (response.contains("\"result\":\"success\"") && response.contains("\"TWD\"")) {
                ApiTestResult(true, "API Working - Test successful", "Exchange Rate")
            } else {
                ApiTestResult(false, "API Error - Invalid response", "Exchange Rate")
            }
        } catch (e: Exception) {
            debugLogManager.logError("API_TEST: Exchange Rate provided key test failed", e)
            ApiTestResult(false, "API Error - ${e.message}", "Exchange Rate")
        }
    }
    
    /**
     * Test all APIs and return combined result
     */
    suspend fun testAllApis(): List<ApiTestResult> {
        return listOf(
            testFinnhubApi(),
            testExchangeRateApi()
        )
    }
}


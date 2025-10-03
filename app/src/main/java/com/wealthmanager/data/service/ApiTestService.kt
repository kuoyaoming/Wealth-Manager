package com.wealthmanager.data.service

import android.content.Context
import com.wealthmanager.R
import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.security.KeyRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for testing API key functionality
 * Tests both Finnhub and Exchange Rate API keys
 */
@Singleton
class ApiTestService
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val debugLogManager: DebugLogManager,
        private val keyRepository: KeyRepository,
        private val secureApiKeyManager: com.wealthmanager.ui.security.SecureApiKeyManager,
    ) {
        data class ApiTestResult(
            val isWorking: Boolean,
            val message: String,
            val apiName: String,
        )

        data class SecureApiTestResult(
            val securityStatus: com.wealthmanager.ui.security.SecurityStatus,
            val finnhubResult: ApiTestResult,
            val exchangeResult: ApiTestResult,
            val overallSecurity: String,
        )

        /**
         * Test Finnhub API key by making a simple request
         */
        suspend fun testFinnhubApi(): ApiTestResult =
            withContext(Dispatchers.IO) {
                try {
                    debugLogManager.log("API_TEST", "Testing Finnhub API key")

                    // Check if API key is available (user key overrides BuildConfig)
                    val key = keyRepository.getUserFinnhubKey() ?: ""
                    if (key.isBlank()) {
                        return@withContext ApiTestResult(
                            isWorking = false,
                            message = context.getString(R.string.api_test_not_configured),
                            apiName = context.getString(R.string.api_name_finnhub),
                        )
                    }

                    // Make a simple test request to get AAPL quote
                    val testUrl = "https://finnhub.io/api/v1/quote?symbol=AAPL&token=$key"
                    val response =
                        java.net.URL(testUrl).openConnection().apply {
                            connectTimeout = 10000
                            readTimeout = 10000
                        }.getInputStream().bufferedReader().readText()

                    // Check if response contains expected data
                    if (response.contains("\"c\":") && response.contains("\"d\":")) {
                        debugLogManager.log("API_TEST", "Finnhub API test successful")
                        ApiTestResult(
                            isWorking = true,
                            message = context.getString(R.string.api_test_working_success),
                            apiName = context.getString(R.string.api_name_finnhub),
                        )
                    } else {
                        debugLogManager.log("API_TEST", "Finnhub API test failed - invalid response")
                        ApiTestResult(
                            isWorking = false,
                            message = context.getString(R.string.api_test_error_invalid_response),
                            apiName = context.getString(R.string.api_name_finnhub),
                        )
                    }
                } catch (e: Exception) {
                    debugLogManager.logError("API_TEST: Finnhub API test failed", e)
                    ApiTestResult(
                        isWorking = false,
                        message = context.getString(R.string.api_test_error_message, e.message ?: ""),
                        apiName = context.getString(R.string.api_name_finnhub),
                    )
                }
            }

        /**
         * Test Finnhub API key with an explicitly provided key (used for validate-before-save in settings)
         */
        suspend fun testFinnhubApiWithKey(key: String): ApiTestResult =
            withContext(Dispatchers.IO) {
                try {
                    debugLogManager.log("API_TEST", "Testing Finnhub API key (provided)")
                    if (key.isBlank()) {
                        return@withContext ApiTestResult(
                            false,
                            context.getString(R.string.api_test_not_configured),
                            context.getString(R.string.api_name_finnhub),
                        )
                    }
                    val testUrl = "https://finnhub.io/api/v1/quote?symbol=AAPL&token=$key"
                    val response =
                        java.net.URL(testUrl).openConnection().apply {
                            connectTimeout = 10000
                            readTimeout = 10000
                        }.getInputStream().bufferedReader().readText()
                    if (response.contains("\"c\":") && response.contains("\"d\":")) {
                        ApiTestResult(
                            true,
                            context.getString(R.string.api_test_working_success),
                            context.getString(R.string.api_name_finnhub),
                        )
                    } else {
                        ApiTestResult(
                            false,
                            context.getString(R.string.api_test_error_invalid_response),
                            context.getString(R.string.api_name_finnhub),
                        )
                    }
                } catch (e: Exception) {
                    debugLogManager.logError("API_TEST: Finnhub provided key test failed", e)
                    ApiTestResult(
                        false,
                        context.getString(R.string.api_test_error_message, e.message ?: ""),
                        context.getString(R.string.api_name_finnhub),
                    )
                }
            }

        /**
         * Test Exchange Rate API key by making a simple request
         */
        suspend fun testExchangeRateApi(): ApiTestResult =
            withContext(Dispatchers.IO) {
                try {
                    debugLogManager.log("API_TEST", "Testing Exchange Rate API key")

                    // Check if API key is available (user key overrides BuildConfig)
                    val key = keyRepository.getUserExchangeKey() ?: ""
                    if (key.isBlank()) {
                        return@withContext ApiTestResult(
                            isWorking = false,
                            message = context.getString(R.string.api_test_not_configured),
                            apiName = context.getString(R.string.api_name_exchange_rate),
                        )
                    }

                    // Make a simple test request to get USD to TWD rate
                    val testUrl = "https://v6.exchangerate-api.com/v6/$key/latest/USD"
                    val response =
                        java.net.URL(testUrl).openConnection().apply {
                            connectTimeout = 10000
                            readTimeout = 10000
                        }.getInputStream().bufferedReader().readText()

                    // Check if response contains expected data
                    if (response.contains("\"result\":\"success\"") && response.contains("\"TWD\"")) {
                        debugLogManager.log("API_TEST", "Exchange Rate API test successful")
                        ApiTestResult(
                            isWorking = true,
                            message = context.getString(R.string.api_test_working_success),
                            apiName = context.getString(R.string.api_name_exchange_rate),
                        )
                    } else {
                        debugLogManager.log("API_TEST", "Exchange Rate API test failed - invalid response")
                        ApiTestResult(
                            isWorking = false,
                            message = context.getString(R.string.api_test_error_invalid_response),
                            apiName = context.getString(R.string.api_name_exchange_rate),
                        )
                    }
                } catch (e: Exception) {
                    debugLogManager.logError("API_TEST: Exchange Rate API test failed", e)
                    ApiTestResult(
                        isWorking = false,
                        message = context.getString(R.string.api_test_error_message, e.message ?: ""),
                        apiName = context.getString(R.string.api_name_exchange_rate),
                    )
                }
            }

        /**
         * Test Exchange Rate API key with an explicitly provided key (used for validate-before-save in settings)
         */
        suspend fun testExchangeRateApiWithKey(key: String): ApiTestResult =
            withContext(Dispatchers.IO) {
                try {
                    debugLogManager.log("API_TEST", "Testing Exchange Rate API key (provided)")
                    if (key.isBlank()) {
                        return@withContext ApiTestResult(
                            false,
                            context.getString(R.string.api_test_not_configured),
                            context.getString(R.string.api_name_exchange_rate),
                        )
                    }
                    val testUrl = "https://v6.exchangerate-api.com/v6/$key/latest/USD"
                    val response =
                        java.net.URL(testUrl).openConnection().apply {
                            connectTimeout = 10000
                            readTimeout = 10000
                        }.getInputStream().bufferedReader().readText()
                    if (response.contains("\"result\":\"success\"") && response.contains("\"TWD\"")) {
                        ApiTestResult(
                            true,
                            context.getString(R.string.api_test_working_success),
                            context.getString(R.string.api_name_exchange_rate),
                        )
                    } else {
                        ApiTestResult(
                            false,
                            context.getString(R.string.api_test_error_invalid_response),
                            context.getString(R.string.api_name_exchange_rate),
                        )
                    }
                } catch (e: Exception) {
                    debugLogManager.logError("API_TEST: Exchange Rate provided key test failed", e)
                    ApiTestResult(
                        false,
                        context.getString(R.string.api_test_error_message, e.message ?: ""),
                        context.getString(R.string.api_name_exchange_rate),
                    )
                }
            }

        /**
         * Test all APIs and return combined result
         */
        suspend fun testAllApis(): List<ApiTestResult> {
            return listOf(
                testFinnhubApi(),
                testExchangeRateApi(),
            )
        }

        /**
         * Securely tests API keys (includes security status check).
         */
        suspend fun testApiKeysSecurely(): SecureApiTestResult {
            debugLogManager.log("API_TEST", "Starting secure API key testing")

            val securityStatus = secureApiKeyManager.getSecurityStatus()
            debugLogManager.log("API_TEST", "Security status: ${securityStatus.securityLevel}")

            val finnhubResult = testFinnhubApi()
            val exchangeResult = testExchangeRateApi()

            return SecureApiTestResult(
                securityStatus = securityStatus,
                finnhubResult = finnhubResult,
                exchangeResult = exchangeResult,
                overallSecurity =
                    when {
                        securityStatus.securityLevel == com.wealthmanager.ui.security.SecurityLevel.HIGH &&
                            finnhubResult.isWorking && exchangeResult.isWorking ->
                            context.getString(
                                R.string.api_test_high_security,
                            )
                        securityStatus.securityLevel == com.wealthmanager.ui.security.SecurityLevel.MEDIUM &&
                            (finnhubResult.isWorking || exchangeResult.isWorking) ->
                            context.getString(
                                R.string.api_test_medium_security,
                            )
                        else -> context.getString(R.string.api_test_low_security)
                    },
            )
        }

        /**
         * Tests key strength (without API calls).
         */
        fun testKeyStrength(
            key: String,
            keyType: String,
        ): com.wealthmanager.security.KeyValidationResult {
            return secureApiKeyManager.validateKeyStrength(key, keyType)
        }
    }

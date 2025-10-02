package com.wealthmanager.data.service

import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simple API provider test class
 * Used to verify if Finnhub fallback functionality works properly
 */
@Singleton
class ApiProviderTest @Inject constructor(
    private val apiProviderService: ApiProviderService,
    private val debugLogManager: DebugLogManager
) {

    /**
     * Test stock quote functionality
     */
    suspend fun testStockQuote(symbol: String = "AAPL"): Boolean {
        return try {
            debugLogManager.log("API_TEST", "Testing stock quote for $symbol")
            val result = apiProviderService.getStockQuote(symbol)

            if (result.isSuccess) {
                val quoteData = result.getOrThrow()
                debugLogManager.log("API_TEST", "Stock quote test passed: ${quoteData.symbol} = ${quoteData.price} (Provider: ${quoteData.provider})")
                true
            } else {
                debugLogManager.logError("Stock quote test failed: ${result.exceptionOrNull()?.message}")
                false
            }
        } catch (e: Exception) {
            debugLogManager.logError("Stock quote test exception: ${e.message}", e)
            false
        }
    }

    /**
     * Test stock search functionality
     */
    suspend fun testStockSearch(query: String = "Apple"): Boolean {
        return try {
            debugLogManager.log("API_TEST", "Testing stock search for '$query'")
            var success = false

            apiProviderService.searchStocks(query, "US").collect { result ->
                when (result) {
                    is com.wealthmanager.data.model.SearchResult.Success -> {
                        debugLogManager.log("API_TEST", "Stock search test passed: Found ${result.results.size} results")
                        success = true
                    }
                    is com.wealthmanager.data.model.SearchResult.Error -> {
                        debugLogManager.logError("Stock search test failed: ${result.errorType}")
                    }
                    is com.wealthmanager.data.model.SearchResult.NoResults -> {
                        debugLogManager.log("API_TEST", "Stock search test: No results found")
                        success = true // No results also counts as success
                    }
                }
            }

            success
        } catch (e: Exception) {
            debugLogManager.logError("Stock search test exception: ${e.message}", e)
            false
        }
    }

    /**
     * Test exchange rate functionality
     */
    suspend fun testExchangeRate(): Boolean {
        return try {
            debugLogManager.log("API_TEST", "Testing exchange rate")
            val result = apiProviderService.getExchangeRate("USD", "TWD")

            if (result.isSuccess) {
                val rateData = result.getOrThrow()
                debugLogManager.log("API_TEST", "Exchange rate test passed: ${rateData.fromCurrency}/${rateData.toCurrency} = ${rateData.rate} (Provider: ${rateData.provider})")
                true
            } else {
                debugLogManager.logError("Exchange rate test failed: ${result.exceptionOrNull()?.message}")
                false
            }
        } catch (e: Exception) {
            debugLogManager.logError("Exchange rate test exception: ${e.message}", e)
            false
        }
    }

    /**
     * Run all tests
     */
    suspend fun runAllTests(): Boolean {
        debugLogManager.log("API_TEST", "Starting API Provider tests...")

        val stockQuoteTest = testStockQuote()
        val stockSearchTest = testStockSearch()
        val exchangeRateTest = testExchangeRate()

        val allPassed = stockQuoteTest && stockSearchTest && exchangeRateTest

        debugLogManager.log("API_TEST", "API Provider tests completed. Results:")
        debugLogManager.log("API_TEST", "- Stock Quote: ${if (stockQuoteTest) "PASS" else "FAIL"}")
        debugLogManager.log("API_TEST", "- Stock Search: ${if (stockSearchTest) "PASS" else "FAIL"}")
        debugLogManager.log("API_TEST", "- Exchange Rate: ${if (exchangeRateTest) "PASS" else "FAIL"}")
        debugLogManager.log("API_TEST", "- Overall: ${if (allPassed) "PASS" else "FAIL"}")

        return allPassed
    }
}

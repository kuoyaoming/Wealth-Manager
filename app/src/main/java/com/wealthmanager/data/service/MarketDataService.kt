package com.wealthmanager.data.service

import com.wealthmanager.data.service.ApiProviderService
import com.wealthmanager.data.service.StockQuoteData
import com.wealthmanager.data.service.ExchangeRateData
import com.wealthmanager.data.service.CacheManager
import com.wealthmanager.data.service.ApiErrorHandler
import com.wealthmanager.data.service.DataValidator
import com.wealthmanager.data.service.RequestDeduplicationManager
import com.wealthmanager.data.service.ApiRetryManager
import com.wealthmanager.utils.NumberFormatter
import com.wealthmanager.data.entity.ExchangeRate
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.data.model.SearchResult
import com.wealthmanager.data.model.NoResultsReason
import com.wealthmanager.data.model.SearchErrorType
import com.wealthmanager.data.model.StockSearchItem
import com.wealthmanager.data.repository.AssetRepository
import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketDataService @Inject constructor(
    private val apiProviderService: ApiProviderService,
    private val assetRepository: AssetRepository,
    private val debugLogManager: DebugLogManager,
    private val cacheManager: CacheManager,
    private val apiErrorHandler: ApiErrorHandler,
    private val dataValidator: DataValidator,
    private val requestDeduplicationManager: RequestDeduplicationManager,
    private val apiRetryManager: ApiRetryManager,
    private val numberFormatter: NumberFormatter
) {
    
    
    suspend fun updateStockPrices() {
        try {
            debugLogManager.log("MARKET_DATA", "Starting stock price update")
            val stockAssets = assetRepository.getAllStockAssets().first()
            
            if (stockAssets.isEmpty()) {
                debugLogManager.log("MARKET_DATA", "No stock assets to update")
                return
            }
            
            for (stock in stockAssets) {
                try {
                    val result = apiRetryManager.executeWithFallback(
                        operation = {
                            val quoteResult = apiProviderService.getStockQuote(stock.symbol)
                            
                            if (quoteResult.isSuccess) {
                                val quoteData = quoteResult.getOrThrow()
                                debugLogManager.log("MARKET_DATA", "Quote data for ${stock.symbol}: price=${quoteData.price}")
                                quoteData
                            } else {
                                throw Exception("API Provider failed: ${quoteResult.exceptionOrNull()?.message}")
                            }
                        },
                        fallbackOperation = {
                            debugLogManager.logWarning("MARKET_DATA", "API Provider failed for ${stock.symbol}, using cached data")
                            val cachedStock = runBlocking { assetRepository.getStockAssetSync(stock.symbol) }
                            if (cachedStock != null) {
                                StockQuoteData(
                                    symbol = stock.symbol,
                                    price = cachedStock.currentPrice,
                                    change = 0.0,
                                    changePercent = 0.0,
                                    volume = 0L,
                                    high = cachedStock.currentPrice,
                                    low = cachedStock.currentPrice,
                                    open = cachedStock.currentPrice,
                                    previousClose = cachedStock.currentPrice,
                                    provider = "Cached"
                                )
                            } else {
                                throw Exception("No cached data available for ${stock.symbol}")
                            }
                        },
                        operationName = "API Provider Stock Price Update for ${stock.symbol}"
                    )
                    
                    if (result.isSuccess) {
                        val quoteData = result.getOrThrow() as StockQuoteData
                        val price = quoteData.price
                        
                        val currency = if (isTaiwanStock(stock.symbol)) "TWD" else "USD"
                        
                        val twdEquivalent = calculateTwdEquivalent(price, stock.shares, currency)
                        
                        val updatedStock = stock.copy(
                            currentPrice = price,
                            twdEquivalent = twdEquivalent,
                            lastUpdated = System.currentTimeMillis()
                        )
                        
                        val validationResult = dataValidator.validateStockData(updatedStock)
                        if (validationResult is DataValidator.ValidationResult.Valid) {
                            assetRepository.updateStockAsset(updatedStock)
                            debugLogManager.log("MARKET_DATA", "Updated ${stock.symbol}: Price=$price, TWD=$twdEquivalent")
                        } else {
                            val errorReason = if (validationResult is DataValidator.ValidationResult.Invalid) {
                                validationResult.reason
                            } else {
                                "Unknown validation error"
                            }
                            debugLogManager.logWarning("MARKET_DATA", "Stock data validation failed: $errorReason")
                        }
                    } else {
                        debugLogManager.log("MARKET_DATA", "Failed to update ${stock.symbol}, keeping existing price")
                    }
                    
                } catch (e: Exception) {
                    debugLogManager.logError("Failed to update ${stock.symbol}: ${e.message}", e)
                }
            }
            
            debugLogManager.log("MARKET_DATA", "Stock price update completed")
            
        } catch (e: Exception) {
            debugLogManager.logError("Failed to update stock prices: ${e.message}", e)
        }
    }
    
    private fun validateApiResponse(response: Any, symbol: String) {
        try {
            val responseClass = response::class.java
            val errorMessageField = responseClass.getDeclaredField("errorMessage")
            errorMessageField.isAccessible = true
            val errorMessage = errorMessageField.get(response) as? String
            
            if (!errorMessage.isNullOrEmpty()) {
                val errorType = apiErrorHandler.analyzeError(Exception(errorMessage))
                val userMessage = apiErrorHandler.getUserFriendlyMessage(errorType)
                debugLogManager.logWarning("API_VALIDATION", "API Error for $symbol: $userMessage")
                throw Exception("API Error: $errorMessage")
            }
            
            val noteField = responseClass.getDeclaredField("note")
            noteField.isAccessible = true
            val note = noteField.get(response) as? String
            
            if (!note.isNullOrEmpty()) {
                debugLogManager.logWarning("API_VALIDATION", "API Note for $symbol: $note")
                throw Exception("API Note: $note")
            }
        } catch (e: Exception) {
            debugLogManager.logWarning("API_VALIDATION", "Could not validate API response: ${e.message}")
        }
    }
    
    private fun createMockGlobalQuote(stock: StockAsset): Any {
        return object {
            val price = stock.currentPrice.toString()
            val change = "0.00"
            val changePercent = "0.00%"
            val volume = "0"
            val previousClose = stock.currentPrice.toString()
            val open = stock.currentPrice.toString()
            val high = stock.currentPrice.toString()
            val low = stock.currentPrice.toString()
        }
    }
    
    private fun createMockExchangeRate(cachedRate: ExchangeRate): Any {
        return object {
            val exchangeRate = cachedRate.rate.toString()
            val fromCurrencyCode = "USD"
            val toCurrencyCode = "TWD"
            val lastRefreshed = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date(cachedRate.lastUpdated))
        }
    }
    
    suspend fun updateExchangeRates() {
        try {
            debugLogManager.log("MARKET_DATA", "Starting exchange rate update")
            
            val result = apiRetryManager.executeWithFallback(
                operation = {
                    val rateResult = apiProviderService.getExchangeRate("USD", "TWD")
                    
                    if (rateResult.isSuccess) {
                        val rateData = rateResult.getOrThrow()
                        debugLogManager.log("MARKET_DATA", "Exchange rate data: USD/TWD = ${rateData.rate}")
                        rateData
                    } else {
                        throw Exception("API Provider failed: ${rateResult.exceptionOrNull()?.message}")
                    }
                },
                fallbackOperation = {
                    debugLogManager.logWarning("MARKET_DATA", "API Provider failed for exchange rate, using cached data")
                    val cachedRate = runBlocking { assetRepository.getExchangeRateSync("USD_TWD") }
                    if (cachedRate != null) {
                        ExchangeRateData(
                            fromCurrency = "USD",
                            toCurrency = "TWD",
                            rate = cachedRate.rate,
                            provider = "Cached"
                        )
                    } else {
                        throw Exception("No cached exchange rate available")
                    }
                },
                operationName = "API Provider Exchange Rate Update"
            )
            
            if (result.isSuccess) {
                val rateData = result.getOrThrow() as ExchangeRateData
                val exchangeRate = ExchangeRate(
                    currencyPair = "USD_TWD",
                    rate = rateData.rate,
                    lastUpdated = System.currentTimeMillis()
                )
                
                assetRepository.insertExchangeRate(exchangeRate)
                debugLogManager.log("MARKET_DATA", "Exchange rate updated: USD/TWD = ${rateData.rate}")
            } else {
                debugLogManager.logWarning("MARKET_DATA", "API Provider failed, using cached exchange rate")
                val cachedRate = runBlocking { assetRepository.getExchangeRateSync("USD_TWD") }
                if (cachedRate != null) {
                    debugLogManager.logWarning("MARKET_DATA", "Using cached rate: ${cachedRate.rate}")
                } else {
                    debugLogManager.logWarning("MARKET_DATA", "No cached rate available, using default 30.0")
                }
            }
            
        } catch (e: Exception) {
            debugLogManager.logError("Failed to update exchange rates: ${e.message}", e)
            val cachedRate = runBlocking { assetRepository.getExchangeRateSync("USD_TWD") }
            if (cachedRate != null) {
                debugLogManager.logWarning("MARKET_DATA", "Using cached exchange rate as fallback: ${cachedRate.rate}")
            }
        }
    }
    
    suspend fun searchStocks(query: String, market: String): Flow<SearchResult> = flow {
        try {
            debugLogManager.logMarketData("SEARCH", "Searching stocks: '$query' in market: '$market'")
            
            if (query.isBlank() || query.length < 2) {
                debugLogManager.logWarning("Invalid search query: '$query'", "MARKET_DATA")
                emit(SearchResult.NoResults(NoResultsReason.INVALID_QUERY))
                return@flow
            }
            apiProviderService.searchStocks(query, market).collect { result ->
                emit(result)
            }
            
        } catch (e: Exception) {
            debugLogManager.logMarketData("ERROR", "Search failed for '$query' in market '$market': ${e.message}")
            val errorType = analyzeException(e)
            emit(SearchResult.Error(errorType))
        }
    }
    
    private fun analyzeApiErrorMessage(errorMessage: String): SearchErrorType {
        return when {
            errorMessage.contains("limit", ignoreCase = true) || 
            errorMessage.contains("quota", ignoreCase = true) ||
            errorMessage.contains("exceeded", ignoreCase = true) -> SearchErrorType.API_LIMIT
            
            errorMessage.contains("network", ignoreCase = true) ||
            errorMessage.contains("connection", ignoreCase = true) ||
            errorMessage.contains("timeout", ignoreCase = true) -> SearchErrorType.NETWORK_ERROR
            
            errorMessage.contains("server", ignoreCase = true) ||
            errorMessage.contains("internal", ignoreCase = true) ||
            errorMessage.contains("500", ignoreCase = true) -> SearchErrorType.SERVER_ERROR
            
            errorMessage.contains("invalid", ignoreCase = true) ||
            errorMessage.contains("key", ignoreCase = true) ||
            errorMessage.contains("401", ignoreCase = true) ||
            errorMessage.contains("403", ignoreCase = true) -> SearchErrorType.INVALID_API_KEY
            
            else -> SearchErrorType.UNKNOWN_ERROR
        }
    }
    
    private fun analyzeApiNote(note: String): SearchErrorType {
        return when {
            note.contains("limit", ignoreCase = true) ||
            note.contains("quota", ignoreCase = true) ||
            note.contains("exceeded", ignoreCase = true) ||
            note.contains("daily", ignoreCase = true) -> SearchErrorType.API_LIMIT
            
            note.contains("network", ignoreCase = true) ||
            note.contains("connection", ignoreCase = true) -> SearchErrorType.NETWORK_ERROR
            
            note.contains("server", ignoreCase = true) ||
            note.contains("internal", ignoreCase = true) -> SearchErrorType.SERVER_ERROR
            
            else -> SearchErrorType.UNKNOWN_ERROR
        }
    }
    
    private fun analyzeException(exception: Exception): SearchErrorType {
        return when (exception) {
            is java.net.UnknownHostException,
            is java.io.IOException -> SearchErrorType.NETWORK_ERROR
            
            is retrofit2.HttpException -> {
                when (exception.code()) {
                    429 -> SearchErrorType.API_LIMIT
                    in 500..599 -> SearchErrorType.SERVER_ERROR
                    401, 403 -> SearchErrorType.INVALID_API_KEY
                    else -> SearchErrorType.UNKNOWN_ERROR
                }
            }
            
            else -> SearchErrorType.UNKNOWN_ERROR
        }
    }
    
    private fun determineMarketState(timezone: String): String {
        return try {
            val currentTime = System.currentTimeMillis()
            val timeZone = java.util.TimeZone.getTimeZone(timezone)
            val hour = timeZone.getOffset(currentTime) / (1000 * 60 * 60)
            
            when {
                timezone.contains("America") -> {
                    val localHour = (hour + 8) % 24
                    if (localHour in 9..16) "OPEN" else "CLOSED"
                }
                timezone.contains("Europe") -> {
                    val localHour = (hour + 8) % 24
                    if (localHour in 8..16) "OPEN" else "CLOSED"
                }
                timezone.contains("Asia") -> {
                    val localHour = (hour + 8) % 24
                    if (localHour in 9..15) "OPEN" else "CLOSED"
                }
                else -> "UNKNOWN"
            }
        } catch (e: Exception) {
            debugLogManager.logWarning("MARKET_DATA", "Failed to determine market state for timezone: $timezone")
            "UNKNOWN"
        }
    }
    
    private suspend fun calculateTwdEquivalent(
        price: Double,
        shares: Double,
        currency: String
    ): Double {
        return if (currency == "TWD") {
            price * shares
        } else {
            val exchangeRate = runBlocking { assetRepository.getExchangeRateSync("USD_TWD") }
            val rate = exchangeRate?.rate ?: 30.0
            price * shares * rate
        }
    }
    
    private fun isTaiwanStock(symbol: String): Boolean {
        return symbol.endsWith(".TW", ignoreCase = true) ||
               symbol.endsWith(".T", ignoreCase = true) ||
               symbol.matches(Regex("^\\d{4}$"))
    }
}

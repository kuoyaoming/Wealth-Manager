package com.wealthmanager.data.service

import com.wealthmanager.data.service.ApiProviderService
import com.wealthmanager.data.entity.ExchangeRate
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.data.repository.AssetRepository
import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fixed MarketDataService with corrected API calls and proper error handling
 */
@Singleton
class MarketDataServiceFixedCorrected @Inject constructor(
    private val apiProviderService: ApiProviderService,
    private val assetRepository: AssetRepository,
    private val debugLogManager: DebugLogManager
) {
    
    /**
     * Update stock prices with graceful error handling
     */
    suspend fun updateStockPrices(): Flow<Result<Unit>> = flow {
        try {
            debugLogManager.log("MARKET_DATA", "Starting stock price update")
            
            val stockAssets = assetRepository.getAllStockAssets().first()
            if (stockAssets.isEmpty()) {
                debugLogManager.log("MARKET_DATA", "No stock assets to update")
                emit(Result.success(Unit))
                return@flow
            }
            
            var successCount = 0
            var failureCount = 0
            
            for (stock in stockAssets) {
                try {
                    val result = updateStockPriceSafely(stock)
                    if (result.isSuccess) {
                        successCount++
                        debugLogManager.log("MARKET_DATA", "Successfully updated ${stock.symbol}")
                    } else {
                        failureCount++
                        debugLogManager.log("MARKET_DATA", "Failed to update ${stock.symbol}: ${result.exceptionOrNull()?.message}")
                    }
                } catch (e: Exception) {
                    failureCount++
                    debugLogManager.log("MARKET_DATA", "Exception updating ${stock.symbol}: ${e.message}")
                }
            }
            
            debugLogManager.log("MARKET_DATA", "Stock update completed: $successCount success, $failureCount failures")
            emit(Result.success(Unit))
            
        } catch (e: Exception) {
            debugLogManager.log("MARKET_DATA", "Stock update failed: ${e.message}")
            emit(Result.failure(e))
        }
    }
    
    /**
     * Update exchange rates with graceful error handling
     */
    suspend fun updateExchangeRates(): Flow<Result<Unit>> = flow {
        try {
            debugLogManager.log("MARKET_DATA", "Starting exchange rate update")
            
            val result = updateExchangeRateSafely()
            if (result.isSuccess) {
                debugLogManager.log("MARKET_DATA", "Exchange rate update successful")
                emit(Result.success(Unit))
            } else {
                debugLogManager.log("MARKET_DATA", "Exchange rate update failed: ${result.exceptionOrNull()?.message}")
                emit(Result.failure(result.exceptionOrNull() ?: Exception("Exchange rate update failed")))
            }
            
        } catch (e: Exception) {
            debugLogManager.log("MARKET_DATA", "Exchange rate update exception: ${e.message}")
            emit(Result.failure(e))
        }
    }
    
    /**
     * Safely update stock price without throwing exceptions
     */
    private suspend fun updateStockPriceSafely(stock: StockAsset): Result<Unit> {
        return try {
            val result = apiProviderService.getStockQuote(stock.symbol)
            
            if (result.isSuccess) {
                val quoteData = result.getOrThrow()
                debugLogManager.log("MARKET_DATA", "Valid price data for ${stock.symbol}: ${quoteData.price}")
                
                // Convert Int shares to Double for calculation
                val twdEquivalent = calculateTwdEquivalent(quoteData.price, stock.shares.toDouble(), "USD")
                val updatedStock = stock.copy(
                    currentPrice = quoteData.price,
                    twdEquivalent = twdEquivalent
                )
                
                assetRepository.updateStockAsset(updatedStock)
                Result.success(Unit)
            } else {
                debugLogManager.log("MARKET_DATA", "Failed to get price for ${stock.symbol}: ${result.exceptionOrNull()?.message}")
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
            
        } catch (e: Exception) {
            debugLogManager.log("MARKET_DATA", "Exception updating ${stock.symbol}: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Safely update exchange rate without throwing exceptions
     */
    private suspend fun updateExchangeRateSafely(): Result<Unit> {
        return try {
            val result = apiProviderService.getExchangeRate("USD", "TWD")
            
            if (result.isSuccess) {
                val exchangeRateData = result.getOrThrow()
                debugLogManager.log("MARKET_DATA", "Valid exchange rate: ${exchangeRateData.rate}")
                
                val exchangeRateEntity = ExchangeRate(
                    currencyPair = "USD_TWD",
                    rate = exchangeRateData.rate,
                    lastUpdated = System.currentTimeMillis()
                )
                
                assetRepository.updateExchangeRate(exchangeRateEntity)
                Result.success(Unit)
            } else {
                debugLogManager.log("MARKET_DATA", "Failed to get exchange rate: ${result.exceptionOrNull()?.message}")
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
            
        } catch (e: Exception) {
            debugLogManager.log("MARKET_DATA", "Exchange rate exception: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Calculate TWD equivalent with error handling
     */
    private fun calculateTwdEquivalent(price: Double, shares: Double, currency: String): Double {
        return try {
            when (currency) {
                "TWD" -> price * shares
                "USD" -> {
                    // Use cached exchange rate or default
                    val exchangeRate = 30.0 // Default rate, should be from cache
                    price * shares * exchangeRate
                }
                else -> price * shares * 30.0 // Default conversion
            }
        } catch (e: Exception) {
            debugLogManager.log("MARKET_DATA", "Error calculating TWD equivalent: ${e.message}")
            price * shares * 30.0 // Fallback calculation
        }
    }
    
    /**
     * Search stocks with improved error handling
     */
    suspend fun searchStocks(query: String): Flow<Result<List<StockSearchResultFixed>>> = flow {
        try {
            if (query.isBlank()) {
                emit(Result.success(emptyList()))
                return@flow
            }
            
            debugLogManager.logMarketData("SEARCH", "Searching for: $query")
            
            // Use ApiProviderService for search
            apiProviderService.searchStocks(query, "US").collect { searchResult ->
                when (searchResult) {
                    is com.wealthmanager.data.model.SearchResult.Success -> {
                        debugLogManager.logMarketData("SUCCESS", "Found ${searchResult.results.size} matches for '$query'")
                        
                        val searchResults = searchResult.results.map { item ->
                            StockSearchResultFixed(
                                symbol = item.symbol,
                                name = item.longName,
                                type = item.exchange,
                                region = item.exchange
                            )
                        }
                        
                        debugLogManager.logMarketData("COMPLETE", "Created ${searchResults.size} search results")
                        emit(Result.success(searchResults))
                    }
                    is com.wealthmanager.data.model.SearchResult.NoResults -> {
                        debugLogManager.logMarketData("NO_RESULTS", "No matches found for '$query'")
                        emit(Result.success(emptyList()))
                    }
                    is com.wealthmanager.data.model.SearchResult.Error -> {
                        debugLogManager.logMarketData("ERROR", "Search failed for '$query': ${searchResult.errorType}")
                        emit(Result.failure(Exception("Search failed: ${searchResult.errorType}")))
                    }
                }
            }
            
        } catch (e: Exception) {
            debugLogManager.logMarketData("ERROR", "Search failed for '$query': ${e.message}")
            emit(Result.failure(e))
        }
    }
}

/**
 * Data class for stock search results (fixed naming conflict)
 */
data class StockSearchResultFixed(
    val symbol: String,
    val name: String,
    val type: String,
    val region: String
)
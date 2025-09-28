package com.wealthmanager.data.service

import com.wealthmanager.data.api.MarketDataApi
import com.wealthmanager.data.entity.ExchangeRate
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.data.repository.AssetRepository
import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketDataService @Inject constructor(
    private val marketDataApi: MarketDataApi,
    private val assetRepository: AssetRepository,
    private val debugLogManager: DebugLogManager,
    private val apiRetryManager: ApiRetryManager
) {
    
    companion object {
        private const val ALPHA_VANTAGE_API_KEY = "ZHQ6865SM7I0IMML"
    }
    
    suspend fun updateStockPrices() {
        try {
            debugLogManager.log("MARKET_DATA", "Starting stock price update with Alpha Vantage API")
            
            val stockAssets = assetRepository.getAllStockAssets().first()
            debugLogManager.log("MARKET_DATA", "Found ${stockAssets.size} stock assets to update")
            
            if (stockAssets.isEmpty()) {
                debugLogManager.log("MARKET_DATA", "No stock assets to update, skipping stock price update")
                return
            }
            
            for (stock in stockAssets) {
                try {
                    debugLogManager.log("MARKET_DATA", "Updating ${stock.symbol} (${stock.market} market)")
                    
                    val result = apiRetryManager.executeWithRetry(
                        operation = {
                            debugLogManager.log("MARKET_DATA", "Alpha Vantage API Request - Symbol: ${stock.symbol}")
                            
                            val response = marketDataApi.getStockQuote("GLOBAL_QUOTE", stock.symbol, ALPHA_VANTAGE_API_KEY)
                            
                            debugLogManager.log("MARKET_DATA", "Alpha Vantage API Response received for ${stock.symbol}")
                            
                            // Check for API errors
                            if (response.errorMessage != null) {
                                throw Exception("Alpha Vantage API Error: ${response.errorMessage}")
                            }
                            
                            if (response.note != null) {
                                throw Exception("Alpha Vantage API Note: ${response.note}")
                            }
                            
                            val globalQuote = response.globalQuote
                            if (globalQuote != null && globalQuote.price.isNotEmpty()) {
                                val price = globalQuote.price.toDoubleOrNull()
                                if (price != null && price > 0) {
                                    debugLogManager.log("MARKET_DATA", "Quote data for ${stock.symbol}: price=$price")
                                    globalQuote
                                } else {
                                    throw Exception("Invalid price data for ${stock.symbol}: ${globalQuote.price}")
                                }
                            } else {
                                throw Exception("No valid price data for ${stock.symbol}")
                            }
                        },
                        operationName = "Alpha Vantage Stock Price Update for ${stock.symbol}"
                    )
                    
                    if (result.isSuccess) {
                        val quoteData = result.getOrThrow()
                        val price = quoteData.price.toDouble()
                        val currency = "USD" // Alpha Vantage typically returns USD prices
                        
                        val twdEquivalent = calculateTwdEquivalent(price, stock.shares, currency)
                        
                        val updatedStock = stock.copy(
                            currentPrice = price,
                            twdEquivalent = twdEquivalent,
                            lastUpdated = System.currentTimeMillis()
                        )
                        
                        assetRepository.updateStockAsset(updatedStock)
                        debugLogManager.log("MARKET_DATA", "Updated ${stock.symbol}: Price=$price, TWD=$twdEquivalent")
                    } else {
                        debugLogManager.log("MARKET_DATA", "Failed to update ${stock.symbol}, keeping existing price")
                    }
                    
                } catch (e: Exception) {
                    debugLogManager.logError("Failed to update ${stock.symbol}: ${e.message}", e)
                }
            }
            
            debugLogManager.log("MARKET_DATA", "Stock price update completed for ${stockAssets.size} stocks")
            
        } catch (e: Exception) {
            debugLogManager.logError("Failed to update stock prices: ${e.message}", e)
        }
    }
    
    suspend fun updateExchangeRates() {
        try {
            debugLogManager.log("MARKET_DATA", "Starting exchange rate update with Alpha Vantage API")
            
            val result = apiRetryManager.executeWithRetry(
                operation = {
                    debugLogManager.log("MARKET_DATA", "Alpha Vantage API Request - Exchange Rate: USD to TWD")
                    val response = marketDataApi.getExchangeRate("CURRENCY_EXCHANGE_RATE", "USD", "TWD", ALPHA_VANTAGE_API_KEY)
                    
                    debugLogManager.log("MARKET_DATA", "Alpha Vantage API Response received for exchange rate")
                    
                    // Check for API errors
                    if (response.errorMessage != null) {
                        throw Exception("Alpha Vantage API Error: ${response.errorMessage}")
                    }
                    
                    if (response.note != null) {
                        throw Exception("Alpha Vantage API Note: ${response.note}")
                    }
                    
                    val exchangeRate = response.exchangeRate
                    if (exchangeRate != null && exchangeRate.exchangeRate.isNotEmpty()) {
                        val rate = exchangeRate.exchangeRate.toDoubleOrNull()
                        if (rate != null && rate > 0) {
                            debugLogManager.log("MARKET_DATA", "Exchange rate data: USD/TWD = $rate")
                            exchangeRate
                        } else {
                            throw Exception("Invalid exchange rate data: ${exchangeRate.exchangeRate}")
                        }
                    } else {
                        throw Exception("No valid exchange rate data received")
                    }
                },
                operationName = "Alpha Vantage Exchange Rate Update"
            )
            
            if (result.isSuccess) {
                val rateData = result.getOrThrow()
                val rate = rateData.exchangeRate.toDouble()
                val exchangeRate = ExchangeRate(
                    currencyPair = "USD_TWD",
                    rate = rate,
                    lastUpdated = System.currentTimeMillis()
                )
                
                assetRepository.insertExchangeRate(exchangeRate)
                debugLogManager.log("MARKET_DATA", "Exchange rate updated: USD/TWD = $rate")
            } else {
                // Fallback to cached data
                debugLogManager.logWarning("MARKET_DATA", "Alpha Vantage API failed, using cached exchange rate")
                val cachedRate = assetRepository.getExchangeRateSync("USD_TWD")
                if (cachedRate != null) {
                    debugLogManager.logWarning("MARKET_DATA", "Using cached rate: ${cachedRate.rate} (last updated: ${cachedRate.lastUpdated})")
                    // Update API status to indicate stale data
                    // Note: This would need ApiStatusManager injection to work properly
                } else {
                    debugLogManager.logWarning("MARKET_DATA", "No cached rate available, using default 30.0")
                }
            }
            
        } catch (e: Exception) {
            debugLogManager.logError("Failed to update exchange rates: ${e.message}", e)
            // Try to use cached data as fallback
            val cachedRate = assetRepository.getExchangeRateSync("USD_TWD")
            if (cachedRate != null) {
                debugLogManager.logWarning("MARKET_DATA", "Using cached exchange rate as fallback: ${cachedRate.rate}")
            }
        }
    }
    
    suspend fun searchStocks(query: String, market: String): List<StockSearchItem> {
        return try {
            debugLogManager.log("MARKET_DATA", "=== STARTING ALPHA VANTAGE STOCK SEARCH ===")
            debugLogManager.log("MARKET_DATA", "Searching stocks: '$query' in market: '$market'")
            
            // Log API request details
            debugLogManager.log("MARKET_DATA", "Alpha Vantage API Request - Query: $query")
            debugLogManager.log("MARKET_DATA", "API URL: https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=$query&apikey=$ALPHA_VANTAGE_API_KEY")
            
            val response = marketDataApi.searchStocks("SYMBOL_SEARCH", query, ALPHA_VANTAGE_API_KEY)
            
            // Log full API response
            debugLogManager.log("MARKET_DATA", "Alpha Vantage API Response received - Status: Success")
            
            // Check for API errors
            if (response.errorMessage != null) {
                throw Exception("Alpha Vantage API Error: ${response.errorMessage}")
            }
            
            if (response.note != null) {
                throw Exception("Alpha Vantage API Note: ${response.note}")
            }
            
            val bestMatches = response.bestMatches
            debugLogManager.log("MARKET_DATA", "API Response - Raw matches count: ${bestMatches?.size ?: 0}")
            
            if (bestMatches != null) {
                // Log each match for debugging with Alpha Vantage specific fields
                bestMatches.forEachIndexed { index, match ->
                    debugLogManager.log("MARKET_DATA", "Match $index: symbol=${match.symbol}, name=${match.name}, type=${match.type}, region=${match.region}, currency=${match.currency}, matchScore=${match.matchScore}")
                }
                
                // Sort by match score (higher is better) and take top results
                val sortedMatches = bestMatches.sortedByDescending { match ->
                    match.matchScore.toDoubleOrNull() ?: 0.0
                }.take(10) // Limit to top 10 results
                
                val searchResults = sortedMatches.map { match ->
                    // Parse company name to extract short and long names
                    val companyName = match.name
                    val shortName = if (companyName.length > 30) {
                        companyName.substring(0, 30) + "..."
                    } else {
                        companyName
                    }
                    
                    // Use type and region to determine exchange
                    val exchange = when {
                        match.region.contains("United States") -> "NASDAQ/NYSE"
                        match.region.contains("Europe") -> "LSE/EPA"
                        match.region.contains("Asia") -> "TSE/HKEX"
                        else -> match.region
                    }
                    
                    // Determine market state based on timezone and current time
                    val marketState = determineMarketState(match.timezone)
                    
                    StockSearchItem(
                        symbol = match.symbol,
                        shortName = shortName,
                        longName = companyName,
                        exchange = exchange,
                        marketState = marketState
                    )
                }
                
                debugLogManager.log("MARKET_DATA", "Stock search completed: ${searchResults.size} results processed")
                debugLogManager.log("MARKET_DATA", "=== ALPHA VANTAGE STOCK SEARCH COMPLETED ===")
                searchResults
            } else {
                debugLogManager.log("MARKET_DATA", "No matches found for query: $query")
                emptyList()
            }
            
        } catch (e: Exception) {
            debugLogManager.logError("=== ALPHA VANTAGE STOCK SEARCH FAILED ===", e)
            debugLogManager.logError("Failed to search stocks: ${e.message}", e)
            debugLogManager.log("MARKET_DATA", "Search failed for query: '$query', market: '$market'")
            debugLogManager.log("MARKET_DATA", "Exception type: ${e::class.simpleName}")
            debugLogManager.log("MARKET_DATA", "Exception message: ${e.message}")
            emptyList()
        }
    }
    
    private fun determineMarketState(timezone: String): String {
        return try {
            val currentTime = System.currentTimeMillis()
            val timeZone = java.util.TimeZone.getTimeZone(timezone)
            val hour = timeZone.getOffset(currentTime) / (1000 * 60 * 60)
            
            // Simple market state determination based on timezone
            when {
                timezone.contains("America") -> {
                    val localHour = (hour + 8) % 24 // Convert to local time
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
        shares: Int,
        currency: String
    ): Double {
        debugLogManager.log("MARKET_DATA", "Calculating TWD equivalent: $price * $shares in $currency")
        
        return if (currency == "TWD") {
            val result = price * shares
            debugLogManager.log("MARKET_DATA", "TWD calculation: $result (already in TWD)")
            result
        } else {
            // Get USD to TWD exchange rate
            val exchangeRate = assetRepository.getExchangeRateSync("USD_TWD")
            val rate = exchangeRate?.rate ?: 30.0
            val result = price * shares * rate
            debugLogManager.log("MARKET_DATA", "USD calculation: $result (rate: $rate)")
            result
        }
    }
}

data class StockSearchItem(
    val symbol: String,
    val shortName: String,
    val longName: String,
    val exchange: String,
    val marketState: String
)
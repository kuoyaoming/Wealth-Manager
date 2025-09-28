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
    
    suspend fun updateStockPrices() {
        try {
            debugLogManager.log("MARKET_DATA", "Starting stock price update")
            
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
                            val region = if (stock.market == "TW") "TW" else "US"
                            debugLogManager.log("MARKET_DATA", "API Request - Symbol: ${stock.symbol}, Region: $region")
                            
                            val response = marketDataApi.getStockQuote(stock.symbol, region)
                            
                            debugLogManager.log("MARKET_DATA", "API Response received for ${stock.symbol}")
                            debugLogManager.log("MARKET_DATA", "Response result count: ${response.quoteResponse.result?.size ?: 0}")
                            
                            val result = response.quoteResponse.result?.firstOrNull()
                            if (result != null && result.regularMarketPrice != null) {
                                debugLogManager.log("MARKET_DATA", "Quote data for ${stock.symbol}: price=${result.regularMarketPrice}, currency=${result.currency}")
                                result
                            } else {
                                throw Exception("No valid price data for ${stock.symbol}")
                            }
                        },
                        operationName = "Stock Price Update for ${stock.symbol}"
                    )
                    
                    if (result.isSuccess) {
                        val quoteData = result.getOrThrow()
                        val twdEquivalent = calculateTwdEquivalent(
                            quoteData.regularMarketPrice!!,
                            stock.shares,
                            quoteData.currency ?: "USD"
                        )
                        
                        val updatedStock = stock.copy(
                            currentPrice = quoteData.regularMarketPrice,
                            twdEquivalent = twdEquivalent,
                            lastUpdated = System.currentTimeMillis()
                        )
                        
                        assetRepository.updateStockAsset(updatedStock)
                        debugLogManager.log("MARKET_DATA", "Updated ${stock.symbol}: Price=${quoteData.regularMarketPrice}, TWD=${twdEquivalent}")
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
            debugLogManager.log("MARKET_DATA", "Starting exchange rate update")
            
            val result = apiRetryManager.executeWithRetry(
                operation = {
                    debugLogManager.log("MARKET_DATA", "API Request - Exchange Rate: USD=X")
                    val response = marketDataApi.getExchangeRate("USD=X")
                    
                    debugLogManager.log("MARKET_DATA", "API Response received for exchange rate")
                    debugLogManager.log("MARKET_DATA", "Response result count: ${response.quoteResponse.result?.size ?: 0}")
                    
                    val result = response.quoteResponse.result?.firstOrNull()
                    if (result != null && result.regularMarketPrice != null) {
                        debugLogManager.log("MARKET_DATA", "Exchange rate data: price=${result.regularMarketPrice}, currency=${result.currency}")
                        result
                    } else {
                        throw Exception("No valid exchange rate data received")
                    }
                },
                operationName = "Exchange Rate Update"
            )
            
            if (result.isSuccess) {
                val rateData = result.getOrThrow()
                val exchangeRate = ExchangeRate(
                    currencyPair = "USD_TWD",
                    rate = rateData.regularMarketPrice!!,
                    lastUpdated = System.currentTimeMillis()
                )
                
                assetRepository.insertExchangeRate(exchangeRate)
                debugLogManager.log("MARKET_DATA", "Exchange rate updated: USD/TWD = ${rateData.regularMarketPrice}")
            } else {
                // Fallback to cached data
                debugLogManager.log("MARKET_DATA", "API failed, using cached exchange rate")
                val cachedRate = assetRepository.getExchangeRateSync("USD_TWD")
                if (cachedRate != null) {
                    debugLogManager.log("MARKET_DATA", "Using cached rate: ${cachedRate.rate} (last updated: ${cachedRate.lastUpdated})")
                } else {
                    debugLogManager.log("MARKET_DATA", "No cached rate available, using default 30.0")
                }
            }
            
        } catch (e: Exception) {
            debugLogManager.logError("Failed to update exchange rates: ${e.message}", e)
            // Try to use cached data as fallback
            val cachedRate = assetRepository.getExchangeRateSync("USD_TWD")
            if (cachedRate != null) {
                debugLogManager.log("MARKET_DATA", "Using cached exchange rate as fallback: ${cachedRate.rate}")
            }
        }
    }
    
    suspend fun searchStocks(query: String, market: String): List<StockSearchItem> {
        return try {
            debugLogManager.log("MARKET_DATA", "=== STARTING STOCK SEARCH ===")
            debugLogManager.log("MARKET_DATA", "Searching stocks: '$query' in market: '$market'")
            
            val region = if (market == "TW") "TW" else "US"
            debugLogManager.log("MARKET_DATA", "Using region: $region for search")
            
            // Log API request details
            debugLogManager.log("MARKET_DATA", "API Request - Query: $query, Region: $region")
            debugLogManager.log("MARKET_DATA", "API URL: https://query1.finance.yahoo.com/v1/finance/search?q=$query&region=$region")
            
            val response = marketDataApi.searchStocks(query, region)
            
            // Log full API response
            debugLogManager.log("MARKET_DATA", "API Response received - Status: Success")
            debugLogManager.log("MARKET_DATA", "API Response - Raw quotes count: ${response.quotes.size}")
            
            // Log each quote for debugging
            response.quotes.forEachIndexed { index, quote ->
                debugLogManager.log("MARKET_DATA", "Quote $index: symbol=${quote.symbol}, shortName=${quote.shortName}, longName=${quote.longName}")
            }
            
            val searchResults = response.quotes.map { quote ->
                StockSearchItem(
                    symbol = quote.symbol,
                    shortName = quote.shortName ?: "",
                    longName = quote.longName ?: "",
                    exchange = quote.exchange ?: "",
                    marketState = quote.marketState ?: ""
                )
            }
            
            debugLogManager.log("MARKET_DATA", "Stock search completed: ${searchResults.size} results processed")
            debugLogManager.log("MARKET_DATA", "=== STOCK SEARCH COMPLETED ===")
            searchResults
            
        } catch (e: Exception) {
            debugLogManager.logError("=== STOCK SEARCH FAILED ===", e)
            debugLogManager.logError("Failed to search stocks: ${e.message}", e)
            debugLogManager.log("MARKET_DATA", "Search failed for query: '$query', market: '$market'")
            debugLogManager.log("MARKET_DATA", "Exception type: ${e::class.simpleName}")
            debugLogManager.log("MARKET_DATA", "Exception message: ${e.message}")
            emptyList()
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
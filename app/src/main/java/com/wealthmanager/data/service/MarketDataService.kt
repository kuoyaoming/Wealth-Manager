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
    private val debugLogManager: DebugLogManager
) {
    
    suspend fun updateStockPrices() {
        try {
            debugLogManager.log("MARKET_DATA", "Starting stock price update")
            
            val stockAssets = assetRepository.getAllStockAssets().first()
            debugLogManager.log("MARKET_DATA", "Found ${stockAssets.size} stock assets to update")
            
            for (stock in stockAssets) {
                try {
                    debugLogManager.log("MARKET_DATA", "Updating ${stock.symbol} (${stock.market} market)")
                    val region = if (stock.market == "TW") "TW" else "US"
                    
                    // Log API request details
                    debugLogManager.log("MARKET_DATA", "API Request - Symbol: ${stock.symbol}, Region: $region")
                    
                    val response = marketDataApi.getStockQuote(stock.symbol, region)
                    
                    // Log full API response
                    debugLogManager.log("MARKET_DATA", "API Response received for ${stock.symbol}")
                    debugLogManager.log("MARKET_DATA", "Response result count: ${response.quoteResponse.result?.size ?: 0}")
                    
                    val result = response.quoteResponse.result?.firstOrNull()
                    if (result != null) {
                        debugLogManager.log("MARKET_DATA", "Quote data for ${stock.symbol}: price=${result.regularMarketPrice}, currency=${result.currency}")
                        
                        if (result.regularMarketPrice != null) {
                            val twdEquivalent = calculateTwdEquivalent(
                                result.regularMarketPrice,
                                stock.shares,
                                result.currency ?: "USD"
                            )
                            
                            val updatedStock = stock.copy(
                                currentPrice = result.regularMarketPrice,
                                twdEquivalent = twdEquivalent,
                                lastUpdated = System.currentTimeMillis()
                            )
                            
                            assetRepository.updateStockAsset(updatedStock)
                            debugLogManager.log("MARKET_DATA", "Updated ${stock.symbol}: Price=${result.regularMarketPrice}, TWD=${twdEquivalent}")
                        } else {
                            debugLogManager.logError("No price data for ${stock.symbol}")
                        }
                    } else {
                        debugLogManager.logError("No market data found for ${stock.symbol}")
                        debugLogManager.log("MARKET_DATA", "API returned empty result for ${stock.symbol}")
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
            
            // Log API request details
            debugLogManager.log("MARKET_DATA", "API Request - Exchange Rate: USD=X")
            
            val response = marketDataApi.getExchangeRate("USD=X")
            
            // Log full API response
            debugLogManager.log("MARKET_DATA", "API Response received for exchange rate")
            debugLogManager.log("MARKET_DATA", "Response result count: ${response.quoteResponse.result?.size ?: 0}")
            
            val result = response.quoteResponse.result?.firstOrNull()
            
            if (result != null) {
                debugLogManager.log("MARKET_DATA", "Exchange rate data: price=${result.regularMarketPrice}, currency=${result.currency}")
                
                if (result.regularMarketPrice != null) {
                    val exchangeRate = ExchangeRate(
                        currencyPair = "USD_TWD",
                        rate = result.regularMarketPrice,
                        lastUpdated = System.currentTimeMillis()
                    )
                    
                    assetRepository.insertExchangeRate(exchangeRate)
                    debugLogManager.log("MARKET_DATA", "Exchange rate updated: USD/TWD = ${result.regularMarketPrice}")
                } else {
                    debugLogManager.logError("No exchange rate price data found")
                }
            } else {
                debugLogManager.logError("No exchange rate data found")
                debugLogManager.log("MARKET_DATA", "API returned empty result for exchange rate")
            }
            
        } catch (e: Exception) {
            debugLogManager.logError("Failed to update exchange rates: ${e.message}", e)
        }
    }
    
    suspend fun searchStocks(query: String, market: String): List<StockSearchItem> {
        return try {
            debugLogManager.log("MARKET_DATA", "Searching stocks: '$query' in market: '$market'")
            
            val region = if (market == "TW") "TW" else "US"
            debugLogManager.log("MARKET_DATA", "Using region: $region for search")
            
            // Log API request details
            debugLogManager.log("MARKET_DATA", "API Request - Query: $query, Region: $region")
            
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
            searchResults
            
        } catch (e: Exception) {
            debugLogManager.logError("Failed to search stocks: ${e.message}", e)
            debugLogManager.log("MARKET_DATA", "Search failed for query: '$query', market: '$market'")
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
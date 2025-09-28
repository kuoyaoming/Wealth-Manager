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
            
            for (stock in stockAssets) {
                try {
                    val region = if (stock.market == "TW") "TW" else "US"
                    val response = marketDataApi.getStockQuote(stock.symbol, region)
                    
                    val updatedStock = stock.copy(
                        currentPrice = response.regularMarketPrice,
                        twdEquivalent = calculateTwdEquivalent(
                            response.regularMarketPrice,
                            stock.shares,
                            response.currency
                        ),
                        lastUpdated = System.currentTimeMillis()
                    )
                    
                    assetRepository.updateStockAsset(updatedStock)
                    debugLogManager.log("MARKET_DATA", "Updated ${stock.symbol}: ${response.regularMarketPrice}")
                    
                } catch (e: Exception) {
                    debugLogManager.logError("Failed to update ${stock.symbol}: ${e.message}")
                }
            }
            
            debugLogManager.log("MARKET_DATA", "Stock price update completed")
            
        } catch (e: Exception) {
            debugLogManager.logError("Failed to update stock prices: ${e.message}")
        }
    }
    
    suspend fun updateExchangeRates() {
        try {
            debugLogManager.log("MARKET_DATA", "Starting exchange rate update")
            
            val response = marketDataApi.getExchangeRate("USD", "TWD")
            
            val exchangeRate = ExchangeRate(
                currencyPair = "USD_TWD",
                rate = response.rate,
                lastUpdated = System.currentTimeMillis()
            )
            
            assetRepository.insertExchangeRate(exchangeRate)
            debugLogManager.log("MARKET_DATA", "Updated USD/TWD rate: ${response.rate}")
            
        } catch (e: Exception) {
            debugLogManager.logError("Failed to update exchange rates: ${e.message}")
        }
    }
    
    suspend fun searchStocks(query: String, market: String): List<StockSearchItem> {
        return try {
            debugLogManager.log("MARKET_DATA", "Searching stocks: $query in $market")
            
            val region = if (market == "TW") "TW" else "US"
            val response = marketDataApi.searchStocks(query, region)
            
            debugLogManager.log("MARKET_DATA", "Found ${response.quotes.size} stocks")
            response.quotes.map { quote ->
                StockSearchItem(
                    symbol = quote.symbol,
                    shortName = quote.shortName,
                    longName = quote.longName,
                    exchange = quote.exchange,
                    marketState = quote.marketState
                )
            }
            
        } catch (e: Exception) {
            debugLogManager.logError("Failed to search stocks: ${e.message}")
            emptyList()
        }
    }
    
    private suspend fun calculateTwdEquivalent(
        price: Double,
        shares: Int,
        currency: String
    ): Double {
        return if (currency == "TWD") {
            price * shares
        } else {
            // Get USD to TWD exchange rate
            val exchangeRate = assetRepository.getExchangeRateSync("USD_TWD")
            price * shares * (exchangeRate?.rate ?: 30.0)
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
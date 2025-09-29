package com.wealthmanager.data.service

import com.wealthmanager.data.api.FinnhubApi
import com.wealthmanager.data.api.FinnhubQuoteResponse
import com.wealthmanager.data.api.FinnhubSearchResponse
import com.wealthmanager.data.api.FinnhubExchangeResponse
import com.wealthmanager.data.api.TwseApi
import com.wealthmanager.data.api.TwseStockData
import com.wealthmanager.data.api.ExchangeRateApi
import com.wealthmanager.data.api.ExchangeRateResponse
import com.wealthmanager.data.model.SearchResult
import com.wealthmanager.data.model.StockSearchItem
import com.wealthmanager.data.model.NoResultsReason
import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiProviderService @Inject constructor(
    private val finnhubApi: FinnhubApi,
    private val twseApi: TwseApi,
    private val exchangeRateApi: ExchangeRateApi,
    private val twseDataParser: TwseDataParser,
    private val debugLogManager: DebugLogManager
) {
    
    companion object {
        private const val FINNHUB_API_KEY = "d3d1ge9r01qmnfgfn4lgd3d1ge9r01qmnfgfn4m0"
        private const val EXCHANGE_RATE_API_KEY = "7b5a247a5c1690934ff0b6a4"
    }
    
    /**
     * 獲取股票報價，根據市場選擇API
     */
    suspend fun getStockQuote(symbol: String): Result<StockQuoteData> {
        return if (isTaiwanStock(symbol)) {
            tryTwseQuote(symbol)
        } else {
            tryFinnhubQuote(symbol)
        }
    }
    
    /**
     * 搜尋股票
     */
    suspend fun searchStocks(query: String, market: String): Flow<SearchResult> = flow {
        try {
            debugLogManager.log("API_PROVIDER", "Searching stocks: '$query' in market: '$market'")
            val response = finnhubApi.searchStocks(query, FINNHUB_API_KEY)
            emit(processFinnhubSearchResults(response.result))
        } catch (e: Exception) {
            debugLogManager.logError("Finnhub search failed for '$query': ${e.message}", e)
            emit(SearchResult.Error(com.wealthmanager.data.model.SearchErrorType.NETWORK_ERROR))
        }
    }
    
    /**
     * 獲取匯率
     */
    suspend fun getExchangeRate(fromCurrency: String = "USD", toCurrency: String = "TWD"): Result<ExchangeRateData> {
        return tryExchangeRateApi(fromCurrency, toCurrency)
    }
    
    /**
     * 嘗試Finnhub美股報價
     */
    private suspend fun tryFinnhubQuote(symbol: String): Result<StockQuoteData> {
        return try {
            debugLogManager.log("API_PROVIDER", "Getting stock quote for $symbol")
            val response = finnhubApi.getStockQuote(symbol, FINNHUB_API_KEY)
            
            Result.success(StockQuoteData(
                symbol = symbol,
                price = response.c,
                change = response.d,
                changePercent = response.dp,
                volume = 0L, // Finnhub quote doesn't include volume
                high = response.h,
                low = response.l,
                open = response.o,
                previousClose = response.pc,
                provider = "Finnhub"
            ))
        } catch (e: Exception) {
            debugLogManager.logError("Finnhub API failed for $symbol: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * 嘗試TWSE台股報價
     */
    private suspend fun tryTwseQuote(symbol: String): Result<StockQuoteData> {
        return try {
            debugLogManager.log("API_PROVIDER", "Getting stock quote for $symbol")
            debugLogManager.log("API_PROVIDER", "Taiwan stock detected: $symbol")
            
            // Clean Taiwan stock code format
            val cleanSymbol = twseDataParser.cleanTaiwanStockSymbol(symbol)
            
            // Use STOCK_DAY_ALL endpoint to get all stock data
            val response = twseApi.getAllStockPrices()
            
            if (twseDataParser.validateTwseResponse(response)) {
                // Find specific stock from all stock data
                val stockData = twseDataParser.findStockFromAllData(response, cleanSymbol)
                
                if (stockData != null) {
                    Result.success(StockQuoteData(
                        symbol = symbol,
                        price = stockData.close.toDouble(),
                        change = stockData.change.toDouble(),
                        changePercent = stockData.changePercent.toDouble(),
                        volume = stockData.tradeVolume.toLong(),
                        high = stockData.high.toDouble(),
                        low = stockData.low.toDouble(),
                        open = stockData.open.toDouble(),
                        previousClose = (stockData.close.toDouble() - stockData.change.toDouble()),
                        provider = "TWSE"
                    ))
                } else {
                    Result.failure(Exception("TWSE API: Stock $symbol not found in today's data"))
                }
            } else {
                Result.failure(Exception("TWSE API returned no data"))
            }
        } catch (e: Exception) {
            debugLogManager.logError("TWSE API failed for $symbol: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * 嘗試ExchangeRate-API匯率
     */
    private suspend fun tryExchangeRateApi(fromCurrency: String, toCurrency: String): Result<ExchangeRateData> {
        return try {
            debugLogManager.log("API_PROVIDER", "Getting exchange rate: $fromCurrency to $toCurrency")
            val response = exchangeRateApi.getExchangeRate(EXCHANGE_RATE_API_KEY, fromCurrency)
            
            // Get exchange rate for target currency from response
            val rate = when (toCurrency) {
                "TWD" -> response.conversion_rate
                else -> response.conversion_rate
            }
            
            Result.success(ExchangeRateData(
                fromCurrency = fromCurrency,
                toCurrency = toCurrency,
                rate = rate,
                provider = "ExchangeRate-API"
            ))
        } catch (e: Exception) {
            debugLogManager.logError("ExchangeRate-API failed: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * 處理Finnhub搜尋結果
     */
    private fun processFinnhubSearchResults(results: List<com.wealthmanager.data.api.FinnhubSearchResult>): SearchResult {
        return if (results.isEmpty()) {
            SearchResult.NoResults(NoResultsReason.STOCK_NOT_FOUND)
        } else {
            val searchItems = results.map { result ->
                StockSearchItem(
                    symbol = result.symbol,
                    shortName = result.displaySymbol,
                    longName = result.description,
                    exchange = determineExchange(result.symbol),
                    marketState = "REGULAR"
                )
            }
            SearchResult.Success(searchItems)
        }
    }
    
    /**
     * 判斷交易所
     */
    private fun determineExchange(symbol: String): String {
        return when {
            symbol.endsWith(".TW", ignoreCase = true) -> "TWSE"
            symbol.endsWith(".T", ignoreCase = true) -> "TWSE"
            symbol.matches(Regex("^\\d{4}$")) -> "TWSE"
            else -> "NASDAQ"
        }
    }
    
    /**
     * 判斷市場類型
     */
    private fun determineMarket(symbol: String): String {
        return when {
            symbol.endsWith(".TW", ignoreCase = true) -> "TW"
            symbol.endsWith(".T", ignoreCase = true) -> "TW"
            symbol.matches(Regex("^\\d{4}$")) -> "TW"
            else -> "US"
        }
    }
    
    /**
     * 判斷是否為台股
     */
    private fun isTaiwanStock(symbol: String): Boolean {
        return symbol.endsWith(".TW", ignoreCase = true) ||
               symbol.endsWith(".T", ignoreCase = true) ||
               symbol.matches(Regex("^\\d{4}$"))
    }
}

// Data models
data class StockQuoteData(
    val symbol: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val volume: Long,
    val high: Double,
    val low: Double,
    val open: Double,
    val previousClose: Double,
    val provider: String
)

data class ExchangeRateData(
    val fromCurrency: String,
    val toCurrency: String,
    val rate: Double,
    val provider: String
)
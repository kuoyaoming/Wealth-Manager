package com.wealthmanager.data.service

import com.wealthmanager.data.api.ExchangeRateApi
import com.wealthmanager.data.api.FinnhubApi
import com.wealthmanager.data.api.TwseApi
import com.wealthmanager.data.model.NoResultsReason
import com.wealthmanager.data.model.SearchResult
import com.wealthmanager.data.model.StockSearchItem
import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for providing unified access to various financial data APIs.
 *
 * This service acts as a facade for multiple API providers including:
 * - Finnhub API for US stock data
 * - TWSE API for Taiwanese stock data
 * - Exchange rate APIs for currency conversion
 *
 * It handles API key management, caching, and error handling across all providers.
 *
 * @property finnhubApi Finnhub API client for US stock data
 * @property twseApi TWSE API client for Taiwanese stock data
 * @property exchangeRateApi Exchange rate API client
 * @property twseDataParser Parser for TWSE data format
 * @property twseCacheManager Cache manager for TWSE data
 * @property debugLogManager Manager for debug logging
 */
@Singleton
class ApiProviderService
    @Inject
    constructor(
        private val finnhubApi: FinnhubApi,
        private val twseApi: TwseApi,
        private val exchangeRateApi: ExchangeRateApi,
        private val twseDataParser: TwseDataParser,
        private val twseCacheManager: TwseCacheManager,
        private val debugLogManager: DebugLogManager,
    ) {
        companion object {}

        suspend fun getStockQuote(symbol: String): Result<StockQuoteData> {
            // API key and biometric checks are removed. Proxy handles auth.
            return if (isTaiwanStock(symbol)) {
                tryTwseQuote(symbol)
            } else {
                tryFinnhubQuote(symbol)
            }
        }

        suspend fun searchStocks(
            query: String,
            market: String,
        ): Flow<SearchResult> =
            flow {
                try {
                    debugLogManager.log("API_PROVIDER", "Searching stocks: '$query' in market: '$market' via proxy")
                    // All diagnostic and key checks are removed. The proxy simplifies client logic.
                    val response = finnhubApi.searchStocks(query)
                    debugLogManager.logInfo("API_PROVIDER", "Proxy search returns ${response.result.size} results")
                    emit(processFinnhubSearchResults(response.result))
                } catch (e: Exception) {
                    debugLogManager.logError("Proxy search failed for '$query': ${e.message}", e)

                    // Error handling is simplified as the proxy provides a unified error source.
                    when {
                        e.message?.contains("503") == true || e.message?.contains("500") == true -> {
                            debugLogManager.logError("API_PROVIDER", "Proxy or upstream API server error.")
                            emit(SearchResult.Error(com.wealthmanager.data.model.SearchErrorType.SERVER_ERROR))
                        }
                        e.message?.contains("timeout") == true -> {
                            debugLogManager.logError("API_PROVIDER", "Request timeout to proxy.")
                            emit(SearchResult.Error(com.wealthmanager.data.model.SearchErrorType.NETWORK_ERROR))
                        }
                        else -> {
                            debugLogManager.logError("API_PROVIDER", "Unknown network error: ${e.message}")
                            emit(SearchResult.Error(com.wealthmanager.data.model.SearchErrorType.NETWORK_ERROR))
                        }
                    }
                }
            }

        suspend fun getExchangeRate(
            fromCurrency: String = "USD",
            toCurrency: String = "TWD",
        ): Result<ExchangeRateData> {
            // API key and biometric checks are removed.
            return tryExchangeRateApi(fromCurrency, toCurrency)
        }

        private suspend fun tryFinnhubQuote(symbol: String): Result<StockQuoteData> {
            return try {
                debugLogManager.logInfo("API_PROVIDER", "Finnhub quote request for $symbol")
                val response = finnhubApi.getStockQuote(symbol)

                Result.success(
                    StockQuoteData(
                        symbol = symbol,
                        price = response.currentPrice,
                        change = response.change,
                        changePercent = response.changePercent,
                        volume = 0L,
                        high = response.highPrice,
                        low = response.lowPrice,
                        open = response.openPrice,
                        previousClose = response.previousClosePrice,
                        provider = "Finnhub",
                    ),
                )
            } catch (e: Exception) {
                debugLogManager.logError("Finnhub API failed for $symbol: ${e.message}", e)
                Result.failure(e)
            }
        }

        private suspend fun tryTwseQuote(symbol: String): Result<StockQuoteData> {
            return try {
                debugLogManager.log("API_PROVIDER", "Getting stock quote for $symbol")
                debugLogManager.log("API_PROVIDER", "Taiwan stock detected: $symbol")

                val cleanSymbol = twseDataParser.cleanTaiwanStockSymbol(symbol)

                var response = twseCacheManager.getCachedStockData()

                if (response == null) {
                    debugLogManager.log("API_PROVIDER", "No cached TWSE data, fetching from API")
                    response = twseApi.getAllStockPrices()
                    twseCacheManager.updateCachedStockData(response)
                } else {
                    debugLogManager.log("API_PROVIDER", "Using cached TWSE data: ${twseCacheManager.getCacheStatus()}")
                }

                if (twseDataParser.validateTwseResponse(response)) {
                    val stockData = twseDataParser.findStockFromAllData(response, cleanSymbol)

                    if (stockData != null) {
                        Result.success(
                            StockQuoteData(
                                symbol = symbol,
                                price = stockData.close.toDouble(),
                                change = stockData.change.toDouble(),
                                changePercent = stockData.changePercent.toDouble(),
                                volume = stockData.tradeVolume.toLong(),
                                high = stockData.high.toDouble(),
                                low = stockData.low.toDouble(),
                                open = stockData.open.toDouble(),
                                previousClose = (stockData.close.toDouble() - stockData.change.toDouble()),
                                provider = "TWSE",
                            ),
                        )
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

        private suspend fun tryExchangeRateApi(
            fromCurrency: String,
            toCurrency: String,
        ): Result<ExchangeRateData> {
            return try {
                debugLogManager.log("API_PROVIDER", "Getting exchange rate via proxy: $fromCurrency to $toCurrency")
                val response = exchangeRateApi.getExchangeRate(fromCurrency)

                val rate = response.conversionRates[toCurrency] ?: 0.0

                debugLogManager.log("API_PROVIDER", "Proxy exchange rate $fromCurrency/$toCurrency = $rate")

                Result.success(
                    ExchangeRateData(
                        fromCurrency = fromCurrency,
                        toCurrency = toCurrency,
                        rate = rate,
                        provider = "ExchangeRate-API",
                    ),
                )
            } catch (e: Exception) {
                debugLogManager.logError("ExchangeRate-API failed: ${e.message}", e)
                Result.failure(e)
            }
        }

        private fun processFinnhubSearchResults(
            results: List<com.wealthmanager.data.api.FinnhubSearchResult>,
        ): SearchResult {
            return if (results.isEmpty()) {
                SearchResult.NoResults(NoResultsReason.STOCK_NOT_FOUND)
            } else {
                val searchItems =
                    results.map { result ->
                        StockSearchItem(
                            symbol = result.symbol,
                            shortName = result.displaySymbol,
                            longName = result.description,
                            exchange = determineExchange(result.symbol),
                            marketState = "REGULAR",
                        )
                    }
                SearchResult.Success(searchItems)
            }
        }

        private fun determineExchange(symbol: String): String {
            return when {
                symbol.endsWith(".TW", ignoreCase = true) -> "TWSE"
                symbol.endsWith(".T", ignoreCase = true) -> "TWSE"
                symbol.matches(Regex("^\\d{4}$")) -> "TWSE"
                else -> "NASDAQ"
            }
        }

        private fun determineMarket(symbol: String): String {
            return when {
                symbol.endsWith(".TW", ignoreCase = true) -> "TW"
                symbol.endsWith(".T", ignoreCase = true) -> "TW"
                symbol.matches(Regex("^\\d{4}$")) -> "TW"
                else -> "US"
            }
        }

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
    val provider: String,
)

data class ExchangeRateData(
    val fromCurrency: String,
    val toCurrency: String,
    val rate: Double,
    val provider: String,
)

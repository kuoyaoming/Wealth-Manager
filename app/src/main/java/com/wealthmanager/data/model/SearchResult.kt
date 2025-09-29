package com.wealthmanager.data.model

/**
 * Stock search result status
 */
sealed class SearchResult {
    data class Success(val results: List<StockSearchItem>) : SearchResult()
    data class NoResults(val reason: NoResultsReason) : SearchResult()
    data class Error(val errorType: SearchErrorType) : SearchResult()
}

/**
 * Reasons for no search results
 */
enum class NoResultsReason {
    STOCK_NOT_FOUND,           // Stock not found
    API_LIMIT_REACHED,         // API limit reached
    NETWORK_ERROR,             // Network error
    INVALID_QUERY,             // Invalid query
    SERVER_ERROR               // Server error
}

/**
 * Search error types
 */
enum class SearchErrorType {
    API_LIMIT,                 // API limit
    NETWORK_ERROR,             // Network error
    SERVER_ERROR,              // Server error
    INVALID_API_KEY,           // Invalid API key
    UNKNOWN_ERROR              // Unknown error
}

/**
 * Stock search item
 */
data class StockSearchItem(
    val symbol: String,
    val shortName: String,
    val longName: String,
    val exchange: String,
    val marketState: String
)

package com.wealthmanager.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wealthmanager.R
import com.wealthmanager.data.model.NoResultsReason
import com.wealthmanager.data.model.SearchErrorType
import com.wealthmanager.data.model.SearchResult

/**
 * Search result handler
 * Provides unified search result processing logic
 */
object SearchResultHandler {
    /**
     * Get user-friendly message for search results
     */
    @Composable
    fun getSearchResultMessage(searchResult: SearchResult): String {
        return when (searchResult) {
            is SearchResult.Success -> {
                if (searchResult.results.isEmpty()) {
                    stringResource(R.string.search_error_stock_not_found)
                } else {
                    "Found ${searchResult.results.size} results"
                }
            }
            is SearchResult.NoResults -> {
                when (searchResult.reason) {
                    NoResultsReason.STOCK_NOT_FOUND -> stringResource(R.string.search_error_stock_not_found)
                    NoResultsReason.API_LIMIT_REACHED -> stringResource(R.string.search_error_api_limit)
                    NoResultsReason.NETWORK_ERROR -> stringResource(R.string.search_error_network)
                    NoResultsReason.INVALID_QUERY -> stringResource(R.string.search_error_invalid_query)
                    NoResultsReason.SERVER_ERROR -> stringResource(R.string.search_error_server)
                }
            }
            is SearchResult.Error -> {
                when (searchResult.errorType) {
                    SearchErrorType.API_LIMIT -> stringResource(R.string.search_error_api_limit)
                    SearchErrorType.NETWORK_ERROR -> stringResource(R.string.search_error_network)
                    SearchErrorType.SERVER_ERROR -> stringResource(R.string.search_error_server)
                    SearchErrorType.INVALID_API_KEY -> stringResource(R.string.search_error_invalid_api_key)
                    SearchErrorType.AUTHENTICATION_ERROR -> stringResource(R.string.search_error_authentication)
                    SearchErrorType.RATE_LIMIT_ERROR -> stringResource(R.string.search_error_rate_limit)
                    SearchErrorType.UNKNOWN_ERROR -> stringResource(R.string.search_error_unknown)
                }
            }
        }
    }

    /**
     * Check if it's an error state
     */
    fun isErrorState(searchResult: SearchResult): Boolean {
        return when (searchResult) {
            is SearchResult.Success -> searchResult.results.isEmpty()
            is SearchResult.NoResults -> true
            is SearchResult.Error -> true
        }
    }

    /**
     * Check if it's an API limit error
     */
    fun isApiLimitError(searchResult: SearchResult): Boolean {
        return when (searchResult) {
            is SearchResult.NoResults -> searchResult.reason == NoResultsReason.API_LIMIT_REACHED
            is SearchResult.Error -> searchResult.errorType == SearchErrorType.API_LIMIT
            else -> false
        }
    }

    /**
     * Check if it's a stock not found error
     */
    fun isStockNotFoundError(searchResult: SearchResult): Boolean {
        return when (searchResult) {
            is SearchResult.Success -> searchResult.results.isEmpty()
            is SearchResult.NoResults -> searchResult.reason == NoResultsReason.STOCK_NOT_FOUND
            else -> false
        }
    }

    /**
     * Get search results list
     */
    fun getSearchResults(searchResult: SearchResult): List<com.wealthmanager.data.model.StockSearchItem> {
        return if (searchResult is SearchResult.Success) {
            searchResult.results
        } else {
            emptyList()
        }
    }
}

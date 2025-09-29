package com.wealthmanager.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wealthmanager.R
import com.wealthmanager.data.model.SearchResult
import com.wealthmanager.data.model.NoResultsReason
import com.wealthmanager.data.model.SearchErrorType

/**
 * 搜索結果處理器
 * 提供統一的搜索結果處理邏輯
 */
object SearchResultHandler {
    
    /**
     * 獲取搜索結果的使用者友善訊息
     */
    @Composable
    fun getSearchResultMessage(searchResult: SearchResult): String {
        return when (searchResult) {
            is SearchResult.Success -> {
                if (searchResult.results.isEmpty()) {
                    stringResource(R.string.search_error_stock_not_found)
                } else {
                    "找到 ${searchResult.results.size} 個結果"
                }
            }
            is SearchResult.NoResults -> {
                when (searchResult.reason) {
                    NoResultsReason.STOCK_NOT_FOUND -> stringResource(R.string.search_error_stock_not_found)
                    NoResultsReason.API_LIMIT_REACHED -> stringResource(R.string.search_error_api_limit_reached)
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
                    SearchErrorType.UNKNOWN_ERROR -> stringResource(R.string.search_error_unknown)
                }
            }
        }
    }
    
    /**
     * 檢查是否為錯誤狀態
     */
    fun isErrorState(searchResult: SearchResult): Boolean {
        return when (searchResult) {
            is SearchResult.Success -> searchResult.results.isEmpty()
            is SearchResult.NoResults -> true
            is SearchResult.Error -> true
        }
    }
    
    /**
     * 檢查是否為 API 限制錯誤
     */
    fun isApiLimitError(searchResult: SearchResult): Boolean {
        return when (searchResult) {
            is SearchResult.NoResults -> searchResult.reason == NoResultsReason.API_LIMIT_REACHED
            is SearchResult.Error -> searchResult.errorType == SearchErrorType.API_LIMIT
            else -> false
        }
    }
    
    /**
     * 檢查是否為股票不存在錯誤
     */
    fun isStockNotFoundError(searchResult: SearchResult): Boolean {
        return when (searchResult) {
            is SearchResult.Success -> searchResult.results.isEmpty()
            is SearchResult.NoResults -> searchResult.reason == NoResultsReason.STOCK_NOT_FOUND
            else -> false
        }
    }
    
    /**
     * 獲取搜索結果列表
     */
    fun getSearchResults(searchResult: SearchResult): List<com.wealthmanager.data.model.StockSearchItem> {
        return when (searchResult) {
            is SearchResult.Success -> searchResult.results
            else -> emptyList()
        }
    }
}

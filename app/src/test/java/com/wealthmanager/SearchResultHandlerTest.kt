package com.wealthmanager

import com.wealthmanager.data.model.NoResultsReason
import com.wealthmanager.data.model.SearchErrorType
import com.wealthmanager.data.model.SearchResult
import com.wealthmanager.data.model.StockSearchItem
import com.wealthmanager.ui.utils.SearchResultHandler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 測試搜索結果處理器
 */
class SearchResultHandlerTest {
    @Test
    fun `test isErrorState with empty success results`() {
        val searchResult = SearchResult.Success(emptyList())
        assertTrue(
            "Empty success results should be error state",
            SearchResultHandler.isErrorState(searchResult),
        )
    }

    @Test
    fun `test isErrorState with non-empty success results`() {
        val searchResult =
            SearchResult.Success(
                listOf(
                    StockSearchItem(
                        "AAPL",
                        "Apple Inc.",
                        "Apple Inc.",
                        "NASDAQ",
                        "OPEN",
                    ),
                ),
            )
        assertFalse(
            "Non-empty success results should not be error state",
            SearchResultHandler.isErrorState(searchResult),
        )
    }

    @Test
    fun `test isApiLimitError with API limit reached`() {
        val searchResult = SearchResult.NoResults(NoResultsReason.API_LIMIT_REACHED)
        assertTrue(
            "API limit reached should be API limit error",
            SearchResultHandler.isApiLimitError(searchResult),
        )
    }

    @Test
    fun `test isApiLimitError with API limit error type`() {
        val searchResult = SearchResult.Error(SearchErrorType.API_LIMIT)
        assertTrue(
            "API limit error type should be API limit error",
            SearchResultHandler.isApiLimitError(searchResult),
        )
    }

    @Test
    fun `test isStockNotFoundError with stock not found reason`() {
        val searchResult = SearchResult.NoResults(NoResultsReason.STOCK_NOT_FOUND)
        assertTrue(
            "Stock not found reason should be stock not found error",
            SearchResultHandler.isStockNotFoundError(searchResult),
        )
    }

    @Test
    fun `test isStockNotFoundError with empty success results`() {
        val searchResult = SearchResult.Success(emptyList())
        assertTrue(
            "Empty success results should be stock not found error",
            SearchResultHandler.isStockNotFoundError(searchResult),
        )
    }

    @Test
    fun `test getSearchResults with success results`() {
        val results =
            listOf(
                StockSearchItem("AAPL", "Apple Inc.", "Apple Inc.", "NASDAQ", "OPEN"),
            )
        val searchResult = SearchResult.Success(results)
        assertEquals("Should return the same results", results, SearchResultHandler.getSearchResults(searchResult))
    }

    @Test
    fun `test getSearchResults with error results`() {
        val searchResult = SearchResult.Error(SearchErrorType.UNKNOWN_ERROR)
        assertTrue(
            "Error results should return empty list",
            SearchResultHandler.getSearchResults(searchResult).isEmpty(),
        )
    }
}

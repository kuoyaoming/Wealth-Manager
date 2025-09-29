package com.wealthmanager.debug

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 測試 Logging 優化效果的類別
 * 用於驗證修復後的 logging 行為
 */
@Singleton
class LoggingOptimizationTest @Inject constructor(
    private val debugLogManager: DebugLogManager
) {
    
    /**
     * 測試市場數據 logging 優化
     */
    fun testMarketDataLogging() {
        Log.d("LoggingTest", "=== Testing Market Data Logging Optimization ===")
        
        // Test general market data logging
        debugLogManager.logMarketData("SEARCH", "Testing search: AAPL")
        debugLogManager.logMarketData("SUCCESS", "Found 5 matching results")
        debugLogManager.logMarketData("COMPLETE", "Successfully created 5 search results")
        
        // Test detailed market data logging (should be filtered out)
        debugLogManager.logMarketDataVerbose("DETAILS", "Processing 5 matches")
        debugLogManager.logMarketDataVerbose("MATCH_0", "Symbol: AAPL, Name: Apple Inc, Type: Equity, Region: United States, Score: 1.0000")
        
        // Test error cases
        debugLogManager.logMarketData("ERROR", "Search failed: Network connection error")
        debugLogManager.logMarketData("NO_RESULTS", "No matching results found")
        
        Log.d("LoggingTest", "=== Market Data Logging Test Complete ===")
    }
    
    /**
     * Test general logging optimization
     */
    fun testGeneralLogging() {
        Log.d("LoggingTest", "=== Testing General Logging Optimization ===")
        
        // Test user action logging
        debugLogManager.logUserAction("Add Asset FAB Clicked")
        debugLogManager.logUserAction("Search Button Clicked")
        
        // Test navigation logging
        debugLogManager.logNavigation("Dashboard", "Assets")
        debugLogManager.logNavigation("Assets", "Add Asset")
        
        // Test asset operation logging
        debugLogManager.logAsset("ADD", "Stock", "AAPL - Apple Inc")
        debugLogManager.logAsset("UPDATE", "Stock", "AAPL - Apple Inc")
        debugLogManager.logAsset("DELETE", "Stock", "AAPL - Apple Inc")
        
        // Test biometric logging
        debugLogManager.logBiometric("AUTHENTICATION", "指紋識別成功")
        debugLogManager.logBiometric("AUTHENTICATION", "指紋識別失敗")
        
        Log.d("LoggingTest", "=== 一般 Logging 測試完成 ===")
    }
    
    /**
     * 測試錯誤處理 logging
     */
    fun testErrorLogging() {
        Log.d("LoggingTest", "=== 測試錯誤處理 Logging ===")
        
        // Test general errors
        debugLogManager.logError("API_ERROR", "API 請求失敗")
        debugLogManager.logError("NETWORK_ERROR", "網路連線超時")
        
        // Test exception handling
        try {
            throw RuntimeException("測試異常")
        } catch (e: Exception) {
            debugLogManager.logError("測試異常處理", e)
        }
        
        Log.d("LoggingTest", "=== 錯誤處理 Logging 測試完成 ===")
    }
    
    /**
     * 執行所有測試
     */
    fun runAllTests() {
        Log.d("LoggingTest", "開始執行 Logging 優化測試...")
        
        testMarketDataLogging()
        testGeneralLogging()
        testErrorLogging()
        
        Log.d("LoggingTest", "所有 Logging 優化測試完成")
        Log.d("LoggingTest", "總共記錄了 ${debugLogManager.getLogCount()} 條 log")
    }
}

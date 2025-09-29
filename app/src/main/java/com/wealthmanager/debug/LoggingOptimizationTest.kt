package com.wealthmanager.debug

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Test class for logging optimization effects
 * Used to verify fixed logging behavior
 */
@Singleton
class LoggingOptimizationTest @Inject constructor(
    private val debugLogManager: DebugLogManager
) {
    
    /**
     * Test market data logging optimization
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
        debugLogManager.logBiometric("AUTHENTICATION", "Fingerprint authentication successful")
        debugLogManager.logBiometric("AUTHENTICATION", "Fingerprint authentication failed")
        
        Log.d("LoggingTest", "=== General Logging Test Complete ===")
    }
    
    /**
     * Test error handling logging
     */
    fun testErrorLogging() {
        Log.d("LoggingTest", "=== Testing Error Handling Logging ===")
        
        // Test general errors
        debugLogManager.logError("API_ERROR", "API request failed")
        debugLogManager.logError("NETWORK_ERROR", "Network connection timeout")
        
        // Test exception handling
        try {
            throw RuntimeException("Test exception")
        } catch (e: Exception) {
            debugLogManager.logError("Test exception handling", e)
        }
        
        Log.d("LoggingTest", "=== Error Handling Logging Test Complete ===")
    }
    
    /**
     * Run all tests
     */
    fun runAllTests() {
        Log.d("LoggingTest", "Starting logging optimization tests...")
        
        testMarketDataLogging()
        testGeneralLogging()
        testErrorLogging()
        
        Log.d("LoggingTest", "All logging optimization tests completed")
        Log.d("LoggingTest", "Total recorded ${debugLogManager.getLogCount()} logs")
    }
}

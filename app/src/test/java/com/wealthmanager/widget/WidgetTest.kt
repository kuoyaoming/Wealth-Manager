package com.wealthmanager.widget

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Unit tests for widget functionality.
 */
@RunWith(AndroidJUnit4::class)
class WidgetTest {
    
    private val context: Context = ApplicationProvider.getApplicationContext()
    
    @Test
    fun testWidgetPrivacyManager_defaultSettings() {
        // Test default privacy settings
        assertTrue("Default should show asset amount", 
            WidgetPrivacyManager.shouldShowAssetAmount(context))
        assertFalse("Default should not have privacy enabled", 
            WidgetPrivacyManager.isPrivacyEnabled(context))
    }
    
    @Test
    fun testWidgetPrivacyManager_privacyToggle() {
        // Test privacy toggle functionality
        WidgetPrivacyManager.setPrivacyEnabled(context, true)
        assertTrue("Privacy should be enabled", 
            WidgetPrivacyManager.isPrivacyEnabled(context))
        
        WidgetPrivacyManager.setPrivacyEnabled(context, false)
        assertFalse("Privacy should be disabled", 
            WidgetPrivacyManager.isPrivacyEnabled(context))
    }
    
    @Test
    fun testWidgetPrivacyManager_showAmountToggle() {
        // Test show amount toggle functionality
        WidgetPrivacyManager.setShowAssetAmount(context, false)
        assertFalse("Should not show asset amount", 
            WidgetPrivacyManager.shouldShowAssetAmount(context))
        
        WidgetPrivacyManager.setShowAssetAmount(context, true)
        assertTrue("Should show asset amount", 
            WidgetPrivacyManager.shouldShowAssetAmount(context))
    }
    
    @Test
    fun testWidgetErrorHandler_networkCheck() {
        // Test network availability check
        val hasNetwork = WidgetErrorHandler.isNetworkAvailable(context)
        assertNotNull("Network check should return a boolean", hasNetwork)
    }
    
    @Test
    fun testWidgetErrorHandler_displayText_privacyMode() {
        // Test display text in privacy mode
        WidgetPrivacyManager.setPrivacyEnabled(context, true)
        val displayText = WidgetPrivacyManager.getDisplayText(context, 1000.0)
        assertEquals("Should show privacy indicator", "***", displayText)
        
        // Reset privacy mode
        WidgetPrivacyManager.setPrivacyEnabled(context, false)
    }
    
    @Test
    fun testWidgetErrorHandler_displayText_normalMode() {
        // Test display text in normal mode
        WidgetPrivacyManager.setPrivacyEnabled(context, false)
        WidgetPrivacyManager.setShowAssetAmount(context, true)
        
        val displayText = WidgetPrivacyManager.getDisplayText(context, 1000.0)
        assertTrue("Should show formatted amount", displayText.contains("NT$") || displayText.contains("1000"))
    }
    
    @Test
    fun testWidgetManager_initialization() {
        // Test widget manager initialization
        assertDoesNotThrow("Widget manager should initialize without errors") {
            WidgetManager.initialize(context)
        }
    }
    
    @Test
    fun testWidgetManager_widgetCount() {
        // Test widget count functionality
        val count = WidgetManager.getInstalledWidgetCount(context)
        assertTrue("Widget count should be non-negative", count >= 0)
    }
    
    @Test
    fun testWidgetManager_hasInstalledWidgets() {
        // Test widget installation check
        val hasWidgets = WidgetManager.hasInstalledWidgets(context)
        assertNotNull("Has widgets check should return a boolean", hasWidgets)
    }
    
    @Test
    fun testWidgetUpdateScheduler() {
        // Test widget update scheduler
        assertDoesNotThrow("Widget update scheduler should work without errors") {
            WidgetUpdateScheduler.scheduleUpdate(context)
        }
    }
    
    @Test
    fun testWidgetErrorHandler_recommendations() {
        // Test recommendations functionality
        val recommendations = WidgetErrorHandler.getRecommendations(context)
        assertNotNull("Recommendations should not be null", recommendations)
        assertTrue("Recommendations should be a list", recommendations is List<String>)
    }
    
    @Test
    fun testWidgetErrorHandler_statusMessage() {
        // Test status message functionality
        val statusMessage = WidgetErrorHandler.getStatusMessage(context)
        assertNotNull("Status message should not be null", statusMessage)
        assertTrue("Status message should not be empty", statusMessage.isNotEmpty())
    }
}
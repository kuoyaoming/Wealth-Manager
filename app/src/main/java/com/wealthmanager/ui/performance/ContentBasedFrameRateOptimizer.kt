package com.wealthmanager.ui.performance

import android.app.Activity
import com.wealthmanager.debug.DebugLogManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Content-based frame rate optimizer
 * Automatically sets optimal frame rates based on screen content and user interactions
 */
@Singleton
class ContentBasedFrameRateOptimizer
    @Inject
    constructor(
        private val frameRateManager: ModernFrameRateManager,
        private val debugLogManager: DebugLogManager,
    ) {
        /**
         * Optimize frame rate for dashboard screen
         * Dashboard contains static content with occasional animations
         */
        fun optimizeForDashboard(activity: Activity) {
            frameRateManager.setFrameRateForContent(activity, ModernFrameRateManager.ContentType.STATIC)
            debugLogManager.log("ContentBasedFrameRateOptimizer", "Optimized for Dashboard - Static content (60Hz)")
        }

        /**
         * Optimize frame rate for assets screen
         * Assets screen has scrolling lists and interactive elements
         */
        fun optimizeForAssets(activity: Activity) {
            frameRateManager.setFrameRateForContent(activity, ModernFrameRateManager.ContentType.SCROLLING)
            debugLogManager.log("ContentBasedFrameRateOptimizer", "Optimized for Assets - Scrolling content (90Hz)")
        }

        /**
         * Optimize frame rate for settings screen
         * Settings screen is mostly static with occasional animations
         */
        fun optimizeForSettings(activity: Activity) {
            frameRateManager.setFrameRateForContent(activity, ModernFrameRateManager.ContentType.STATIC)
            debugLogManager.log("ContentBasedFrameRateOptimizer", "Optimized for Settings - Static content (60Hz)")
        }

        /**
         * Optimize frame rate for authentication screen
         * Auth screen has animations and biometric interactions
         */
        fun optimizeForAuth(activity: Activity) {
            frameRateManager.setFrameRateForContent(activity, ModernFrameRateManager.ContentType.ANIMATION)
            debugLogManager.log("ContentBasedFrameRateOptimizer", "Optimized for Auth - Animated content (120Hz)")
        }

        /**
         * Optimize frame rate for onboarding flow
         * Onboarding has smooth animations and transitions
         */
        fun optimizeForOnboarding(activity: Activity) {
            frameRateManager.setFrameRateForContent(activity, ModernFrameRateManager.ContentType.ANIMATION)
            debugLogManager.log("ContentBasedFrameRateOptimizer", "Optimized for Onboarding - Animated content (120Hz)")
        }

        /**
         * Optimize frame rate for about dialog
         * About dialog has minimal animations
         */
        fun optimizeForAboutDialog(activity: Activity) {
            frameRateManager.setFrameRateForContent(activity, ModernFrameRateManager.ContentType.STATIC)
            debugLogManager.log("ContentBasedFrameRateOptimizer", "Optimized for About Dialog - Static content (60Hz)")
        }

        /**
         * Optimize frame rate for chart interactions
         * Charts require smooth interactions and animations
         */
        fun optimizeForChartInteraction(activity: Activity) {
            frameRateManager.setFrameRateForContent(activity, ModernFrameRateManager.ContentType.ANIMATION)
            debugLogManager.log(
                "ContentBasedFrameRateOptimizer",
                "Optimized for Chart Interaction - Animated content (120Hz)",
            )
        }

        /**
         * Optimize frame rate for list scrolling
         * Lists benefit from smooth scrolling
         */
        fun optimizeForListScrolling(activity: Activity) {
            frameRateManager.setFrameRateForContent(activity, ModernFrameRateManager.ContentType.SCROLLING)
            debugLogManager.log(
                "ContentBasedFrameRateOptimizer",
                "Optimized for List Scrolling - Scrolling content (90Hz)",
            )
        }

        /**
         * Optimize frame rate for form interactions
         * Forms have minimal animations
         */
        fun optimizeForFormInteraction(activity: Activity) {
            frameRateManager.setFrameRateForContent(activity, ModernFrameRateManager.ContentType.STATIC)
            debugLogManager.log(
                "ContentBasedFrameRateOptimizer",
                "Optimized for Form Interaction - Static content (60Hz)",
            )
        }

        /**
         * Reset to default frame rate
         * Use when returning to main activity or pausing
         */
        fun resetToDefault(activity: Activity) {
            frameRateManager.setOptimalFrameRate(activity, 60f)
            debugLogManager.log("ContentBasedFrameRateOptimizer", "Reset to default frame rate (60Hz)")
        }

        /**
         * Get current optimization status
         */
        fun getCurrentOptimizationStatus(): String {
            val currentFrameRate = frameRateManager.getCurrentFrameRate()
            val contentType =
                when {
                    currentFrameRate >= 120f -> "Animation/Gaming (120Hz)"
                    currentFrameRate >= 90f -> "Scrolling (90Hz)"
                    else -> "Static (60Hz)"
                }
            return "Current: $contentType"
        }
    }

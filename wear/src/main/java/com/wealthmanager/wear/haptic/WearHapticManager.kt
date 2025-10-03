package com.wealthmanager.wear.haptic

import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Wear OS haptic feedback manager
 * Optimized for watch interactions with appropriate intensity levels
 */
class WearHapticManager {
    
    /**
     * Haptic feedback intensity levels for Wear OS
     */
    enum class WearHapticIntensity {
        LIGHT,    // Light - navigation and selection
        MEDIUM,   // Medium - general interactions
        STRONG,   // Strong - important actions
        CONFIRM,  // Confirm - critical actions
    }
    
    /**
     * Trigger haptic feedback optimized for Wear OS
     */
    fun triggerHaptic(
        view: View,
        intensity: WearHapticIntensity = WearHapticIntensity.MEDIUM
    ) {
        val hapticConstant = when (intensity) {
            WearHapticIntensity.LIGHT -> HapticFeedbackConstants.KEYBOARD_TAP
            WearHapticIntensity.MEDIUM -> HapticFeedbackConstants.VIRTUAL_KEY
            WearHapticIntensity.STRONG -> HapticFeedbackConstants.LONG_PRESS
            WearHapticIntensity.CONFIRM -> HapticFeedbackConstants.CONFIRM
        }
        
        view.performHapticFeedback(hapticConstant)
    }
    
    /**
     * Trigger vibration feedback for Wear OS
     */
    @SuppressLint("MissingPermission")
    fun triggerVibration(
        context: Context,
        intensity: WearHapticIntensity = WearHapticIntensity.MEDIUM
    ) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vibrator = vibratorManager.defaultVibrator
        
        val vibrationEffect = when (intensity) {
            WearHapticIntensity.LIGHT -> VibrationEffect.createOneShot(
                50L,
                VibrationEffect.DEFAULT_AMPLITUDE / 3
            )
            WearHapticIntensity.MEDIUM -> VibrationEffect.createOneShot(
                100L,
                VibrationEffect.DEFAULT_AMPLITUDE / 2
            )
            WearHapticIntensity.STRONG -> VibrationEffect.createOneShot(
                150L,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
            WearHapticIntensity.CONFIRM -> VibrationEffect.createWaveform(
                longArrayOf(0, 50, 25, 50),
                intArrayOf(0, 128, 0, 128),
                -1
            )
        }
        
        vibrator.vibrate(vibrationEffect)
    }
    
    /**
     * Trigger combined haptic and vibration feedback
     */
    fun triggerCombinedFeedback(
        view: View,
        context: Context,
        intensity: WearHapticIntensity = WearHapticIntensity.MEDIUM
    ) {
        triggerHaptic(view, intensity)
        triggerVibration(context, intensity)
    }
}

/**
 * Remember haptic feedback manager for Wear OS
 */
@Composable
fun rememberWearHapticManager(): WearHapticManager {
    return remember { WearHapticManager() }
}

/**
 * Remember haptic feedback manager with current View for Wear OS
 */
@Composable
fun rememberWearHapticManagerWithView(): Pair<WearHapticManager, View> {
    val hapticManager = remember { WearHapticManager() }
    val view = LocalView.current
    return remember(hapticManager, view) { hapticManager to view }
}

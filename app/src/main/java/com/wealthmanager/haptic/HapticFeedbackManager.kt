package com.wealthmanager.haptic

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.VibrationEffect
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Haptic feedback manager
 * Provides haptic and sound feedback with multiple intensity levels
 */
@Singleton
class HapticFeedbackManager
    @Inject
    constructor() {
        /**
         * Haptic feedback intensity levels
         */
        enum class HapticIntensity {
            LIGHT, // Light - navigation actions
            MEDIUM, // Medium - general actions
            STRONG, // Strong - important actions
            CONFIRM, // Confirm - critical actions
        }

        /**
         * Haptic feedback types
         */
        enum class HapticType {
            TAP, // Tap
            LONG_PRESS, // Long press
            SUCCESS, // Success
            ERROR, // Error
            SELECTION, // Selection
        }

        /**
         * Haptic feedback settings
         */
        data class HapticSettings(
            val hapticEnabled: Boolean = true,
            val soundEnabled: Boolean = true,
            val intensity: HapticIntensity = HapticIntensity.MEDIUM,
        )

        private var settings = HapticSettings()
        private var soundPool: SoundPool? = null
        private var audioManager: AudioManager? = null

        /**
         * Initialize sound feedback
         */
        fun initializeSound(context: Context) {
            if (soundPool == null) {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                
                soundPool = SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build()
                
                audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            }
        }

        /**
         * Update haptic feedback settings
         */
        fun updateSettings(newSettings: HapticSettings) {
            settings = newSettings
        }

        /**
         * Get current settings
         */
        fun getSettings(): HapticSettings = settings

        /**
         * Trigger haptic feedback
         */
        fun triggerHaptic(
            view: View,
            intensity: HapticIntensity = HapticIntensity.MEDIUM,
            @Suppress("UNUSED_PARAMETER") type: HapticType = HapticType.TAP,
        ) {
            if (!settings.hapticEnabled) return

            val hapticConstant =
                when (intensity) {
                    HapticIntensity.LIGHT -> HapticFeedbackConstants.KEYBOARD_TAP
                    HapticIntensity.MEDIUM -> HapticFeedbackConstants.VIRTUAL_KEY
                    HapticIntensity.STRONG -> HapticFeedbackConstants.LONG_PRESS
                    HapticIntensity.CONFIRM -> HapticFeedbackConstants.CONFIRM
                }

            view.performHapticFeedback(hapticConstant)
        }

        /**
         * Trigger sound feedback
         */
        fun triggerSound(context: Context, intensity: HapticIntensity = HapticIntensity.MEDIUM) {
            if (!settings.soundEnabled) return
            
            initializeSound(context)
            
            // Check if device is in silent mode
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val ringerMode = audioManager.ringerMode
            
            // Only play sound if not in silent mode
            if (ringerMode != AudioManager.RINGER_MODE_SILENT) {
                // Use system sound effects based on intensity
                val soundType = when (intensity) {
                    HapticIntensity.LIGHT -> AudioManager.FX_KEY_CLICK
                    HapticIntensity.MEDIUM -> AudioManager.FX_KEYPRESS_STANDARD
                    HapticIntensity.STRONG -> AudioManager.FX_KEYPRESS_DELETE
                    HapticIntensity.CONFIRM -> AudioManager.FX_KEYPRESS_RETURN
                }
                
                audioManager.playSoundEffect(soundType)
            }
        }

        /**
         * Trigger vibration feedback (for stronger feedback)
         */
        fun triggerVibration(
            context: Context,
            duration: Long = 50L,
            intensity: HapticIntensity = HapticIntensity.MEDIUM,
        ) {
            if (!settings.hapticEnabled) return

            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator

            val vibrationEffect =
                when (intensity) {
                    HapticIntensity.LIGHT ->
                        VibrationEffect.createOneShot(
                            duration,
                            VibrationEffect.DEFAULT_AMPLITUDE / 2,
                        )
                    HapticIntensity.MEDIUM -> VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
                    HapticIntensity.STRONG ->
                        VibrationEffect.createOneShot(
                            duration * 2,
                            VibrationEffect.DEFAULT_AMPLITUDE,
                        )
                    HapticIntensity.CONFIRM ->
                        VibrationEffect.createWaveform(
                            longArrayOf(0, 100, 50, 100),
                            intArrayOf(0, 255, 0, 255),
                            -1,
                        )
                }
            @SuppressLint("MissingPermission")
            vibrator.vibrate(vibrationEffect)
        }

        /**
         * Trigger success feedback
         */
        fun triggerSuccess(
            view: View,
            context: Context,
        ) {
            triggerHaptic(view, HapticIntensity.CONFIRM, HapticType.SUCCESS)
            triggerSound(context, HapticIntensity.CONFIRM)
            triggerVibration(context, 100L, HapticIntensity.CONFIRM)
        }

        /**
         * Trigger error feedback
         */
        fun triggerError(
            view: View,
            context: Context,
        ) {
            triggerHaptic(view, HapticIntensity.STRONG, HapticType.ERROR)
            triggerSound(context, HapticIntensity.STRONG)
            triggerVibration(context, 200L, HapticIntensity.STRONG)
        }

        /**
         * Trigger selection feedback
         */
        fun triggerSelection(view: View) {
            triggerHaptic(view, HapticIntensity.LIGHT, HapticType.SELECTION)
        }

        /**
         * Trigger navigation feedback
         */
        fun triggerNavigation(view: View) {
            triggerHaptic(view, HapticIntensity.LIGHT, HapticType.TAP)
        }
    }

/**
 * Haptic feedback hook for Compose
 */
@Composable
fun rememberHapticFeedback(): HapticFeedbackManager {
    return remember { HapticFeedbackManager() }
}

/**
 * Get haptic feedback manager with current View
 */
@Composable
fun rememberHapticFeedbackWithView(): Pair<HapticFeedbackManager, View> {
    val hapticManager = remember { HapticFeedbackManager() }
    val view = LocalView.current
    return remember(hapticManager, view) { hapticManager to view }
}

/**
 * Hilt module to provide the haptic feedback manager
 */
@Module
@InstallIn(SingletonComponent::class)
object HapticFeedbackModule {
    @Provides
    @Singleton
    fun provideHapticFeedbackManager(): HapticFeedbackManager {
        return HapticFeedbackManager()
    }
}

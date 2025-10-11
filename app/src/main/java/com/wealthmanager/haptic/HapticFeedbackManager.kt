package com.wealthmanager.haptic

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Haptic feedback manager providing centralized control over haptic and sound feedback.
 *
 * This manager is a ViewModel, ensuring its lifecycle is tied to the composable scope.
 * It should be accessed in Composables via `hiltViewModel()`.
 */
@HiltViewModel
class HapticFeedbackManager @Inject constructor() : ViewModel() {

    /** Haptic feedback intensity levels. */
    enum class HapticIntensity {
        LIGHT, MEDIUM, STRONG, CONFIRM
    }

    /** Haptic feedback settings. */
    data class HapticSettings(
        val hapticEnabled: Boolean = true,
        val soundEnabled: Boolean = true,
        val intensity: HapticIntensity = HapticIntensity.MEDIUM,
    )

    private var settings = HapticSettings()
    private var soundPool: SoundPool? = null

    private fun initializeSound(context: Context) {
        if (soundPool == null) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build()
        }
    }

    fun updateSettings(newSettings: HapticSettings) {
        settings = newSettings
    }

    fun getSettings(): HapticSettings = settings

    fun triggerHaptic(view: View, intensity: HapticIntensity = HapticIntensity.MEDIUM) {
        if (!settings.hapticEnabled) return

        val hapticConstant = when (intensity) {
            HapticIntensity.LIGHT -> HapticFeedbackConstants.KEYBOARD_TAP
            HapticIntensity.MEDIUM -> HapticFeedbackConstants.VIRTUAL_KEY
            HapticIntensity.STRONG -> HapticFeedbackConstants.LONG_PRESS
            HapticIntensity.CONFIRM -> HapticFeedbackConstants.CONFIRM
        }
        view.performHapticFeedback(hapticConstant)
    }

    fun triggerSound(context: Context, intensity: HapticIntensity = HapticIntensity.MEDIUM) {
        if (!settings.soundEnabled) return
        initializeSound(context)

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        if (audioManager?.ringerMode != AudioManager.RINGER_MODE_SILENT) {
            val soundType = when (intensity) {
                HapticIntensity.LIGHT -> AudioManager.FX_KEY_CLICK
                HapticIntensity.MEDIUM -> AudioManager.FX_KEYPRESS_STANDARD
                HapticIntensity.STRONG -> AudioManager.FX_KEYPRESS_DELETE
                HapticIntensity.CONFIRM -> AudioManager.FX_KEYPRESS_RETURN
            }
            audioManager?.playSoundEffect(soundType)
        }
    }

    @Suppress("DEPRECATION")
    fun triggerVibration(context: Context, duration: Long = 50L, intensity: HapticIntensity = HapticIntensity.MEDIUM) {
        if (!settings.hapticEnabled) return

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = when (intensity) {
                HapticIntensity.LIGHT -> VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE / 2)
                HapticIntensity.MEDIUM -> VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
                HapticIntensity.STRONG -> VibrationEffect.createOneShot(duration * 2, VibrationEffect.DEFAULT_AMPLITUDE)
                HapticIntensity.CONFIRM -> VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100), -1)
            }
            vibrator.vibrate(vibrationEffect)
        } else {
            vibrator.vibrate(duration)
        }
    }

    fun triggerSuccess(view: View, context: Context) {
        triggerHaptic(view, HapticIntensity.CONFIRM)
        triggerSound(context, HapticIntensity.CONFIRM)
    }

    fun triggerError(view: View, context: Context) {
        triggerHaptic(view, HapticIntensity.STRONG)
        triggerSound(context, HapticIntensity.STRONG)
    }
}

/**
 * A Composable hook to remember and provide the HapticFeedbackManager and the current View.
 *
 * This is the recommended way to access the HapticFeedbackManager in Composables.
 * It ensures that the same ViewModel-scoped instance is used, and provides the necessary View for haptic feedback.
 */
@Composable
fun rememberHapticFeedbackWithView(): Pair<HapticFeedbackManager, View> {
    val hapticManager: HapticFeedbackManager = hiltViewModel()
    val view = LocalView.current
    return remember(hapticManager, view) { hapticManager to view }
}

package com.wealthmanager.haptic

import android.content.Context
import android.media.AudioManager
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
 * Manages haptic and sound feedback throughout the application.
 *
 * This manager is a ViewModel, ensuring a single instance is used within a lifecycle scope.
 * It is the central point for triggering all tactile and auditory user feedback.
 */
@HiltViewModel
class HapticFeedbackManager @Inject constructor() : ViewModel() {

    /** Defines the intensity of the haptic feedback. */
    enum class HapticIntensity {
        LIGHT, MEDIUM, STRONG, CONFIRM
    }

    /**
     * Triggers a standard haptic feedback event.
     *
     * @param view The view to perform the haptic feedback on.
     * @param intensity The desired intensity of the feedback.
     */
    fun triggerHaptic(view: View, intensity: HapticIntensity = HapticIntensity.MEDIUM) {
        val hapticConstant = when (intensity) {
            HapticIntensity.LIGHT -> HapticFeedbackConstants.KEYBOARD_TAP
            HapticIntensity.MEDIUM -> HapticFeedbackConstants.VIRTUAL_KEY
            HapticIntensity.STRONG -> HapticFeedbackConstants.LONG_PRESS
            HapticIntensity.CONFIRM -> HapticFeedbackConstants.CONFIRM
        }
        view.performHapticFeedback(hapticConstant)
    }

    /**
     * Triggers a standard system sound effect.
     *
     * @param context The context used to access the AudioManager.
     * @param intensity The intensity of the sound, which maps to different system sound types.
     */
    fun triggerSound(context: Context, intensity: HapticIntensity = HapticIntensity.MEDIUM) {
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

    /**
     * Triggers a custom vibration effect.
     *
     * @param context The context used to access the Vibrator service.
     * @param duration The duration of the vibration in milliseconds.
     */
    @Suppress("DEPRECATION")
    fun triggerVibration(context: Context, duration: Long = 50L) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(duration)
        }
    }

    /** Triggers a combined haptic and sound feedback for success events. */
    fun triggerSuccess(view: View, context: Context) {
        triggerHaptic(view, HapticIntensity.CONFIRM)
        triggerSound(context, HapticIntensity.CONFIRM)
    }

    /** Triggers a combined haptic and sound feedback for error events. */
    fun triggerError(view: View, context: Context) {
        triggerHaptic(view, HapticIntensity.STRONG)
        triggerSound(context, HapticIntensity.STRONG)
    }
}

/**
 * A Composable hook to remember and provide the HapticFeedbackManager and the current View.
 */
@Composable
fun rememberHapticFeedbackWithView(): Pair<HapticFeedbackManager, View> {
    val hapticManager: HapticFeedbackManager = hiltViewModel()
    val view = LocalView.current
    return remember(hapticManager, view) { hapticManager to view }
}

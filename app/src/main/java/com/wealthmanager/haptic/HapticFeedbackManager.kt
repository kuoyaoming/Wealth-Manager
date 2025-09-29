package com.wealthmanager.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 觸覺回饋管理器
 * 提供不同強度的觸覺回饋和音效回饋
 */
@Singleton
class HapticFeedbackManager @Inject constructor() {
    
    /**
     * 觸覺回饋強度等級
     */
    enum class HapticIntensity {
        LIGHT,      // 輕微 - 導航操作
        MEDIUM,     // 中等 - 一般操作
        STRONG,     // 強烈 - 重要操作
        CONFIRM     // 確認 - 關鍵操作
    }
    
    /**
     * 觸覺回饋類型
     */
    enum class HapticType {
        TAP,        // 點擊
        LONG_PRESS, // 長按
        SUCCESS,    // 成功
        ERROR,      // 錯誤
        SELECTION   // 選擇
    }
    
    /**
     * 觸覺回饋設置
     */
    data class HapticSettings(
        val hapticEnabled: Boolean = true,
        val soundEnabled: Boolean = true,
        val intensity: HapticIntensity = HapticIntensity.MEDIUM
    )
    
    private var settings = HapticSettings()
    
    /**
     * 更新觸覺回饋設置
     */
    fun updateSettings(newSettings: HapticSettings) {
        settings = newSettings
    }
    
    /**
     * 獲取當前設置
     */
    fun getSettings(): HapticSettings = settings
    
    /**
     * 觸發觸覺回饋
     */
    fun triggerHaptic(
        view: View,
        intensity: HapticIntensity = HapticIntensity.MEDIUM,
        type: HapticType = HapticType.TAP
    ) {
        if (!settings.hapticEnabled) return
        
        val hapticConstant = when (intensity) {
            HapticIntensity.LIGHT -> HapticFeedbackConstants.KEYBOARD_TAP
            HapticIntensity.MEDIUM -> HapticFeedbackConstants.VIRTUAL_KEY
            HapticIntensity.STRONG -> HapticFeedbackConstants.LONG_PRESS
            HapticIntensity.CONFIRM -> HapticFeedbackConstants.CONFIRM
        }
        
        view.performHapticFeedback(hapticConstant)
    }
    
    /**
     * 觸發震動回饋（用於更強烈的回饋）
     */
    fun triggerVibration(
        context: Context,
        duration: Long = 50L,
        intensity: HapticIntensity = HapticIntensity.MEDIUM
    ) {
        if (!settings.hapticEnabled) return
        
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = when (intensity) {
                HapticIntensity.LIGHT -> VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE / 2)
                HapticIntensity.MEDIUM -> VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
                HapticIntensity.STRONG -> VibrationEffect.createOneShot(duration * 2, VibrationEffect.DEFAULT_AMPLITUDE)
                HapticIntensity.CONFIRM -> VibrationEffect.createWaveform(
                    longArrayOf(0, 100, 50, 100),
                    intArrayOf(0, 255, 0, 255),
                    -1
                )
            }
            vibrator.vibrate(vibrationEffect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }
    
    /**
     * 觸發成功回饋
     */
    fun triggerSuccess(view: View, context: Context) {
        triggerHaptic(view, HapticIntensity.CONFIRM, HapticType.SUCCESS)
        triggerVibration(context, 100L, HapticIntensity.CONFIRM)
    }
    
    /**
     * 觸發錯誤回饋
     */
    fun triggerError(view: View, context: Context) {
        triggerHaptic(view, HapticIntensity.STRONG, HapticType.ERROR)
        triggerVibration(context, 200L, HapticIntensity.STRONG)
    }
    
    /**
     * 觸發選擇回饋
     */
    fun triggerSelection(view: View) {
        triggerHaptic(view, HapticIntensity.LIGHT, HapticType.SELECTION)
    }
    
    /**
     * 觸發導航回饋
     */
    fun triggerNavigation(view: View) {
        triggerHaptic(view, HapticIntensity.LIGHT, HapticType.TAP)
    }
}

/**
 * Compose 中的觸覺回饋 Hook
 */
@Composable
fun rememberHapticFeedback(): HapticFeedbackManager {
    return remember { HapticFeedbackManager() }
}

/**
 * 獲取當前 View 和 Context 的觸覺回饋管理器
 */
@Composable
fun rememberHapticFeedbackWithView(): Pair<HapticFeedbackManager, View> {
    val hapticManager = remember { HapticFeedbackManager() }
    val view = LocalView.current
    return remember(hapticManager, view) { hapticManager to view }
}

/**
 * Hilt 模組用於提供觸覺回饋管理器
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

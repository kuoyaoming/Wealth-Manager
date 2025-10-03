package com.wealthmanager.ui.onboarding

import androidx.compose.runtime.*
import com.wealthmanager.data.FirstLaunchManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 首次導覽管理器
 * 管理完整的首次啟動導覽流程
 */
@Singleton
class OnboardingManager
    @Inject
    constructor(
        private val firstLaunchManager: FirstLaunchManager,
    ) {
        /**
         * 導覽步驟枚舉
         */
        enum class OnboardingStep {
            WELCOME, // 歡迎與價值介紹
            GOOGLE_PASSWORD, // Google Password Manager 介紹
            API_KEY_GUIDE, // API Key 取得指南
            COMPLETE, // 完成
        }

        /**
         * 檢查是否應該顯示首次導覽
         */
        fun shouldShowOnboarding(): Boolean {
            return firstLaunchManager.shouldShowGooglePasswordManagerOnboarding()
        }

        /**
         * 標記導覽完成
         */
        fun markOnboardingCompleted() {
            firstLaunchManager.markGooglePasswordManagerOnboardingShown()
        }

        /**
         * 重置導覽狀態（用於測試或重新導覽）
         */
        fun resetOnboarding() {
            firstLaunchManager.resetFirstLaunch()
        }
    }

package com.wealthmanager.ui.onboarding

import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wealthmanager.data.FirstLaunchManager

/**
 * 完整的首次導覽流程
 * 包含歡迎、GPM 介紹、API Key 指南三個步驟
 */
@Composable
fun OnboardingFlow(
    firstLaunchManager: FirstLaunchManager,
    onComplete: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    var currentStep by remember { mutableStateOf(OnboardingManager.OnboardingStep.WELCOME) }

    Dialog(
        onDismissRequest = { /* 不允許關閉，必須完成導覽 */ },
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
    ) {
        when (currentStep) {
            OnboardingManager.OnboardingStep.WELCOME -> {
                WelcomeOnboardingDialog(
                    onNext = {
                        currentStep = OnboardingManager.OnboardingStep.GOOGLE_PASSWORD
                    },
                    onSkip = {
                        // 跳過導覽，直接完成
                        firstLaunchManager.markGooglePasswordManagerOnboardingShown()
                        onComplete()
                    },
                )
            }

            OnboardingManager.OnboardingStep.GOOGLE_PASSWORD -> {
                GooglePasswordManagerOnboardingDialog(
                    onDismiss = {
                        // 不允許關閉，繼續下一步
                    },
                    onEnableAutoFill = {
                        currentStep = OnboardingManager.OnboardingStep.API_KEY_GUIDE
                    },
                )
            }

            OnboardingManager.OnboardingStep.API_KEY_GUIDE -> {
                ApiKeyGuideOnboardingDialog(
                    onNext = {
                        currentStep = OnboardingManager.OnboardingStep.COMPLETE
                    },
                    onGoToSettings = {
                        // 前往設定頁面
                        firstLaunchManager.markGooglePasswordManagerOnboardingShown()
                        onNavigateToSettings()
                    },
                    onSkip = {
                        // 完成導覽
                        firstLaunchManager.markGooglePasswordManagerOnboardingShown()
                        onComplete()
                    },
                )
            }

            OnboardingManager.OnboardingStep.COMPLETE -> {
                // 導覽完成
                firstLaunchManager.markGooglePasswordManagerOnboardingShown()
                onComplete()
            }
        }
    }
}

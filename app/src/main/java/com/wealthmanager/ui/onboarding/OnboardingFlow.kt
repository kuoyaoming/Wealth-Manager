package com.wealthmanager.ui.onboarding

import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wealthmanager.data.FirstLaunchManager

/**
 * Complete first-time onboarding flow.
 */
@Composable
fun OnboardingFlow(
    firstLaunchManager: FirstLaunchManager,
    onComplete: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    var currentStep by remember { mutableStateOf(OnboardingManager.OnboardingStep.WELCOME) }

    Dialog(
        onDismissRequest = { /* Not allowed to close, must complete onboarding */ },
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
                        firstLaunchManager.markGooglePasswordManagerOnboardingShown()
                        onComplete()
                    },
                )
            }

            OnboardingManager.OnboardingStep.GOOGLE_PASSWORD -> {
                GooglePasswordManagerOnboardingDialog(
                    onDismiss = {
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
                        firstLaunchManager.markGooglePasswordManagerOnboardingShown()
                        onNavigateToSettings()
                    },
                    onSkip = {
                        firstLaunchManager.markGooglePasswordManagerOnboardingShown()
                        onComplete()
                    },
                )
            }

            OnboardingManager.OnboardingStep.COMPLETE -> {
                firstLaunchManager.markGooglePasswordManagerOnboardingShown()
                onComplete()
            }
        }
    }
}

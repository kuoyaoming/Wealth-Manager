package com.wealthmanager.ui.onboarding

import androidx.compose.runtime.*
import com.wealthmanager.data.FirstLaunchManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * First-time onboarding manager.
 */
@Singleton
class OnboardingManager
    @Inject
    constructor(
        private val firstLaunchManager: FirstLaunchManager,
    ) {
        /**
         * Onboarding step enumeration.
         */
        enum class OnboardingStep {
            WELCOME,
            GOOGLE_PASSWORD,
            API_KEY_GUIDE,
            COMPLETE,
        }

        /**
         * Check if first-time onboarding should be shown.
         */
        fun shouldShowOnboarding(): Boolean {
            return firstLaunchManager.shouldShowGooglePasswordManagerOnboarding()
        }

        /**
         * Mark onboarding as completed.
         */
        fun markOnboardingCompleted() {
            firstLaunchManager.markGooglePasswordManagerOnboardingShown()
        }

        /**
         * Reset onboarding state (for testing or re-onboarding).
         */
        fun resetOnboarding() {
            firstLaunchManager.resetFirstLaunch()
        }
    }

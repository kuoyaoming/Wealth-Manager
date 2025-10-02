package com.wealthmanager.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricAuthManager
    @Inject
    constructor() {
        fun isBiometricAvailable(context: Context): BiometricStatus {
            val biometricManager = BiometricManager.from(context)
            return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NO_HARDWARE
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.HW_UNAVAILABLE
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NONE_ENROLLED
                else -> BiometricStatus.UNKNOWN_ERROR
            }
        }

        fun createBiometricPrompt(
            activity: FragmentActivity,
            onSuccess: () -> Unit,
            onError: (String) -> Unit,
            onCancel: () -> Unit,
        ): BiometricPrompt {
            val executor = ContextCompat.getMainExecutor(activity)

            val callback =
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onSuccess()
                    }

                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence,
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        when (errorCode) {
                            BiometricPrompt.ERROR_NO_BIOMETRICS -> onError("No biometrics enrolled")
                            BiometricPrompt.ERROR_HW_UNAVAILABLE -> onError("Hardware unavailable")
                            BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> onError("Unable to process")
                            BiometricPrompt.ERROR_TIMEOUT -> onError("Authentication timeout")
                            BiometricPrompt.ERROR_CANCELED -> onCancel()
                            BiometricPrompt.ERROR_LOCKOUT -> onError("Too many failed attempts")
                            BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> onError("Biometric permanently locked")
                            else -> onError("Authentication failed: $errString")
                        }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        onError("Authentication failed")
                    }
                }

            return BiometricPrompt(activity, executor, callback)
        }

        fun showBiometricPrompt(
            prompt: BiometricPrompt,
            title: String,
            subtitle: String,
            negativeButtonText: String,
        ) {
            val promptInfo =
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setNegativeButtonText(negativeButtonText)
                    .build()

            prompt.authenticate(promptInfo)
        }
    }

enum class BiometricStatus {
    AVAILABLE,
    NO_HARDWARE,
    HW_UNAVAILABLE,
    NONE_ENROLLED,
    UNKNOWN_ERROR,
}

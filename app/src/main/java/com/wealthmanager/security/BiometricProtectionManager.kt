package com.wealthmanager.security

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.wealthmanager.R
import com.wealthmanager.debug.DebugLogManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricProtectionManager @Inject constructor(
    private val context: Context,
    private val debugLogManager: DebugLogManager
) {

    companion object {
        private const val BIOMETRIC_TITLE = "biometric_prompt_title"
        private const val BIOMETRIC_SUBTITLE = "biometric_prompt_subtitle"
        private const val BIOMETRIC_NEGATIVE_TEXT = "biometric_prompt_negative"
    }

    /**
     * Checks if biometric authentication is available on the device.
     *
     * @return [BiometricStatus] indicating the current biometric availability state
     */
    fun isBiometricAvailable(): BiometricStatus {
        val biometricManager = BiometricManager.from(context)

        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                debugLogManager.log("BIOMETRIC", "Biometric authentication available")
                BiometricStatus.AVAILABLE
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                debugLogManager.log("BIOMETRIC", "No biometric hardware available")
                BiometricStatus.NO_HARDWARE
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                debugLogManager.log("BIOMETRIC", "Biometric hardware unavailable")
                BiometricStatus.HARDWARE_UNAVAILABLE
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                debugLogManager.log("BIOMETRIC", "No biometric enrolled")
                BiometricStatus.NONE_ENROLLED
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                debugLogManager.log("BIOMETRIC", "Security update required")
                BiometricStatus.SECURITY_UPDATE_REQUIRED
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                debugLogManager.log("BIOMETRIC", "Biometric unsupported")
                BiometricStatus.UNSUPPORTED
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                debugLogManager.log("BIOMETRIC", "Biometric status unknown")
                BiometricStatus.UNKNOWN
            }
            else -> {
                debugLogManager.log("BIOMETRIC", "Unknown biometric error")
                BiometricStatus.UNKNOWN
            }
        }
    }

    /**
     * Creates a biometric authentication prompt.
     *
     * @param activity The activity to show the prompt on
     * @param onSuccess Callback for successful authentication
     * @param onError Callback for authentication errors
     * @param onCancel Callback for user cancellation
     * @return [BiometricPrompt] instance for authentication
     */
    fun createBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onCancel: () -> Unit
    ): BiometricPrompt {

        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                debugLogManager.log("BIOMETRIC", "Authentication succeeded")
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                debugLogManager.logError("BIOMETRIC", "Authentication error: $errString")
                when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        onCancel()
                    }
                    else -> {
                        onError(errString.toString())
                    }
                }
            }

            override fun onAuthenticationFailed() {
                debugLogManager.log("BIOMETRIC", "Authentication failed")
                onError(context.getString(R.string.biometric_error_retry))
            }
        }

        return BiometricPrompt(activity, executor, callback)
    }

    /**
     * Shows a biometric authentication prompt to the user.
     *
     * @param activity The activity to show the prompt on
     * @param onSuccess Callback for successful authentication
     * @param onError Callback for authentication errors
     * @param onCancel Callback for user cancellation
     */
    fun showBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onCancel: () -> Unit
    ) {
        val biometricPrompt = createBiometricPrompt(activity, onSuccess, onError, onCancel)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometric_prompt_title))
            .setSubtitle(context.getString(R.string.biometric_prompt_subtitle))
            .setNegativeButtonText(context.getString(R.string.biometric_prompt_negative))
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
            debugLogManager.log("BIOMETRIC", "Biometric prompt shown")
        } catch (e: Exception) {
            debugLogManager.logError("BIOMETRIC", "Failed to show biometric prompt: ${e.message}")
            onError(context.getString(R.string.biometric_error_cannot_show))
        }
    }

    /**
     * Checks if biometric authentication is supported on the current device.
     *
     * @return true if biometric authentication is supported and available
     */
    fun isBiometricSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
               isBiometricAvailable() == BiometricStatus.AVAILABLE
    }

    /**
     * Gets a human-readable description of the current biometric status.
     *
     * @return Localized string describing the biometric authentication status
     */
    fun getBiometricStatusDescription(): String {
        return when (isBiometricAvailable()) {
            BiometricStatus.AVAILABLE -> context.getString(R.string.biometric_status_available)
            BiometricStatus.NO_HARDWARE -> context.getString(R.string.biometric_status_no_hardware)
            BiometricStatus.HARDWARE_UNAVAILABLE -> context.getString(R.string.biometric_status_hardware_unavailable)
            BiometricStatus.NONE_ENROLLED -> context.getString(R.string.biometric_status_none_enrolled)
            BiometricStatus.SECURITY_UPDATE_REQUIRED -> context.getString(R.string.biometric_status_security_update_required)
            BiometricStatus.UNSUPPORTED -> context.getString(R.string.biometric_status_unsupported)
            BiometricStatus.UNKNOWN -> context.getString(R.string.biometric_status_unknown)
        }
    }
}

/**
 * Enumeration of possible biometric authentication states.
 */
enum class BiometricStatus {
    AVAILABLE,
    NO_HARDWARE,
    HARDWARE_UNAVAILABLE,
    NONE_ENROLLED,
    SECURITY_UPDATE_REQUIRED,
    UNSUPPORTED,
    UNKNOWN
}


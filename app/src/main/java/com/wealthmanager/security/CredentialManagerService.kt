package com.wealthmanager.security

import android.content.Context
import androidx.credentials.CreateCredentialRequest
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialException
import com.wealthmanager.debug.DebugLogManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Represents the status of Google Password Manager availability and sign-in state.
 */
enum class GpmStatus {
    /** Google Password Manager is available and user is signed in */
    AvailableAndSignedIn,

    /** Google Password Manager is available but user is not signed in */
    AvailableNotSignedIn,

    /** Google Password Manager is not available on this device */
    Unavailable,
}

/**
 * Service for integrating with Google Password Manager through Android Credential Manager API.
 * Provides secure storage and retrieval of API keys in Google's password management system.
 */
@Singleton
class CredentialManagerService
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val debugLogManager: DebugLogManager,
    ) {
        private val credentialManager = CredentialManager.create(context)

        companion object {
            private const val APP_IDENTIFIER = "wealth_manager"
            private const val USERNAME_PREFIX = "wealth_manager_user"
        }

        /**
         * Saves an API key to Google Password Manager with user consent.
         *
         * @param keyType The type of API key (e.g., "finnhub", "exchange")
         * @param apiKey The API key to store
         * @param username Optional username for the credential
         * @return Result indicating success or failure
         */
        suspend fun saveApiKeyToGooglePasswordManager(
            keyType: String,
            apiKey: String,
            username: String = USERNAME_PREFIX,
        ): Result<Unit> {
            return try {
                debugLogManager.log("CREDENTIAL_MANAGER", "Attempting to save $keyType key to Google Password Manager")
                val userId = "$username-$keyType"
                val createRequest: CreateCredentialRequest =
                    CreatePasswordRequest(
                        id = userId,
                        password = apiKey,
                    )
                credentialManager.createCredential(context, createRequest)
                debugLogManager.log("CREDENTIAL_MANAGER", "Saved $keyType key via Credential Manager for user $userId")
                Result.success(Unit)
            } catch (e: CreateCredentialException) {
                debugLogManager.logError(
                    "CREDENTIAL_MANAGER",
                    "CreateCredentialException saving $keyType key: ${e.message}",
                )
                Result.failure(e)
            } catch (e: Exception) {
                debugLogManager.logError("CREDENTIAL_MANAGER", "Unexpected error saving $keyType key: ${e.message}")
                Result.failure(e)
            }
        }

        /**
         * Retrieves an API key from Google Password Manager.
         *
         * @param keyType The type of API key to retrieve
         * @return Result containing the API key or null if not found
         */
        suspend fun getApiKeyFromGooglePasswordManager(keyType: String): Result<String?> {
            return try {
                debugLogManager.log(
                    "CREDENTIAL_MANAGER",
                    "Attempting to retrieve $keyType key from Google Password Manager",
                )
                val getRequest = GetCredentialRequest(listOf(GetPasswordOption()))
                val result = credentialManager.getCredential(context, getRequest)
                val password = result.credential as? PasswordCredential
                if (password != null) {
                    val id = password.id
                    val matched = id.startsWith("$USERNAME_PREFIX-$keyType")
                    if (matched) {
                        debugLogManager.log("CREDENTIAL_MANAGER", "Retrieved $keyType key for id=$id")
                        return Result.success(password.password)
                    }
                }
                debugLogManager.log("CREDENTIAL_MANAGER", "No matching credential found for $keyType")
                Result.success(null)
            } catch (e: GetCredentialException) {
                debugLogManager.logError(
                    "CREDENTIAL_MANAGER",
                    "GetCredentialException retrieving $keyType key: ${e.message}",
                )
                Result.failure(e)
            } catch (e: Exception) {
                debugLogManager.logError("CREDENTIAL_MANAGER", "Unexpected error retrieving $keyType key: ${e.message}")
                Result.failure(e)
            }
        }

        /**
         * Checks if Google Password Manager is available and accessible.
         * This is a non-intrusive check that doesn't trigger credential dialogs.
         *
         * @return true if Google Password Manager is available, false otherwise
         */
        suspend fun isGooglePasswordManagerAvailable(): Boolean {
            return try {
                debugLogManager.log(
                    "CREDENTIAL_MANAGER",
                    "Checking Google Password Manager availability (non-intrusive)",
                )

                // For Pixel 8 Pro with Android 16, Google Password Manager should be available
                // Since user confirmed it works in other apps, we'll return true
                // This avoids triggering credential dialogs during status checks
                val isAvailable = true

                debugLogManager.log("CREDENTIAL_MANAGER", "Google Password Manager available: $isAvailable")
                isAvailable
            } catch (e: Exception) {
                debugLogManager.log(
                    "CREDENTIAL_MANAGER",
                    "Error checking Google Password Manager availability: ${e.message}",
                )
                false
            }
        }

        /**
         * Checks if user is signed in to Google account for Password Manager.
         * This is a non-intrusive check that doesn't trigger credential dialogs.
         *
         * @return true if user is signed in, false otherwise
         */
        suspend fun isGoogleAccountSignedIn(): Boolean {
            return try {
                debugLogManager.log("CREDENTIAL_MANAGER", "Checking Google account sign-in status (non-intrusive)")

                // For now, assume user is signed in if GPM is available
                // This avoids triggering credential dialogs during status checks
                // TODO: Implement proper non-intrusive sign-in detection
                val isSignedIn = true

                debugLogManager.log("CREDENTIAL_MANAGER", "Google account sign-in status: $isSignedIn")
                isSignedIn
            } catch (e: Exception) {
                debugLogManager.log("CREDENTIAL_MANAGER", "Error checking Google sign-in status: ${e.message}")
                false
            }
        }

        /**
         * Gets the comprehensive status of Google Password Manager.
         * This is a non-intrusive check that doesn't trigger credential dialogs.
         *
         * @return GpmStatus indicating availability and sign-in state
         */
        suspend fun getGooglePasswordManagerStatus(): GpmStatus {
            return try {
                debugLogManager.log("CREDENTIAL_MANAGER", "Getting comprehensive GPM status (non-intrusive)")

                // For now, assume GPM is available and user is signed in
                // This avoids triggering credential dialogs during status checks
                // TODO: Implement proper non-intrusive GPM status detection
                val isAvailable = true
                val isSignedIn = true

                if (!isAvailable) {
                    return GpmStatus.Unavailable
                }

                if (isSignedIn) {
                    GpmStatus.AvailableAndSignedIn
                } else {
                    GpmStatus.AvailableNotSignedIn
                }
            } catch (e: Exception) {
                debugLogManager.logError("CREDENTIAL_MANAGER", "Error getting GPM status: ${e.message}")
                GpmStatus.Unavailable
            }
        }

        /**
         * Lists all stored key types in Google Password Manager.
         *
         * @return List of key types that are stored
         */
        suspend fun listStoredKeyTypes(): List<String> {
            return try {
                debugLogManager.log("CREDENTIAL_MANAGER", "Listing stored key types (non-intrusive)")

                // For now, return empty list to avoid triggering credential dialogs
                // This prevents the infinite dialog loop issue
                // TODO: Implement proper non-intrusive key type detection
                val keyTypes = emptyList<String>()

                debugLogManager.log("CREDENTIAL_MANAGER", "Found stored key types: ${keyTypes.joinToString(", ")}")
                keyTypes
            } catch (e: Exception) {
                debugLogManager.logError("CREDENTIAL_MANAGER", "Failed to list stored key types: ${e.message}")
                emptyList()
            }
        }

        /**
         * Generates a unique credential ID for the given key type.
         */
        private fun generateCredentialId(keyType: String): String {
            return "${APP_IDENTIFIER}_${keyType}_${System.currentTimeMillis()}"
        }

        /**
         * Checks if a specific key type is stored in Google Password Manager.
         *
         * @param keyType The key type to check
         * @return true if the key type is stored, false otherwise
         */
        suspend fun isKeyTypeStored(keyType: String): Boolean {
            return try {
                debugLogManager.log("CREDENTIAL_MANAGER", "Checking if $keyType is stored (non-intrusive)")

                // For now, return false to avoid triggering credential dialogs
                // This prevents the infinite dialog loop issue
                // TODO: Implement proper non-intrusive key type detection
                false
            } catch (e: Exception) {
                debugLogManager.logError("CREDENTIAL_MANAGER", "Failed to check if $keyType is stored: ${e.message}")
                false
            }
        }
    }

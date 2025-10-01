package com.wealthmanager.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wealthmanager.R
import com.wealthmanager.auth.BiometricStatus
import com.wealthmanager.debug.DebugLogManager
import com.wealthmanager.haptic.HapticFeedbackManager
import com.wealthmanager.haptic.rememberHapticFeedbackWithView

@Composable
fun BiometricAuthScreen(
    onAuthSuccess: () -> Unit,
    onSkipAuth: () -> Unit = {},
    viewModel: BiometricAuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val debugLogManager = remember { DebugLogManager() }
    val (hapticManager, view) = rememberHapticFeedbackWithView()
    
    LaunchedEffect(Unit) {
        debugLogManager.logUserAction("Biometric Auth Screen Opened")
        viewModel.checkBiometricAvailability(context)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = stringResource(R.string.biometric_auth_title),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(R.string.biometric_auth_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Privacy Protection Notice
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.privacy_protection_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Text(
                            text = stringResource(R.string.privacy_protection_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = stringResource(R.string.privacy_protection_points),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                when (uiState.biometricStatus) {
                    BiometricStatus.AVAILABLE -> {
                        Button(
                            onClick = { 
                                debugLogManager.logUserAction("Biometric Authenticate Button Clicked")
                                debugLogManager.log("UI", "User clicked biometric authenticate button")
                                hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.CONFIRM)
                                viewModel.authenticate(context) 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.biometric_auth_button))
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        TextButton(
                            onClick = { 
                                debugLogManager.logUserAction("Skip Authentication Button Clicked")
                                debugLogManager.log("UI", "User clicked skip authentication button")
                                hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                                onSkipAuth() 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.button_skip_authentication))
                        }
                    }
                    BiometricStatus.NO_HARDWARE -> {
                        Text(
                            text = stringResource(R.string.biometric_auth_error_no_hardware),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { 
                                debugLogManager.logUserAction("Continue Without Biometric (No Hardware)")
                                debugLogManager.log("UI", "User clicked continue without biometric - no hardware available")
                                onSkipAuth() 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.button_continue_without_biometric))
                        }
                    }
                    BiometricStatus.HW_UNAVAILABLE -> {
                        Text(
                            text = stringResource(R.string.biometric_auth_error_hw_unavailable),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { 
                                debugLogManager.logUserAction("Continue Without Biometric (HW Unavailable)")
                                debugLogManager.log("UI", "User clicked continue without biometric - hardware unavailable")
                                onSkipAuth() 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.button_continue_without_biometric))
                        }
                    }
                    BiometricStatus.NONE_ENROLLED -> {
                        Text(
                            text = stringResource(R.string.biometric_auth_error_no_biometrics),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { 
                                debugLogManager.logUserAction("Continue Without Biometric (None Enrolled)")
                                debugLogManager.log("UI", "User clicked continue without biometric - no biometrics enrolled")
                                onSkipAuth() 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.button_continue_without_biometric))
                        }
                    }
                    BiometricStatus.UNKNOWN_ERROR -> {
                        Text(
                            text = stringResource(R.string.biometric_auth_error),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { 
                                debugLogManager.logUserAction("Continue Without Biometric (Unknown Error)")
                                debugLogManager.log("UI", "User clicked continue without biometric - unknown error occurred")
                                onSkipAuth() 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.button_continue_without_biometric))
                        }
                    }
                }
                
                if (uiState.errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    
    // Handle authentication success
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onAuthSuccess()
        }
    }
}
package com.wealthmanager.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wealthmanager.R
import com.wealthmanager.data.FirstLaunchManager
import com.wealthmanager.haptic.HapticFeedbackManager
import com.wealthmanager.haptic.rememberHapticFeedbackWithView
import javax.inject.Inject

/**
 * About Dialog Component
 * Shows app information, data usage policy, third-party API usage, and compliance information
 */
@Composable
fun AboutDialog(
    onDismiss: () -> Unit,
    firstLaunchManager: FirstLaunchManager? = null
) {
    // Context is available but not currently used in this dialog
    // val context = LocalContext.current
    val (hapticManager, view) = rememberHapticFeedbackWithView()
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.about_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                        onDismiss()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.cd_close)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // App Version and Info
                    AppVersionSection()
                    
                    // Data Usage Policy
                    DataUsageSection()
                    
                    // Third-party API Usage
                    ThirdPartyApiSection()
                    
                    // Security and Compliance
                    SecurityComplianceSection()
                    
                    // Privacy Policy
                    PrivacyPolicySection()
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.LIGHT)
                        onDismiss()
                    }) {
                        Text("Later")
                    }
                    
                    Button(
                        onClick = {
                            hapticManager.triggerHaptic(view, HapticFeedbackManager.HapticIntensity.CONFIRM)
                            firstLaunchManager?.markAboutDialogShown()
                            onDismiss()
                        }
                    ) {
                        Text("I Understand")
                    }
                }
            }
        }
    }
}

@Composable
private fun AppVersionSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "App Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = "Wealth Manager v1.0.0",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "A comprehensive personal finance management application that helps you track your assets, monitor stock investments, and manage your financial portfolio with real-time market data. Features include biometric authentication, multi-currency support, and secure local data storage.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun DataUsageSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Data Usage Policy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = "Your financial data is stored locally on your device and is never transmitted to external servers without your explicit consent.",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "• Asset information (cash, stocks) is stored locally using Android Room database\n" +
                        "• Biometric authentication data is handled by Android's secure hardware\n" +
                        "• No personal financial data is collected or transmitted to our servers\n" +
                        "• All calculations are performed locally on your device",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ThirdPartyApiSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Third-Party API Usage",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "This app uses the following third-party services:",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "• Finnhub API: For real-time stock prices and market data\n" +
                        "  - Rate limit: 60 requests per minute (free tier)\n" +
                        "  - Data: Stock quotes, stock search, forex rates\n" +
                        "  - Privacy: Only stock symbols and market data are transmitted\n\n" +
                        "• Exchange Rate API: For currency conversion rates\n" +
                        "  - Rate limit: 1000 requests per month (free tier)\n" +
                        "  - Data: Real-time exchange rates for multiple currencies\n" +
                        "  - Privacy: Only currency codes are transmitted\n\n" +
                        "• TWSE API: For Taiwan stock market data\n" +
                        "  - Public API with no rate limits\n" +
                        "  - Data: Taiwan stock prices and market information\n" +
                        "  - Privacy: Only stock symbols are transmitted\n\n" +
                        "• Android Biometric API: For secure authentication\n" +
                        "  - Handled by Android's secure hardware\n" +
                        "  - No biometric data is stored or transmitted",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SecurityComplianceSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Security & Compliance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = "• All data is encrypted using Android's built-in encryption\n" +
                        "• Biometric authentication provides secure access\n" +
                        "• No network requests are made without user consent\n" +
                        "• API usage is monitored and rate-limited to prevent abuse\n" +
                        "• Local database is protected by Android's app sandbox\n" +
                        "• No third-party analytics or tracking services",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun PrivacyPolicySection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "We are committed to protecting your privacy and financial data. This app:",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "• Does not collect personal information\n" +
                        "• Does not share data with third parties\n" +
                        "• Stores all data locally on your device\n" +
                        "• Uses secure, industry-standard encryption\n" +
                        "• Complies with Android's privacy guidelines\n" +
                        "• Provides transparent data usage information",
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "By continuing to use this app, you acknowledge that you have read and understood this privacy policy.",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
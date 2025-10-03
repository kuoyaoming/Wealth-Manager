package com.wealthmanager.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wealthmanager.R
import com.wealthmanager.backup.EnhancedBackupManager

/**
 * Enhanced backup settings card that integrates both Android Auto Backup and Google Password Manager.
 */
@Composable
fun EnhancedBackupSettingsCard(
    backupStatus: EnhancedBackupManager.BackupStatus,
    onLocalBackupToggle: (Boolean) -> Unit,
    onShowGooglePasswordManagerInfo: () -> Unit,
    onShowBackupRecommendations: () -> Unit,
) {
    var showGoogleInfoDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Enhanced Backup Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            
            // Description
            Text(
                text = "Choose your backup strategy for API keys and financial data.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            
            // Current Strategy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Current Strategy:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = getBackupStrategyDescription(backupStatus.strategy),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            
            // Android Auto Backup Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Android Auto Backup",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Switch(
                    checked = backupStatus.isLocalBackupEnabled,
                    onCheckedChange = onLocalBackupToggle,
                )
            }
            
            // Google Password Manager Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (backupStatus.isGooglePasswordManagerAvailable) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                    Text(
                        text = "Google Password Manager",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (backupStatus.isGooglePasswordManagerAvailable) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
                Text(
                    text = if (backupStatus.isGooglePasswordManagerAvailable) {
                        "Available"
                    } else {
                        "Not Available"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (backupStatus.isGooglePasswordManagerAvailable) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
            
            // Stored Keys Information
            if (backupStatus.storedKeyTypes.isNotEmpty()) {
                Text(
                    text = "Stored in Google Password Manager: ${backupStatus.storedKeyTypes.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(
                    onClick = onShowGooglePasswordManagerInfo,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Google Password Manager Info")
                }
                
                TextButton(
                    onClick = onShowBackupRecommendations,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Recommendations")
                }
            }
        }
    }
    
    // Google Password Manager Info Dialog
    if (showGoogleInfoDialog) {
        GooglePasswordManagerInfoDialog(
            onDismiss = { showGoogleInfoDialog = false },
            isAvailable = backupStatus.isGooglePasswordManagerAvailable,
            isSignedIn = backupStatus.isGoogleAccountSignedIn,
        )
    }
}

@Composable
private fun GooglePasswordManagerInfoDialog(
    onDismiss: () -> Unit,
    isAvailable: Boolean,
    isSignedIn: Boolean,
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text("Google Password Manager")
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = if (isAvailable && isSignedIn) {
                        "Google Password Manager is available and you are signed in. Your API keys can be securely stored and synced across your devices."
                    } else if (isAvailable) {
                        "Google Password Manager is available but you need to sign in to your Google account to use this feature."
                    } else {
                        "Google Password Manager is not available on this device. You can still use Android Auto Backup for local backup."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
                
                if (!isSignedIn && isAvailable) {
                    Text(
                        text = "To sign in, go to your device Settings > Google > Sign in to your Google account.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
    )
}

@Composable
private fun getBackupStrategyDescription(strategy: EnhancedBackupManager.BackupStrategy): String {
    return when (strategy) {
        EnhancedBackupManager.BackupStrategy.NO_BACKUP -> "No Backup"
        EnhancedBackupManager.BackupStrategy.LOCAL_ONLY -> "Local Only"
        EnhancedBackupManager.BackupStrategy.GOOGLE_ONLY -> "Google Only"
        EnhancedBackupManager.BackupStrategy.DUAL_BACKUP -> "Dual Backup"
    }
}

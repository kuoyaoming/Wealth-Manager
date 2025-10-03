package com.wealthmanager.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.unit.dp
import com.wealthmanager.R
import com.wealthmanager.backup.EnhancedBackupManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    var showRecommendationsDialog by remember { mutableStateOf(false) }

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
                    text = stringResource(id = R.string.backup_enhanced_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Description
            Text(
                text = stringResource(id = R.string.backup_enhanced_desc),
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
                    text = stringResource(id = R.string.backup_current_strategy_label),
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
                        text = stringResource(id = R.string.backup_android_auto_backup_label),
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
                        tint =
                            if (backupStatus.isGooglePasswordManagerAvailable) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                    )
                    Text(
                        text = stringResource(id = R.string.backup_gpm_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color =
                            if (backupStatus.isGooglePasswordManagerAvailable) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                    )
                }
                Text(
                    text =
                        if (backupStatus.isGooglePasswordManagerAvailable) {
                            stringResource(id = R.string.backup_availability_available)
                        } else {
                            stringResource(id = R.string.backup_availability_not_available)
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        if (backupStatus.isGooglePasswordManagerAvailable) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }

            // Stored Keys Information
            if (backupStatus.storedKeyTypes.isNotEmpty()) {
                Text(
                    text =
                        stringResource(
                            id = R.string.backup_stored_in_gpm,
                            backupStatus.storedKeyTypes.joinToString(", "),
                        ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            // Last Backup Times
            if (backupStatus.lastLocalBackupTime > 0 || backupStatus.lastGpmBackupTime > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(id = R.string.backup_last_backup_times),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )

                // Local Backup Time
                if (backupStatus.lastLocalBackupTime > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(id = R.string.backup_local_backup_time),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = formatBackupTime(backupStatus.lastLocalBackupTime),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                // GPM Backup Time
                if (backupStatus.lastGpmBackupTime > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(id = R.string.backup_gpm_backup_time),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = formatBackupTime(backupStatus.lastGpmBackupTime),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(
                    onClick = {
                        onShowGooglePasswordManagerInfo()
                        showGoogleInfoDialog = true
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(id = R.string.backup_gpm_info_button))
                }

                TextButton(
                    onClick = {
                        onShowBackupRecommendations()
                        showRecommendationsDialog = true
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(id = R.string.backup_recommendations_button))
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

    // Backup Recommendations Dialog
    if (showRecommendationsDialog) {
        BackupRecommendationsDialog(
            onDismiss = { showRecommendationsDialog = false },
            backupStatus = backupStatus,
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
                Text(stringResource(id = R.string.gpm_title))
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text =
                        if (isAvailable && isSignedIn) {
                            stringResource(id = R.string.gpm_info_available_signed_in)
                        } else if (isAvailable) {
                            stringResource(id = R.string.gpm_info_available_not_signed_in)
                        } else {
                            stringResource(id = R.string.gpm_info_unavailable)
                        },
                    style = MaterialTheme.typography.bodyMedium,
                )

                if (!isSignedIn && isAvailable) {
                    Text(
                        text = stringResource(id = R.string.gpm_sign_in_instruction),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.ok))
            }
        },
    )
}

@Composable
private fun BackupRecommendationsDialog(
    onDismiss: () -> Unit,
    backupStatus: EnhancedBackupManager.BackupStatus,
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(stringResource(id = R.string.backup_recommendations_title))
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.backup_recommendations_intro),
                    style = MaterialTheme.typography.bodyMedium,
                )

                // Local Backup Recommendation
                if (!backupStatus.isLocalBackupEnabled) {
                    Text(
                        text = stringResource(id = R.string.backup_rec_enable_local_backup),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                // Google Password Manager Recommendation
                if (backupStatus.isGooglePasswordManagerAvailable && !backupStatus.isGoogleAccountSignedIn) {
                    Text(
                        text = stringResource(id = R.string.backup_rec_sign_in_gpm),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                } else if (!backupStatus.isGooglePasswordManagerAvailable) {
                    Text(
                        text = stringResource(id = R.string.backup_rec_gpm_unavailable),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // General Security Recommendations
                Text(
                    text = stringResource(id = R.string.backup_rec_backup_regularly),
                    style = MaterialTheme.typography.bodySmall,
                )

                Text(
                    text = stringResource(id = R.string.backup_rec_keep_device_updated),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.ok))
            }
        },
    )
}

@Composable
private fun getBackupStrategyDescription(strategy: EnhancedBackupManager.BackupStrategy): String {
    return when (strategy) {
        EnhancedBackupManager.BackupStrategy.NO_BACKUP -> stringResource(id = R.string.backup_strategy_no_backup)
        EnhancedBackupManager.BackupStrategy.LOCAL_ONLY -> stringResource(id = R.string.backup_strategy_local_only)
        EnhancedBackupManager.BackupStrategy.GOOGLE_ONLY -> stringResource(id = R.string.backup_strategy_google_only)
        EnhancedBackupManager.BackupStrategy.DUAL_BACKUP -> stringResource(id = R.string.backup_strategy_dual_backup)
    }
}

@Composable
private fun formatBackupTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> stringResource(id = R.string.backup_time_just_now) // 1 minute
        diff < 3_600_000 -> stringResource(id = R.string.backup_time_minutes_ago, (diff / 60_000).toInt()) // 1 hour
        diff < 86_400_000 -> stringResource(id = R.string.backup_time_hours_ago, (diff / 3_600_000).toInt()) // 1 day
        diff < 604_800_000 -> stringResource(id = R.string.backup_time_days_ago, (diff / 86_400_000).toInt()) // 1 week
        else -> {
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            dateFormat.format(Date(timestamp))
        }
    }
}

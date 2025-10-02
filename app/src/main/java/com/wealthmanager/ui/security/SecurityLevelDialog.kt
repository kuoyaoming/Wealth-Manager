package com.wealthmanager.ui.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Dialog
import com.wealthmanager.R
import com.wealthmanager.security.SecurityLevelManager

/**
 * 安全級別選擇對話框
 * 讓用戶選擇適合的安全級別
 */
@Composable
fun SecurityLevelDialog(
    onDismiss: () -> Unit,
    onSecurityLevelSelected: (SecurityLevelManager.SecurityLevel) -> Unit,
    currentLevel: SecurityLevelManager.SecurityLevel = SecurityLevelManager.SecurityLevel.HIGH,
) {
    var selectedLevel by remember { mutableStateOf(currentLevel) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.security_level_choose),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.security_level_description),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(24.dp))

                SecurityLevelOption(
                    level = SecurityLevelManager.SecurityLevel.HIGH,
                    title = stringResource(R.string.security_level_high_title),
                    description = stringResource(R.string.security_level_high_desc),
                    icon = Icons.Default.Security,
                    isSelected = selectedLevel == SecurityLevelManager.SecurityLevel.HIGH,
                    onSelected = { selectedLevel = SecurityLevelManager.SecurityLevel.HIGH },
                )

                Spacer(modifier = Modifier.height(12.dp))

                SecurityLevelOption(
                    level = SecurityLevelManager.SecurityLevel.MEDIUM,
                    title = stringResource(R.string.security_level_medium_title),
                    description = stringResource(R.string.security_level_medium_desc),
                    icon = Icons.Default.Info,
                    isSelected = selectedLevel == SecurityLevelManager.SecurityLevel.MEDIUM,
                    onSelected = { selectedLevel = SecurityLevelManager.SecurityLevel.MEDIUM },
                )

                Spacer(modifier = Modifier.height(12.dp))

                SecurityLevelOption(
                    level = SecurityLevelManager.SecurityLevel.LOW,
                    title = stringResource(R.string.security_level_low_title),
                    description = stringResource(R.string.security_level_low_desc),
                    icon = Icons.Default.Info,
                    isSelected = selectedLevel == SecurityLevelManager.SecurityLevel.LOW,
                    onSelected = { selectedLevel = SecurityLevelManager.SecurityLevel.LOW },
                )

                Spacer(modifier = Modifier.height(12.dp))

                SecurityLevelOption(
                    level = SecurityLevelManager.SecurityLevel.FALLBACK,
                    title = stringResource(R.string.security_level_fallback_title),
                    description = stringResource(R.string.security_level_fallback_desc),
                    icon = Icons.Default.Warning,
                    isSelected = selectedLevel == SecurityLevelManager.SecurityLevel.FALLBACK,
                    onSelected = { selectedLevel = SecurityLevelManager.SecurityLevel.FALLBACK },
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(
                        onClick = {
                            onSecurityLevelSelected(selectedLevel)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityLevelOption(
    level: SecurityLevelManager.SecurityLevel,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 4.dp else 2.dp,
            ),
        onClick = onSelected,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelected,
            )

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint =
                    if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color =
                        if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
        }
    }
}

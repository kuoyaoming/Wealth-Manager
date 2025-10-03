package com.wealthmanager.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wealthmanager.R

/**
 * Dialog showing Google Password Manager information and status.
 *
 * @param onDismiss Callback when dialog is dismissed
 * @param isAvailable Whether Google Password Manager is available on this device
 * @param isSignedIn Whether user is signed in to Google account
 * @param onEnableAutofill Callback to open autofill settings
 */
@Composable
fun GooglePasswordManagerInfoDialog(
    onDismiss: () -> Unit,
    isAvailable: Boolean,
    isSignedIn: Boolean,
    onEnableAutofill: () -> Unit,
) {
    AlertDialog(
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
        dismissButton = null, // Temporarily disabled to avoid issues
        // dismissButton = if (isAvailable) {
        //     {
        //         TextButton(onClick = onEnableAutofill) {
        //             Text(stringResource(id = R.string.action_enable_autofill))
        //         }
        //     }
        // } else null,
    )
}

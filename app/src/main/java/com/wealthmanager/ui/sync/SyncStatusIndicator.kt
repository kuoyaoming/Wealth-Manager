package com.wealthmanager.ui.sync

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wealthmanager.R

/**
 * A detailed indicator that displays the current status of synchronization operations.
 */
@Composable
fun SyncStatusIndicator(
    syncFeedbackManager: SyncFeedbackManager,
    modifier: Modifier = Modifier,
) {
    val currentOperation by syncFeedbackManager.currentOperation.collectAsState()
    val syncResults by syncFeedbackManager.syncResults.collectAsState()

    val hasActiveSync = syncResults.values.any { it is SyncResult.InProgress }
    val hasErrors = syncResults.values.any { it is SyncResult.Failure }

    if (hasActiveSync || hasErrors) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when {
                hasActiveSync -> {
                    val infiniteTransition = rememberInfiniteTransition(label = "sync-icon-rotation")
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(animation = tween(1000)),
                        label = "sync-rotation"
                    )
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = stringResource(R.string.sync_in_progress),
                        modifier = Modifier.size(20.dp).graphicsLayer { rotationZ = rotation },
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                hasErrors -> {
                    Icon(
                        imageVector = Icons.Default.SyncProblem,
                        contentDescription = stringResource(R.string.sync_error),
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }

            Text(
                text = when {
                    hasActiveSync -> currentOperation?.description ?: stringResource(R.string.sync_in_progress)
                    hasErrors -> stringResource(R.string.sync_issues_detected)
                    else -> ""
                },
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    hasActiveSync -> MaterialTheme.colorScheme.primary
                    hasErrors -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.weight(1f))

            if (hasActiveSync) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

/**
 * A compact indicator that displays only an icon representing the sync status.
 */
@Composable
fun CompactSyncStatusIndicator(
    syncFeedbackManager: SyncFeedbackManager,
    modifier: Modifier = Modifier,
) {
    val syncResults by syncFeedbackManager.syncResults.collectAsState()

    val hasActiveSync = syncResults.values.any { it is SyncResult.InProgress }
    val hasErrors = syncResults.values.any { it is SyncResult.Failure }

    if (hasActiveSync || hasErrors) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            when {
                hasActiveSync -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                hasErrors -> {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = stringResource(R.string.sync_error),
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

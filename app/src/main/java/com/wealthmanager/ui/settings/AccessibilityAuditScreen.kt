package com.wealthmanager.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wealthmanager.R
import com.wealthmanager.accessibility.AccessibilityAuditCard
import com.wealthmanager.accessibility.AccessibilityAuditResult
import com.wealthmanager.accessibility.AccessibilityIssue
import com.wealthmanager.accessibility.IssueType
import com.wealthmanager.accessibility.Severity
import com.wealthmanager.accessibility.rememberAccessibilityState
import com.wealthmanager.ui.responsive.rememberResponsiveLayout

/**
 * Accessibility audit screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilityAuditScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val accessibilityState = rememberAccessibilityState()
    val responsiveLayout = rememberResponsiveLayout()

    val auditResults =
        remember {
            listOf(
                AccessibilityAuditResult(
                    hasContentDescription = true,
                    hasSemanticRole = true,
                    hasProperContrast = true,
                    hasTouchTarget = true,
                    hasKeyboardNavigation = true,
                    issues = emptyList(),
                ),
                AccessibilityAuditResult(
                    hasContentDescription = false,
                    hasSemanticRole = true,
                    hasProperContrast = false,
                    hasTouchTarget = true,
                    hasKeyboardNavigation = false,
                    issues =
                        listOf(
                            AccessibilityIssue(
                                type = IssueType.MISSING_CONTENT_DESCRIPTION,
                                message = "Some icons are missing content descriptions",
                                severity = Severity.MEDIUM,
                            ),
                            AccessibilityIssue(
                                type = IssueType.INSUFFICIENT_CONTRAST,
                                message = "Insufficient contrast between text and background",
                                severity = Severity.HIGH,
                            ),
                            AccessibilityIssue(
                                type = IssueType.KEYBOARD_NAVIGATION_ISSUE,
                                message = "Incomplete keyboard navigation support",
                                severity = Severity.MEDIUM,
                            ),
                        ),
                ),
            )
        }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Text(
                        text = stringResource(R.string.accessibility_audit_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.accessibility_audit_back),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            contentPadding = PaddingValues(responsiveLayout.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(responsiveLayout.paddingMedium),
        ) {
            item {
                AccessibilityStatusOverview(accessibilityState = accessibilityState)
            }

            items(auditResults) { auditResult ->
                AccessibilityAuditCard(auditResult = auditResult)
            }

            item {
                AccessibilityImprovementSuggestions()
            }
        }
    }
}

@Composable
private fun AccessibilityStatusOverview(accessibilityState: com.wealthmanager.accessibility.AccessibilityState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.accessibility_audit_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            AccessibilityStatusItem(
                label = stringResource(R.string.accessibility_audit_talkback_label),
                enabled = accessibilityState.isTalkBackEnabled,
                description = stringResource(R.string.accessibility_audit_talkback_desc),
            )

            AccessibilityStatusItem(
                label = stringResource(R.string.accessibility_audit_large_font_label),
                enabled = accessibilityState.isLargeFontEnabled,
                description =
                    stringResource(
                        R.string.accessibility_audit_large_font_desc,
                        String.format("%.1f", accessibilityState.fontScale),
                    ),
            )

            AccessibilityStatusItem(
                label = stringResource(R.string.accessibility_audit_high_contrast_label),
                enabled = accessibilityState.isHighContrastEnabled,
                description = stringResource(R.string.accessibility_audit_high_contrast_desc),
            )

            if (accessibilityState.isAccessibilityMode) {
                Text(
                    text = stringResource(R.string.accessibility_audit_talkback_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun AccessibilityStatusItem(
    label: String,
    enabled: Boolean,
    description: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Icon(
            imageVector = if (enabled) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription =
                if (enabled) {
                    stringResource(
                        R.string.settings_status_enabled,
                    )
                } else {
                    stringResource(R.string.settings_status_disabled)
                },
            tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun AccessibilityImprovementSuggestions() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.accessibility_audit_issues_found),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )

            val suggestions =
                listOf(
                    stringResource(R.string.accessibility_audit_issue_missing_content),
                    stringResource(R.string.accessibility_audit_issue_contrast),
                    stringResource(R.string.accessibility_audit_issue_keyboard),
                )

            suggestions.forEach { suggestion ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = suggestion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
        }
    }
}

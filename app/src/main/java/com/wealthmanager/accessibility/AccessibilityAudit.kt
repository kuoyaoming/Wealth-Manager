package com.wealthmanager.accessibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 無障礙性審計結果
 */
data class AccessibilityAuditResult(
    val hasContentDescription: Boolean,
    val hasSemanticRole: Boolean,
    val hasProperContrast: Boolean,
    val hasTouchTarget: Boolean,
    val hasKeyboardNavigation: Boolean,
    val issues: List<AccessibilityIssue> = emptyList()
)

/**
 * 無障礙性問題
 */
data class AccessibilityIssue(
    val type: IssueType,
    val message: String,
    val severity: Severity
)

enum class IssueType {
    MISSING_CONTENT_DESCRIPTION,
    INSUFFICIENT_CONTRAST,
    TOO_SMALL_TOUCH_TARGET,
    MISSING_SEMANTIC_ROLE,
    KEYBOARD_NAVIGATION_ISSUE
}

enum class Severity {
    HIGH,
    MEDIUM,
    LOW
}

/**
 * 無障礙性審計組件
 */
@Composable
fun AccessibilityAuditCard(
    auditResult: AccessibilityAuditResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                auditResult.issues.any { it.severity == Severity.HIGH } -> 
                    MaterialTheme.colorScheme.errorContainer
                auditResult.issues.any { it.severity == Severity.MEDIUM } -> 
                    MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 標題
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = when {
                        auditResult.issues.any { it.severity == Severity.HIGH } -> Icons.Default.Error
                        auditResult.issues.any { it.severity == Severity.MEDIUM } -> Icons.Default.Warning
                        else -> Icons.Default.CheckCircle
                    },
                    contentDescription = null,
                    tint = when {
                        auditResult.issues.any { it.severity == Severity.HIGH } -> 
                            MaterialTheme.colorScheme.error
                        auditResult.issues.any { it.severity == Severity.MEDIUM } -> 
                            MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
                Text(
                    text = "無障礙性檢查",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
            
            // 檢查項目
            AccessibilityCheckItem(
                label = "Content Description",
                passed = auditResult.hasContentDescription
            )
            AccessibilityCheckItem(
                label = "Semantic Role",
                passed = auditResult.hasSemanticRole
            )
            AccessibilityCheckItem(
                label = "Color Contrast",
                passed = auditResult.hasProperContrast
            )
            AccessibilityCheckItem(
                label = "Touch Target",
                passed = auditResult.hasTouchTarget
            )
            AccessibilityCheckItem(
                label = "Keyboard Navigation",
                passed = auditResult.hasKeyboardNavigation
            )
            
            // 問題列表
            if (auditResult.issues.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "發現的問題:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
                auditResult.issues.forEach { issue ->
                    AccessibilityIssueItem(issue = issue)
                }
            }
        }
    }
}

@Composable
private fun AccessibilityCheckItem(
    label: String,
    passed: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            imageVector = if (passed) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = if (passed) "通過" else "未通過",
            tint = if (passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun AccessibilityIssueItem(
    issue: AccessibilityIssue
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = when (issue.severity) {
                Severity.HIGH -> Icons.Default.Error
                Severity.MEDIUM -> Icons.Default.Warning
                Severity.LOW -> Icons.Default.CheckCircle
            },
            contentDescription = null,
            modifier = Modifier.width(16.dp),
            tint = when (issue.severity) {
                Severity.HIGH -> MaterialTheme.colorScheme.error
                Severity.MEDIUM -> MaterialTheme.colorScheme.tertiary
                Severity.LOW -> MaterialTheme.colorScheme.primary
            }
        )
        Text(
            text = issue.message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

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
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wealthmanager.R
import com.wealthmanager.accessibility.AccessibilityAuditResult
import com.wealthmanager.accessibility.AccessibilityIssue
import com.wealthmanager.accessibility.AccessibilityManager
import com.wealthmanager.accessibility.AccessibilityAuditCard
import com.wealthmanager.accessibility.IssueType
import com.wealthmanager.accessibility.Severity
import com.wealthmanager.accessibility.rememberAccessibilityState
import com.wealthmanager.ui.responsive.rememberResponsiveLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 無障礙性審計屏幕
 * 提供全面的無障礙性檢查和建議
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilityAuditScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val accessibilityState = rememberAccessibilityState()
    val responsiveLayout = rememberResponsiveLayout()
    
    // 模擬審計結果（實際應用中應該從ViewModel獲取）
    val auditResults = remember {
        listOf(
            AccessibilityAuditResult(
                hasContentDescription = true,
                hasSemanticRole = true,
                hasProperContrast = true,
                hasTouchTarget = true,
                hasKeyboardNavigation = true,
                issues = emptyList()
            ),
            AccessibilityAuditResult(
                hasContentDescription = false,
                hasSemanticRole = true,
                hasProperContrast = false,
                hasTouchTarget = true,
                hasKeyboardNavigation = false,
                issues = listOf(
                    AccessibilityIssue(
                        type = IssueType.MISSING_CONTENT_DESCRIPTION,
                        message = "Some icons are missing content descriptions",
                        severity = Severity.MEDIUM
                    ),
                    AccessibilityIssue(
                        type = IssueType.INSUFFICIENT_CONTRAST,
                        message = "Insufficient contrast between text and background",
                        severity = Severity.HIGH
                    ),
                    AccessibilityIssue(
                        type = IssueType.KEYBOARD_NAVIGATION_ISSUE,
                        message = "Incomplete keyboard navigation support",
                        severity = Severity.MEDIUM
                    )
                )
            )
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
                        text = "無障礙性檢查",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Settings"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(responsiveLayout.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(responsiveLayout.paddingMedium)
        ) {
            // 無障礙性狀態概覽
            item {
                AccessibilityStatusOverview(accessibilityState = accessibilityState)
            }
            
            // 審計結果
            items(auditResults) { auditResult ->
                AccessibilityAuditCard(auditResult = auditResult)
            }
            
            // 改進建議
            item {
                AccessibilityImprovementSuggestions()
            }
        }
    }
}

@Composable
private fun AccessibilityStatusOverview(
    accessibilityState: com.wealthmanager.accessibility.AccessibilityState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "無障礙性狀態",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            AccessibilityStatusItem(
                label = "TalkBack",
                enabled = accessibilityState.isTalkBackEnabled,
                description = "Screen reader support"
            )
            
            AccessibilityStatusItem(
                label = "Large Font",
                enabled = accessibilityState.isLargeFontEnabled,
                description = "Font scale: ${String.format("%.1f", accessibilityState.fontScale)}x"
            )
            
            AccessibilityStatusItem(
                label = "High Contrast",
                enabled = accessibilityState.isHighContrastEnabled,
                description = "High contrast mode"
            )
            
            if (accessibilityState.isAccessibilityMode) {
                Text(
                    text = "檢測到無障礙性功能已啟用，應用已自動調整以提供最佳體驗。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun AccessibilityStatusItem(
    label: String,
    enabled: Boolean,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Icon(
            imageVector = if (enabled) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = if (enabled) "已啟用" else "未啟用",
            tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun AccessibilityImprovementSuggestions() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "改進建議",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            val suggestions = listOf(
                "確保所有互動元素都有適當的內容描述",
                "維持至少4.5:1的顏色對比度比例",
                "觸控目標至少44dp x 44dp",
                "支援鍵盤導航和焦點管理",
                "提供語義化標籤和角色"
            )
            
            suggestions.forEach { suggestion ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = suggestion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

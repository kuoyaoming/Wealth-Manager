package com.wealthmanager.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wealthmanager.data.service.ApiUsageManager
import com.wealthmanager.data.service.ApiUsageStats

/**
 * API使用限制警告組件
 * 當API使用量接近或達到限制時顯示警告
 */
@Composable
fun ApiLimitWarning(
    apiUsageManager: ApiUsageManager,
    onUpgrade: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val usageStats by remember {
        derivedStateOf { apiUsageManager.getUsageStats() }
    }
    
    // 只在接近或達到限制時顯示
    if (!usageStats.isNearLimit && !usageStats.isAtLimit) {
        return
    }
    
    val (title, message, actionText) = when {
        usageStats.isAtLimit -> Triple(
            "API使用量已達上限",
            "今日API請求次數已達${usageStats.dailyLimit}次限制。請等待明天或升級到付費版本。",
            "升級到付費版本"
        )
        usageStats.isNearLimit -> Triple(
            "API使用量接近限制",
            "今日API使用量已達${usageStats.dailyUsagePercent.toInt()}%，剩餘${usageStats.requestsToday}次請求。",
            "查看使用詳情"
        )
        else -> return
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (usageStats.isAtLimit) 
                MaterialTheme.colorScheme.errorContainer 
            else 
                MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (usageStats.isAtLimit) 
                        MaterialTheme.colorScheme.error 
                    else 
                        MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(24.dp)
                )
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (usageStats.isAtLimit) 
                        MaterialTheme.colorScheme.onErrorContainer 
                    else 
                        MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (usageStats.isAtLimit) 
                    MaterialTheme.colorScheme.onErrorContainer 
                else 
                    MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            // 使用量進度條
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "今日使用量",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (usageStats.isAtLimit) 
                            MaterialTheme.colorScheme.onErrorContainer 
                        else 
                            MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    
                    Text(
                        text = "${usageStats.requestsToday}/${usageStats.dailyLimit}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = if (usageStats.isAtLimit) 
                            MaterialTheme.colorScheme.onErrorContainer 
                        else 
                            MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                
                LinearProgressIndicator(
                    progress = usageStats.dailyUsagePercent / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (usageStats.isAtLimit) 
                        MaterialTheme.colorScheme.error 
                    else 
                        MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.surface
                )
                
                Text(
                    text = "${usageStats.dailyUsagePercent.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (usageStats.isAtLimit) 
                        MaterialTheme.colorScheme.onErrorContainer 
                    else 
                        MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.align(Alignment.End)
                )
            }
            
            // 操作按鈕
            if (usageStats.isAtLimit) {
                Button(
                    onClick = onUpgrade,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = actionText,
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onUpgrade,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text(text = actionText)
                }
            }
        }
    }
}

/**
 * 簡化版API限制警告（用於通知欄）
 */
@Composable
fun CompactApiLimitWarning(
    apiUsageManager: ApiUsageManager,
    modifier: Modifier = Modifier
) {
    val usageStats by remember {
        derivedStateOf { apiUsageManager.getUsageStats() }
    }
    
    if (!usageStats.isNearLimit && !usageStats.isAtLimit) {
        return
    }
    
    val color = if (usageStats.isAtLimit) 
        MaterialTheme.colorScheme.error 
    else 
        MaterialTheme.colorScheme.tertiary
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        
        Text(
            text = if (usageStats.isAtLimit) 
                "API已達上限" 
            else 
                "API使用量${usageStats.dailyUsagePercent.toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
        
        LinearProgressIndicator(
            progress = usageStats.dailyUsagePercent / 100f,
            modifier = Modifier.width(40.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surface
        )
    }
}
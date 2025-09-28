package com.wealthmanager.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import javax.inject.Inject

/**
 * API使用狀態指示器
 * 顯示Alpha Vantage API的使用情況和限制
 */
@Composable
fun ApiUsageIndicator(
    apiUsageManager: ApiUsageManager,
    modifier: Modifier = Modifier
) {
    val usageStats by remember {
        derivedStateOf { apiUsageManager.getUsageStats() }
    }
    
    // 根據使用情況決定顯示顏色和圖標
    val (icon, color, message) = when {
        usageStats.isAtLimit -> Triple(
            Icons.Default.Warning,
            MaterialTheme.colorScheme.error,
            "API限制已達上限"
        )
        usageStats.isNearLimit -> Triple(
            Icons.Default.Warning,
            MaterialTheme.colorScheme.tertiary,
            "API使用量接近限制"
        )
        else -> Triple(
            Icons.Default.Info,
            MaterialTheme.colorScheme.primary,
            "API使用正常"
        )
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (usageStats.isAtLimit) 
                MaterialTheme.colorScheme.errorContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                
                Column {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = color
                    )
                    
                    Text(
                        text = "今日: ${usageStats.requestsToday}/${usageStats.dailyLimit} | 本分鐘: ${usageStats.requestsThisMinute}/${usageStats.minuteLimit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // 使用量進度條
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 每日使用量進度條
                LinearProgressIndicator(
                    progress = { usageStats.dailyUsagePercent / 100f },
                    modifier = Modifier.width(60.dp),
                    color = color,
                    trackColor = MaterialTheme.colorScheme.surface
                )
                
                Text(
                    text = "${usageStats.dailyUsagePercent.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }
        }
    }
}

/**
 * 簡化版API使用指示器（用於設置頁面）
 */
@Composable
fun CompactApiUsageIndicator(
    apiUsageManager: ApiUsageManager,
    modifier: Modifier = Modifier
) {
    val usageStats by remember {
        derivedStateOf { apiUsageManager.getUsageStats() }
    }
    
    val color = when {
        usageStats.isAtLimit -> MaterialTheme.colorScheme.error
        usageStats.isNearLimit -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        
        Text(
            text = "API使用: ${usageStats.requestsToday}/${usageStats.dailyLimit}",
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
        
        LinearProgressIndicator(
            progress = { usageStats.dailyUsagePercent / 100f },
            modifier = Modifier.width(40.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surface
        )
    }
}
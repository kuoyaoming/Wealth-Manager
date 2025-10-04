package com.wealthmanager.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wealthmanager.R
import com.wealthmanager.ui.components.PrimaryCard
import com.wealthmanager.ui.components.SecondaryCard
import com.wealthmanager.ui.components.StatusCard
import com.wealthmanager.ui.components.StatusType
import com.wealthmanager.ui.responsive.ResponsiveLayout
import com.wealthmanager.ui.responsive.rememberResponsiveLayout
import com.wealthmanager.utils.MoneyFormatter
import com.wealthmanager.utils.rememberMoneyText

/**
 * 使用统一卡片设计系统的仪表板组件
 * 展示如何从旧系统迁移到新系统
 */

/**
 * 总资产卡片 - 使用主要卡片类型
 * 迁移前: 自定义Card with elevation 4-6dp
 * 迁移后: PrimaryCard with 统一设计系统
 */
@Composable
fun TotalAssetsCardUnified(
    totalValue: Double,
    isLoading: Boolean,
) {
    PrimaryCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.total_assets),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                val responsiveLayout = rememberResponsiveLayout()
                CircularProgressIndicator(
                    modifier = Modifier.size(if (responsiveLayout.isTablet) 40.dp else 32.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                Text(
                    text = rememberMoneyText(
                        totalValue,
                        "TWD",
                        style = MoneyFormatter.Style.CurrencyCode,
                        moneyContext = MoneyFormatter.MoneyContext.Total,
                    ),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

/**
 * 现金资产卡片 - 使用次要卡片类型
 * 迁移前: 自定义Card with elevation 2-4dp
 * 迁移后: SecondaryCard with 统一设计系统
 */
@Composable
fun CashAssetsCardUnified(
    cashValue: Double,
    totalAssets: Double,
    isLoading: Boolean,
) {
    SecondaryCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                text = stringResource(R.string.cash_assets),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.loading),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                val percentage = if (totalAssets > 0) (cashValue / totalAssets * 100) else 0.0
                Text(
                    text = rememberMoneyText(
                        cashValue,
                        "TWD",
                        style = MoneyFormatter.Style.CurrencyCode,
                        moneyContext = MoneyFormatter.MoneyContext.Total,
                    ) + " (${String.format("%.1f", percentage)}%)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

/**
 * 股票资产卡片 - 使用次要卡片类型
 * 迁移前: 自定义Card with elevation 2-4dp
 * 迁移后: SecondaryCard with 统一设计系统
 */
@Composable
fun StockAssetsCardUnified(
    stockValue: Double,
    totalAssets: Double,
    isLoading: Boolean,
) {
    SecondaryCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                text = stringResource(R.string.stock_assets),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.loading),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                val percentage = if (totalAssets > 0) (stockValue / totalAssets * 100) else 0.0
                Text(
                    text = rememberMoneyText(
                        stockValue,
                        "TWD",
                        style = MoneyFormatter.Style.CurrencyCode,
                        moneyContext = MoneyFormatter.MoneyContext.Total,
                    ) + " (${String.format("%.1f", percentage)}%)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

/**
 * API状态卡片 - 使用状态卡片类型
 * 展示如何使用状态卡片来显示不同的状态信息
 */
@Composable
fun ApiStatusCardUnified(
    isConnected: Boolean,
    lastSyncTime: String?,
    errorMessage: String?,
) {
    when {
        errorMessage != null -> {
            StatusCard(
                statusType = StatusType.Error,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "API连接错误: $errorMessage",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        isConnected -> {
            StatusCard(
                statusType = StatusType.Success,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "API连接正常",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (lastSyncTime != null) {
                        Text(
                            text = "最后同步: $lastSyncTime",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        else -> {
            StatusCard(
                statusType = StatusType.Warning,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "API连接状态未知",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * 迁移对比说明:
 * 
 * 1. 代码简化:
 *    - 移除了手动设置elevation、colors、shape的代码
 *    - 统一使用UnifiedCard组件
 * 
 * 2. 一致性提升:
 *    - 所有卡片使用相同的设计系统
 *    - 响应式设计自动处理
 * 
 * 3. 可维护性:
 *    - 设计变更只需修改UnifiedCard组件
 *    - 新增卡片类型只需使用对应的便捷组件
 * 
 * 4. 语义化:
 *    - PrimaryCard用于重要数据
 *    - SecondaryCard用于次要信息
 *    - StatusCard用于状态提示
 */
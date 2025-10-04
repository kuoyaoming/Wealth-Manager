# 卡片设计系统迁移指南

## 🎯 迁移目标

将现有的不一致卡片样式统一到新的设计系统，提升视觉一致性和开发效率。

## 📋 迁移清单

### Phase 1: 核心组件迁移 (高优先级)

#### 1. 仪表板卡片
- [ ] `TotalAssetsCardOptimized` → `TotalAssetsCardUnified`
- [ ] `CashAssetsCardOptimized` → `CashAssetsCardUnified`  
- [ ] `StockAssetsCardOptimized` → `StockAssetsCardUnified`

#### 2. 资产项目卡片
- [ ] `CashAssetItem` → 使用 `SecondaryCard`
- [ ] `StockAssetItem` → 使用 `SecondaryCard`

#### 3. 设置卡片
- [ ] `LanguageSettingsCard` → 使用 `SecondaryCard`
- [ ] `HapticFeedbackSettingsCard` → 使用 `SecondaryCard`
- [ ] `ApiKeyApplicationCard` → 使用 `PrimaryCard`
- [ ] `ApiKeyManagementCard` → 使用 `SecondaryCard`
- [ ] `BiometricSettingsCard` → 使用 `SecondaryCard`
- [ ] `BackupSettingsCard` → 使用 `SecondaryCard`
- [ ] `AboutSettingsCard` → 使用 `SecondaryCard`

### Phase 2: 状态和反馈卡片 (中优先级)

#### 1. 状态卡片
- [ ] `ApiErrorBanner` → 使用 `StatusCard(StatusType.Error)`
- [ ] `ApiKeyEmptyStateCard` → 使用 `StatusCard(StatusType.Info)`
- [ ] `ApiKeyValidationFeedback` → 使用 `StatusCard`

#### 2. 对话框卡片
- [ ] `SecurityLevelDialog` → 使用 `PrimaryCard`
- [ ] `BiometricFallbackDialog` → 使用 `SecondaryCard`
- [ ] `ApiKeyGuideDialog` → 使用 `PrimaryCard`
- [ ] `WelcomeOnboardingDialog` → 使用 `PrimaryCard`

### Phase 3: 其他组件 (低优先级)

#### 1. 图表卡片
- [ ] `PieChartComponentFixed` → 使用 `SecondaryCard`
- [ ] `TreemapChartComponent` → 使用 `SecondaryCard`

#### 2. 其他UI组件
- [ ] `HighRefreshRateCard` → 使用 `SecondaryCard`
- [ ] `ResponsiveCard` → 使用 `UnifiedCard`

## 🔄 迁移步骤

### 步骤1: 导入新组件

```kotlin
import com.wealthmanager.ui.components.PrimaryCard
import com.wealthmanager.ui.components.SecondaryCard
import com.wealthmanager.ui.components.OutlinedCard
import com.wealthmanager.ui.components.StatusCard
import com.wealthmanager.ui.components.StatusType
```

### 步骤2: 替换Card组件

#### 迁移前:
```kotlin
Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
    ),
) {
    Column(
        modifier = Modifier.padding(16.dp),
        // content
    ) {
        // content
    }
}
```

#### 迁移后:
```kotlin
SecondaryCard(
    modifier = Modifier.fillMaxWidth()
) {
    Column {
        // content
    }
}
```

### 步骤3: 选择正确的卡片类型

#### 主要数据展示 → PrimaryCard
```kotlin
PrimaryCard {
    // 总资产、重要指标等
}
```

#### 设置项、次要信息 → SecondaryCard
```kotlin
SecondaryCard {
    // 设置项、列表项等
}
```

#### 可选内容、辅助信息 → OutlinedCard
```kotlin
OutlinedCard {
    // 可选功能、辅助信息等
}
```

#### 状态提示、警告信息 → StatusCard
```kotlin
StatusCard(statusType = StatusType.Success) {
    // 成功状态
}

StatusCard(statusType = StatusType.Warning) {
    // 警告状态
}

StatusCard(statusType = StatusType.Error) {
    // 错误状态
}

StatusCard(statusType = StatusType.Info) {
    // 信息提示
}
```

### 步骤4: 处理点击事件

```kotlin
SecondaryCard(
    onClick = { /* 处理点击 */ }
) {
    // content
}
```

### 步骤5: 测试和验证

1. **视觉测试**: 确保卡片样式符合设计规范
2. **响应式测试**: 在不同屏幕尺寸下测试
3. **交互测试**: 验证点击、悬停等交互效果
4. **可访问性测试**: 确保颜色对比度符合标准

## 📊 迁移效果对比

### 代码简化
- **迁移前**: 每个卡片需要15-20行代码设置样式
- **迁移后**: 每个卡片只需要3-5行代码

### 一致性提升
- **迁移前**: elevation值不统一 (0dp, 2dp, 4dp, 6dp)
- **迁移后**: 统一的elevation层级系统

### 响应式设计
- **迁移前**: 手动处理不同屏幕尺寸
- **迁移后**: 自动响应式调整

### 维护性
- **迁移前**: 设计变更需要修改多个文件
- **迁移后**: 设计变更只需修改UnifiedCard组件

## 🚨 注意事项

### 1. 向后兼容性
- 保持现有API不变
- 逐步迁移，避免大规模重构
- 提供迁移工具和文档

### 2. 性能考虑
- 新组件使用Compose最佳实践
- 避免不必要的重组
- 合理使用remember和derivedStateOf

### 3. 测试策略
- 单元测试: 验证组件行为
- UI测试: 验证视觉效果
- 集成测试: 验证完整流程

### 4. 文档更新
- 更新组件文档
- 提供使用示例
- 记录迁移过程

## 🎉 预期收益

### 开发效率
- 减少重复代码
- 提高开发速度
- 降低维护成本

### 用户体验
- 一致的视觉体验
- 更好的信息层次
- 现代化的设计语言

### 设计系统
- 可扩展的组件库
- 统一的设计规范
- 易于维护和更新

## 📅 时间计划

- **Week 1**: 完成核心组件迁移
- **Week 2**: 完成状态卡片迁移
- **Week 3**: 完成其他组件迁移
- **Week 4**: 测试、优化和文档更新

## 🔗 相关资源

- [统一卡片设计系统文档](./card-design-analysis-2025.md)
- [UnifiedCard组件源码](../app/src/main/java/com/wealthmanager/ui/components/UnifiedCard.kt)
- [迁移示例代码](../app/src/main/java/com/wealthmanager/ui/dashboard/DashboardComponentsUnified.kt)
- [设计系统示例](../app/src/main/java/com/wealthmanager/ui/components/CardDesignSystemExamples.kt)
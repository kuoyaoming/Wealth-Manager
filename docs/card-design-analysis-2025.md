# Wealth Manager App - 卡片设计统整分析 (2025)

## 📋 当前卡片样式分析

### 1. 现有卡片类型分类

#### A. 数据展示卡片 (Data Display Cards)
- **TotalAssetsCardOptimized**: 总资产展示
- **CashAssetsCardOptimized**: 现金资产展示  
- **StockAssetsCardOptimized**: 股票资产展示
- **PieChartCardOptimized**: 饼图展示

**共同特征:**
- `elevation = 2-6.dp` (根据设备类型调整)
- `containerColor = MaterialTheme.colorScheme.surface`
- 使用 `responsiveLayout.paddingMedium/Large`

#### B. 资产项目卡片 (Asset Item Cards)
- **CashAssetItem**: 现金资产项目
- **StockAssetItem**: 股票资产项目

**共同特征:**
- `elevation = 2.dp`
- `modifier = Modifier.fillMaxWidth()`
- `padding = 16.dp`
- 包含编辑/删除操作按钮

#### C. 设置卡片 (Settings Cards)
- **LanguageSettingsCard**: 语言设置
- **HapticFeedbackSettingsCard**: 触觉反馈设置
- **ApiKeyApplicationCard**: API密钥应用
- **ApiKeyManagementCard**: API密钥管理
- **BiometricSettingsCard**: 生物识别设置
- **BackupSettingsCard**: 备份设置
- **AboutSettingsCard**: 关于设置

**共同特征:**
- `elevation = 2.dp`
- `modifier = Modifier.fillMaxWidth()`
- `padding = 16.dp`
- 包含图标 + 标题 + 描述的结构

#### D. 对话框卡片 (Dialog Cards)
- **SecurityLevelDialog**: 安全级别对话框
- **BiometricFallbackDialog**: 生物识别备用对话框
- **ApiKeyGuideDialog**: API密钥指南对话框
- **WelcomeOnboardingDialog**: 欢迎引导对话框

**共同特征:**
- 通常作为对话框内容容器
- 使用不同的elevation值
- 包含交互元素

#### E. 状态卡片 (Status Cards)
- **ApiErrorBanner**: API错误横幅
- **ApiKeyEmptyStateCard**: API密钥空状态卡片
- **ApiKeyValidationFeedback**: API密钥验证反馈

**共同特征:**
- 用于显示状态信息
- 通常有特殊的颜色或样式
- 包含操作按钮

### 2. 当前设计问题

#### A. 不一致的Elevation值
- 数据展示卡片: `2-6.dp` (响应式)
- 资产项目卡片: `2.dp`
- 设置卡片: `2.dp`
- 对话框卡片: 变化较大

#### B. 不一致的Padding值
- 大部分使用 `16.dp`
- 响应式卡片使用 `responsiveLayout.paddingMedium/Large`
- 缺乏统一标准

#### C. 颜色使用不统一
- 有些硬编码颜色值
- 缺乏语义化颜色使用
- 没有充分利用Material Design 3的颜色系统

#### D. 圆角半径不统一
- 默认使用Material Design 3的默认圆角
- 没有根据卡片类型定义不同的圆角

## 🎯 2025年Material Design 3 最佳实践

### 1. Elevation层级系统
```
Level 0: 0dp (Surface)
Level 1: 1dp (Cards, Buttons)
Level 2: 3dp (Floating Action Button)
Level 3: 6dp (Bottom Sheets)
Level 4: 8dp (Dialogs)
Level 5: 12dp (Modal Bottom Sheets)
```

### 2. 卡片类型定义
- **Outlined Cards**: 用于次要内容
- **Elevated Cards**: 用于主要内容
- **Filled Cards**: 用于强调内容

### 3. 间距系统
- **Small**: 8dp
- **Medium**: 16dp  
- **Large**: 24dp
- **Extra Large**: 32dp

### 4. 圆角系统
- **Small**: 4dp
- **Medium**: 8dp (默认)
- **Large**: 12dp
- **Extra Large**: 16dp

## 📐 建议的统一设计系统

### 1. 卡片类型定义

#### A. PrimaryCard (主要卡片)
- **用途**: 重要数据展示、主要功能
- **Elevation**: 4dp
- **Padding**: 20dp
- **圆角**: 12dp
- **颜色**: surface + primary accent

#### B. SecondaryCard (次要卡片)  
- **用途**: 设置项、次要信息
- **Elevation**: 2dp
- **Padding**: 16dp
- **圆角**: 8dp
- **颜色**: surface

#### C. OutlinedCard (轮廓卡片)
- **用途**: 可选内容、辅助信息
- **Elevation**: 0dp
- **Padding**: 16dp
- **圆角**: 8dp
- **颜色**: surface + outline

#### D. StatusCard (状态卡片)
- **用途**: 状态提示、警告信息
- **Elevation**: 3dp
- **Padding**: 16dp
- **圆角**: 8dp
- **颜色**: 根据状态动态变化

### 2. 响应式设计
- **手机**: 使用标准尺寸
- **平板**: 增加20%的padding和elevation
- **大屏**: 增加40%的padding和elevation

### 3. 颜色语义化
- **Primary**: 主要内容和操作
- **Secondary**: 次要信息和设置
- **Success**: 成功状态和正面数据
- **Warning**: 警告和注意事项
- **Error**: 错误和负面数据
- **Info**: 信息和提示

## 🚀 实施计划

### Phase 1: 创建统一卡片组件
1. 创建 `UnifiedCard.kt` 组件
2. 定义卡片类型枚举
3. 实现响应式设计

### Phase 2: 更新现有卡片
1. 按优先级更新卡片组件
2. 保持向后兼容性
3. 逐步迁移

### Phase 3: 测试和优化
1. 在不同设备上测试
2. 验证可访问性
3. 性能优化

## 📊 预期效果

### 视觉一致性
- 统一的elevation层级
- 一致的间距和圆角
- 语义化的颜色使用

### 用户体验
- 更好的视觉层次
- 清晰的信息架构
- 现代化的设计语言

### 开发效率
- 可复用的组件
- 减少重复代码
- 易于维护和更新
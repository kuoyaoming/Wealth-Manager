# UI 卡片設計指南 - 基於 Android 2025/10 開發指南

## 📋 概述

本指南基於 Android 2025/10 開發指南和 Material Design 3 最佳實踐，為 Wealth Manager 應用程式提供統一的卡片設計系統。

## 🎯 設計原則

### 1. 一致性 (Consistency)
- 統一的 elevation 層級
- 標準化的圓角半徑
- 一致的内邊距規範
- 語義化顏色使用

### 2. 可訪問性 (Accessibility)
- 符合 WCAG 2.1 AA 標準
- 適當的對比度
- 觸控目標大小 ≥ 48dp
- 語義化標籤

### 3. 響應式設計 (Responsive)
- 適配不同螢幕尺寸
- 平板和手機優化
- 動態調整間距和大小

## 📐 卡片類型規範

### Primary Card (主要卡片)
- **用途**: 重要數據展示、主要功能入口
- **Elevation**: 4dp
- **Padding**: 20dp
- **圓角**: 12dp
- **使用場景**: 總資產顯示、主要功能按鈕

### Secondary Card (次要卡片)
- **用途**: 設置項、次要信息、列表項
- **Elevation**: 2dp
- **Padding**: 16dp
- **圓角**: 8dp
- **使用場景**: 設置選項、次要功能、列表項

### Outlined Card (輪廓卡片)
- **用途**: 可選內容、輔助信息
- **Elevation**: 0dp
- **Padding**: 16dp
- **圓角**: 8dp
- **使用場景**: 可選設置、輔助信息

### Status Card (狀態卡片)
- **用途**: 狀態提示、警告信息
- **Elevation**: 3dp
- **Padding**: 16dp
- **圓角**: 8dp
- **使用場景**: 成功/錯誤/警告/信息提示

### Dialog Card (對話框卡片)
- **用途**: 模態對話框、重要確認
- **Elevation**: 8dp
- **Padding**: 24dp
- **圓角**: 16dp
- **使用場景**: 對話框、彈窗、重要確認

### Banner Card (橫幅卡片)
- **用途**: 頂部橫幅、通知
- **Elevation**: 4dp
- **Padding**: 16dp
- **圓角**: 8dp
- **使用場景**: 錯誤橫幅、通知橫幅、狀態橫幅

## 🎨 顏色規範

### 容器顏色
- **Surface**: `MaterialTheme.colorScheme.surface`
- **Primary Container**: `MaterialTheme.colorScheme.primaryContainer`
- **Error Container**: `MaterialTheme.colorScheme.errorContainer`
- **Secondary Container**: `MaterialTheme.colorScheme.secondaryContainer`

### 狀態顏色
- **Success**: `MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)`
- **Warning**: `MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)`
- **Error**: `MaterialTheme.colorScheme.error.copy(alpha = 0.1f)`
- **Info**: `MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)`

## 📱 響應式設計

### 平板適配
- **Multiplier**: 1.2x
- **額外間距**: 20% 增加
- **圓角**: 相應調整

### 大螢幕適配
- **Multiplier**: 1.4x
- **額外間距**: 40% 增加
- **圓角**: 相應調整

## 🔧 使用方式

### 基本使用
```kotlin
// 主要卡片
PrimaryCard {
    // 內容
}

// 次要卡片
SecondaryCard {
    // 內容
}

// 狀態卡片
StatusCard(statusType = StatusType.Success) {
    // 內容
}
```

### 可點擊卡片
```kotlin
SecondaryCard(
    onClick = { /* 處理點擊 */ }
) {
    // 內容
}
```

### 自定義卡片
```kotlin
UnifiedCard(
    cardType = CardType.Primary,
    statusType = StatusType.Warning
) {
    // 內容
}
```

## ⚠️ 注意事項

1. **不要混用不同的卡片類型** - 保持一致性
2. **避免自定義 elevation** - 使用預定義的類型
3. **確保觸控目標足夠大** - 至少 48dp
4. **使用語義化顏色** - 不要硬編碼顏色值
5. **考慮無障礙訪問** - 提供適當的內容描述

## 🔄 遷移指南

### 從舊卡片遷移
1. 識別卡片用途和重要性
2. 選擇適當的卡片類型
3. 替換硬編碼的 elevation、padding、圓角
4. 使用統一的顏色系統
5. 測試響應式行為

### 常見遷移模式
```kotlin
// 舊方式
Card(
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // 內容
    }
}

// 新方式
SecondaryCard {
    // 內容
}
```

## 📊 設計系統檢查清單

- [ ] 使用統一的卡片類型
- [ ] 符合 elevation 規範
- [ ] 使用標準化的 padding
- [ ] 應用正確的圓角半徑
- [ ] 使用語義化顏色
- [ ] 支持響應式設計
- [ ] 提供無障礙訪問
- [ ] 測試不同螢幕尺寸
- [ ] 驗證觸控目標大小
- [ ] 檢查顏色對比度

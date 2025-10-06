# 文字設計指南 - 基於 Android 2025 官方設計指南

## 📋 概述

本指南基於 Android 2025 官方設計指南和 Material Design 3 最佳實踐，為 Wealth Manager 應用程式提供統一的文字設計系統。

## 🎯 設計原則

### 1. 一致性 (Consistency)
- 統一的字型系統
- 標準化的文字大小和行高
- 語義化顏色使用
- 一致的間距規範

### 2. 可訪問性 (Accessibility)
- 符合 WCAG 2.1 AA 標準
- 適當的對比度 (至少 4.5:1)
- 支援動態字型大小
- 語義化標籤

### 3. 繁體中文優化
- 針對繁體中文優化的行高
- 適當的字距調整
- 支援多語言顯示

## 📐 文字層級系統

### Display 文字 (顯示文字)
用於大型數據展示，如總資產金額

| 樣式 | 大小 | 行高 | 用途 |
|------|------|------|------|
| Display Large | 57sp | 68sp | 主要統計數據 |
| Display Medium | 45sp | 56sp | 次要統計數據 |
| Display Small | 36sp | 48sp | 重要數值 |

### Headline 文字 (標題文字)
用於頁面和區塊標題

| 樣式 | 大小 | 行高 | 用途 |
|------|------|------|------|
| Headline Large | 32sp | 44sp | 頁面主標題 |
| Headline Medium | 28sp | 40sp | 區塊標題 |
| Headline Small | 24sp | 36sp | 子區塊標題 |

### Title 文字 (標題文字)
用於卡片和對話框標題

| 樣式 | 大小 | 行高 | 用途 |
|------|------|------|------|
| Title Large | 22sp | 32sp | 卡片主標題 |
| Title Medium | 16sp | 28sp | 卡片副標題 |
| Title Small | 14sp | 24sp | 小標題 |

### Body 文字 (正文文字)
用於主要內容

| 樣式 | 大小 | 行高 | 用途 |
|------|------|------|------|
| Body Large | 16sp | 28sp | 主要內容 |
| Body Medium | 14sp | 24sp | 一般內容 |
| Body Small | 12sp | 20sp | 次要內容 |

### Label 文字 (標籤文字)
用於按鈕和標籤

| 樣式 | 大小 | 行高 | 用途 |
|------|------|------|------|
| Label Large | 14sp | 24sp | 按鈕文字 |
| Label Medium | 12sp | 20sp | 標籤文字 |
| Label Small | 11sp | 18sp | 小標籤 |

## 🎨 顏色系統

### 語義化顏色
- **Primary**: 主要品牌色
- **Secondary**: 次要品牌色
- **Error**: 錯誤狀態
- **Success**: 成功狀態
- **Warning**: 警告狀態
- **Info**: 信息狀態

### 文字顏色層級
- **onBackground**: 主要文字顏色
- **onSurface**: 表面文字顏色
- **onSurfaceVariant**: 次要文字顏色
- **onPrimary**: 主要容器上的文字
- **onSecondary**: 次要容器上的文字

## 💻 使用方式

### 1. 使用標準化文字組件

```kotlin
// 頁面標題
PageTitle(text = "資產總覽")

// 區塊標題
SectionTitle(text = "現金資產")

// 卡片標題
CardTitle(text = "台幣現金")

// 主要內容
BodyText(text = "您的總資產為 NT$ 1,000,000")

// 次要內容
SecondaryText(text = "最後更新：2025-01-10")

// 說明文字
CaptionText(text = "點擊查看詳細資訊")

// 狀態文字
ErrorText(text = "網路連線失敗")
SuccessText(text = "資料同步成功")
WarningText(text = "API 配額即將用完")
InfoText(text = "新功能已上線")
```

### 2. 使用通用文字組件

```kotlin
// 自定義用途
StandardizedText(
    text = "自定義文字",
    purpose = TextPurpose.BODY_MEDIUM,
    color = MaterialTheme.colorScheme.primary
)
```

## 📱 響應式設計

### 字型大小調整
- 支援系統字型大小設定
- 自動適應不同螢幕密度
- 平板和手機優化

### 行高優化
- 針對繁體中文增加行高
- 確保文字可讀性
- 支援多行文字顯示

## 🔧 最佳實踐

### 1. 文字層級使用
- 使用適當的文字層級表達內容重要性
- 保持層級的一致性
- 避免過度使用粗體

### 2. 顏色使用
- 優先使用語義化顏色
- 確保足夠的對比度
- 支援深色模式

### 3. 間距規範
- 使用 8dp 的倍數作為間距
- 保持文字與其他元素的適當距離
- 考慮觸控目標大小

### 4. 可訪問性
- 提供適當的內容描述
- 支援螢幕閱讀器
- 確保鍵盤導航

## 🚫 避免事項

### 1. 不要直接使用 Text 組件
```kotlin
// ❌ 錯誤做法
Text(
    text = "標題",
    fontSize = 18.sp,
    fontWeight = FontWeight.Bold,
    color = Color.Black
)

// ✅ 正確做法
PageTitle(text = "標題")
```

### 2. 不要硬編碼顏色
```kotlin
// ❌ 錯誤做法
Text(
    text = "內容",
    color = Color(0xFF333333)
)

// ✅ 正確做法
BodyText(text = "內容")
```

### 3. 不要忽略可訪問性
```kotlin
// ❌ 錯誤做法
Text(text = "按鈕")

// ✅ 正確做法
StandardizedText(
    text = "按鈕",
    purpose = TextPurpose.BUTTON_TEXT
)
```

## 📊 檢查清單

在實施文字設計時，請確保：

- [ ] 使用標準化文字組件
- [ ] 選擇適當的文字層級
- [ ] 使用語義化顏色
- [ ] 確保足夠的對比度
- [ ] 支援動態字型大小
- [ ] 提供內容描述
- [ ] 測試深色模式
- [ ] 驗證可訪問性

## 🔄 更新記錄

- **2025-01-10**: 初始版本，基於 Android 2025 官方設計指南
- **2025-01-10**: 針對繁體中文優化行高和字距
- **2025-01-10**: 新增標準化文字組件系統

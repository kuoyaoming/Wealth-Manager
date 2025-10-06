# UI 按鈕和輸入框設計指南 (基於 Android 2025/10 開發指南和 Material Design 3)

本文件旨在為 Wealth Manager 應用程式中的所有按鈕和輸入框組件提供統一的設計規格和使用指南，以確保應用程式的整體一致性、優化用戶體驗，並符合最新的 Android 設計趨勢。

## 1. 設計原則

- **一致性**: 所有按鈕和輸入框應遵循統一的視覺語言，包括尺寸、圓角、間距和顏色。
- **可訪問性**: 確保所有交互元素符合無障礙標準，包括最小觸控目標大小 (48dp)。
- **響應式設計**: 按鈕和輸入框應能適應不同螢幕尺寸和設備方向，提供最佳顯示效果。
- **語義化顏色**: 根據 Material Design 3 的顏色系統，使用語義化的顏色來傳達按鈕和輸入框的目的或狀態。
- **狀態反饋**: 提供清晰的視覺反饋，包括按下、焦點、錯誤、禁用等狀態。

## 2. 按鈕類型 (`ButtonType`)

我們定義了以下幾種標準按鈕類型，每種都有其特定的用途和預設樣式：

### 2.1. Primary Button (主要按鈕)
- **用途**: 用於最重要的操作，如保存、確認、提交等。
- **高度**: 48dp (響應式調整後可能更高)
- **圓角**: 8dp
- **內邊距**: 16dp 水平, 12dp 垂直
- **顏色**: `MaterialTheme.colorScheme.primary` (背景), `MaterialTheme.colorScheme.onPrimary` (文字)

### 2.2. Secondary Button (次要按鈕)
- **用途**: 用於次要操作，如取消、返回、替代功能。
- **高度**: 48dp (響應式調整後可能更高)
- **圓角**: 8dp
- **內邊距**: 16dp 水平, 12dp 垂直
- **顏色**: 透明背景, `MaterialTheme.colorScheme.primary` (邊框和文字)

### 2.3. Text Button (文字按鈕)
- **用途**: 用於低優先級操作，如鏈接、輔助操作。
- **高度**: 48dp (響應式調整後可能更高)
- **圓角**: 8dp
- **內邊距**: 16dp 水平, 12dp 垂直
- **顏色**: 透明背景, `MaterialTheme.colorScheme.primary` (文字)

### 2.4. Tonal Button (色調按鈕)
- **用途**: 用於中等優先級操作，如替代操作、中性操作。
- **高度**: 48dp (響應式調整後可能更高)
- **圓角**: 8dp
- **內邊距**: 16dp 水平, 12dp 垂直
- **顏色**: `MaterialTheme.colorScheme.secondaryContainer` (背景), `MaterialTheme.colorScheme.onSecondaryContainer` (文字)

## 3. 按鈕尺寸 (`ButtonSize`)

### 3.1. Small (小按鈕)
- **高度**: 40dp
- **圓角**: 4dp
- **內邊距**: 12dp 水平, 8dp 垂直
- **用途**: 緊湊空間、次要操作

### 3.2. Medium (中等按鈕)
- **高度**: 48dp
- **圓角**: 8dp
- **內邊距**: 16dp 水平, 12dp 垂直
- **用途**: 標準按鈕、一般操作

### 3.3. Large (大按鈕)
- **高度**: 56dp
- **圓角**: 12dp
- **內邊距**: 20dp 水平, 16dp 垂直
- **用途**: 重要操作、主要功能

## 4. 輸入框類型 (`TextFieldType`)

### 4.1. Outlined TextField (輪廓輸入框)
- **用途**: 標準表單輸入，一般用途。
- **高度**: 48dp (響應式調整後可能更高)
- **圓角**: 8dp
- **邊框**: 1dp `MaterialTheme.colorScheme.outline`
- **顏色**: `MaterialTheme.colorScheme.surface` (背景)

### 4.2. Filled TextField (填充輸入框)
- **用途**: 重要表單輸入，需要強調的輸入。
- **高度**: 48dp (響應式調整後可能更高)
- **圓角**: 8dp
- **背景**: `MaterialTheme.colorScheme.surface`
- **指示器**: 底部 2dp 線條

## 5. 輸入框尺寸 (`TextFieldSize`)

### 5.1. Small (小輸入框)
- **高度**: 40dp
- **圓角**: 4dp
- **用途**: 緊湊空間、次要輸入

### 5.2. Medium (中等輸入框)
- **高度**: 48dp
- **圓角**: 8dp
- **用途**: 標準輸入框、一般用途

### 5.3. Large (大輸入框)
- **高度**: 56dp
- **圓角**: 12dp
- **用途**: 重要輸入、主要功能

## 6. 驗證狀態 (`ValidationState`)

### 6.1. 錯誤狀態
- **圖標**: `Icons.Default.Error`
- **顏色**: `MaterialTheme.colorScheme.error`
- **用途**: 輸入驗證失敗

### 6.2. 警告狀態
- **圖標**: `Icons.Default.Warning`
- **顏色**: `MaterialTheme.colorScheme.tertiary`
- **用途**: 輸入有警告但可接受

### 6.3. 成功狀態
- **圖標**: `Icons.Default.CheckCircle`
- **顏色**: `Color(0xFF4CAF50)`
- **用途**: 輸入驗證成功

### 6.4. 建議狀態
- **圖標**: 無
- **顏色**: `MaterialTheme.colorScheme.onSurfaceVariant`
- **用途**: 提供輸入建議

## 7. 響應式設計

所有按鈕和輸入框組件都應透過 `rememberResponsiveLayout` 進行響應式調整：

- **平板設備**: 尺寸增加 10%
- **大螢幕設備**: 尺寸增加 20%
- **手機設備**: 標準尺寸

## 8. 無障礙訪問

- **最小觸控目標**: 48dp x 48dp
- **對比度**: 符合 WCAG 2.1 AA 標準
- **內容描述**: 為所有圖標提供適當的內容描述
- **鍵盤導航**: 支持鍵盤和輔助技術導航

## 9. 使用指南

### 9.1. 按鈕使用

```kotlin
// 主要按鈕 - 用於重要操作
PrimaryButton(
    onClick = { /* 操作 */ },
    size = ButtonSize.Medium
) {
    Text("保存")
}

// 次要按鈕 - 用於次要操作
SecondaryButton(
    onClick = { /* 操作 */ },
    size = ButtonSize.Medium
) {
    Text("取消")
}

// 文字按鈕 - 用於低優先級操作
TextButton(
    onClick = { /* 操作 */ },
    size = ButtonSize.Medium
) {
    Text("了解更多")
}

// 色調按鈕 - 用於中等優先級操作
TonalButton(
    onClick = { /* 操作 */ },
    size = ButtonSize.Medium
) {
    Text("替代操作")
}
```

### 9.2. 輸入框使用

```kotlin
// 輪廓輸入框 - 標準輸入
OutlinedTextField(
    value = value,
    onValueChange = { value = it },
    label = { Text("標籤") },
    placeholder = { Text("提示文字") },
    size = TextFieldSize.Medium,
    validationState = ValidationState(
        isError = hasError,
        errorMessage = errorMessage
    )
)

// 填充輸入框 - 重要輸入
FilledTextField(
    value = value,
    onValueChange = { value = it },
    label = { Text("標籤") },
    placeholder = { Text("提示文字") },
    size = TextFieldSize.Medium,
    isPassword = true,
    showPasswordToggle = true
)
```

## 10. 遷移指南

### 10.1. 從現有按鈕遷移

1. **替換 Button()** → 使用 `PrimaryButton()`
2. **替換 OutlinedButton()** → 使用 `SecondaryButton()`
3. **替換 TextButton()** → 使用 `TextButton()`
4. **移除硬編碼的顏色和尺寸** → 使用統一的配置

### 10.2. 從現有輸入框遷移

1. **替換 OutlinedTextField()** → 使用 `OutlinedTextField()`
2. **替換 FilledTextField()** → 使用 `FilledTextField()`
3. **統一驗證狀態顯示** → 使用 `ValidationState`
4. **移除硬編碼的樣式** → 使用統一的配置

## 11. 檢查清單

在實施新的按鈕和輸入框設計時，請確保：

- [ ] 使用統一的按鈕類型 (`ButtonType`)
- [ ] 使用統一的按鈕尺寸 (`ButtonSize`)
- [ ] 使用統一的輸入框類型 (`TextFieldType`)
- [ ] 使用統一的輸入框尺寸 (`TextFieldSize`)
- [ ] 實施適當的驗證狀態 (`ValidationState`)
- [ ] 支持響應式設計
- [ ] 符合無障礙訪問標準
- [ ] 提供適當的狀態反饋
- [ ] 使用語義化顏色
- [ ] 測試不同設備和螢幕尺寸

## 12. 最佳實踐

1. **優先使用統一組件**: 在所有新的 UI 實現中，應優先使用 `UnifiedButton` 和 `UnifiedTextField` 或其便捷組件。

2. **避免硬編碼樣式**: 盡量避免在各處硬編碼按鈕和輸入框的顏色、尺寸和樣式，而是透過統一的配置來控制。

3. **審查現有組件**: 對現有代碼中的按鈕和輸入框組件進行審查，逐步替換為統一組件。

4. **測試**: 在不同設備和螢幕尺寸上測試按鈕和輸入框的顯示效果和交互行為。

5. **文檔化**: 為自定義的按鈕和輸入框變體提供適當的文檔說明。

---

**備註**: 本指南將根據 Material Design 和 Android 開發指南的未來更新進行迭代。

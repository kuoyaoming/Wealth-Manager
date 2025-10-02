# Typography Review - Material Design 3 & SDK 36 Compliance

## ✅ 字型系統設計指南遵循

### 1. **Material Design 3 Typography Scale**
已完整實現所有 13 種字型樣式：

| 樣式 | 字號 | 行高 | 字重 | 字距 | 用途 |
|------|------|------|------|------|------|
| **Display Large** | 57sp | 64sp | Normal | -0.25sp | 超大數字顯示 |
| **Display Medium** | 45sp | 52sp | Normal | 0sp | 大數字顯示 |
| **Display Small** | 36sp | 44sp | Normal | 0sp | 重點數字 |
| **Headline Large** | 32sp | 40sp | Normal | 0sp | 主標題 |
| **Headline Medium** | 28sp | 36sp | Normal | 0sp | 次標題 |
| **Headline Small** | 24sp | 32sp | Normal | 0sp | 小標題 |
| **Title Large** | 22sp | 28sp | Normal | 0sp | 頁面標題 |
| **Title Medium** | 16sp | 24sp | Medium | 0.15sp | 卡片標題 |
| **Title Small** | 14sp | 20sp | Medium | 0.1sp | 小卡片標題 |
| **Body Large** | 16sp | 24sp | Normal | 0.5sp | 主要內文 |
| **Body Medium** | 14sp | 20sp | Normal | 0.25sp | 次要內文 |
| **Body Small** | 12sp | 16sp | Normal | 0.4sp | 輔助文字 |
| **Label Large** | 14sp | 20sp | Medium | 0.1sp | 按鈕文字 |
| **Label Medium** | 12sp | 16sp | Medium | 0.5sp | Tab 標籤 |
| **Label Small** | 11sp | 16sp | Medium | 0.5sp | 小標籤 |

---

### 2. **Android SDK 36 合規性** ✅

#### 2.1 可縮放單位 (Scalable Units)
- ✅ 所有字號使用 `sp` (Scalable Pixels)
- ✅ 支援系統字型大小調整
- ✅ 行高使用 `sp` 確保比例正確

#### 2.2 最小字號要求
- ✅ 最小字號 11sp (Label Small)
- ✅ 符合 WCAG AA 無障礙標準
- ✅ 主要內文 ≥ 14sp，確保可讀性

#### 2.3 行高比例 (Line Height Ratio)
- ✅ Display: ~1.12x (緊湊)
- ✅ Headline: ~1.25x (標準)
- ✅ Title: ~1.27-1.5x (舒適)
- ✅ Body: ~1.4-1.5x (閱讀最佳)
- ✅ Label: ~1.4-1.6x (按鈕最佳)

#### 2.4 字距 (Letter Spacing)
- ✅ Display Large: -0.25sp (大字號緊湊)
- ✅ Headline/Title Large: 0sp (無額外字距)
- ✅ Body/Label: 0.1-0.5sp (提升可讀性)

---

### 3. **繁體中文優化** ✅

#### 3.1 字型選擇
- ✅ 使用 `FontFamily.Default` (系統字型)
- ✅ Android 自動選擇 Noto Sans CJK TC
- ✅ 支援所有中文字符

#### 3.2 行高優化
- ✅ 中文字高 ≈ 1em，行高增加 20-50%
- ✅ Body Text: 1.4-1.5x 行高
- ✅ 避免文字重疊

#### 3.3 字距優化
- ✅ 中文字距 0.15-0.5sp
- ✅ 避免過緊造成閱讀困難
- ✅ Title/Label 使用 Medium 字重提升辨識度

---

### 4. **使用建議**

#### 4.1 頁面標題
```kotlin
Text(
    text = "財富管理",
    style = MaterialTheme.typography.titleLarge  // 22sp, Medium
)
```

#### 4.2 卡片標題
```kotlin
Text(
    text = "總資產",
    style = MaterialTheme.typography.titleMedium  // 16sp, Medium
)
```

#### 4.3 內文
```kotlin
Text(
    text = "您的投資組合表現良好",
    style = MaterialTheme.typography.bodyMedium  // 14sp, Normal
)
```

#### 4.4 輔助文字
```kotlin
Text(
    text = "最後更新: 2025-10-02",
    style = MaterialTheme.typography.bodySmall,  // 12sp
    color = MaterialTheme.colorScheme.onSurfaceVariant
)
```

#### 4.5 數字顯示
```kotlin
Text(
    text = "NT$ 1,234,567",
    style = MaterialTheme.typography.displayMedium  // 45sp
)
```

#### 4.6 按鈕
```kotlin
Button(onClick = { }) {
    Text("儲存")  // 自動使用 labelLarge (14sp, Medium)
}
```

---

### 5. **無障礙設計** ✅

#### 5.1 對比度
- ✅ 主文字與背景對比度 ≥ 4.5:1 (WCAG AA)
- ✅ 大文字 (≥18sp) 對比度 ≥ 3:1

#### 5.2 觸控目標
- ✅ 按鈕文字 ≥ 14sp
- ✅ 最小觸控面積 48dp × 48dp

#### 5.3 動態字型
- ✅ 支援系統字型縮放 (100%-200%)
- ✅ Layout 自適應調整

---

### 6. **常見錯誤避免** ⚠️

❌ **避免**：
```kotlin
// 不要直接指定字號
Text(
    text = "標題",
    fontSize = 20.sp  // 錯誤：不符合設計系統
)
```

✅ **正確**：
```kotlin
// 使用 Typography Scale
Text(
    text = "標題",
    style = MaterialTheme.typography.titleLarge  // 正確
)
```

❌ **避免**：
```kotlin
// 不要使用過小的字號
Text(
    text = "提示",
    style = MaterialTheme.typography.bodySmall,
    fontSize = 8.sp  // 錯誤：太小，難以閱讀
)
```

✅ **正確**：
```kotlin
// 最小使用 labelSmall (11sp)
Text(
    text = "提示",
    style = MaterialTheme.typography.labelSmall  // 正確：11sp
)
```

---

### 7. **字型階層示例**

```
┌─────────────────────────────────────┐
│  財富管理 (titleLarge, 22sp)         │  ← Top App Bar
├─────────────────────────────────────┤
│  總資產 (titleMedium, 16sp, Bold)    │  ← Card Title
│  NT$ 1,234,567 (displayMedium, 45sp)│  ← Big Number
│  ▲ NT$ 12,345 (+1.5%)               │  ← Body Small
├─────────────────────────────────────┤
│  現金資產 (titleMedium, 16sp)        │
│  NT$ 500,000 (headlineSmall, 24sp)  │
│  佔總資產 40.5% (bodySmall, 12sp)    │
├─────────────────────────────────────┤
│  股票資產 (titleMedium, 16sp)        │
│  NT$ 734,567 (headlineSmall, 24sp)  │
│  佔總資產 59.5% (bodySmall, 12sp)    │
├─────────────────────────────────────┤
│  最後更新: 2025-10-02 23:15         │  ← Body Small
│  (bodySmall, 12sp, Secondary Color) │
└─────────────────────────────────────┘
```

---

### 8. **測試清單** ✅

- [ ] 所有文字使用 Typography Scale
- [ ] 最小字號 ≥ 11sp
- [ ] 主要內文 ≥ 14sp
- [ ] 行高比例適當 (1.2x - 1.5x)
- [ ] 支援系統字型縮放
- [ ] 繁體中文顯示正常
- [ ] Dark Mode 對比度足夠
- [ ] 觸控目標 ≥ 48dp

---

## 📚 參考資料

1. [Material Design 3 Typography](https://m3.material.io/styles/typography/overview)
2. [Android Typography Guide](https://developer.android.com/develop/ui/compose/text)
3. [WCAG 2.1 Accessibility Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
4. [Android SDK 36 Best Practices](https://developer.android.com/about/versions/15)

---

**最後更新**: 2025-10-02
**版本**: 1.4.6
**審查人**: AI Assistant


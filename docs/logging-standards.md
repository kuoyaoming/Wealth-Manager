# 日誌記錄標準

本文檔說明了 Wealth Manager 應用中的日誌記錄標準，遵循 Android 設計指南的最佳實踐。

## 日誌級別使用指南

### VERBOSE (`Log.v()`)
- **用途**: 最詳細的調試信息，僅用於開發階段
- **使用場景**: 詳細的執行流程追蹤、變量狀態檢查
- **發布版本**: 不會輸出

### DEBUG (`Log.d()`)
- **用途**: 一般調試信息，有助於了解應用運行狀態
- **使用場景**: 方法進入/退出、狀態變化、性能監控
- **發布版本**: 不會輸出

### INFO (`Log.i()`)
- **用途**: 基本信息，顯示應用狀態或功能
- **使用場景**: 用戶操作、導航、業務邏輯關鍵點
- **發布版本**: 會輸出

### WARN (`Log.w()`)
- **用途**: 警告信息，可能表明潛在問題
- **使用場景**: 性能警告、異常情況、降級處理
- **發布版本**: 會輸出

### ERROR (`Log.e()`)
- **用途**: 錯誤信息，處理嚴重問題
- **使用場景**: 異常捕獲、API 錯誤、系統錯誤
- **發布版本**: 會輸出

## 標準化日誌工具

### StandardLogger

使用 `StandardLogger` 替代直接的 `Log` 調用：

```kotlin
// 錯誤用法
Log.d("TAG", "message")

// 正確用法
StandardLogger.debug("TAG", "message")
```

### 專用日誌方法

`StandardLogger` 提供了專用的日誌方法：

```kotlin
// 性能監控
StandardLogger.performance("Component render took 15ms")
StandardLogger.performanceWarning("High memory usage: 85%")

// 用戶操作
StandardLogger.userAction("Button clicked", "Add Asset")

// 導航
StandardLogger.navigation("Dashboard", "Assets")

// 生物識別
StandardLogger.biometric("Authentication successful")

// 資產管理
StandardLogger.asset("ADD", "Stock", "AAPL")

// API 請求
StandardLogger.apiRequest("Search stocks", "query: AAPL")
StandardLogger.apiError("Search stocks", "Network timeout")
```

## 日誌格式

### 統一格式
```
[時間戳] [TAG] 消息內容
```

### TAG 命名規範
- 使用 `WealthManager.TAG` 格式
- TAG 應該描述日誌來源的組件或功能
- 常見 TAG：
  - `MainActivity`
  - `PERFORMANCE`
  - `USER_ACTION`
  - `NAVIGATION`
  - `BIOMETRIC`
  - `ASSET`
  - `API`

## 最佳實踐

### 1. 條件日誌
```kotlin
// 性能敏感的日誌應該有條件檢查
if (BuildConfig.DEBUG) {
    StandardLogger.debug("TAG", "Expensive operation result: $result")
}
```

### 2. 異常處理
```kotlin
try {
    // 操作
} catch (e: Exception) {
    StandardLogger.error("TAG", "Operation failed", e)
}
```

### 3. 避免敏感信息
```kotlin
// 錯誤：記錄敏感信息
StandardLogger.debug("AUTH", "User password: $password")

// 正確：不記錄敏感信息
StandardLogger.debug("AUTH", "User authentication attempted")
```

### 4. 性能考慮
```kotlin
// 避免在循環中進行昂貴的字符串操作
for (item in items) {
    // 錯誤：每次循環都創建字符串
    StandardLogger.debug("TAG", "Processing item: ${item.toString()}")
    
    // 正確：只在需要時記錄
    if (BuildConfig.DEBUG && shouldLog) {
        StandardLogger.debug("TAG", "Processing item: ${item.toString()}")
    }
}
```

## 遷移指南

### 從直接 Log 調用遷移

1. **替換 import**：
```kotlin
// 舊的
import android.util.Log

// 新的
import com.wealthmanager.utils.StandardLogger
```

2. **替換方法調用**：
```kotlin
// 舊的
Log.d("TAG", "message")
Log.i("TAG", "message")
Log.w("TAG", "message")
Log.e("TAG", "message", throwable)

// 新的
StandardLogger.debug("TAG", "message")
StandardLogger.info("TAG", "message")
StandardLogger.warn("TAG", "message")
StandardLogger.error("TAG", "message", throwable)
```

3. **使用專用方法**：
```kotlin
// 性能監控
StandardLogger.performance("message")
StandardLogger.performanceWarning("message")

// 用戶操作
StandardLogger.userAction("action", "details")
```

## 調試工具

### DebugLogManager
`DebugLogManager` 提供了額外的調試功能：
- 日誌收集和限制
- 複製到剪貼板
- 市場數據專用日誌
- 生物識別日誌

### 性能監控
`PerformanceMonitor` 使用 `StandardLogger` 記錄性能指標：
- 組件渲染時間
- 記憶體使用情況
- CPU 密集型操作
- 重組次數

## 檢查清單

- [ ] 所有直接 `Log` 調用已替換為 `StandardLogger`
- [ ] 日誌級別使用正確
- [ ] TAG 命名符合規範
- [ ] 敏感信息未記錄
- [ ] 性能敏感的日誌有條件檢查
- [ ] 異常處理包含適當的日誌記錄

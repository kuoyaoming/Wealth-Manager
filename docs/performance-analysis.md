# Wealth Manager APP 性能分析指南

## 🚀 快速性能分析工具

### 1. Android Studio Profiler
```bash
# 啟用性能分析
./gradlew assembleDebug
# 在 Android Studio 中：
# Tools -> Profiler -> CPU/Memory/Network
```

### 2. 系統級性能監控
```bash
# 監控 CPU 使用率
adb shell top -p $(adb shell pidof com.wealthmanager)

# 監控記憶體使用
adb shell dumpsys meminfo com.wealthmanager

# 監控 GPU 渲染
adb shell dumpsys gfxinfo com.wealthmanager framestats
```

### 3. 自定義性能監控
在 APP 中添加性能監控代碼：

```kotlin
// 在 DashboardScreen.kt 中添加
import android.os.SystemClock

@Composable
fun PerformanceMonitor() {
    val startTime = remember { SystemClock.elapsedRealtime() }
    
    LaunchedEffect(Unit) {
        val endTime = SystemClock.elapsedRealtime()
        val duration = endTime - startTime
        Log.d("PERFORMANCE", "Dashboard render time: ${duration}ms")
    }
}
```

## 🎯 潛在性能問題分析

### 1. Treemap 計算問題
- **問題**: `derivedStateOf` 在每次資產變化時重新計算
- **影響**: CPU 密集計算可能造成卡頓
- **解決**: 使用 `remember` 緩存結果

### 2. 過度重組問題
- **問題**: 多個 `collectAsState()` 可能觸發過多重組
- **影響**: UI 頻繁重繪
- **解決**: 合併狀態或使用 `derivedStateOf`

### 3. 記憶體洩漏問題
- **問題**: `remember` 和 `LaunchedEffect` 可能造成記憶體洩漏
- **影響**: 長時間使用後性能下降
- **解決**: 正確的生命週期管理

## 🔧 性能優化建議

### 1. Treemap 優化
```kotlin
// 使用 remember 緩存計算結果
val treemapRects = remember(assets) {
    derivedStateOf {
        TreemapLayout.computeTreemapRects(
            assets = assets,
            width = containerWidth,
            height = containerHeight,
            spacing = 16f
        )
    }
}
```

### 2. 狀態管理優化
```kotlin
// 合併多個狀態為單一狀態
data class DashboardState(
    val uiState: UiState,
    val apiStatus: ApiStatus,
    val manualSyncStatus: ManualSyncStatus
)
```

### 3. 渲染優化
```kotlin
// 使用 LazyColumn 替代 Column
LazyColumn {
    items(assets) { asset ->
        TreemapLegendItem(asset)
    }
}
```

## 📊 性能監控實作

### 1. 添加性能監控代碼
```kotlin
@Composable
fun PerformanceTracker(
    componentName: String,
    content: @Composable () -> Unit
) {
    val startTime = remember { SystemClock.elapsedRealtime() }
    
    content()
    
    LaunchedEffect(Unit) {
        val endTime = SystemClock.elapsedRealtime()
        val duration = endTime - startTime
        if (duration > 16) { // 超過一幀的時間
            Log.w("PERFORMANCE", "$componentName took ${duration}ms")
        }
    }
}
```

### 2. 記憶體監控
```kotlin
@Composable
fun MemoryMonitor() {
    LaunchedEffect(Unit) {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        val memoryUsage = (usedMemory.toFloat() / maxMemory.toFloat()) * 100
        
        Log.d("MEMORY", "Memory usage: ${memoryUsage}%")
    }
}
```

## 🚨 常見性能問題

### 1. 過度重組
- **症狀**: 畫面卡頓、CPU 使用率高
- **原因**: 不必要的狀態變化
- **解決**: 使用 `remember` 和 `derivedStateOf`

### 2. 記憶體洩漏
- **症狀**: 長時間使用後越來越慢
- **原因**: 未正確釋放資源
- **解決**: 檢查 `LaunchedEffect` 和 `remember` 的使用

### 3. 主線程阻塞
- **症狀**: ANR (Application Not Responding)
- **原因**: 在主線程執行重計算
- **解決**: 使用 `Dispatchers.Default` 進行背景計算

## 📈 性能測試步驟

1. **開啟 Android Studio Profiler**
2. **運行 APP 並記錄性能數據**
3. **分析 CPU 使用率峰值**
4. **檢查記憶體使用模式**
5. **識別性能瓶頸**
6. **實施優化方案**
7. **重新測試驗證改善**

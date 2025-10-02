package com.wealthmanager.utils

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 性能監控工具類
 * 用於分析 APP 中的性能瓶頸和卡頓問題
 */
object PerformanceMonitor {
    private const val MEMORY_WARNING_THRESHOLD = 80
    private const val SLOW_OPERATION_THRESHOLD = 100
    private const val EXCESSIVE_RECOMPOSITION_THRESHOLD = 10

    /**
     * 測量組件渲染時間
     */
    @Composable
    fun trackRenderTime(
        componentName: String,
        threshold: Long = 16L, // Threshold exceeding one frame time
    ) {
        val startTime = remember { SystemClock.elapsedRealtime() }

        LaunchedEffect(Unit) {
            val endTime = SystemClock.elapsedRealtime()
            val duration = endTime - startTime

            if (duration > threshold) {
                StandardLogger.performanceWarning(
                    "⚠️ $componentName render took ${duration}ms (threshold: ${threshold}ms)",
                )
            } else {
                StandardLogger.performance("✅ $componentName render took ${duration}ms")
            }
        }
    }

    /**
     * 測量異步操作時間
     */
    suspend fun <T> measureAsyncOperation(
        operationName: String,
        operation: suspend () -> T,
    ): T {
        val startTime = SystemClock.elapsedRealtime()

        return try {
            val result = operation()
            val duration = SystemClock.elapsedRealtime() - startTime
            StandardLogger.performance("🔄 $operationName completed in ${duration}ms")
            result
        } catch (e: Exception) {
            val duration = SystemClock.elapsedRealtime() - startTime
            StandardLogger.error("PERFORMANCE", "❌ $operationName failed after ${duration}ms: ${e.message}", e)
            throw e
        }
    }

    /**
     * 監控記憶體使用
     */
    @Composable
    fun trackMemoryUsage(componentName: String) {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.Default) {
                val runtime = Runtime.getRuntime()
                val usedMemory = runtime.totalMemory() - runtime.freeMemory()
                val maxMemory = runtime.maxMemory()
                val memoryUsage = usedMemory.toFloat() / maxMemory.toFloat() * 100

                StandardLogger.performance("🧠 $componentName memory usage: ${String.format("%.1f", memoryUsage)}%")

                if (memoryUsage > MEMORY_WARNING_THRESHOLD) {
                    StandardLogger.performanceWarning(
                        "⚠️ High memory usage detected: ${String.format("%.1f", memoryUsage)}%",
                    )
                }
            }
        }
    }

    /**
     * 監控 CPU 密集型操作
     */
    suspend fun <T> measureCpuIntensiveOperation(
        operationName: String,
        operation: suspend () -> T,
    ): T {
        val startTime = SystemClock.elapsedRealtime()

        return withContext(Dispatchers.Default) {
            val result = operation()
            val duration = SystemClock.elapsedRealtime() - startTime

            StandardLogger.performance("⚡ CPU intensive $operationName took ${duration}ms")

            if (duration > SLOW_OPERATION_THRESHOLD) {
                StandardLogger.performanceWarning("⚠️ Slow CPU operation: $operationName took ${duration}ms")
            }

            result
        }
    }

    /**
     * 監控重組次數
     */
    @Composable
    fun trackRecomposition(componentName: String) {
        val recompositionCount = remember { mutableStateOf(0) }

        LaunchedEffect(Unit) {
            recompositionCount.value = recompositionCount.value + 1
            StandardLogger.performance("🔄 $componentName recomposed ${recompositionCount.value} times")

            if (recompositionCount.value > EXCESSIVE_RECOMPOSITION_THRESHOLD) {
                StandardLogger.performanceWarning(
                    "⚠️ Excessive recomposition detected for $componentName: ${recompositionCount.value} times",
                )
            }
        }
    }
}

/**
 * 性能監控的 Composable 包裝器
 */
@Composable
fun PerformanceTracker(
    componentName: String,
    trackMemory: Boolean = false,
    trackRecomposition: Boolean = false,
    content: @Composable () -> Unit,
) {
    PerformanceMonitor.trackRenderTime(componentName)

    if (trackMemory) {
        PerformanceMonitor.trackMemoryUsage(componentName)
    }

    if (trackRecomposition) {
        PerformanceMonitor.trackRecomposition(componentName)
    }

    content()
}

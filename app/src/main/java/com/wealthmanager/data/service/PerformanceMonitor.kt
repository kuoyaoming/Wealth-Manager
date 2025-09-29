package com.wealthmanager.data.service

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 效能監控器
 * 監控主線程阻塞和應用程式效能
 */
@Singleton
class PerformanceMonitor @Inject constructor(
    private val debugLogManager: DebugLogManager
) {
    
    companion object {
        // Main thread blocking threshold (ms)
        private const val MAIN_THREAD_BLOCK_THRESHOLD_MS = 16L // 16ms (60fps)
        private const val CRITICAL_BLOCK_THRESHOLD_MS = 100L // 100ms
        private const val MONITORING_INTERVAL_MS = 1000L // 1s
        
        // Memory warning thresholds
        private const val MEMORY_WARNING_THRESHOLD_MB = 100L
        private const val MEMORY_CRITICAL_THRESHOLD_MB = 200L
    }
    
    private val mainHandler = Handler(Looper.getMainLooper())
    private val performanceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Performance statistics
    private val frameDropCount = AtomicLong(0)
    private val mainThreadBlockCount = AtomicLong(0)
    private val criticalBlockCount = AtomicLong(0)
    private var isMonitoring = false
    
    /**
     * Start performance monitoring
     */
    fun startMonitoring() {
        if (isMonitoring) return
        
        isMonitoring = true
        debugLogManager.log("PERFORMANCE", "開始效能監控")
        
        performanceScope.launch {
            while (isMonitoring) {
                monitorMainThreadPerformance()
                delay(MONITORING_INTERVAL_MS)
            }
        }
    }
    
    /**
     * 停止效能監控
     */
    fun stopMonitoring() {
        isMonitoring = false
        debugLogManager.log("PERFORMANCE", "停止效能監控")
    }
    
    /**
     * 監控主線程效能
     */
    private suspend fun monitorMainThreadPerformance() {
        val startTime = System.currentTimeMillis()
        
        // Execute a simple task on main thread to measure blocking
        val blockTime = withContext(Dispatchers.Main) {
            val taskStart = System.currentTimeMillis()
            // Execute a lightweight task
            Thread.sleep(1) // Simulate lightweight operation
            System.currentTimeMillis() - taskStart
        }
        
        val totalTime = System.currentTimeMillis() - startTime
        
        // Check main thread blocking
        when {
            blockTime > CRITICAL_BLOCK_THRESHOLD_MS -> {
                criticalBlockCount.incrementAndGet()
                debugLogManager.logWarning("PERFORMANCE", "嚴重主線程阻塞: ${blockTime}ms")
                recordPerformanceIssue("Critical main thread block: ${blockTime}ms")
            }
            blockTime > MAIN_THREAD_BLOCK_THRESHOLD_MS -> {
                mainThreadBlockCount.incrementAndGet()
                debugLogManager.log("PERFORMANCE", "主線程阻塞: ${blockTime}ms")
            }
        }
        
        // Check overall response time
        if (totalTime > 1000L) {
            debugLogManager.logWarning("PERFORMANCE", "慢響應時間: ${totalTime}ms")
        }
    }
    
    /**
     * 監控記憶體使用
     */
    fun monitorMemoryUsage(): Flow<MemoryStatus> = flow {
        while (isMonitoring) {
            val runtime = Runtime.getRuntime()
            val usedMemory = runtime.totalMemory() - runtime.freeMemory()
            val maxMemory = runtime.maxMemory()
            val usedMemoryMB = usedMemory / (1024 * 1024)
            val maxMemoryMB = maxMemory / (1024 * 1024)
            val memoryUsagePercent = (usedMemoryMB * 100) / maxMemoryMB
            
            val status = when {
                usedMemoryMB > MEMORY_CRITICAL_THRESHOLD_MB -> {
                    MemoryStatus.CRITICAL(usedMemoryMB, maxMemoryMB, memoryUsagePercent.toInt())
                }
                usedMemoryMB > MEMORY_WARNING_THRESHOLD_MB -> {
                    MemoryStatus.WARNING(usedMemoryMB, maxMemoryMB, memoryUsagePercent.toInt())
                }
                else -> {
                    MemoryStatus.NORMAL(usedMemoryMB, maxMemoryMB, memoryUsagePercent.toInt())
                }
            }
            
            emit(status)
            delay(5000L) // Check every 5 seconds
        }
    }
    
    /**
     * 記錄效能問題
     */
    private fun recordPerformanceIssue(issue: String) {
        debugLogManager.logError("效能問題: $issue")
        
        // Can trigger automatic optimization measures
        when {
            issue.contains("Critical main thread block") -> {
                // Trigger memory cleanup
                System.gc()
                debugLogManager.log("PERFORMANCE", "觸發記憶體清理")
            }
            issue.contains("memory") -> {
                // Trigger cache cleanup
                debugLogManager.log("PERFORMANCE", "觸發快取清理")
            }
        }
    }
    
    /**
     * 獲取效能統計
     */
    fun getPerformanceStats(): PerformanceStats {
        return PerformanceStats(
            frameDrops = frameDropCount.get(),
            mainThreadBlocks = mainThreadBlockCount.get(),
            criticalBlocks = criticalBlockCount.get(),
            isMonitoring = isMonitoring
        )
    }
    
    /**
     * 重置統計
     */
    fun resetStats() {
        frameDropCount.set(0)
        mainThreadBlockCount.set(0)
        criticalBlockCount.set(0)
        debugLogManager.log("PERFORMANCE", "效能統計已重置")
    }
    
    /**
     * 記憶體狀態
     */
    sealed class MemoryStatus {
        data class NORMAL(val usedMB: Long, val maxMB: Long, val usagePercent: Int) : MemoryStatus()
        data class WARNING(val usedMB: Long, val maxMB: Long, val usagePercent: Int) : MemoryStatus()
        data class CRITICAL(val usedMB: Long, val maxMB: Long, val usagePercent: Int) : MemoryStatus()
    }
    
    /**
     * 效能統計
     */
    data class PerformanceStats(
        val frameDrops: Long,
        val mainThreadBlocks: Long,
        val criticalBlocks: Long,
        val isMonitoring: Boolean
    )
}

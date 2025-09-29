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
 * Performance monitor for main thread blocking and application performance
 */
@Singleton
class PerformanceMonitor @Inject constructor(
    private val debugLogManager: DebugLogManager
) {
    
    companion object {
        private const val MAIN_THREAD_BLOCK_THRESHOLD_MS = 16L
        private const val CRITICAL_BLOCK_THRESHOLD_MS = 100L
        private const val MONITORING_INTERVAL_MS = 1000L
        private const val MEMORY_WARNING_THRESHOLD_MB = 100L
        private const val MEMORY_CRITICAL_THRESHOLD_MB = 200L
    }
    
    private val mainHandler = Handler(Looper.getMainLooper())
    private val performanceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
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
        debugLogManager.log("PERFORMANCE", "Starting performance monitoring")
        
        performanceScope.launch {
            while (isMonitoring) {
                monitorMainThreadPerformance()
                delay(MONITORING_INTERVAL_MS)
            }
        }
    }
    
    /**
     * Stop performance monitoring
     */
    fun stopMonitoring() {
        isMonitoring = false
        debugLogManager.log("PERFORMANCE", "Stop performance monitoring")
    }
    
    /**
     * Monitor main thread performance
     */
    private suspend fun monitorMainThreadPerformance() {
        val startTime = System.currentTimeMillis()
        
        val blockTime = withContext(Dispatchers.Main) {
            val taskStart = System.currentTimeMillis()
            Thread.sleep(1)
            System.currentTimeMillis() - taskStart
        }
        
        val totalTime = System.currentTimeMillis() - startTime
        
        when {
            blockTime > CRITICAL_BLOCK_THRESHOLD_MS -> {
                criticalBlockCount.incrementAndGet()
                debugLogManager.logWarning("PERFORMANCE", "Critical main thread blocking: ${blockTime}ms")
                recordPerformanceIssue("Critical main thread block: ${blockTime}ms")
            }
            blockTime > MAIN_THREAD_BLOCK_THRESHOLD_MS -> {
                mainThreadBlockCount.incrementAndGet()
                debugLogManager.log("PERFORMANCE", "Main thread blocking: ${blockTime}ms")
            }
        }
        
        if (totalTime > 1000L) {
            debugLogManager.logWarning("PERFORMANCE", "Slow response time: ${totalTime}ms")
        }
    }
    
    /**
     * Monitor memory usage
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
            delay(5000L)
        }
    }
    
    /**
     * Record performance issues
     */
    private fun recordPerformanceIssue(issue: String) {
        debugLogManager.logError("Performance issue: $issue")
        
        when {
            issue.contains("Critical main thread block") -> {
                System.gc()
                debugLogManager.log("PERFORMANCE", "Trigger memory cleanup")
            }
            issue.contains("memory") -> {
                debugLogManager.log("PERFORMANCE", "Trigger cache cleanup")
            }
        }
    }
    
    /**
     * Get performance statistics
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
     * Reset statistics
     */
    fun resetStats() {
        frameDropCount.set(0)
        mainThreadBlockCount.set(0)
        criticalBlockCount.set(0)
        debugLogManager.log("PERFORMANCE", "Performance statistics reset")
    }
    
    /**
     * Memory status
     */
    sealed class MemoryStatus {
        data class NORMAL(val usedMB: Long, val maxMB: Long, val usagePercent: Int) : MemoryStatus()
        data class WARNING(val usedMB: Long, val maxMB: Long, val usagePercent: Int) : MemoryStatus()
        data class CRITICAL(val usedMB: Long, val maxMB: Long, val usagePercent: Int) : MemoryStatus()
    }
    
    /**
     * Performance statistics
     */
    data class PerformanceStats(
        val frameDrops: Long,
        val mainThreadBlocks: Long,
        val criticalBlocks: Long,
        val isMonitoring: Boolean
    )
}

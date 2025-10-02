package com.wealthmanager.data.service

import android.os.Handler
import android.os.Looper
import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 120Hz optimized performance monitor
 * Optimized for high refresh rate screens
 */
@Singleton
class PerformanceMonitor120Hz
    @Inject
    constructor(
        private val debugLogManager: DebugLogManager,
    ) {
        companion object {
            // Main thread blocking threshold (ms) - optimized for 120Hz
            private const val MAIN_THREAD_BLOCK_THRESHOLD_MS = 8L // 8.3ms (120fps)
            private const val MAIN_THREAD_BLOCK_THRESHOLD_60FPS = 16L // 16ms (60fps)
            private const val CRITICAL_BLOCK_THRESHOLD_MS = 50L // 50ms (reduced for 120Hz)
            private const val MONITORING_INTERVAL_MS = 500L // 0.5s (more frequent monitoring)

            // Memory warning thresholds - 120Hz requires more aggressive memory management
            private const val MEMORY_WARNING_THRESHOLD_MB = 80L
            private const val MEMORY_CRITICAL_THRESHOLD_MB = 150L

            // High refresh rate related constants
            private const val TARGET_FPS_120 = 120f
            private const val TARGET_FPS_60 = 60f
            private const val FRAME_TIME_120HZ_MS = 8.33f // 1000/120
            private const val FRAME_TIME_60HZ_MS = 16.67f // 1000/60

            // Performance statistics thresholds
            private const val FRAME_DROP_WARNING_THRESHOLD = 5 // 5 frame drops warning
            private const val FRAME_DROP_CRITICAL_THRESHOLD = 10 // 10 frame drops critical
        }

        private val mainHandler = Handler(Looper.getMainLooper())
        private val performanceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

        // Performance statistics
        private val frameDropCount = AtomicLong(0)
        private val mainThreadBlockCount = AtomicLong(0)
        private val criticalBlockCount = AtomicLong(0)
        private val highRefreshRateFrameCount = AtomicLong(0)
        private var isMonitoring = false
        private var currentRefreshRate = 60f

        /**
         * Start performance monitoring
         */
        fun startMonitoring() {
            if (isMonitoring) return

            isMonitoring = true
            // Remove screen refresh log

            performanceScope.launch {
                while (isMonitoring) {
                    monitorMainThreadPerformance()
                    monitorFrameRate()
                    delay(MONITORING_INTERVAL_MS)
                }
            }
        }

        /**
         * Stop performance monitoring
         */
        fun stopMonitoring() {
            isMonitoring = false
            // Remove screen refresh log
        }

        /**
         * Monitor main thread performance
         */
        private suspend fun monitorMainThreadPerformance() {
            val startTime = System.currentTimeMillis()

            // Execute a simple task on main thread to measure blocking time
            withContext(Dispatchers.Main) {
                // Simulate main thread work
                Thread.sleep(1) // 1ms tiny delay
            }

            val endTime = System.currentTimeMillis()
            val blockTime = endTime - startTime

            // 120Hz threshold check
            if (blockTime > MAIN_THREAD_BLOCK_THRESHOLD_MS) {
                mainThreadBlockCount.incrementAndGet()
                // Remove screen refresh log

                if (blockTime > CRITICAL_BLOCK_THRESHOLD_MS) {
                    criticalBlockCount.incrementAndGet()
                    // Remove screen refresh log
                }
            }
        }

        /**
         * Monitor frame rate
         */
        private suspend fun monitorFrameRate() {
            // Simulate frame rate monitoring
            val currentFrameTime = System.currentTimeMillis() % 1000
            val expectedFrameTime = if (currentRefreshRate >= 120f) FRAME_TIME_120HZ_MS else FRAME_TIME_60HZ_MS

            if (currentFrameTime > expectedFrameTime * 1.5) {
                frameDropCount.incrementAndGet()
                // Remove screen refresh log
            } else {
                highRefreshRateFrameCount.incrementAndGet()
            }
        }

        /**
         * Set current refresh rate
         */
        fun setCurrentRefreshRate(refreshRate: Float) {
            currentRefreshRate = refreshRate
            // Remove screen refresh log
        }

        /**
         * Get performance statistics
         */
        fun getPerformanceStats(): PerformanceStats120Hz {
            return PerformanceStats120Hz(
                frameDropCount = frameDropCount.get(),
                mainThreadBlockCount = mainThreadBlockCount.get(),
                criticalBlockCount = criticalBlockCount.get(),
                highRefreshRateFrameCount = highRefreshRateFrameCount.get(),
                currentRefreshRate = currentRefreshRate,
                isHighRefreshRate = currentRefreshRate >= 120f,
            )
        }

        /**
         * Check if high refresh rate is supported
         */
        fun isHighRefreshRateSupported(): Boolean {
            return currentRefreshRate >= 120f
        }

        /**
         * Get recommended animation duration
         */
        fun getRecommendedAnimationDuration(): Int {
            return if (isHighRefreshRateSupported()) {
                150 // Fast animation at 120Hz
            } else {
                300 // Standard animation at 60Hz
            }
        }
    }

/**
 * 120Hz performance statistics data
 */
data class PerformanceStats120Hz(
    val frameDropCount: Long,
    val mainThreadBlockCount: Long,
    val criticalBlockCount: Long,
    val highRefreshRateFrameCount: Long,
    val currentRefreshRate: Float,
    val isHighRefreshRate: Boolean,
) {
    val frameDropRate: Float
        get() =
            if (highRefreshRateFrameCount > 0) {
                frameDropCount.toFloat() / highRefreshRateFrameCount.toFloat()
            } else {
                0f
            }

    val performanceScore: Float
        get() =
            when {
                frameDropRate < 0.05f && criticalBlockCount == 0L -> 100f // Excellent
                frameDropRate < 0.1f && criticalBlockCount < 5L -> 80f // Good
                frameDropRate < 0.2f && criticalBlockCount < 10L -> 60f // Average
                else -> 40f // Needs optimization
            }
}

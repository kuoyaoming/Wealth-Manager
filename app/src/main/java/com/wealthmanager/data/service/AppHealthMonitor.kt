package com.wealthmanager.data.service

import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitor application health and performance
 */
@Singleton
class AppHealthMonitor
    @Inject
    constructor(
        private val debugLogManager: DebugLogManager,
    ) {
        private var errorCount = 0
        private var lastErrorTime = 0L
        private var isHealthy = true

        /**
         * Monitor API health
         */
        suspend fun monitorApiHealth(): Flow<HealthStatus> =
            flow {
                while (true) {
                    val status = checkApiHealth()
                    emit(status)
                    delay(30000L) // Check every 30 seconds
                }
            }

        /**
         * Check API health status
         */
        private suspend fun checkApiHealth(): HealthStatus {
            return try {
                // Simulate API health check
                val responseTime = measureApiResponseTime()
                val errorRate = calculateErrorRate()

                when {
                    errorRate > 0.5 -> {
                        isHealthy = false
                        HealthStatus.UNHEALTHY("High error rate: $errorRate")
                    }
                    responseTime > 10000 -> {
                        isHealthy = false
                        HealthStatus.DEGRADED("Slow response time: ${responseTime}ms")
                    }
                    else -> {
                        isHealthy = true
                        HealthStatus.HEALTHY("API is healthy")
                    }
                }
            } catch (e: Exception) {
                isHealthy = false
                HealthStatus.UNHEALTHY("Health check failed: ${e.message}")
            }
        }

        /**
         * Record error for monitoring
         */
        fun recordError(error: String) {
            errorCount++
            lastErrorTime = System.currentTimeMillis()
            debugLogManager.log("HEALTH_MONITOR", "Error recorded: $error (Total: $errorCount)")
        }

        /**
         * Record successful operation
         */
        fun recordSuccess() {
            debugLogManager.log("HEALTH_MONITOR", "Success recorded")
        }

        /**
         * Get current health status
         */
        fun getCurrentHealth(): HealthStatus {
            return if (isHealthy) {
                HealthStatus.HEALTHY("Application is healthy")
            } else {
                HealthStatus.UNHEALTHY("Application has issues")
            }
        }

        /**
         * Measure API response time
         */
        private suspend fun measureApiResponseTime(): Long {
            val startTime = System.currentTimeMillis()
            try {
                // Simulate API call
                delay(100L)
                return System.currentTimeMillis() - startTime
            } catch (e: Exception) {
                return Long.MAX_VALUE
            }
        }

        /**
         * Calculate error rate
         */
        private fun calculateErrorRate(): Double {
            val timeSinceLastError = System.currentTimeMillis() - lastErrorTime
            val timeWindow = 300000L // 5 minutes

            return if (timeSinceLastError < timeWindow) {
                errorCount.toDouble() / (timeWindow / 1000.0)
            } else {
                0.0
            }
        }

        /**
         * Reset health status
         */
        fun resetHealth() {
            errorCount = 0
            lastErrorTime = 0L
            isHealthy = true
            debugLogManager.log("HEALTH_MONITOR", "Health status reset")
        }
    }

/**
 * Health status enumeration
 */
sealed class HealthStatus {
    data class HEALTHY(val message: String) : HealthStatus()

    data class DEGRADED(val message: String) : HealthStatus()

    data class UNHEALTHY(val message: String) : HealthStatus()
}

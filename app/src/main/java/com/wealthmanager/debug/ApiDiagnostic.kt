package com.wealthmanager.debug

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiDiagnostic
    @Inject
    constructor(
        private val context: Context,
        private val debugLogManager: DebugLogManager,
    ) {
        fun runDiagnostic(): DiagnosticResult {
            debugLogManager.log("DIAGNOSTIC", "Starting network-only diagnostic...")
            val networkStatus = checkNetworkConnectivity()
            return DiagnosticResult(networkStatus = networkStatus)
        }

        private fun checkNetworkConnectivity(): NetworkStatus {
            return try {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)

                val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
                val hasWifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
                val hasCellular = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true

                debugLogManager.log(
                    "DIAGNOSTIC",
                    "Network status: Internet=$hasInternet, WiFi=$hasWifi, Cellular=$hasCellular",
                )

                NetworkStatus(
                    isConnected = hasInternet,
                    hasWifi = hasWifi,
                    hasCellular = hasCellular,
                )
            } catch (e: Exception) {
                debugLogManager.logError("DIAGNOSTIC", "Network check failed: ${e.message}")
                NetworkStatus(isConnected = false, hasWifi = false, hasCellular = false)
            }
        }
    }

data class DiagnosticResult(
    val networkStatus: NetworkStatus,
) {
    val isHealthy: Boolean
        get() = networkStatus.isConnected
}

data class NetworkStatus(
    val isConnected: Boolean,
    val hasWifi: Boolean,
    val hasCellular: Boolean,
)

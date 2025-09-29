package com.wealthmanager.debug

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.wealthmanager.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiDiagnostic @Inject constructor(
    private val context: Context,
    private val debugLogManager: DebugLogManager
) {
    
    suspend fun runDiagnostic(): DiagnosticResult {
        debugLogManager.log("DIAGNOSTIC", "Starting API diagnostic...")
        
        val networkStatus = checkNetworkConnectivity()
        val apiKeyStatus = checkApiKey()
        val finnhubStatus = testFinnhubApi()
        
        return DiagnosticResult(
            networkStatus = networkStatus,
            apiKeyStatus = apiKeyStatus,
            finnhubStatus = finnhubStatus
        )
    }
    
    private fun checkNetworkConnectivity(): NetworkStatus {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            
            val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            val hasWifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
            val hasCellular = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
            
            debugLogManager.log("DIAGNOSTIC", "Network status: Internet=$hasInternet, WiFi=$hasWifi, Cellular=$hasCellular")
            
            NetworkStatus(
                isConnected = hasInternet,
                hasWifi = hasWifi,
                hasCellular = hasCellular
            )
        } catch (e: Exception) {
            debugLogManager.logError("DIAGNOSTIC", "Network check failed: ${e.message}")
            NetworkStatus(isConnected = false, hasWifi = false, hasCellular = false)
        }
    }
    
    private fun checkApiKey(): ApiKeyStatus {
        val finnhubKey = BuildConfig.FINNHUB_API_KEY
        val exchangeKey = BuildConfig.EXCHANGE_RATE_API_KEY
        
        debugLogManager.log("DIAGNOSTIC", "Finnhub key length: ${finnhubKey.length}")
        debugLogManager.log("DIAGNOSTIC", "Exchange key length: ${exchangeKey.length}")
        
        return ApiKeyStatus(
            finnhubKeyValid = finnhubKey.isNotEmpty() && finnhubKey.length > 10,
            exchangeKeyValid = exchangeKey.isNotEmpty() && exchangeKey.length > 10,
            finnhubKeyPreview = finnhubKey.take(8) + "...",
            exchangeKeyPreview = exchangeKey.take(8) + "..."
        )
    }
    
    private suspend fun testFinnhubApi(): FinnhubStatus {
        return withContext(Dispatchers.IO) {
            try {
                debugLogManager.log("DIAGNOSTIC", "Testing Finnhub API connectivity...")
                
                val url = URL("https://finnhub.io/api/v1/quote?symbol=AAPL&token=${BuildConfig.FINNHUB_API_KEY}")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                
                val responseCode = connection.responseCode
                val responseMessage = connection.responseMessage
                
                debugLogManager.log("DIAGNOSTIC", "Finnhub API response: $responseCode - $responseMessage")
                
                connection.disconnect()
                
                FinnhubStatus(
                    isReachable = responseCode in 200..299,
                    responseCode = responseCode,
                    responseMessage = responseMessage,
                    isApiKeyValid = responseCode != 401 && responseCode != 403
                )
            } catch (e: Exception) {
                debugLogManager.logError("DIAGNOSTIC", "Finnhub API test failed: ${e.message}")
                FinnhubStatus(
                    isReachable = false,
                    responseCode = -1,
                    responseMessage = e.message ?: "Unknown error",
                    isApiKeyValid = false
                )
            }
        }
    }
}

data class DiagnosticResult(
    val networkStatus: NetworkStatus,
    val apiKeyStatus: ApiKeyStatus,
    val finnhubStatus: FinnhubStatus
) {
    val isHealthy: Boolean
        get() = networkStatus.isConnected && apiKeyStatus.finnhubKeyValid && finnhubStatus.isReachable
}

data class NetworkStatus(
    val isConnected: Boolean,
    val hasWifi: Boolean,
    val hasCellular: Boolean
)

data class ApiKeyStatus(
    val finnhubKeyValid: Boolean,
    val exchangeKeyValid: Boolean,
    val finnhubKeyPreview: String,
    val exchangeKeyPreview: String
)

data class FinnhubStatus(
    val isReachable: Boolean,
    val responseCode: Int,
    val responseMessage: String,
    val isApiKeyValid: Boolean
)

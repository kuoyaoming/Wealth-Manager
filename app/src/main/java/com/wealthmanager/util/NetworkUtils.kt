package com.wealthmanager.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Network connection status manager.
 */
@Singleton
class NetworkUtils
    @Inject
    constructor(
        private val context: Context,
    ) {
        private val _isConnected = MutableStateFlow(false)
        val isConnected: Flow<Boolean> = _isConnected.asStateFlow()

        private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        init {
            _isConnected.value = isNetworkAvailable()
        }

        /**
         * Check if network is available.
         */
        fun isNetworkAvailable(): Boolean {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        }

        /**
         * Check if there is network connection.
         */
        suspend fun hasNetworkConnection(): Boolean {
            return isNetworkAvailable()
        }
    }

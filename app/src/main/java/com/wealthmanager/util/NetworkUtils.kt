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
 * 網路連接狀態管理器
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
            // 初始檢查網路狀態
            _isConnected.value = isNetworkAvailable()
        }

        /**
         * 檢查網路是否可用
         */
        fun isNetworkAvailable(): Boolean {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        }

        /**
         * 檢查是否有網路連接
         */
        suspend fun hasNetworkConnection(): Boolean {
            return isNetworkAvailable()
        }
    }

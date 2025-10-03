package com.wealthmanager.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * URL 開啟工具
 * 用於開啟外部連結
 */
object UrlLauncher {
    /**
     * 開啟外部 URL
     */
    fun openUrl(
        context: Context,
        url: String,
    ) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // 如果無法開啟，可以記錄錯誤或顯示提示
            e.printStackTrace()
        }
    }
}

/**
 * Compose 版本的 URL 開啟器
 */
@Composable
fun rememberUrlLauncher(): (String) -> Unit {
    val context = LocalContext.current
    return remember {
        { url -> UrlLauncher.openUrl(context, url) }
    }
}

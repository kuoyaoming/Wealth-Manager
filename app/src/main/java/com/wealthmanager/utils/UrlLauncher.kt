package com.wealthmanager.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * URL launcher utility for opening external links.
 */
object UrlLauncher {
    /**
     * Open external URL.
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
            e.printStackTrace()
        }
    }
}

/**
 * Compose version of URL launcher.
 */
@Composable
fun rememberUrlLauncher(): (String) -> Unit {
    val context = LocalContext.current
    return remember {
        { url -> UrlLauncher.openUrl(context, url) }
    }
}

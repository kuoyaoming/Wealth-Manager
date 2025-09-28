package com.wealthmanager

import android.app.Application
import com.wealthmanager.auth.AuthStateManager
import com.wealthmanager.auth.AuthStateManagerEntryPoint
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WealthManagerApplication : Application(), AuthStateManagerEntryPoint {
    
    @Inject
    lateinit var authStateManager: AuthStateManager
    
    override fun authStateManager(): AuthStateManager = authStateManager
}
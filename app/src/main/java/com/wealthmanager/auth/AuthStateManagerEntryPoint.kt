package com.wealthmanager.auth

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AuthStateManagerEntryPoint {
    fun authStateManager(): AuthStateManager
}
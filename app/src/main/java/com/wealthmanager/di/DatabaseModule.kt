package com.wealthmanager.di

import android.content.Context
import com.wealthmanager.auth.BiometricAuthManager
import com.wealthmanager.data.dao.CashAssetDao
import com.wealthmanager.data.dao.ExchangeRateDao
import com.wealthmanager.data.dao.StockAssetDao
import com.wealthmanager.data.database.WealthManagerDatabase
import com.wealthmanager.data.repository.AssetRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideWealthManagerDatabase(@ApplicationContext context: Context): WealthManagerDatabase {
        return WealthManagerDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideCashAssetDao(database: WealthManagerDatabase): CashAssetDao {
        return database.cashAssetDao()
    }
    
    @Provides
    fun provideStockAssetDao(database: WealthManagerDatabase): StockAssetDao {
        return database.stockAssetDao()
    }
    
    @Provides
    fun provideExchangeRateDao(database: WealthManagerDatabase): ExchangeRateDao {
        return database.exchangeRateDao()
    }
    
    @Provides
    @Singleton
    fun provideBiometricAuthManager(): BiometricAuthManager {
        return BiometricAuthManager()
    }
}
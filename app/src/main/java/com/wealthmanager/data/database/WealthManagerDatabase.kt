package com.wealthmanager.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wealthmanager.data.dao.CashAssetDao
import com.wealthmanager.data.dao.ExchangeRateDao
import com.wealthmanager.data.dao.StockAssetDao
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.ExchangeRate
import com.wealthmanager.data.entity.StockAsset

@Database(
    entities = [CashAsset::class, StockAsset::class, ExchangeRate::class],
    version = 4,
    exportSchema = false,
)
abstract class WealthManagerDatabase : RoomDatabase() {
    abstract fun cashAssetDao(): CashAssetDao

    abstract fun stockAssetDao(): StockAssetDao

    abstract fun exchangeRateDao(): ExchangeRateDao
}

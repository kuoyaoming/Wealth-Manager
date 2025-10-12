package com.wealthmanager.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wealthmanager.data.dao.CashAssetDao
import com.wealthmanager.data.dao.ExchangeRateDao
import com.wealthmanager.data.dao.StockAssetDao
import com.wealthmanager.data.database.WealthManagerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@EntryPoint
interface DatabaseEntryPoint {
    fun wealthManagerDatabase(): WealthManagerDatabase
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private fun getMigration1to2(): Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE stock_assets_new (
                    id TEXT NOT NULL PRIMARY KEY,
                    symbol TEXT NOT NULL,
                    companyName TEXT NOT NULL,
                    shares REAL NOT NULL,
                    market TEXT NOT NULL,
                    currentPrice REAL NOT NULL,
                    originalCurrency TEXT NOT NULL,
                    twdEquivalent REAL NOT NULL,
                    lastUpdated INTEGER NOT NULL
                )
            """
            )
            database.execSQL(
                """
                INSERT INTO stock_assets_new
                SELECT id, symbol, companyName, CAST(shares AS REAL), market,
                       currentPrice, originalCurrency, twdEquivalent, lastUpdated
                FROM stock_assets
            """
            )
            database.execSQL("DROP TABLE stock_assets")
            database.execSQL("ALTER TABLE stock_assets_new RENAME TO stock_assets")
        }
    }

    private fun getMigration2to3(): Migration = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE stock_assets ADD COLUMN dayChange REAL NOT NULL DEFAULT 0.0")
            database.execSQL("ALTER TABLE stock_assets ADD COLUMN dayChangePercentage REAL NOT NULL DEFAULT 0.0")
        }
    }

    private fun getMigration3to4(): Migration = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create a new table with the desired schema
            database.execSQL(
                """
                CREATE TABLE exchange_rates_new (
                    currencyPair TEXT NOT NULL PRIMARY KEY,
                    rate REAL NOT NULL,
                    lastUpdated INTEGER NOT NULL
                )
                """
            )
            // Copy the data from the old table to the new table
            database.execSQL(
                """
                INSERT INTO exchange_rates_new (currencyPair, rate, lastUpdated)
                SELECT currency_pair, rate, last_updated FROM exchange_rates
                """
            )
            // Remove the old table
            database.execSQL("DROP TABLE exchange_rates")
            // Rename the new table to the original table name
            database.execSQL("ALTER TABLE exchange_rates_new RENAME TO exchange_rates")
        }
    }

    @Provides
    @Singleton
    fun provideWealthManagerDatabase(@ApplicationContext context: Context): WealthManagerDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            WealthManagerDatabase::class.java,
            "wealth_manager_database"
        )
            .addMigrations(getMigration1to2(), getMigration2to3(), getMigration3to4())
            .build()
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
}

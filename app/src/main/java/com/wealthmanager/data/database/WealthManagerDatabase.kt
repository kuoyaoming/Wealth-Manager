package com.wealthmanager.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.wealthmanager.data.dao.CashAssetDao
import com.wealthmanager.data.dao.ExchangeRateDao
import com.wealthmanager.data.dao.StockAssetDao
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.ExchangeRate
import com.wealthmanager.data.entity.StockAsset

@Database(
    entities = [CashAsset::class, StockAsset::class, ExchangeRate::class],
    version = 2,
    exportSchema = false
)
abstract class WealthManagerDatabase : RoomDatabase() {

    abstract fun cashAssetDao(): CashAssetDao
    abstract fun stockAssetDao(): StockAssetDao
    abstract fun exchangeRateDao(): ExchangeRateDao

    companion object {
        @Volatile
        private var INSTANCE: WealthManagerDatabase? = null

        // Database migration strategy: from version 1 to version 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Since shares field type changed from INTEGER to REAL, need to rebuild table
                database.execSQL("""
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
                """)

                // Copy data, convert INTEGER shares to REAL
                database.execSQL("""
                    INSERT INTO stock_assets_new
                    SELECT id, symbol, companyName, CAST(shares AS REAL), market,
                           currentPrice, originalCurrency, twdEquivalent, lastUpdated
                    FROM stock_assets
                """)

                // Delete old table
                database.execSQL("DROP TABLE stock_assets")

                // Rename new table
                database.execSQL("ALTER TABLE stock_assets_new RENAME TO stock_assets")
            }
        }

        fun getDatabase(context: Context): WealthManagerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WealthManagerDatabase::class.java,
                    "wealth_manager_database"
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration() // If migration fails, rebuild database
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

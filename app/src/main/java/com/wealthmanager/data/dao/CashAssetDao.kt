package com.wealthmanager.data.dao

import androidx.room.*
import com.wealthmanager.data.entity.CashAsset
import kotlinx.coroutines.flow.Flow

@Dao
interface CashAssetDao {
    @Query("SELECT * FROM cash_assets ORDER BY lastUpdated DESC")
    fun getAllCashAssets(): Flow<List<CashAsset>>

    @Query("SELECT * FROM cash_assets WHERE currency = :currency")
    fun getCashAssetsByCurrency(currency: String): Flow<List<CashAsset>>

    @Query("SELECT * FROM cash_assets WHERE id = :id")
    suspend fun getCashAssetById(id: String): CashAsset?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashAsset(cashAsset: CashAsset)

    @Delete
    suspend fun deleteCashAsset(cashAsset: CashAsset)

    @Query("DELETE FROM cash_assets WHERE id = :id")
    suspend fun deleteCashAssetById(id: String)

    @Query("SELECT SUM(twdEquivalent) FROM cash_assets")
    suspend fun getTotalCashValueInTWD(): Double?

    @Query("SELECT * FROM cash_assets WHERE currency = :currency LIMIT 1")
    suspend fun getCashAssetByCurrencySync(currency: String): CashAsset?
}

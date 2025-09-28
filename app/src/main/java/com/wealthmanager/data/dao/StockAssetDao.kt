package com.wealthmanager.data.dao

import androidx.room.*
import com.wealthmanager.data.entity.StockAsset
import kotlinx.coroutines.flow.Flow

@Dao
interface StockAssetDao {
    
    @Query("SELECT * FROM stock_assets ORDER BY lastUpdated DESC")
    fun getAllStockAssets(): Flow<List<StockAsset>>
    
    @Query("SELECT * FROM stock_assets WHERE market = :market")
    fun getStockAssetsByMarket(market: String): Flow<List<StockAsset>>
    
    @Query("SELECT * FROM stock_assets WHERE id = :id")
    suspend fun getStockAssetById(id: String): StockAsset?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockAsset(stockAsset: StockAsset)
    
    @Update
    suspend fun updateStockAsset(stockAsset: StockAsset)
    
    @Delete
    suspend fun deleteStockAsset(stockAsset: StockAsset)
    
    @Query("DELETE FROM stock_assets WHERE id = :id")
    suspend fun deleteStockAssetById(id: String)
    
    @Query("SELECT SUM(twdEquivalent) FROM stock_assets")
    suspend fun getTotalStockValueInTWD(): Double?
    
    @Query("SELECT * FROM stock_assets WHERE symbol = :symbol")
    suspend fun getStockAssetBySymbol(symbol: String): StockAsset?
}
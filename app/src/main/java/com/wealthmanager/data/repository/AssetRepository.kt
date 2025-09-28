package com.wealthmanager.data.repository

import com.wealthmanager.data.dao.CashAssetDao
import com.wealthmanager.data.dao.ExchangeRateDao
import com.wealthmanager.data.dao.StockAssetDao
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.ExchangeRate
import com.wealthmanager.data.entity.StockAsset
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetRepository @Inject constructor(
    private val cashAssetDao: CashAssetDao,
    private val stockAssetDao: StockAssetDao,
    private val exchangeRateDao: ExchangeRateDao
) {
    
    // Cash Assets
    fun getAllCashAssets(): Flow<List<CashAsset>> = cashAssetDao.getAllCashAssets()
    
    fun getCashAssetsByCurrency(currency: String): Flow<List<CashAsset>> = 
        cashAssetDao.getCashAssetsByCurrency(currency)
    
    suspend fun getCashAssetById(id: String): CashAsset? = cashAssetDao.getCashAssetById(id)
    
    suspend fun insertCashAsset(cashAsset: CashAsset) = cashAssetDao.insertCashAsset(cashAsset)
    
    suspend fun updateCashAsset(cashAsset: CashAsset) = cashAssetDao.updateCashAsset(cashAsset)
    
    suspend fun deleteCashAsset(cashAsset: CashAsset) = cashAssetDao.deleteCashAsset(cashAsset)
    
    suspend fun deleteCashAssetById(id: String) = cashAssetDao.deleteCashAssetById(id)
    
    suspend fun getTotalCashValueInTWD(): Double? = cashAssetDao.getTotalCashValueInTWD()
    
    // Stock Assets
    fun getAllStockAssets(): Flow<List<StockAsset>> = stockAssetDao.getAllStockAssets()
    
    fun getStockAssetsByMarket(market: String): Flow<List<StockAsset>> = 
        stockAssetDao.getStockAssetsByMarket(market)
    
    suspend fun getStockAssetById(id: String): StockAsset? = stockAssetDao.getStockAssetById(id)
    
    suspend fun getStockAssetBySymbol(symbol: String): StockAsset? = 
        stockAssetDao.getStockAssetBySymbol(symbol)
    
    suspend fun insertStockAsset(stockAsset: StockAsset) = stockAssetDao.insertStockAsset(stockAsset)
    
    suspend fun updateStockAsset(stockAsset: StockAsset) = stockAssetDao.updateStockAsset(stockAsset)
    
    suspend fun deleteStockAsset(stockAsset: StockAsset) = stockAssetDao.deleteStockAsset(stockAsset)
    
    suspend fun deleteStockAssetById(id: String) = stockAssetDao.deleteStockAssetById(id)
    
    suspend fun getTotalStockValueInTWD(): Double? = stockAssetDao.getTotalStockValueInTWD()
    
    // Exchange Rates
    fun getAllExchangeRates(): Flow<List<ExchangeRate>> = exchangeRateDao.getAllExchangeRates()
    
    suspend fun getExchangeRateSync(currencyPair: String): ExchangeRate? = 
        exchangeRateDao.getExchangeRateSync(currencyPair)
    
    suspend fun insertExchangeRate(exchangeRate: ExchangeRate) = 
        exchangeRateDao.insertExchangeRate(exchangeRate)
    
    suspend fun updateExchangeRate(exchangeRate: ExchangeRate) = 
        exchangeRateDao.updateExchangeRate(exchangeRate)
    
    suspend fun deleteExchangeRate(exchangeRate: ExchangeRate) = 
        exchangeRateDao.deleteExchangeRate(exchangeRate)
    
    // Exchange Rate Flow
    fun getExchangeRate(currencyPair: String): Flow<ExchangeRate> = 
        exchangeRateDao.getExchangeRate(currencyPair)
}
package com.wealthmanager.data.repository

import com.wealthmanager.data.dao.CashAssetDao
import com.wealthmanager.data.dao.ExchangeRateDao
import com.wealthmanager.data.dao.StockAssetDao
import com.wealthmanager.data.entity.CashAsset
import com.wealthmanager.data.entity.ExchangeRate
import com.wealthmanager.data.entity.StockAsset
import com.wealthmanager.debug.DebugLogManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing asset data operations.
 *
 * This repository provides a unified interface for:
 * - Cash asset management (CRUD operations)
 * - Stock asset management (CRUD operations)
 *
 * @property cashAssetDao DAO for cash asset database operations
 * @property stockAssetDao DAO for stock asset database operations
 * @property debugLogManager Manager for debug logging
 */
@Singleton
class AssetRepository
@Inject
constructor(
    private val cashAssetDao: CashAssetDao,
    private val stockAssetDao: StockAssetDao,
    private val exchangeRateDao: ExchangeRateDao,
    private val debugLogManager: DebugLogManager,
) {
    // Cash Assets
    fun getAllCashAssets(): Flow<List<CashAsset>> = cashAssetDao.getAllCashAssets()

    fun getCashAssetsByCurrency(currency: String): Flow<List<CashAsset>> =
        cashAssetDao.getCashAssetsByCurrency(currency)

    suspend fun getCashAssetById(id: String): CashAsset? = cashAssetDao.getCashAssetById(id)

    suspend fun insertCashAsset(cashAsset: CashAsset) {
        debugLogManager.log("REPOSITORY", "Inserting cash asset: ${cashAsset.currency} ${cashAsset.amount}")
        cashAssetDao.insertCashAsset(cashAsset)
        debugLogManager.log("REPOSITORY", "Cash asset inserted successfully")
    }

    suspend fun updateCashAsset(cashAsset: CashAsset) {
        debugLogManager.log("REPOSITORY", "Updating cash asset: ${cashAsset.id}")
        cashAssetDao.insertCashAsset(cashAsset)
        debugLogManager.log("REPOSITORY", "Cash asset updated successfully")
    }

    suspend fun deleteCashAsset(cashAsset: CashAsset) {
        debugLogManager.log("REPOSITORY", "Deleting cash asset: ${cashAsset.id}")
        cashAssetDao.deleteCashAsset(cashAsset)
        debugLogManager.log("REPOSITORY", "Cash asset deleted successfully")
    }

    suspend fun deleteCashAssetById(id: String) {
        debugLogManager.log("REPOSITORY", "Deleting cash asset by ID: $id")
        cashAssetDao.deleteCashAssetById(id)
        debugLogManager.log("REPOSITORY", "Cash asset deleted by ID successfully")
    }

    suspend fun getTotalCashValueInTWD(): Double? = cashAssetDao.getTotalCashValueInTWD()

    suspend fun getCashAssetByCurrencySync(currency: String): CashAsset? =
        cashAssetDao.getCashAssetByCurrencySync(currency)

    // Stock Assets
    fun getAllStockAssets(): Flow<List<StockAsset>> = stockAssetDao.getAllStockAssets()

    suspend fun getAllCashAssetsSync(): List<CashAsset> = cashAssetDao.getAllCashAssets().first()

    suspend fun getAllStockAssetsSync(): List<StockAsset> = stockAssetDao.getAllStockAssets().first()

    fun getStockAssetsByMarket(market: String): Flow<List<StockAsset>> =
        stockAssetDao.getStockAssetsByMarket(
            market,
        )

    suspend fun getStockAssetById(id: String): StockAsset? = stockAssetDao.getStockAssetById(id)

    suspend fun getStockAssetBySymbol(symbol: String): StockAsset? = stockAssetDao.getStockAssetBySymbol(symbol)

    suspend fun getStockAssetSync(symbol: String): StockAsset? = stockAssetDao.getStockAssetBySymbol(symbol)

    suspend fun insertStockAsset(stockAsset: StockAsset) {
        debugLogManager.log("REPOSITORY", "Inserting stock asset: ${stockAsset.symbol} ${stockAsset.shares} shares")
        stockAssetDao.insertStockAsset(stockAsset)
        debugLogManager.log("REPOSITORY", "Stock asset inserted successfully")
    }

    suspend fun updateStockAsset(stockAsset: StockAsset) {
        debugLogManager.log("REPOSITORY", "Updating stock asset: ${stockAsset.symbol}")
        stockAssetDao.insertStockAsset(stockAsset)
        debugLogManager.log("REPOSITORY", "Stock asset updated successfully")
    }

    suspend fun deleteStockAsset(stockAsset: StockAsset) {
        debugLogManager.log("REPOSITORY", "Deleting stock asset: ${stockAsset.symbol}")
        stockAssetDao.deleteStockAsset(stockAsset)
        debugLogManager.log("REPOSITORY", "Stock asset deleted successfully")
    }

    suspend fun deleteStockAssetById(id: String) {
        debugLogManager.log("REPOSITORY", "Deleting stock asset by ID: $id")
        stockAssetDao.deleteStockAssetById(id)
        debugLogManager.log("REPOSITORY", "Stock asset deleted by ID successfully")
    }

    suspend fun getTotalStockValueInTWD(): Double? = stockAssetDao.getTotalStockValueInTWD()

    // Exchange Rates
    fun getExchangeRate(currencyPair: String): Flow<ExchangeRate?> =
        exchangeRateDao.getExchangeRate(currencyPair)

    suspend fun getExchangeRateSync(currencyPair: String): ExchangeRate? =
        exchangeRateDao.getExchangeRateSync(currencyPair)

    suspend fun insertExchangeRate(exchangeRate: ExchangeRate) {
        debugLogManager.log("REPOSITORY", "Inserting exchange rate: ${exchangeRate.currencyPair}")
        exchangeRateDao.insertExchangeRate(exchangeRate)
        debugLogManager.log("REPOSITORY", "Exchange rate inserted successfully")
    }
}

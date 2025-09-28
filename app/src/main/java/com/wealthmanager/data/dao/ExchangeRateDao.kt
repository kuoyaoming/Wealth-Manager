package com.wealthmanager.data.dao

import androidx.room.*
import com.wealthmanager.data.entity.ExchangeRate
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    
    @Query("SELECT * FROM exchange_rates")
    fun getAllExchangeRates(): Flow<List<ExchangeRate>>
    
    @Query("SELECT * FROM exchange_rates WHERE currencyPair = :currencyPair")
    suspend fun getExchangeRateSync(currencyPair: String): ExchangeRate?
    
    @Query("SELECT * FROM exchange_rates WHERE currencyPair = :currencyPair")
    fun getExchangeRate(currencyPair: String): Flow<ExchangeRate>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRate(exchangeRate: ExchangeRate)
    
    @Update
    suspend fun updateExchangeRate(exchangeRate: ExchangeRate)
    
    @Delete
    suspend fun deleteExchangeRate(exchangeRate: ExchangeRate)
    
    @Query("DELETE FROM exchange_rates WHERE currencyPair = :currencyPair")
    suspend fun deleteExchangeRateByPair(currencyPair: String)
}
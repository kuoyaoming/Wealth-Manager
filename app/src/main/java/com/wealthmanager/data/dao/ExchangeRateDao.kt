package com.wealthmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wealthmanager.data.entity.ExchangeRate
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    @Query("SELECT * FROM exchange_rates")
    fun getAllExchangeRates(): Flow<List<ExchangeRate>>

    @Query("SELECT * FROM exchange_rates WHERE currency_pair = :currencyPair")
    fun getExchangeRate(currencyPair: String): Flow<ExchangeRate?>

    @Query("SELECT * FROM exchange_rates WHERE currency_pair = :currencyPair")
    suspend fun getExchangeRateSync(currencyPair: String): ExchangeRate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRate(exchangeRate: ExchangeRate)

    @Update
    suspend fun updateExchangeRate(exchangeRate: ExchangeRate)

    @Query("DELETE FROM exchange_rates WHERE currency_pair = :currencyPair")
    suspend fun deleteExchangeRate(currencyPair: String)
}
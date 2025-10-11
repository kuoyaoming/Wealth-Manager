package com.wealthmanager.data.repository

import com.wealthmanager.data.dao.ExchangeRateDao
import com.wealthmanager.data.entity.ExchangeRate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExchangeRateRepositoryImpl @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao
) : ExchangeRateRepository {
    override fun getLatestExchangeRateFlow(currencyPair: String): Flow<ExchangeRate?> {
        return exchangeRateDao.getExchangeRate(currencyPair)
    }
}
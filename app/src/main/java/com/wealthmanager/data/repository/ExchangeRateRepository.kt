package com.wealthmanager.data.repository

import com.wealthmanager.data.entity.ExchangeRate
import kotlinx.coroutines.flow.Flow

interface ExchangeRateRepository {
    fun getLatestExchangeRateFlow(currencyPair: String): Flow<ExchangeRate?>
}
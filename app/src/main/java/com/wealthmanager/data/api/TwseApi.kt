package com.wealthmanager.data.api

import retrofit2.http.GET

interface TwseApi {
    @GET("v1/exchangeReport/STOCK_DAY_ALL")
    suspend fun getAllStockPrices(): List<TwseStockItem>
}

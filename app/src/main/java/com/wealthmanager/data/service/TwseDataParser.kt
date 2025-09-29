package com.wealthmanager.data.service

import com.wealthmanager.data.api.TwseStockData
import com.wealthmanager.data.api.TwseStockItem
import com.wealthmanager.debug.DebugLogManager
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TWSE資料解析器
 * 負責解析台灣證券交易所API返回的資料
 */
@Singleton
class TwseDataParser @Inject constructor(
    private val debugLogManager: DebugLogManager
) {
    
    /**
     * 從所有股票資料中查找特定股票
     */
    fun findStockFromAllData(allData: List<TwseStockItem>, stockNo: String): TwseStockData? {
        return try {
            val stockItem = allData.find { it.Code == stockNo }
            stockItem?.let { convertToTwseStockData(it) }
        } catch (e: Exception) {
            debugLogManager.logError("Failed to find stock $stockNo in TWSE data: ${e.message}", e)
            null
        }
    }
    
    /**
     * 將TwseStockItem轉換為TwseStockData
     */
    private fun convertToTwseStockData(item: TwseStockItem): TwseStockData {
        return TwseStockData(
            stockNo = item.Code,
            stockName = item.Name,
            tradeVolume = item.TradeVolume,
            tradeValue = item.TradeValue,
            open = item.OpeningPrice,
            high = item.HighestPrice,
            low = item.LowestPrice,
            close = item.ClosingPrice,
            change = item.Change,
            changePercent = calculateChangePercent(item.Change, item.ClosingPrice),
            transactionCount = item.Transaction
        )
    }
    
    /**
     * 計算漲跌百分比
     */
    private fun calculateChangePercent(change: String, closingPrice: String): String {
        return try {
            val changeValue = change.toDouble()
            val priceValue = closingPrice.toDouble()
            if (priceValue != 0.0) {
                val percent = (changeValue / (priceValue - changeValue)) * 100
                String.format("%.2f", percent)
            } else {
                "0.00"
            }
        } catch (e: Exception) {
            debugLogManager.logError("Failed to calculate change percent: ${e.message}", e)
            "0.00"
        }
    }
    
    /**
     * 獲取今天的日期字串 (YYYYMMDD格式)
     */
    fun getTodayDateString(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    /**
     * 清理台股代碼格式
     */
    fun cleanTaiwanStockSymbol(symbol: String): String {
        return symbol.removeSuffix(".TW").removeSuffix(":TW")
    }
    
    /**
     * 驗證TWSE API回應
     */
    fun validateTwseResponse(data: List<TwseStockItem>): Boolean {
        return data.isNotEmpty()
    }
}
package com.wealthmanager.data.service

import com.wealthmanager.data.api.TwseStockItem
import com.wealthmanager.debug.DebugLogManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

// Create a mock DebugLogManager for testing
private fun createMockDebugLogManager(): DebugLogManager {
    return Mockito.mock(DebugLogManager::class.java)
}

class TwseDataParserTest {
    private lateinit var parser: TwseDataParser

    @Before
    fun setup() {
        parser = TwseDataParser(createMockDebugLogManager())
    }

    @Test
    fun cleanTaiwanStockSymbol_removesSuffixes() {
        assertEquals("2330", parser.cleanTaiwanStockSymbol("2330.TW"))
        assertEquals("2330", parser.cleanTaiwanStockSymbol("2330:TW"))
        assertEquals("AAPL", parser.cleanTaiwanStockSymbol("AAPL"))
    }

    @Test
    fun validateTwseResponse_checksNonEmpty() {
        val empty = emptyList<TwseStockItem>()
        val nonEmpty = listOf(sampleItem(code = "2330"))
        assertEquals(false, parser.validateTwseResponse(empty))
        assertEquals(true, parser.validateTwseResponse(nonEmpty))
    }

    @Test
    fun findStockFromAllData_returnsConvertedData_whenPresent() {
        val items =
            listOf(
                sampleItem(code = "1101"),
                sampleItem(
                    code = "2330",
                    change = "2.00",
                    close = "600.00",
                ),
                sampleItem(code = "2603"),
            )

        val result = parser.findStockFromAllData(items, "2330")
        assertNotNull(result)
        result!!
        assertEquals("2330", result.stockNo)
        assertEquals("2330 Name", result.stockName)
        assertEquals("600.00", result.close)
        // changePercent = change / (close - change) * 100 = 2 / 598 * 100
        assertEquals(String.format("%.2f", 2.0 / 598.0 * 100.0), result.changePercent)
    }

    @Test
    fun findStockFromAllData_returnsNull_whenMissing() {
        val items =
            listOf(
                sampleItem(code = "1101"),
                sampleItem(code = "2603"),
            )
        val result = parser.findStockFromAllData(items, "2330")
        assertNull(result)
    }

    private fun sampleItem(
        code: String,
        name: String = "$code Name",
        tradeVolume: String = "1000",
        tradeValue: String = "1000000",
        open: String = "590.00",
        high: String = "610.00",
        low: String = "580.00",
        close: String = "600.00",
        change: String = "1.00",
        tx: String = "100",
    ): TwseStockItem {
        return TwseStockItem(
            Date = "20250101",
            Code = code,
            Name = name,
            TradeVolume = tradeVolume,
            TradeValue = tradeValue,
            OpeningPrice = open,
            HighestPrice = high,
            LowestPrice = low,
            ClosingPrice = close,
            Change = change,
            Transaction = tx,
        )
    }
}

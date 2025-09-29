# Wealth Manager

一個使用 Jetpack Compose 建構的現代個人理財追蹤應用程式。具備生物識別安全、本地資產管理、即時市場數據和智能 API 切換功能。

## 主要功能

- 🔐 **生物識別安全** - 指紋/臉部識別認證
- 💰 **投資組合追蹤** - 現金和股票投資管理
- 📊 **即時市場數據** - 透過 Finnhub API 和 Alpha Vantage 備援
- 🎨 **Material You** - 動態主題設計
- 🌍 **多語言支援** - 英文和繁體中文
- 📱 **Android 16** - 最新 Android 功能

## 安全特性

- **本地儲存** - 所有數據加密儲存在裝置上
- **生物識別認證** - 無需密碼，24小時會話超時
- **無雲端同步** - 完全隱私保護
- **會話管理** - 自動認證狀態管理

## 資產管理

- **現金追蹤** - 支援 TWD 和 USD
- **股票投資組合** - 台灣和美國市場
- **智能搜尋** - 即時股票代碼搜尋和匹配
- **資產編輯** - 完整的 CRUD 操作
- **即時價格更新** - 自動和手動刷新

## 市場數據

- **Finnhub API** - 主要數據源，支援美股和台股即時價格
- **Alpha Vantage API** - 備援數據源，自動故障轉移
- **TWSE API** - 台灣股票交易所數據整合
- **智能 API 切換** - 自動故障轉移確保數據可用性
- **匯率轉換** - 自動計算 TWD 等值
- **快取支援** - 離線時使用快取數據

## 技術架構

- **UI**: Jetpack Compose + Material 3
- **架構模式**: MVVM + Repository Pattern
- **依賴注入**: Hilt
- **資料庫**: Room (本地加密儲存)
- **網路**: Retrofit + OkHttp
- **認證**: Android Biometric API
- **程式語言**: Kotlin
- **目標平台**: Android 16 (API 36)
- **響應式設計**: 自適應佈局系統

## 安裝說明

```bash
# 複製專案
git clone https://github.com/yourusername/wealth-manager.git

# 建置專案
./gradlew assembleDebug

# 安裝到裝置
./gradlew installDebug
```

## 系統需求

- Android 16+ (API 36)
- 生物識別認證 (建議)
- 網路連線 (市場數據)
- 最少 100MB 儲存空間

## 應用程式架構

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   UI 層         │    │   業務邏輯層     │    │   資料層        │
│                 │    │                 │    │                 │
│ • Compose UI    │◄──►│ • ViewModels    │◄──►│ • Repository    │
│ • Navigation    │    │ • Use Cases     │    │ • Room DB       │
│ • Material 3    │    │ • Managers      │    │ • API Service   │
│ • 響應式設計     │    │ • 認證管理      │    │ • 快取管理      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 核心功能模組

### 🔐 認證系統
- **BiometricAuthManager** - 生物識別認證管理
- **AuthStateManager** - 會話狀態管理 (24小時超時)
- **BiometricAuthScreen** - 認證介面

### 💰 資產管理
- **AssetsScreen** - 資產列表管理
- **AddAssetDialog** - 新增資產對話框
- **EditAssetDialog** - 編輯資產功能
- **CashAsset/StockAsset** - 現金/股票實體

### 📊 市場數據
- **MarketDataService** - 市場數據服務
- **ApiProviderService** - API 提供者服務
- **FinnhubApi/TwseApi** - 多 API 整合
- **CacheManager** - 數據快取管理

### 🎨 使用者介面
- **DashboardScreen** - 主儀表板
- **WealthManagerNavigation** - 導航系統
- **響應式佈局** - 自適應設計
- **Material You** - 動態主題

## 開發貢獻

1. Fork 專案
2. 建立功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交變更 (`git commit -m 'Add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 開啟 Pull Request

## 授權條款

MIT License - 詳見 [LICENSE](LICENSE) 檔案

---

**版本**: 0.1.10  
**最後更新**: 2025  
**Android 支援**: 16+ (API 36)  
**建置狀態**: 生產就緒

## 開發狀態

### ✅ 已完成功能
- 生物識別認證系統
- 資產管理 (現金/股票)
- 即時市場數據整合
- 多 API 故障轉移
- 響應式 UI 設計
- 多語言支援

### 🚧 開發中功能
- 投資組合視覺化圖表
- 進階分析功能
- 數據匯出功能
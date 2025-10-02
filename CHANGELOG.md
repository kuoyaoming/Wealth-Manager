# Changelog

All notable changes to the Wealth Manager project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- GitHub Issues and Pull Request templates
- Code of Conduct
- Changelog documentation

## [1.4.6] - 2025-10-02

### Fixed
- 🛠 修復 release 版 API 查詢失效：R8 混淆導致 Gson 欄位對應失敗
- 🛡️ 針對 Retrofit/Gson 增加 ProGuard 規則、為 DTO 加上 `@SerializedName`
- 🔇 Release 關閉 OkHttp BODY 級別日誌並遮蔽敏感標頭，避免外洩 token

### Notes
- 測試與設定頁「API Key 測試」一致，實際搜尋/報價恢復正常

## [1.4.5] - 2025-01-02

### Added
- 🚀 **Android 2025 官方設計指南合規性**：
  - 更新至 Android API 36 (compileSdk/targetSdk 36)
  - 實現 Splash Screen API 提供專業啟動體驗
  - 完善 Edge-to-Edge 實現，支援沉浸式全螢幕體驗
  - 添加 Android 13+ 通知權限處理
- 🔔 **通知系統**：
  - 新增 `NotificationPermissionManager` 組件
  - 支援 Android 13+ 通知權限請求
  - 整合到設定頁面，提供完整的通知管理
- 🎨 **UI/UX 改進**：
  - 平滑的應用啟動畫面，消除黑屏閃爍
  - 沉浸式全螢幕設計，內容延伸到狀態欄
  - 現代化的系統欄行為配置
- 🌐 **多語言支援**：
  - 新增通知權限相關的中英文字符串
  - 完善無障礙設計的內容描述

### Changed
- 📱 **目標平台**：minSdk 34 → 34, targetSdk 35 → 36, compileSdk 35 → 36
- 🏗️ **構建配置**：添加 Splash Screen 依賴 `androidx.core:core-splashscreen:1.0.1`
- 🎯 **權限聲明**：添加 Android 13+ 通知權限聲明
- 🔧 **代碼修復**：修復 TreemapChartComponent 中的 @Composable 問題

### Fixed
- 🐛 修復 TreemapChartComponent 中的 @Composable 調用問題
- 🐛 修復 semantics 塊中的賦值問題
- 🐛 確保所有改進都能正常編譯和運行

### Security
- 🔒 符合 2025 年 Android 官方安全指南
- 🔒 支援最新的 Android 安全機制
- 🔒 完整的通知權限管理

### Performance
- ⚡ 優化應用啟動性能
- ⚡ 改善全螢幕渲染性能
- ⚡ 更好的記憶體管理

## [1.4.0] - 2025-10-01

### Added
- 🚀 CI 版控與發佈流程：
  - versionName 由 Git Tag（`vX.Y.Z` → `X.Y.Z`）決定
  - versionCode 由 GitHub Actions `GITHUB_RUN_NUMBER` 自動產生（嚴格遞增）
  - Tag 觸發產出簽章 `:app:bundleRelease` 與 `mapping.txt`
- 🧭 響應式佈局：新增 `WindowSizeClass`，優化平板與大螢幕體驗
- 💵 `MoneyFormatter` 公用模組：一致的金額格式化與本地化顯示
- 🔐 安全性/診斷：優化 `ApiDiagnostic` 與網路模組可觀測性

### Changed
- 📦 Gradle：`app/build.gradle` 改為接收 `-PwmVersionName/-PwmVersionCode`，避免設定期呼叫 git，並新增非 CI 禁止 release AAB 保護
- 🧱 Build/Targets：minSdk 34、targetSdk 35、compileSdk 35、Build Tools 36.1.0
- 🧭 Dashboard/Charts：最佳化重組與記憶體配置，改善效能與流暢度
- 🌐 多語言：優化設定頁與語言切換流程，符合 2025 per-app-language 指南

### Fixed
- 🛠️ 修正市場數據查詢穩定性與錯誤處理；改善網路異常情境下的回復能力
- 🧹 修正部分 UI 元件的狀態同步與邏輯邊界條件

### Notes
- 正式釋出 AAB 僅由 CI 產出；本地禁止 `bundleRelease`

## [1.2.0] - 2025-09-30

### Changed
- Version bump for app and wear modules: versionCode 15, versionName 1.2.0
- Improved stock search UX: debounce 450ms, min query length 1, immediate IME search

### Fixed
- Cancelled outdated stock search requests using flatMapLatest to prevent stale results

---

## [1.1.0] - 2025-01-30

### Added
- 🌐 **Multi-language Support**
  - English and Traditional Chinese language options
  - Language switching in Settings page
  - Automatic app restart when language changes
  - Localized UI strings and content descriptions

### Changed
- 🔧 **Language Management**
  - Unified dynamic coloring across all interfaces
  - Consistent theming with system preferences
  - Improved language switching user experience
  - Direct app restart without user confirmation

### Fixed
- 🐛 **Dashboard Stock Price Updates**
  - Fixed stock price query functionality on homepage
  - Corrected refresh button to trigger market data updates
  - Resolved dashboard data loading issues

### Technical Improvements
- 📝 **Code Quality**
  - All comments and logs converted to English
  - Unified code formatting and structure
  - Improved error handling for locale management
  - Enhanced application startup stability

## [1.0.0] - 2025-01-XX

### Added
- 🔐 **Biometric Authentication System**
  - Fingerprint/face recognition authentication
  - 24-hour session timeout
  - Secure session management
  - Skip authentication option

- 💰 **Asset Management**
  - Cash asset tracking (TWD and USD support)
  - Stock portfolio management (Taiwan and US markets)
  - Complete CRUD operations for assets
  - Smart stock search with real-time symbol lookup
  - Asset editing and deletion functionality

- 📊 **Real-time Market Data**
  - Finnhub API integration for US and international stocks
  - TWSE API integration for Taiwan stock exchange
  - Exchange Rate API for USD/TWD conversion
  - Smart API failover and request deduplication
  - Intelligent caching system with offline support
  - Error recovery and retry mechanisms

- 🎨 **Modern UI/UX**
  - Jetpack Compose with Material 3 design
  - Material You dynamic theming
  - Responsive design for different screen sizes
  - 120Hz performance optimization
  - Multi-language support (English/Traditional Chinese)

- ⚡ **Performance Features**
  - Real-time performance monitoring
  - Memory management optimization
  - Smart cache strategy
  - Request deduplication
  - Background data refresh

- 🗄️ **Data Management**
  - Room database with local encrypted storage
  - No cloud sync for complete privacy
  - Secure data deletion
  - Offline data availability

- 🔧 **Developer Experience**
  - Comprehensive documentation
  - Security best practices
  - API setup guides
  - Development environment scripts
  - Contributing guidelines

### Technical Details
- **Architecture**: MVVM + Repository Pattern
- **Dependency Injection**: Hilt
- **Database**: Room with encryption
- **Networking**: Retrofit + OkHttp
- **Authentication**: Android Biometric API
- **Target SDK**: Android 16 (API 36)
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose

### Security Features
- Local-only data storage with encryption
- No personal data collection
- No analytics tracking
- No third-party data sharing
- Secure API key management
- HTTPS-only communications

## [0.1.6] - 2024-XX-XX

### Added
- Initial release preparation
- Basic asset management functionality
- Market data integration
- Biometric authentication

### Changed
- Optimized for Google Play Store
- Enhanced UI components
- Improved performance

## [0.1.0] - 2024-XX-XX

### Added
- Initial project setup
- Basic Android application structure
- Core dependencies and configuration

---

## Version History

| Version | Release Date | Key Features |
|---------|-------------|--------------|
| 1.4.0   | 2025-10-01  | CI-driven versioning, responsive layout, MoneyFormatter |
| 1.1.0   | 2025-01-30  | Multi-language support, improved UX |
| 1.0.0   | 2025-01-XX  | Complete feature set, production ready |
| 0.1.6   | 2024-XX-XX  | Play Store optimization |
| 0.1.0   | 2024-XX-XX  | Initial release |

## Release Notes

### v1.0.0 - Production Ready
This is the first major release of Wealth Manager, featuring a complete personal finance tracking application with biometric security, real-time market data, and modern Android development practices.

### Key Highlights
- **Security First**: Local-only storage with biometric authentication
- **Real-time Data**: Multi-API integration with smart failover
- **Modern UI**: Jetpack Compose with Material 3 design
- **Performance**: 120Hz optimization with intelligent caching
- **Privacy**: No cloud sync, complete data privacy

### Migration Guide
This is the initial release, so no migration is required.

### Breaking Changes
None - this is the initial release.

### Deprecations
None - this is the initial release.

---

**Maintainer**: Wealth Manager Team  
**Last Updated**: 2025年1月  
**Next Release**: TBD

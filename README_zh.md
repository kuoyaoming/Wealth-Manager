# Wealth Manager 財富管家

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Build Status](https://github.com/kuoyaoming/Wealth-Manager/actions/workflows/release.yml/badge.svg)](https://github.com/kuoyaoming/Wealth-Manager/actions)

現代化、隱私優先的 Android 與 Wear OS 個人理財追蹤工具。採用 Jetpack Compose、Kotlin 及最新 Android 技術打造。

---

## 目錄
- [功能特色](#功能特色)
- [快速開始](#快速開始)
- [架構說明](#架構說明)
- [模組結構](#模組結構)
- [安全性](#安全性)
- [在地化](#在地化)
- [貢獻指南](#貢獻指南)
- [授權](#授權)
- [支援](#支援)

---

## 功能特色
- **本機加密儲存**：所有財務資料僅儲存於裝置，無雲端同步、無分析、無第三方分享。
- **生物識別登入**：支援指紋或臉部辨識，硬體安全防護。
- **資產管理**：現金（台幣/美金）與股票投資組合，完整 CRUD。
- **市場數據整合**：即時行情（Finnhub、TWSE、ExchangeRate-API），韌性快取與離線支援。
- **120Hz 效能最佳化**：高刷新率裝置流暢體驗。
- **多語系支援**：繁體中文與英文，介面即時切換。
- **Wear OS 伴侶**：獨立 Wear OS 應用，資料同步、錶面 Tiles、震動回饋。
- **現代化 UI/UX**：Material 3、響應式版面、全螢幕設計。

---

## 快速開始

### 環境需求
- Android Studio Hedgehog 或更新版
- JDK 17+
- Android SDK API 34+（Android 14+）
- Kotlin 1.9.0+

### 建置與執行
```sh
git clone https://github.com/kuoyaoming/Wealth-Manager.git
cd Wealth-Manager
# 設定 Android SDK
# echo "sdk.dir=/path/to/android/sdk" > local.properties
./gradlew assembleDebug
./gradlew installDebug
```

### API 金鑰
- 於 App 設定頁管理 API 金鑰（設定 → 管理 API 金鑰）。
- 不會有硬編碼金鑰，所有金鑰皆加密儲存於本機。
- 支援 API：Finnhub、TWSE、ExchangeRate-API。

---

## 架構說明
- **MVVM + Repository Pattern**：UI、業務邏輯、資料層分離。
- **依賴注入**：Hilt，易於擴充與測試。
- **資料庫**：Room 加密。
- **網路**：Retrofit + OkHttp。
- **認證**：Android Biometric API。
- **非同步**：Kotlin Coroutines 與 Flow。

---

## 模組結構
- `app/` — 主 Android 應用程式
- `wear/` — Wear OS 伴侶應用
- `docs/` — 文件、開發腳本、貢獻指南
- `.github/` — CI/CD 工作流程、議題模板

---

## 安全性
- 所有敏感資料皆以 Android Keystore 與 EncryptedSharedPreferences 加密。
- 原始碼中無硬編碼金鑰。
- API 金鑰不會進入版本控制。
- 僅使用 HTTPS 網路通訊。

---

## 在地化
- 支援繁體中文與英文
- 貨幣格式、數字系統
- 無障礙設計與內容描述

---

## 貢獻指南
歡迎任何貢獻！請參閱 [CONTRIBUTING.md](CONTRIBUTING.md) 取得詳細規範。

---

## 授權
MIT License，詳見 [LICENSE](LICENSE)。

---


## 支援
- 文件：[docs/README.md](docs/README.md)
- API 設定：[docs/API_SETUP.md](docs/API_SETUP.md)
- 安全性：[docs/SECURITY.md](docs/SECURITY.md)
- 隱私權政策：[docs/privacy_policy.md](docs/privacy_policy.md)
- 更新日誌：[docs/CHANGELOG.md](docs/CHANGELOG.md)
- 發佈說明：[docs/RELEASE_NOTES.md](docs/RELEASE_NOTES.md)
- 問題回報：[GitHub Issues](https://github.com/kuoyaoming/Wealth-Manager/issues)

---

**Wealth Manager 財富管家** — 安全、隱私、現代化的 Android 與 Wear OS 個人理財追蹤工具。

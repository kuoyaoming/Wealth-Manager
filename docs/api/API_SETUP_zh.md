# API 設定指南

語言： [English](API_SETUP.md) | [繁體中文](API_SETUP_zh.md)

使用 App 內建的設定頁面配置 API 金鑰；金鑰將以本機加密方式儲存。

## 需要的 API 金鑰

### Finnhub
- 用途：股票行情與報價
- 註冊： https://finnhub.io/register
- 方案：提供免費層級

### ExchangeRate‑API
- 用途：匯率（USD/TWD）
- 註冊： https://www.exchangerate-api.com/
- 方案：提供免費層級

## 設定步驟（在 App 內）

1) 取得金鑰
- 前往上方連結完成註冊並取得 API 金鑰。

2) 在 App 內加入金鑰
- 開啟 App → 設定 → 管理 API 金鑰 → 貼上金鑰 → 按「Validate & Save」。
- App 會與供應商驗證金鑰並透過 `KeyRepository` 與 EncryptedSharedPreferences 加密儲存。

3) 驗證資料
- 返回儀表板或資產頁檢查行情與匯率是否成功載入。

注意
- 不使用 BuildConfig 金鑰；不要把金鑰放在 `local.properties`。
- 金鑰僅保存在裝置上並以加密儲存，不會上傳雲端備份。
- 日誌與 UI 會遮罩敏感內容。

## 安全最佳實務

建議
- 僅在 App 內儲存金鑰（設定 → 管理 API 金鑰）。
- 區分開發／正式環境金鑰。
- 定期輪替金鑰並監控使用量。
- 將原始金鑰保存在密碼管理工具中。

避免
- 不要將金鑰提交到版本控制。
- 不要把金鑰寫在原始碼內。
- 不要把金鑰放在 `local.properties` 或其他設定檔。
- 不要在截圖或日誌中公開金鑰。

## 測試設定

在 App 內驗證
- 於設定 → 管理 API 金鑰使用「Validate & Save」。
- 成功後應可看到最新行情與匯率。

選擇性診斷
- 可查看 App 內的偵錯記錄（敏感內容已遮罩）。

## 疑難排解

金鑰未被接受
- 重新開啟管理頁，清空並重新貼上。
- 與供應商確認金鑰格式與方案限額。
- 確認裝置時間與網路狀態。

請求失敗
- 檢查是否觸發 Rate Limit 或金鑰失效。
- 檢查網路連線與服務狀態頁。
- 於設定再次執行 Validate。

建置問題
- 清理並重建（`./gradlew clean assembleDebug`）。
- 確認 AGP 與相依套件版本與專案相容。

## 支援

若遇到問題：
1) 參考 [Security Policy](../security/SECURITY.md)
2) 參考 [Development Setup](../setup/README.md)
3) 於 GitHub 建立 Issue



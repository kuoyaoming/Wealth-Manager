# APK 安裝和日誌記錄腳本

這組腳本可以自動化 APK 安裝、應用程式啟動和完整日誌記錄的流程。

## 檔案說明

### 1. `apk_installer_logger.py` - 通用版本
- 支援任何 APK 檔案
- 自動檢測套件名稱
- 可指定主活動名稱

### 2. `wealthmanager_logger.py` - WealthManager 專用版本
- 專門針對 WealthManager 應用程式
- 簡化使用流程
- 自動處理套件名稱和主活動

### 3. `run_wealthmanager_logger.bat` - Windows 批次檔案
- 一鍵執行 WealthManager 日誌記錄
- 自動檢查前置條件

## 使用方法

### 方法一：使用批次檔案（推薦）
```bash
# 雙擊執行
run_wealthmanager_logger.bat
```

### 方法二：直接使用 Python 腳本
```bash
# WealthManager 專用版本
python wealthmanager_logger.py WealthManager-v0.1.5-complete.apk

# 通用版本
python apk_installer_logger.py WealthManager-v0.1.5-complete.apk
```

## 前置條件

1. **Python 3.6+** 已安裝
2. **Android SDK** 已安裝並設定環境變數
3. **ADB 工具** 可用
4. **Android 設備** 已連接並啟用 USB 調試
5. **APK 檔案** 在專案目錄中

## 功能特點

### ✅ 自動化流程
- 檢查 ADB 工具可用性
- 檢查設備連接狀態
- 自動卸載已存在的同名應用程式
- 安裝指定的 APK 檔案
- 啟動應用程式
- 開始記錄完整日誌

### 📊 日誌記錄
- 記錄所有層級的日誌（VERBOSE, DEBUG, INFO, WARN, ERROR）
- 包含時間戳記
- 自動保存到專案根目錄
- 檔案命名格式：`WealthManager_logs_YYYYMMDD_HHMMSS.txt`

### 🔍 監控功能
- 實時監控應用程式狀態
- 當應用程式關閉時自動停止記錄
- 支援手動中斷（Ctrl+C）

## 輸出檔案

### 日誌檔案
- **檔案名**：`WealthManager_logs_20240928_223000.txt`
- **位置**：專案根目錄
- **內容**：完整的應用程式運行日誌

## 故障排除

### 常見問題

1. **ADB 工具不可用**
   ```
   ❌ ADB 工具不可用
   ```
   **解決方案**：安裝 Android SDK 並設定環境變數

2. **設備未連接**
   ```
   ❌ 沒有檢測到連接的 Android 設備
   ```
   **解決方案**：
   - 確保設備已連接
   - 啟用 USB 調試
   - 授權電腦進行 USB 調試

3. **APK 安裝失敗**
   ```
   ❌ APK 安裝失敗
   ```
   **解決方案**：
   - 檢查 APK 檔案是否損壞
   - 確保設備有足夠空間
   - 檢查設備是否允許安裝未知來源應用程式

4. **應用程式啟動失敗**
   ```
   ⚠️ 應用程式啟動可能失敗
   ```
   **解決方案**：
   - 檢查應用程式是否與設備相容
   - 查看設備日誌獲取詳細錯誤資訊

## 進階使用

### 自定義主活動
```bash
python apk_installer_logger.py app.apk -a com.example.MainActivity
```

### 查看即時日誌
```bash
# 查看所有日誌
adb logcat -v time *:V

# 只查看特定應用程式
adb logcat -v time com.wealthmanager:* *:S
```

## 注意事項

1. **日誌檔案大小**：長時間運行會產生大量日誌，注意磁碟空間
2. **性能影響**：記錄所有層級日誌可能影響設備性能
3. **隱私保護**：日誌可能包含敏感資訊，請妥善保管
4. **中斷處理**：使用 Ctrl+C 可以安全中斷記錄過程

## 技術細節

### 日誌層級
- **V (VERBOSE)**：最詳細的日誌
- **D (DEBUG)**：調試資訊
- **I (INFO)**：一般資訊
- **W (WARN)**：警告
- **E (ERROR)**：錯誤

### 監控機制
- 每 3 秒檢查應用程式狀態
- 當應用程式進程消失時自動停止記錄
- 支援手動中斷和自動停止

## 支援

如有問題，請檢查：
1. Python 版本是否符合要求
2. ADB 工具是否正常工作
3. 設備連接是否穩定
4. APK 檔案是否完整

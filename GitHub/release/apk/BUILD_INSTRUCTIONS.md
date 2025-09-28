# Wealth Manager APK Build Instructions

## 建置環境需求

### 方法一：使用Android Studio（推薦）

1. **下載並安裝Android Studio**
   - 從 https://developer.android.com/studio 下載
   - 安裝時選擇包含Android SDK

2. **開啟專案**
   ```bash
   # 在Android Studio中開啟專案
   File -> Open -> 選擇 Wealth-Manager 資料夾
   ```

3. **建置APK**
   - 在Android Studio中選擇 `Build -> Build Bundle(s) / APK(s) -> Build APK(s)`
   - 或使用終端機：`./gradlew assembleDebug`

### 方法二：使用Docker（需要Docker環境）

1. **安裝Docker**
   ```bash
   # Ubuntu/Debian
   sudo apt-get install docker.io
   sudo systemctl start docker
   sudo systemctl enable docker
   ```

2. **建置APK**
   ```bash
   ./build-apk.sh
   ```

### 方法三：手動安裝Android SDK

1. **下載Android SDK**
   ```bash
   wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
   unzip commandlinetools-linux-9477386_latest.zip
   ```

2. **設定環境變數**
   ```bash
   export ANDROID_HOME=/path/to/android-sdk
   export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
   export PATH=$PATH:$ANDROID_HOME/platform-tools
   ```

3. **安裝必要套件**
   ```bash
   yes | sdkmanager --licenses
   sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
   ```

4. **建置APK**
   ```bash
   ./gradlew assembleDebug
   ```

## APK檔案位置

建置完成後，APK檔案將位於：
```
app/build/outputs/apk/debug/app-debug.apk
```

## 測試安裝

1. **啟用開發者選項**
   - 設定 -> 關於手機 -> 點擊版本號碼7次

2. **啟用USB偵錯**
   - 設定 -> 開發人員選項 -> USB偵錯

3. **安裝APK**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

## 功能測試清單

### ✅ 基本功能
- [ ] 應用程式啟動
- [ ] 生物識別驗證（需要實體裝置）
- [ ] 語言切換（中文/英文）
- [ ] 主題切換（明暗模式）

### ✅ 資產管理
- [ ] 新增現金資產
- [ ] 新增股票資產
- [ ] 查看資產列表
- [ ] 編輯/刪除資產

### ✅ 儀表板
- [ ] 總資產顯示
- [ ] 現金資產統計
- [ ] 股票資產統計
- [ ] 資產分配圖表

## 已知限制

1. **生物識別功能**：需要實體裝置，模擬器可能不支援
2. **市場數據**：需要網路連線獲取即時行情
3. **圖表功能**：目前為佔位符，需要後續實現

## 故障排除

### 建置失敗
- 檢查Android SDK版本（需要API 34+）
- 確認Java版本（需要Java 8+）
- 檢查網路連線（下載依賴套件）

### 安裝失敗
- 確認裝置已啟用USB偵錯
- 檢查APK簽名
- 確認裝置相容性（Android 6.0+）

### 運行錯誤
- 檢查權限設定
- 確認生物識別硬體支援
- 檢查資料庫初始化

## 聯絡支援

如有問題，請聯繫開發團隊或查看專案README檔案。
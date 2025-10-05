#!/bin/bash

echo "=== 檢查小工具相關文件的語法 ==="

# 檢查 Kotlin 文件是否存在
echo "1. 檢查文件存在性..."
files=(
    "app/src/main/java/com/wealthmanager/widget/TotalAssetWidgetProvider.kt"
    "app/src/main/java/com/wealthmanager/widget/WidgetManager.kt"
    "app/src/main/java/com/wealthmanager/widget/WidgetPrivacyManager.kt"
    "app/src/main/java/com/wealthmanager/widget/WidgetErrorHandler.kt"
    "app/src/main/java/com/wealthmanager/widget/WidgetStatusMonitor.kt"
    "app/src/main/java/com/wealthmanager/ui/widget/WidgetSettingsScreen.kt"
)

for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file 存在"
    else
        echo "❌ $file 不存在"
    fi
done

echo ""
echo "2. 檢查導入語句..."

# 檢查 TotalAssetWidgetProvider.kt 的導入
echo "檢查 TotalAssetWidgetProvider.kt 的導入..."
if grep -q "import androidx.work.WorkManager" app/src/main/java/com/wealthmanager/widget/TotalAssetWidgetProvider.kt; then
    echo "✅ WorkManager 導入正確"
else
    echo "❌ WorkManager 導入缺失"
fi

if grep -q "import androidx.work.ExistingWorkPolicy" app/src/main/java/com/wealthmanager/widget/TotalAssetWidgetProvider.kt; then
    echo "✅ ExistingWorkPolicy 導入正確"
else
    echo "❌ ExistingWorkPolicy 導入缺失"
fi

if grep -q "import androidx.work.OneTimeWorkRequestBuilder" app/src/main/java/com/wealthmanager/widget/TotalAssetWidgetProvider.kt; then
    echo "✅ OneTimeWorkRequestBuilder 導入正確"
else
    echo "❌ OneTimeWorkRequestBuilder 導入缺失"
fi

if grep -q "import androidx.work.PeriodicWorkRequestBuilder" app/src/main/java/com/wealthmanager/widget/TotalAssetWidgetProvider.kt; then
    echo "✅ PeriodicWorkRequestBuilder 導入正確"
else
    echo "❌ PeriodicWorkRequestBuilder 導入缺失"
fi

echo ""
echo "3. 檢查 build.gradle 依賴..."

if grep -q "androidx.work:work-runtime-ktx" app/build.gradle; then
    echo "✅ WorkManager 依賴已添加"
else
    echo "❌ WorkManager 依賴缺失"
fi

echo ""
echo "4. 檢查 AndroidManifest.xml 註冊..."

if grep -q "TotalAssetWidgetProvider" app/src/main/AndroidManifest.xml; then
    echo "✅ 小工具已在 AndroidManifest.xml 中註冊"
else
    echo "❌ 小工具未在 AndroidManifest.xml 中註冊"
fi

echo ""
echo "5. 檢查資源文件..."

if [ -f "app/src/main/res/layout/widget_total_asset_layout.xml" ]; then
    echo "✅ 小工具佈局文件存在"
else
    echo "❌ 小工具佈局文件缺失"
fi

if [ -f "app/src/main/res/xml/total_asset_widget_info.xml" ]; then
    echo "✅ 小工具配置文件存在"
else
    echo "❌ 小工具配置文件缺失"
fi

echo ""
echo "=== 語法檢查完成 ==="
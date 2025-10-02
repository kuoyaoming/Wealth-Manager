# 添加 Compose 相關的 ProGuard 規則
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.runtime.snapshots.** { *; }
-keep class androidx.compose.runtime.snapshots.SnapshotStateList { *; }

# 保持 Compose 狀態管理相關的方法
-keepclassmembers class androidx.compose.runtime.snapshots.SnapshotStateList {
    public boolean conditionalUpdate(boolean, kotlin.jvm.functions.Function1);
    public java.lang.Object mutate(kotlin.jvm.functions.Function1);
    public void update(boolean, kotlin.jvm.functions.Function1);
}

# 防止 Compose 相關類被混淆
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# -------- Gson/Retrofit/Model 保護規則（避免 release 解析失敗） --------
# 保留泛型與註解資訊，供 Gson/Retrofit 於執行時反射使用
-keepattributes Signature, RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations, RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations, *Annotation*

# 保留 Gson 本身
-keep class com.google.gson.** { *; }
-dontwarn sun.misc.**

# 保留 Retrofit 介面（方法簽名與註解）
-keep interface retrofit2.** { *; }
-keep class retrofit2.** { *; }

# 關鍵：保留 API 資料類與其欄位名稱，避免欄位被混淆
-keep class com.wealthmanager.data.api.** { *; }

# 可選：若有使用反射的 Debug/Diagnostic 類別
-keep class com.wealthmanager.debug.** { *; }
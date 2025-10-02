# ============================================================
# 重要：保留所有反射和泛型資訊（避免 Retrofit/Gson 失敗）
# ============================================================
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes Exceptions

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

# 保留所有 @Composable 函數（防止 Compose UI 組件被混淆）
-keep @androidx.compose.runtime.Composable class * { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# 保留 Compose UI 相關的 Kotlin metadata（重要！）
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# 保留 Compose 中使用的 lambda 和函數引用
-keepclassmembers class * {
    kotlin.jvm.functions.Function* *;
}

# -------- Gson/Retrofit/Model 保護規則（避免 release 解析失敗） --------
# 保留泛型與註解資訊，供 Gson/Retrofit 於執行時反射使用（關鍵！）
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations
-keepattributes AnnotationDefault

# 保留 Gson 本身
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-dontwarn sun.misc.**

# 保留 Gson 使用的 JsonAdapter
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# 保留 Retrofit 介面（方法簽名與註解）
-keep interface retrofit2.** { *; }
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# 關鍵：保留 API 資料類與其欄位名稱，避免欄位被混淆
-keep class com.wealthmanager.data.api.** { *; }

# 保留所有 data class (Kotlin)
-keep class com.wealthmanager.data.api.FinnhubQuoteResponse { *; }
-keep class com.wealthmanager.data.api.FinnhubSearchResponse { *; }
-keep class com.wealthmanager.data.api.FinnhubSearchResult { *; }
-keep class com.wealthmanager.data.api.FinnhubExchangeResponse { *; }
-keep class com.wealthmanager.data.api.FinnhubExchangeRate { *; }
-keep class com.wealthmanager.data.api.ExchangeRateResponse { *; }

# 保留所有使用 @SerializedName 的欄位
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# 保留泛型簽名（修復 ParameterizedType 錯誤）
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**

# -------- OkHttp 官方規則 --------
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# -------- Retrofit 官方規則（關鍵！）--------
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# 可選：若有使用反射的 Debug/Diagnostic 類別
-keep class com.wealthmanager.debug.** { *; }

# -------- UI 組件保護規則（防止 Compose UI 被混淆） --------
# 保留所有 UI 相關的類別和方法
-keep class com.wealthmanager.ui.** { *; }
-keep class com.wealthmanager.haptic.** { *; }

# 保留 BuildConfig（用於版本資訊顯示）
-keep class com.wealthmanager.BuildConfig { *; }

# 保留所有帶有 @Inject 註解的建構函數（Hilt DI）
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
}

# 保留 FirstLaunchManager（AboutDialog 依賴）
-keep class com.wealthmanager.data.FirstLaunchManager { *; }

# -------- 安全性相關保護規則 --------
# 保留所有安全性相關的類別（關鍵！）
-keep class com.wealthmanager.security.** { *; }
-keep class com.wealthmanager.auth.** { *; }

# 保留 KeyRepository 和相關類別的所有方法
-keep class com.wealthmanager.security.KeyRepository {
    public <methods>;
    private <fields>;
}

# 保留 EncryptedSharedPreferences 相關
-keep class androidx.security.crypto.** { *; }

# 保留 Android Keystore 相關
-keep class android.security.keystore.** { *; }

# -------- 資料層保護規則 --------
# 保留所有 data 包下的類別（避免資料存取問題）
-keep class com.wealthmanager.data.** { *; }

# 保留 Room Database 相關
-keep class com.wealthmanager.data.database.** { *; }
-keep class com.wealthmanager.data.entity.** { *; }
-keep class com.wealthmanager.data.dao.** { *; }

# 保留所有 Service 類別
-keep class com.wealthmanager.data.service.** { *; }

# -------- Hilt/Dagger 保護規則 --------
# 保留 Hilt 生成的類別
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keep class **_HiltModules** { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }

# 保留所有 @Module 和 @Provides 註解
-keep @dagger.Module class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keepclassmembers class * {
    @dagger.Provides <methods>;
    @javax.inject.Inject <methods>;
}
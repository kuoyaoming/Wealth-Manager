# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep Room entities and DAOs
-keep class com.wealthmanager.data.entity.** { *; }
-keep class com.wealthmanager.data.dao.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class com.wealthmanager.di.** { *; }

# Keep Retrofit and OkHttp classes
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.wealthmanager.data.service.** { *; }
# Keep Security Crypto (EncryptedSharedPreferences, MasterKey)
-keep class androidx.security.** { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }

# Keep ViewModels
-keep class com.wealthmanager.ui.**.ViewModel { *; }
-keep class * extends androidx.lifecycle.ViewModel

# Keep Parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keep class * implements java.io.Serializable

# Keep Biometric classes
-keep class androidx.biometric.** { *; }

# Keep Navigation classes
-keep class androidx.navigation.** { *; }

# Keep Coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }

# Keep data classes
-keep class com.wealthmanager.data.model.** { *; }

# Keep UI classes
-keep class com.wealthmanager.ui.** { *; }

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
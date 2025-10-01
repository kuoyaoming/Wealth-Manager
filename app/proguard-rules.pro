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
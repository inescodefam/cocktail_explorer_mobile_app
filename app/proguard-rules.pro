# =============================================================================
# PROGUARD/R8 CONFIGURATION
# =============================================================================
#
# This file configures R8 (the modern replacement for ProGuard) which performs:
# 1. CODE SHRINKING - Removes unused classes, methods, and fields
# 2. OBFUSCATION - Renames identifiers to short, meaningless names (a, b, c...)
# 3. OPTIMIZATION - Inlines methods, removes dead code, optimizes bytecode
#
# WHY OBFUSCATION MATTERS FOR SECURITY:
# - Decompiled code becomes much harder to read and understand
# - Protects business logic and algorithms from competitors
# - Makes it harder for attackers to find vulnerabilities
# - Hides API keys and sensitive string patterns (though they should still be
#   stored securely, not hardcoded)
#
# WHAT HAPPENS WITHOUT OBFUSCATION:
# - Anyone can decompile APK with tools like jadx, apktool
# - Class names, method names, variable names are fully readable
# - Application logic is easily understood
# - Attackers can find security vulnerabilities more easily
#
# WHAT HAPPENS WITH OBFUSCATION:
# - Class: "NotificationHelper" becomes "a"
# - Method: "sendFavoriteAddedNotification" becomes "b"
# - Understanding code flow becomes very difficult
# - Reverse engineering takes much more time and effort
# =============================================================================

# Keep line numbers for crash reports (recommended for debugging)
-keepattributes SourceFile,LineNumberTable

# Hide original source file name in stack traces
-renamesourcefileattribute SourceFile

# =============================================================================
# DATA MODELS - Must be kept for Gson/Retrofit serialization
# =============================================================================
# These classes use reflection for JSON parsing, so field names must be preserved
# Otherwise, Gson cannot map JSON keys to object fields

-keep class hr.algebra.cocktailexplorer.models.** { *; }
-keep class hr.algebra.cocktailexplorer.data.** { *; }

# Keep Gson serialization/deserialization
-keepattributes Signature
-keepattributes *Annotation*

# =============================================================================
# RETROFIT - Keep API interface methods
# =============================================================================
-keep,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# =============================================================================
# OKHTTP - Keep for network operations
# =============================================================================
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# =============================================================================
# GLIDE - Image loading library
# =============================================================================
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# =============================================================================
# COROUTINES - Keep coroutine internals
# =============================================================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# =============================================================================
# ANDROID COMPONENTS - Keep for system interaction
# =============================================================================
# Keep BroadcastReceivers (they are instantiated by the system)
-keep class hr.algebra.cocktailexplorer.Receiver { *; }
-keep class hr.algebra.cocktailexplorer.notification.AlarmReceiver { *; }

# Keep ContentProvider
-keep class hr.algebra.cocktailexplorer.CocktailContentProvider { *; }

# =============================================================================
# PARCELABLE - Keep for Intent extras
# =============================================================================
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# =============================================================================
# ENUM - Keep enum values
# =============================================================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

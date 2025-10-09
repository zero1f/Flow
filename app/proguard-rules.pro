# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# ========== Kotlin ==========
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

# ========== Coroutines ==========
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ========== Jetpack Compose ==========
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keepclassmembers class androidx.compose.** { *; }

# ========== Hilt ==========
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keepclasseswithmembers class * {
    @dagger.* <methods>;
}
-keepclasseswithmembers class * {
    @javax.inject.* <methods>;
}

# ========== Room ==========
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ========== Data Classes ==========
-keep class com.zero.flow.domain.model.** { *; }
-keep class com.zero.flow.data.local.** { *; }

# ========== Serialization ==========
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.zero.flow.**$$serializer { *; }
-keepclassmembers class com.zero.flow.** {
    *** Companion;
}
-keepclasseswithmembers class com.zero.flow.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ========== DataStore ==========
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}

# ========== Retrofit / OkHttp (if added later) ==========
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.*

# ========== Media3 / ExoPlayer ==========
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ========== Vico Charts ==========
-keep class com.patrykandpatrick.vico.** { *; }
-dontwarn com.patrykandpatrick.vico.**

# ========== Generic ==========
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends java.lang.Exception
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# ========== Debugging ==========
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes Signature
-keepattributes Deprecated
-keepattributes SourceFile,LineNumberTable
-keepattributes EnclosingMethod
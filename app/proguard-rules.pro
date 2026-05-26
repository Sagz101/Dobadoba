# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable

# Moshi ProGuard Rules
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}
# Keep Moshi / JSON models intact from minification obfuscation
-keep class com.example.data.** { *; }
-keep class *JsonAdapter { *; }
-keep class *Serializer { *; }

# Retrofit Rules
-keepattributes Signature, InnerClasses, EnclosingMethod
-keep class retrofit2.** { *; }
-keepclassmembers class * {
    @retrofit2.http.** <methods>;
}

# OkHttp Rules
-keepattributes Signature, *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Room Database Rules
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Firebase AI and Messaging Rules
-keep class com.google.firebase.** { *; }
-keep interface com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# MapLibre Rules (prevent shrinking of native JNI bindings/C++ callbacks)
-keep class org.maplibre.gl.** { *; }
-dontwarn org.maplibre.gl.**
-keepclassmembers class org.maplibre.gl.** {
    native <methods>;
}

# SQLCipher rules
-keep class net.zetetic.database.** { *; }
-keep class net.zetetic.database.sqlcipher.** { *; }
-dontwarn net.zetetic.database.**

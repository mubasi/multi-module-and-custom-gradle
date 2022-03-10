# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable
#-keepnames class androidx.navigation.fragment.NavHostFragment
-keepattributes Signature

-dontwarn module-info
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# databinding
-dontwarn androidx.databinding.**
-keep class androidx.lifecycle.** { *; }
-keep class androidx.databinding.** { *; }

-dontwarn javax.naming.**
-dontwarn sun.misc.Unsafe

# google
-dontwarn com.google.common.**
-dontwarn afu.org.checkerframework.**
-dontwarn org.checkerframework.**
-dontwarn com.google.errorprone.**
-dontwarn sun.misc.Unsafe
-dontwarn java.lang.ClassValue

# material design
-dontwarn com.google.android.material.**
-keep class com.google.android.material.** { *; }
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
# Uncomment this to preserve the line number information for
# debugging stack traces.

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn io.mockk.**
-keep class io.mockk.** { *; }

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\adt-bundle-windows-x86_64-20140702\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#-dontwarn com.jakewharton.**
-dontwarn com.squareup.**
-dontwarn io.reactivex.**

#如果使用是okhttp和rxAndroid，retrofit2
#在打包release時會報錯，找不到okio，rx，retrofit
#只需要在app下的proguard-rules.pro混淆文件中加入下列四項即可
-dontwarn org.codehaus.**
-dontwarn java.nio.**
-dontwarn java.lang.invoke.**
-dontwarn rx.**

-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

-keep class android.support.v4.** { *; }
-keep class android.support.v7.** { *; }
#-keep interface android.support.v4.app.** { *; }
#-keep interface android.support.v7.app.** { *; }


#混淆時不會產生形形色色的類名
#-dontusemixedcaseclassnames

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
-keepattributes Exceptions

# For using GSON @Expose annotation
-keepattributes *Annotation*
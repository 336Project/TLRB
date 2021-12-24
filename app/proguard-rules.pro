# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\Gray\android\sdk/tools/proguard/proguard-android.txt
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

#-dontwarn net.youmi.android.**
#-keep class net.youmi.android.** {
#    *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
#-verbose
#-ignorewarnings
-dontpreverify
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * implements android.os.IInterface {*;}
-keep public class * extends android.os.IInterface {*;}

-dontwarn android.support.v4.**
-dontwarn android.support.v7.**
-keep class android.support.v4.** { *; }
-keep class android.support.v7.** { *; }
-keep class android.support.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-keep public class android.webkit.**{*;}


-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}


#不混淆 Entity
-keepclassmembers class * extends com.ttm.tlrb.ui.entity.BaseEn{
	*;
}

-keep class com.ttm.tlrb.ui.entity.**{
*;
}

-keep class com.ttm.tlrb.api.e.**{
*;
}

-keep class com.andexert.library.**{*;}
# fresco
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}
-keepclasseswithmembernames class * {
native <methods>;
}

-dontwarn okio.**
-dontwarn javax.annotation.**

# retrofit
-keepclassmembers class okhttp3.**{
 *;
}
-keep class okhttp3.** {
 *;
}
-keepclassmembers class okio.**{
 *;
}
-keep class okio.** {
 *;
}

-keep class retrofit2.converter.gson.** {
 *;
}

-dontwarn com.google.gson.**
-keep class com.google.gson.** { *; }
-keep public class * extends com.google.gson.**

-dontwarn retrofit2.**
-keepclassmembers class retrofit2.**{
 *;
}
-keep class retrofit2.** { *; }

-dontwarn rx.**
-keepclassmembers class rx.**{
 *;
}
-keep class rx.** {
 *;
}

#友盟统计
-keep class com.umeng.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class com.ttm.tlrb.R$*{
public static final int *;
}
-dontwarn com.umeng.**
-keep class com.tencent.stat.**{
*;
}
-keep class com.tencent.**{
*;
}
-dontwarn com.tencent.**
-keep class com.sina.**{
*;
}
-dontwarn com.sina.**
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

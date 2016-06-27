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
-keepattributes Signature
-dontpreverify
-keepattributes Exceptions

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}

-keep class com.baidu.** {
 public protected *;
}

#不混淆 Entity
-keepclassmembers class * extends com.ttm.tlrb.ui.entity.BaseEn{
	*;
}

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

-dontwarn okio.**
-dontwarn javax.annotation.**

# retrofit
-keep class okhttp3.** {
 *;
}
-keep class okio.** {
 *;
}

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

-dontwarn rx.**
-keep class rx.** {
 *;
}

#友盟统计
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keep public class com.ttm.tlrb.R$*{
public static final int *;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#高德地图
-keep class com.amap.api.location.**{*;}

-keep class com.amap.api.fence.**{*;}

-keep class com.autonavi.aps.amapapi.model.**{*;}
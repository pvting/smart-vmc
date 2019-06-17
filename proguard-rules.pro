# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/gordon/Library/Android/sdk/tools/proguard/proguard-android.txt
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

### WANT混淆模板 #### 版本为V1.0
-optimizationpasses 5
#When not preverifing in a case-insensitive filing system, such as Windows. Because this tool unpacks your processed jars, you should then use:
-dontusemixedcaseclassnames
#Specifies not to ignore non-public library classes. As of version 4.5, this is the default setting
-dontskipnonpubliclibraryclasses
#Preverification is irrelevant for the dex compiler and the Dalvik VM, so we can switch it off with the -dontpreverify option.
-dontpreverify
#Specifies to write out some more information during processing. If the program terminates with an exception, this option will print out the entire stack trace, instead of just the exception message.
-verbose
#The -optimizations option disables some arithmetic simplifications that Dalvik 1.0 and 1.5 can't handle. Note that the Dalvik VM also can't handle aggressive overloading (of static fields).
#To understand or change this check http://proguard.sourceforge.net/index.html#/manual/optimizations.html
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-dontwarn java.awt.**
-dontwarn javax.security.**
-dontwarn java.beans.Beans
-dontwarn android.support.**

#To repackage classes on a single package
#-repackageclasses ''

#Uncomment if using annotations to keep them.
#-keepattributes *Annotation*

#Keep classes that are referenced on the AndroidManifest
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep class com.android.vending.billing.** { *;}

#To remove debug logs:
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
}

#To avoid changing names of methods invoked on layout's onClick.
# Uncomment and add specific method names if using onClick on layouts
#-keepclassmembers class * {
# public void onClickButton(android.view.View);
#}

-keepattributes Signature
-keepattributes *Annotation*

#Maintain java native methods
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#Maintain enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#Keep the R
-keepclassmembers class **.R$* {
    public static <fields>;
}

#To maintain custom components names that are used on layouts XML.
#Uncomment if having any problem with the approach below
#-keep public class custom.components.package.and.name.**

#To maintain custom components names that are used on layouts XML:
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#To keep parcelable classes (to serialize - deserialize objects to sent through Intents)
-keep class * implements android.os.Parcelable {
  *;
}


#Uncomment if using Serializable
#-keepclassmembers class * implements java.io.Serializable {
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}
-keep class * implements java.io.Serializable {
    *;
}

###### BASE SDK OPTIONS
-dontwarn com.google.gson.**
-dontwarn com.want.base.http.**
-dontwarn de.greenrobot.event.**

###To remove debug logs:
-assumenosideeffects class com.want.core.log.lg{
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
}

### Umeng analytic
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

###### ADDITIONAL OPTIONS NOT USED NORMALLY ######
# Databinding
-dontwarn android.databinding.**
-keep class android.databinding.** { *; }


###### JPUSH ######
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

# protobuf
-dontwarn com.google.**
-keep class com.google.protobuf.** { *; }

###### Umeng ######
-keep class com.umeng.* {
    public <fields>;
    public <methods>;
}
-keep class com.umeng.message.protobuffer.* {
	public <fields>;
    public <methods>;
}
-keep class com.squareup.wire.* {
	public <fields>;
    public <methods>;
}
-keep class org.android.agoo.impl.*{
	public <fields>;
    public <methods>;
}
-keep class org.android.agoo.service.* {*;}
-keep class org.android.spdy.**{*;}
-keep public class **.R$*{
   public static final int *;
}
-dontwarn com.umeng.**

###### okhttp ######
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}
-dontwarn okio.**

###### EventBus #####
-keepclassmembers class ** {
   public void onEvent*(**);
}

###### ActionMenu ######
-keep class * extends android.support.v4.view.ActionProvider { *; }

###### Location ######
## 高德
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}


### Keep BuildConfig
-keep class **.BuildConfig {
    *;
}

##### pldroid #####
-keep class com.pili.pldroid.player.** { *; }
-keep class tv.danmaku.ijk.media.player.** {*;}
-dontwarn com.pili.pldroid.**

##### log4j #####
-dontwarn org.apache.**
-dontwarn org.apache.log4j.**

##### facebook fresco ######
# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
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
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**
# animated-gif
# can not display gif image.
-keep class com.facebook.imagepipeline.animated.factory.AnimatedFactoryImpl {
    public AnimatedFactoryImpl(com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory, com.facebook.imagepipeline.core.ExecutorSupplier);
}
-keep class com.facebook.animated.gif.** { *; }

##### Baidu BOS ######
-dontwarn com.baidubce.**
-keep class com.baidubce.**{ *; }

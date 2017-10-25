# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\adnroidID\android_studio_sdk/tools/proguard/proguard-android.txt
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
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5                                                           # 指定代码的压缩级别
-dontusemixedcaseclassnames                                                     # 是否使用大小写混合
-dontskipnonpubliclibraryclasses                                                # 指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclassmembers                                           #指定不去忽略非公共的库的类的成员
-dontpreverify                                                                  # 混淆时是否做预校验
-verbose                                                                        # 混淆时是否记录日志
-printmapping proguardMapping.txt                                               #生成原类名和混淆后的类名的映射文件
-optimizations !code/simplification/cast,!field/*,!class/merging/*              # 混淆时所采用的算法
-keepattributes *Annotation*,InnerClasses                                       #不混淆Annotation
-keepattributes Signature                                                       #不混淆泛型
-keepattributes SourceFile,LineNumberTable                                      # 抛出异常时保留代码行号
#-------------------------------------基本指令区结束---------------------------------------
#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keepattributes EnclosingMethod

-keepclasseswithmembernames class * {                                         # 保持 native 方法不被混淆
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{                       # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {                                                    # 保持枚举enum类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{                               # 保持自定义组件不被混淆
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {                                            # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {                             # 保持Parcelable不被混淆
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {                  # 保持Serializable不被混淆
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class ** {                                            # 保持Subscribe不被混淆
    @org.greenrobot.eventbus.Subscribe <methods>;
}

-keep class **.R$* {                                                          #不混淆资源类
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}
#-------------------------------------默认保留区结束---------------------------------------

#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}
#-----------------------------------webview区结束-----------------------------------------
#---------------------------------1.实体类---------------------------------

#---------------------------------实体类区结束----------------------------------------

#---------------------------------2.反射相关的类和方法-----------------------

#------------------------------------2.反射相关的类和方法结束----------------------------------------
#---------------------------------自定义的类----------------------------------------

-keep class com.ubt.alpha1e.business.IHtsHelperListener
-keep class com.ubt.alpha1e.business.HtsHelper
-keep class com.ubt.alpha1e.business.NewActionsManager
-keep class com.ubt.alpha1e.blockly.BlocklyJsInterface

-keepclassmembers class com.ubt.alpha1e.business.IHtsHelperListener {
   public *;
}

-keepclassmembers class com.ubt.alpha1e.business.HtsHelper {
   public *;
}

-keepclassmembers class com.ubt.alpha1e.blockly.BlocklyJsInterface {
   public *;
}

#---------------------------------自定义的类结束----------------------------------------
#---------------------------------3.第三方jar包-------------------------------

-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-keep class android.support.v7.** { *; }

-dontwarn android.support.**
-keep class android.support.** {*;}

-dontwarn org.codehaus.jackson.**
-keep class org.codehaus.jackson.** { *; }

-dontwarn com.loopj.android.http.**
-keep class com.loopj.android.http.** { *; }

-dontwarn com.fasterxml.**
-keep class com.fasterxml.** { *; }

-dontwarn com.baoyz.**
-keep class com.baoyz.** { *; }

-dontwarn com.ubt.alpha1e.business.thrid_party.**
-keep class com.ubt.alpha1e.business.thrid_party.** { *; }

-dontwarn com.ubt.alpha1e.ui.custom.**
-keep class com.ubt.alpha1e.ui.custom.** { *; }

-dontwarn com.ubt.alpha1e.data.model.**
-keep class com.ubt.alpha1e.data.model.** { *; }

-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.** { *; }

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-dontwarn com.qiniu.android.**
-keep class com.qiniu.android.** { *; }

-dontwarn com.sina.**
-keep class com.sina.** { *; }

-dontwarn com.universalvideoview.**
-keep class com.universalvideoview.** { *; }

-dontwarn com.yixia.**
-keep class com.yixia.** { *; }

-dontwarn org.json.**
-keep class org.json.** { *; }

-dontwarn  com.google.**
-keep class com.google.**{ *; }

-dontwarn  org.kobjects.**
-keep class org.kobjects.**{ *; }

-dontwarn  org.ksoap2.**
-keep class org.ksoap2.**{ *; }

-dontwarn  com.tencent.**
-keep class com.tencent.**{ *; }

-dontwarn  org.litepal.**
-keep class org.litepal.**{ *; }

-dontwarn  Decoder.**
-keep class Decoder.**{ *; }

-dontwarn  twitter4j.**
-keep class twitter4j.**{ *; }

-dontwarn  com.google.zxing.**
-keep class com.google.zxing.**{ *; }

-dontwarn  org.apache.**
-keep class org.apache.**{ *; }

-dontwarn  com.zhy.changeskin.**
-keep class com.zhy.changeskin.**{ *; }

-dontwarn  com.ant.country.**
-keep class com.ant.country.**{ *; }

-dontwarn  com.facebook.**
-keep class com.facebook.**{ *; }

-dontwarn  org.greenrobot.eventbus.**
-keep class org.greenrobot.eventbus.**{ *; }

-dontwarn  com.jeremyfeinstein.slidingmenu.lib.**
-keep class com.jeremyfeinstein.slidingmenu.lib.**{ *; }

-dontwarn okio.**
-keep class okio.**{ *; }

#okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}


#----------------------------------第三方包结束---------------------------------------

#
# This ProGuard configuration file illustrates how to process Android
# applications.
# Usage:
#     java -jar proguard.jar @android.pro
#
# If you're using the Android SDK (version 2.3 or higher), the android tool
# already creates a file like this in your project, called proguard.cfg.
# It should contain the settings of this file, minus the input and output paths
# (-injars, -outjars, -libraryjars, -printmapping, and -printseeds).
# The generated Ant build file automatically sets these paths.

# Specify the input jars, output jars, and library jars.
# Note that ProGuard works with Java bytecode (.class),
# before the dex compiler converts it into Dalvik code (.dex).

#-injars  bin/classes
-injars  libs
#-outjars bin/classes-processed.jar

#-libraryjars /android-15/android.jar
#-libraryjars /usr/local/android-sdk/add-ons/google_apis-7_r01/libs/maps.jar
# ...

# Save the obfuscation mapping to a file, so you can de-obfuscate any stack
# traces later on.

#-printmapping bin/classes-processed.map

# You can print out the seeds that are matching the keep options below.

#-printseeds bin/classes-processed.seeds

# Preverification is irrelevant for the dex compiler and the Dalvik VM.

-dontpreverify

# Reduce the size of the output some more.
# 混淆的类存放处
-repackageclasses 'com.td'
-allowaccessmodification

# Switch off some optimizations that trip older versions of the Dalvik VM.

-optimizations !code/simplification/arithmetic

# Keep a fixed source file attribute and all line number tables to get line
# numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# RemoteViews might need annotations.

-keepattributes *Annotation*

# Preserve all fundamental application classes.

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Preserve all View implementations, their special context constructors, and
# their setters.

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Preserve all classes that have special context constructors, and the
# constructors themselves.

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# Preserve all classes that have special context constructors, and the
# constructors themselves.

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Preserve the special fields of all Parcelable implementations.

-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.

-keepclassmembers class **.R$* {
  public static <fields>;
}

# Preserve the required interface from the License Verification Library
# (but don't nag the developer if the library is not used at all).

#-keep public interface com.android.vending.licensing.ILicensingService

#-dontnote com.android.vending.licensing.ILicensingService

# The Android Compatibility library references some classes that may not be
# present in all versions of the API, but we know that's ok.

-dontwarn android.support.**

# Preserve all native method names and the names of their classes.

-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve the special static methods that are required in all enumeration
# classes.

-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your application doesn't use serialization.
# If your code contains serializable classes that have to be backward 
# compatible, please refer to the manual.

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


-keep public class * extends android.app.Fragment



# Your application may contain more items that need to be preserved; 
# typically classes that are dynamically created using Class.forName:

#-keep public class weiwei.com.proguard._Log{*;}
# -keep public interface mypackage.MyInterface
# -keep public class * implements mypackage.MyInterface
#-keep class **{*;}

###############################第三方包不混淆开始#####################################################
# 不混淆第三方包, 第三方包不警告:
-keep class com.baidu.**{*;}
-dontwarn com.baidu.**

-keep class c.t.m.g.**{*;}
-dontwarn c.t.m.g.**

-keep class uk.co.senab.photoview.**{*;}
-dontwarn uk.co.senab.photoview.**

-keep class com.umeng.**{*;}
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**
-dontwarn com.umeng.**
#
#
-keep class org.webrtc.**{*;}
-keep class org.bson.**{*;}
-keep class org.a.**{*;}
#
-keep class com.google.**{*;}
-dontwarn com.google.**
#
-keep class com.tencent.**{*;}
-keep public interface com.tencent.**
-dontwarn com.tencent.**
#
-keep class android.support.v4.**{*;}
-keep public class * extends android.support.v4.**
-dontwarn android.support.v4.**
#
-keep class android.annotation.SuppressLint.**{*;}
-dontwarn android.annotation.SuppressLint.**
#
-keep class com.yzx.**{*;}
-dontwarn com.yzx.**
#
-keep class com.google.zxing.**{*;}
-dontwarn com.google.zxing.**
#
-keep class com.alipay.**{*;}
-dontwarn com.alipay.**
#
-keep class com.unionpay.**{*;}
-dontwarn com.unionpay.**
#
-keep class com.lthy.**{*;}
-dontwarn com.lthy.**
#
-keep class com.UCMobile.**{*;}
-dontwarn com.UCMobile.**

-keep class org.apache.http.**{*;}
-dontwarn org.apache.http.**

-keep class android.webkit.**
-dontwarn android.webkit.**

-keep class io.rong.**{*;}
-dontwarn io.rong.**
-keep class de.greenrobot.dao.**{*;}
-dontwarn de.greenrobot.dao.**
-keep public class com.tddemo.R**{*;}
###############################第三方包不混淆结束#####################################################
###############################自定义类不混淆开始#####################################################
-keep class com.weiwei.base.application.VsApplication{*;}
-keep class com.weiwei.json.me.**{*;}
-keep class com.weiwei.dynamictest.plugin.**{*;}
-keep class com.keepc.**{*;}
-keep class com.weiwei.base.widgets.**{*;}
-keep public class com.zte.softphone.**{*;}
-keep public class com.weiwei.base.fragment.**{*;}
-keep public class com.weiwei.base.im.**{*;}
-keep public class com.zte.softphone.SoftManager{
    public <fields>;
    public <methods>;
}
###############################自定义类不混淆结束#####################################################
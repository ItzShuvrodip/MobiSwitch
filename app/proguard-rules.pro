# ✅ Keep all classes in your app package
-keep class com.mobiswitch.** { *; }
-keep class com.example.mobiswitch.** { *; }

# ✅ Keep AndroidX components
-keep class androidx.** { *; }
-dontwarn androidx.**

# ✅ Prevent issues with Android services
-dontwarn android.os.ServiceManager*

# ✅ Avoid warnings for common SDKs
-dontwarn com.bun.miitmdid.core.MdidSdkHelper*
-dontwarn com.google.firebase.iid.FirebaseInstanceId*
-dontwarn com.huawei.hms.ads.identifier.AdvertisingIdClient*

# ✅ Keep essential Android components
-keep public class * extends android.app.Application { *; }
-keep public class * extends android.app.Activity { *; }
-keep public class * extends android.app.Service { *; }
-keep public class * extends android.content.BroadcastReceiver { *; }
-keep public class * extends android.content.ContentProvider { *; }

# ✅ Keep classes that use Parcelable or Serializable
-keep class * implements java.io.Serializable { *; }
-keep class * implements android.os.Parcelable { *; }

# ✅ Preserve all class members used in XML layouts
-keepclassmembers class * {
    public void *(android.view.View);
    public void *(android.content.Context);
}

# ✅ Keep debugging info (Optional, but helps with crashes)
-keepattributes SourceFile,LineNumberTable
# Keep BouncyCastle security classes
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

# Keep Conscrypt security classes
-keep class org.conscrypt.** { *; }
-dontwarn org.conscrypt.**

# Keep OpenJSSE security classes
-keep class org.openjsse.** { *; }
-dontwarn org.openjsse.**

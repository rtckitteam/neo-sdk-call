# ProGuard rules that are applied to any app consuming this SDK

# Keep Neo SDK Call internal classes
-keep class cc.neo.sdkcall.** { *; }
-dontwarn cc.neo.sdkcall.**

# Keep WebRTC classes to avoid JNI UnsatisfiedLinkError
-keep class org.webrtc.** { *; }
-dontwarn org.webrtc.**

# Keep Socket.IO and Engine.IO
-keep class io.socket.** { *; }
-dontwarn io.socket.**
-keep class engine.io.** { *; }

# Keep Retrofit and OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}

# Keep Gson SerializedName annotations for data classes
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# (Optional) Keep App specific models if they don't use @SerializedName
# But usually app developers will add their own rules for their models.

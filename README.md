# NEO SDK Call Android

This SDK allows you to integrate **outgoing and incoming call features** into your Android app using the **NEO SDK**.

---

## Installation

Add the JitPack repository to your root `build.gradle.kts` (or `settings.gradle.kts`):

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency in your app module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.rtckitteam:neo-sdk-call:1.2.1-rc.20")
}
```

---

## Android Setup Requirements

### 1. Android Manifest Permissions

Ensure the following permissions are added in your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MICROPHONE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_PHONE_CALL" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---

### 2. ProGuard Configuration

If your app uses ProGuard/R8 obfuscation, add these rules to your `proguard-rules.pro` file:

```proguard
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
```

---

## Usage

### 1. Initialize & Configure SDK

Initialize the SDK in your `Application` class or main activity:

```kotlin
import cc.neo.sdkcall.NeoSdkCall

// Initialize with Context
NeoSdkCall.init(context)

// Set API URL and token
NeoSdkCall.setAPI(
    baseUrl = "https://your-api-url.com", 
    token = "your-api-token"
)

// (Optional) Set Custom Ringtone
val ringtoneUri = Uri.parse("android.resource://${context.packageName}/raw/miremix")
NeoSdkCall.setRingTone(ringtoneUri)

// (Optional) Set Custom Notification Icon
NeoSdkCall.setNotificationIcon(R.drawable.ic_neocall_notification)
```

---

### 2. Check and Request Permissions

The SDK offers a helper to handle runtime permissions automatically before starting calls:

```kotlin
if (NeoSdkCall.checkAndRequestPermissions(activity)) {
    // Permissions are granted, proceed to make calls
}
```

---

### 3. Make an Outgoing Call

Use `makeCall` to start an app-to-app call:

```kotlin
NeoSdkCall.makeCall(
    activity = activity,
    callerId = "2",
    callerName = "Halis",
    callerAvatar = "https://avatar.iran.liara.run/public/boy",
    calleeId = "3",
    calleeName = "Anas",
    calleeAvatar = "https://avatar.iran.liara.run/public",
    checkSum = "checksum_token",
    metaData = emptyMap()
)
```

---

### 4. Make an Outgoing Call (SIP)

Use `makeCallSip` to call a SIP destination / phone extension. For SIP calls, the avatar parameters can be left empty:

```kotlin
NeoSdkCall.makeCallSip(
    activity = activity,
    callerId = "2",
    callerName = "Halis",
    callerAvatar = "", // Leave empty for SIP
    destination = "3",
    destinationName = "Anas",
    destinationAvatar = "", // Leave empty for SIP
    checkSum = "checksum_token",
    metaData = mapOf("call_title" to "SIP Extension Call")
)
```

---

## Optional Metadata

You can customize call labels, status texts, or connection states using the `metaData` parameter.

### Custom Status & Connection Metadata Keys

| Metadata Key | Description / Status | Default Value (ID) |
| --- | --- | --- |
| **`call_title`** | Title displayed on the call screen | `"Free Call"` |
| **`call_name_title`** | Custom Callee Name overlay | Callee Name |
| **`call_incoming`** | Incoming Call Status Label | `"Incoming"` |
| **`call_connecting`** | Connecting State Status Label | `"Menghubungkan"` |
| **`call_calling`** | Calling State Status Label | `"Menghubungi"` |
| **`call_ringing`** | Ringing State Status Label | `"Ringing..."` |
| **`call_connected`** | Connected State Status Label | `"Terhubung"` |
| **`call_weak_signal`** | Weak connection status label | `"Koneksi tidak stabil"` |
| **`call_lost_connection`** | Lost connection status label | `"Panggilan Terputus"` |
| **`call_end`** | End of call status label | `"Panggilan Berakhir"` |
| **`call_busy`** | Callee busy status label | `"The customer is busy and cannot be reached"` |
| **`call_refused`** | Call refused/declined status label | `"Decline"` |
| **`call_btn_mute`** | Label for Mute button | `"Mute"` |
| **`call_btn_speaker`** | Label for Speaker button | `"Speaker"` |

---

## Call Event Listener

Implement `CallEventListener` to monitor the call state:

```kotlin
import cc.neo.sdkcall.event.CallEventListener
import cc.neo.sdkcall.event.CallState

NeoSdkCall.setEventListener(object : CallEventListener {
    override fun onCallStateChanged(state: CallState) {
        // Handle state changes (RINGING, CONNECTED, RECONNECTING, ENDED, etc.)
        Log.d("CallSDK", "Call State: ${state.name}")
    }

    override fun onError(code: Int, message: String) {
        // Handle call errors (e.g. 101: Permission Denied)
        Log.e("CallSDK", "Error: $code - $message")
    }
})
```

---

## Key Features (Recent Updates)

* **Keypad (DTMF Dialpad) Support**: Users can now open the keypad during an active call to send DTMF tones directly from the call screen interface.
* **Light and Dark Mode Support**: The call screen UI elements, backgrounds, and text colors dynamically adapt to the system theme (Light or Dark Mode).


# Auro-Proctoring-SDK

Proctoring is a process used to monitor and supervise online exams or assessments to ensure their integrity and prevent cheating. It involves the use of technology and human invigilators to monitor test-takers remotely.

## Installation

Step 1.  build file


```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```
Step 2. Add the dependency [![Latest Release](https://img.shields.io/github/v/release/AuroScholar/Auro_ProctoringSDK_Library?include_prereleases&sort=semver)](https://github.com/azzadpandit1/Auro-Proctoring-SDK/releases/latest)

```kotlin
dependencies {
    implementation 'com.github.AuroScholar:Auro_ProctoringSDK_Library:0.4.7'
}
```

## Setup ProctoringSDK 

## Manifest Permission ( android version 9 to 14 ) 
```manifest
     <uses-feature
        android:name="android.hardware.camera"
        android:required="true" />
    <uses-feature android:name="android.hardware.usb.host" />

    <uses-permission android:name="android.permission.DISABLE_KEYGUARD" />

    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />


    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission
        android:name="android.permission.WRITE_SETTINGS"
        tools:ignore="ProtectedPermissions" />

    <uses-feature android:name="android.hardware.camera.autofocus" />


    <uses-permission android:name="android.permission.USB_PERMISSION" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.ACCESS_NOTIFICATION_POLICY" />
    <uses-permission android:name="android.permission.EXPAND_STATUS_BAR" />
    <uses-permission
        android:name="android.permission.WRITE_SECURE_SETTINGS"
        tools:ignore="ProtectedPermissions" />

    <uses-permission android:name="android.permission.WRITE_SECURE_SETTINGS"
        tools:ignore="ProtectedPermissions" />

    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

    <uses-permission android:name="android.permission.ACCESS_NOTIFICATION_POLICY" />

    <uses-permission android:name="android.permission.RECORD_AUDIO"/>
```


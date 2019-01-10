Gradle引入tapjoyconnectlibrary.jar：v11.12.2
需要额外引入：
dependencies {
    implementation 'com.liulishuo.okdownload:okdownload:1.0.3'
    implementation 'com.liulishuo.okdownload:sqlite:1.0.3'
    implementation 'com.liulishuo.okdownload:okhttp:1.0.3'

    implementation 'com.squareup.okhttp3:okhttp:3.10.0'
}

AndroidManifest配置：
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

<activity
    android:name="com.tapjoy.TJAdUnitActivity"
    android:configChanges="orientation|keyboardHidden|screenSize"
    android:theme="@android:style/Theme.Translucent.NoTitleBar.Fullscreen"
    android:hardwareAccelerated="true" />
<activity
    android:name="com.tapjoy.mraid.view.ActionHandler"
    android:configChanges="orientation|keyboardHidden|screenSize" />
<activity
    android:name="com.tapjoy.mraid.view.Browser"
    android:configChanges="orientation|keyboardHidden|screenSize" />

<activity
    android:name="com.tapjoy.TJContentActivity"
    android:configChanges="orientation|keyboardHidden|screenSize"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />


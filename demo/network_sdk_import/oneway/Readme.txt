Gradle引入OnewaySdk-2.1.3_release.jar：v2.1.3
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
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />

<activity
    android:name="mobi.oneway.sdk.AdShowActivity"
    android:configChanges="fontScale|keyboard|keyboardHidden|locale|mnc|mcc|navigation|orientation|screenLayout|screenSize|smallestScreenSize|uiMode|touchscreen"
    android:hardwareAccelerated="true"
    android:launchMode="singleTask"
    android:theme="@android:style/Theme.NoTitleBar.Fullscreen" />

<!--targetSDKVersion>=24 -->
<provider
    android:name="com.uparpu.network.oneway.OnewayUpArpuFileProvider"
    android:authorities="${applicationId}.provider"
    android:exported="false"
    android:grantUriPermissions="true">
<meta-data
    android:name="android.support.FILE_PROVIDER_PATHS"
    android:resource="@xml/file_path" />
</provider>

file_path的内容：
<paths xmlns:android="http://schemas.android.com/apk/res/android">
<files-path
name="files"
path="" />
<external-path
name="sdcard"
path="" />
<cache-path
name="cache"
path="" />
<external-files-path
name="exFiles"
path="" />
<external-cache-path
name="exCache"
path="" />
</paths>
通过Gradle导入目录下的open_ad_sdk.aar：v2.0.1.1
额外引入：
dependencies {
    compile "pl.droidsonroids.gif:android-gif-drawable:1.2.6" //only for splashad
}

AndrodManifest配置：
 <!--头条-->
<!-- Must be provided permissions-->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
<uses-permission android:name="android.permission.GET_TASKS"/>
<!-- Optional permissions to enable better geo-targeting of ads (recommended) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<provider
    android:name="com.bytedance.sdk.openadsdk.multipro.TTMultiProvider"
    android:authorities="${applicationId}.TTMultiProvider"
    android:exported="false" />

<!--targetSDKVersion>=24-->
<provider
    android:name="com.bytedance.sdk.openadsdk.TTFileProvider"
    android:authorities="${applicationId}.TTFileProvider"
    android:exported="false"
    android:grantUriPermissions="true">
<meta-data
    android:name="android.support.FILE_PROVIDER_PATHS"
    android:resource="@xml/file_paths" />
</provider>

file_path内容：
<?xml version="1.0" encoding="utf-8"?>
    <paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-files-path name="external_files_path" path="Download" />
</paths>
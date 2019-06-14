Gradle引入Uniplay_AdSDK_6.0.2.jar：v6.0.2


AndroidManifest配置：
<uses-permission android:name="android.permission.INTERNET" /> <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /> <uses-permission android:name="android.permission.READ_PHONE_STATE" /> <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" /> <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /> <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />

<activity android:name="com.uniplay.adsdk.AdActivity" android:configChanges="keyboard|keyboardHidden|orientation|screenSize"> </activity> <activity android:name="com.uniplay.adsdk.InterstitialAdActivity" android:theme="@android:style/Theme.Translucent" android:configChanges="keyboard|keyboardHidden|orientation|screenSize" />
<receiver android:name="com.uniplay.adsdk.PackageReceiver">
    <intent-filter android:priority="2147483647">
        <action android:name="android.net.conn.CONNECTIVITY_CHANGE"/>
        <action android:name="android.intent.action.USER_PRESENT"/> </intent-filter> <intent-filter android:priority="2147483647"> <action android:name="android.intent.action.PACKAGE_ADDED"/>
        <data android:scheme="package"/>
    </intent-filter>
</receiver>
<activity
    android:name="com.uniplay.adsdk.NetworkChangeActivity"
    android:configChanges="keyboard|keyboardHidden|orientation|screenSize"
    android:theme="@android:style/Theme.Translucent"/> <service android:name="com.uniplay.adsdk.DownloadService"/>

<!--targetSDKVersion>=24-->
<provider
    android:name="com.uniplay.adsdk.UniPlayFileProvider"
    android:authorities="${applicationId}.uniplay.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_path" />
</provider>

file_path的内容：
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <root-path name="download" path="" />
</paths>
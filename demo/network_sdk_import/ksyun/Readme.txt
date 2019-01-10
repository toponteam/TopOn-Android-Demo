Gradle引入ksc_sdk_v407.jar：v4.0.7


AndroidManifest配置：
<!--Permissions must be added -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<uses-permission
android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission
android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION"/>
<uses-permission
android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission
android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission
android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>

//Activity for showing the Rewarded video
<activity
    android:name="com.ksc.ad.sdk.ui.AdProxyActivity" android:hardwareAccelerated="true"
    android:theme="@android:style/Theme.Black.NoTitleBar.Fullscreen"
    android:configChanges="keyboardHidden|orientation|screenSize" />
//Dynamic permission, transparent layer Activity
<activity
    android:name="com.ksc.ad.sdk.ui.AdPermissionProxyActivity" android:configChanges="keyboardHidden|orientation|screenSize"
    android:theme="@android:style/Theme.Translucent.NoTitleBar.Fullscreen" />
<service android:name="com.ksc.ad.sdk.service.AdProxyService" />
//Add provider here, in order to be compatible with the automatic installation of applications after android 7.0
<provider
    android:name="com.ksc.ad.sdk.util.KsyunFileProvider"
    android:authorities="com.xxx.xxx.xxx.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths"/>
</provider>


file_path的内容：
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-cache-path name="files_root" path="apk/."/>
</paths>
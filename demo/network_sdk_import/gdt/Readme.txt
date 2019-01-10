Gradle引入GDTUnionSDK.4.20.580.min.jar：v4.20.580


AndroidManifest配置：
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /> <!-- Please add this permission if you need precise positioning. -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

!-- GDT -->
<service
    android:name="com.qq.e.comm.DownloadService"
    android:exported="false" />
<activity
    android:name="com.qq.e.ads.ADActivity"
    android:configChanges="keyboard|keyboardHidden|orientation|screenSize" />
<activity android:name="com.qq.e.ads.PortraitADActivity"
    android:screenOrientation="portrait"
    android:configChanges="keyboard|keyboardHidden|orientation|screenSize" />
<activity android:name="com.qq.e.ads.LandscapeADActivity"
    android:screenOrientation="landscape"
    android:configChanges="keyboard|keyboardHidden|orientation|screenSize" />

<!-- targetSDKVersion >=24 需要导入-->
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
<meta-data
    android:name="android.support.FILE_PROVIDER_PATHS"
    android:resource="@xml/file_path" />
</provider>


file_path的内容：
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="gdt_sdk_download_path" path="GDTDOWNLOAD" />
</paths>
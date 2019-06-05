Gradle导入：

dependencies {
     compile 'com.applovin:applovin-sdk:9.7.0'
}


AndroidManifest配置：
<activity
    android:name="com.applovin.adview.AppLovinInterstitialActivity"
    android:configChanges="orientation|screenSize" />

<activity android:name="com.applovin.adview.AppLovinConfirmationActivity" />
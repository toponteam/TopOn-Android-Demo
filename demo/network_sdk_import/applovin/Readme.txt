Gradle导入：

dependencies {
     compile 'com.applovin:applovin-sdk:9.4.2'
}


AndroidManifest配置：
<activity
    android:name="com.applovin.adview.AppLovinInterstitialActivity"
    android:configChanges="orientation|screenSize" />

<activity android:name="com.applovin.adview.AppLovinConfirmationActivity" />
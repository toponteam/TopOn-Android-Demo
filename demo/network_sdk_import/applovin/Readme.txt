Gradle导入：

dependencies {
     //applovin 9.1.3 支持GDPR
     compile 'com.applovin:applovin-sdk:9.1.3'
}


AndroidManifest配置：
<activity
    android:name="com.applovin.adview.AppLovinInterstitialActivity"
    android:configChanges="orientation|screenSize" />

<activity android:name="com.applovin.adview.AppLovinConfirmationActivity" />
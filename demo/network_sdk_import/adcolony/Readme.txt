Gradle引入：

allprojects {
    repositories {
        //adcolony
        maven {
            url  "https://adcolony.bintray.com/AdColony"
        }
    }
}
dependencies {
    //adcolony SDK dependency
    compile 'com.adcolony:sdk:3.3.10'
}


AndroidManifest配置：
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.VIBRATE" />

<!--Adcolony-->
<activity
    android:name="com.adcolony.sdk.AdColonyInterstitialActivity"
    android:configChanges="keyboardHidden|orientation|screenSize"
    android:hardwareAccelerated="true" />
<activity
    android:name="com.adcolony.sdk.AdColonyAdViewActivity"
    android:configChanges="keyboardHidden|orientation|screenSize" />


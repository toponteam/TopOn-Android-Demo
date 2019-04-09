通过Gradle导入：

allprojects {
    repositories {
        //vungle
        maven {
            url "https://jitpack.io"
        }
    }
}

dependencies {

}

AndroidManifest配置：

<!--Necessary Permissions-->
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />


 <!--yeahmobi-->
 <activity android:name="com.zcoup.base.view.InnerWebViewActivity" />

<activity
    android:name="com.zcoup.video.view.RewardedVideoActivity"
    android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize" />

<activity android:name="com.zcoup.base.view.InterstitialActivity" />



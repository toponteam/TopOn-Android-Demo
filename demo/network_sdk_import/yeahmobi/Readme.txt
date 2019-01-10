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
    //yeahmbi
    compile 'com.cloudtech:ads:3.3.0'
    compile 'com.cloudtech:imageloader:3.3.0'   // for preload image
    compile 'com.cloudtech:videoads:3.3.0'
}

AndroidManifest配置：

<!--Necessary Permissions-->
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />


 <!--yeahmobi-->
<activity
    android:name="com.cloudtech.ads.view.InnerWebLandingActivity"
    android:launchMode="singleInstance" />
<activity android:name="com.cloudtech.ads.view.InterstitialActivity" />
<activity android:name="com.cloudtech.videoads.view.CTInterstitialActivity"
    android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize"/>



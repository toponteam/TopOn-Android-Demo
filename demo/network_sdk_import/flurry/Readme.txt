Gradle引入：

dependencies {
    //flurry 11.2.0
    compile 'com.flurry.android:analytics:11.2.0@aar'
    compile 'com.flurry.android:ads:11.2.0@aar'
}


AndroidManifest配置：
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<activity
    android:name="com.flurry.android.FlurryFullscreenTakeoverActivity"
    android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize"/>


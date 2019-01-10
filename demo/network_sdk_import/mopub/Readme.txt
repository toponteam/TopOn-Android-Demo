通过Gradle导入：
dependencies {
    compile('com.mopub:mopub-sdk:5.3.0@aar') {
        transitive = true
        exclude module: 'libAvid-mopub' // To exclude AVID
        exclude module: 'moat-mobile-app-kit' // To exclude Moat
    }
    // For native static (images).
    compile('com.mopub:mopub-sdk-native-static:5.3.0@aar') {
        transitive = true
        exclude module: 'libAvid-mopub' // To exclude AVID
        exclude module: 'moat-mobile-app-kit' // To exclude Moat
    }
    // For native video. This will automatically also include native static
    compile('com.mopub:mopub-sdk-native-video:5.3.0@aar') {
        transitive = true
        exclude module: 'libAvid-mopub' // To exclude AVID
        exclude module: 'moat-mobile-app-kit' // To exclude Moat
    }

    compile('com.mopub:mopub-sdk-rewardedvideo:5.3.0@aar') {
        transitive = true
    }

    // For banners
    compile('com.mopub:mopub-sdk-banner:5.3.0@aar') {
        transitive = true
    }

    // For interstitials
    compile('com.mopub:mopub-sdk-interstitial:5.3.0@aar') {
        transitive = true
    }
}

AndroidManifest配置：
<!-- All ad formats -->
<activity
    android:name="com.mopub.mobileads.MoPubActivity"
    android:configChanges="keyboardHidden|orientation|screenSize" />
<activity
    android:name="com.mopub.mobileads.MraidActivity"
    android:configChanges="keyboardHidden|orientation|screenSize" />
<activity
    android:name="com.mopub.common.MoPubBrowser"
    android:configChanges="keyboardHidden|orientation|screenSize" />
<activity
    android:name="com.mopub.mobileads.MraidVideoPlayerActivity"
    android:configChanges="keyboardHidden|orientation|screenSize" />
<activity
    android:name="com.mopub.mobileads.RewardedMraidActivity"
    android:configChanges="keyboardHidden|orientation|screenSize" />
<activity
    android:name="com.mopub.common.privacy.ConsentDialogActivity"
    android:configChanges="keyboardHidden|orientation|screenSize" />
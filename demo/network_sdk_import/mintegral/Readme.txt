通过Gradle导入目录下的所有的AAR文件：v9.10.0

AndrodManifest配置：
<activity
    android:name="com.mintegral.msdk.activity.MTGCommonActivity"
    android:configChanges="keyboard|orientation"
    android:screenOrientation="portrait"
    android:exported="true"
    android:theme="@android:style/Theme.Translucent.NoTitleBar">
</activity>

<receiver android:name="com.mintegral.msdk.click.AppReceiver" >
    <intent-filter>
        <action android:name="android.intent.action.PACKAGE_ADDED" />
        <data android:scheme="package" />
    </intent-filter>
</receiver>
<service android:name="com.mintegral.msdk.shell.MTGService" >
    <intent-filter>
        <action android:name="com.mintegral.msdk.download.action" />
    </intent-filter>
</service>

<!--RewardedVideo-->
<activity
    android:name="com.mintegral.msdk.reward.player.MTGRewardVideoActivity"
    android:configChanges="orientation|keyboardHidden|screenSize"
    android:theme="@android:style/Theme.NoTitleBar.Fullscreen" />

<!--Interstitial-->

<activity
android:name="com.mintegral.msdk.interstitial.view.MTGInterstitialActivity"
android:configChanges="orientation|screenSize"
android:screenOrientation="portrait" />
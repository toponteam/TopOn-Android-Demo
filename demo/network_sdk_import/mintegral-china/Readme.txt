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
        <activity
            android:name="com.mintegral.msdk.reward.player.MTGRewardVideoActivity"
            android:configChanges="orientation|keyboardHidden|screenSize"
            android:theme="@android:style/Theme.NoTitleBar.Fullscreen" />

        <activity
            android:name="com.mintegral.msdk.interstitial.view.MTGInterstitialActivity"
            android:configChanges="orientation|screenSize"
            android:screenOrientation="portrait" />

        <provider
            android:name="com.mintegral.msdk.base.utils.MTGFileProvider"
            android:authorities="${applicationId}.mtgFileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/uparpu_bk_mtg_file_path" />
        </provider>

混淆配置：
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.mintegral.** {*; }
-keep interface com.mintegral.** {*; }
-keep class android.support.v4.** { *; }
-dontwarn com.mintegral.**
-keep class **.R$* { public static final int mintegral*; }
-keep class com.alphab.** {*; }
-keep interface com.alphab.** {*; }

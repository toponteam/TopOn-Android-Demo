通过Gradle导入目录下的Baidu_MobAds_SDK-release.aar：v4.6.0

AndrodManifest配置：
<!--baidu-->
<!-- 如果targetSdkVersion设置值>=24，则强烈建议添加以下provider，否则会影响app变现效率 -->
<provider
    android:name="com.baidu.mobads.openad.FileProvider"
    android:authorities="${packageName}.bd.provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_path" />
</provider>

<!-- for baidu xadsdk internal web-browser -->
<!-- 非信息广告必须添加 -->
<activity
    android:name="com.baidu.mobads.AppActivity"
    android:configChanges="screenSize|keyboard|keyboardHidden|orientation"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
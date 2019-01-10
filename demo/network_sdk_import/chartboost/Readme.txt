通过Gradle导入目录下的chartboost.jar：v4.6.0

AndrodManifest配置：
<!--chartboost -->
<activity
    android:name="com.chartboost.sdk.CBImpressionActivity"
    android:configChanges="keyboardHidden|orientation|screenSize"
    android:excludeFromRecents="true"
    android:hardwareAccelerated="true"
    android:theme="@android:style/Theme.Translucent.NoTitleBar.Fullscreen" />
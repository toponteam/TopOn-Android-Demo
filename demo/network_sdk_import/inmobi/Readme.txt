Gradle引入：

dependencies {
    compile "com.inmobi.monetization:inmobi-ads:7.2.7"
    compile 'com.squareup.picasso:picasso:2.5.2'
}


AndroidManifest配置：
<activity
    android:name="com.inmobi.rendering.InMobiAdActivity"
    android:configChanges="keyboardHidden|orientation|keyboard|smallestScreenSize|screenSize|screenLayout"
    android:hardwareAccelerated="true"
    android:resizeableActivity="false"
    android:theme="@android:style/Theme.NoTitleBar"
    tools:ignore="UnusedAttribute"/>


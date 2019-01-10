Gradle引入：
allprojects {
    repositories {
    maven {
            url  "https://dl.bintray.com/ironsource-mobile/android-sdk"
    }
    maven {
            url  "http://dl.bintray.com/ironsource-mobile/android-adapters"
    }
    }
}

dependencies {
    // ironsource
    compile 'com.ironsource.sdk:mediationsdk:6.7.9.1@jar'
}


AndroidManifest配置：
<activity
    android:name="com.inmobi.rendering.InMobiAdActivity"
    android:configChanges="keyboardHidden|orientation|keyboard|smallestScreenSize|screenSize|screenLayout"
    android:hardwareAccelerated="true"
    android:resizeableActivity="false"
    android:theme="@android:style/Theme.NoTitleBar"
    tools:ignore="UnusedAttribute"/>
<service
    android:name="com.inmobi.signals.activityrecognition.ActivityRecognitionManager"
    android:enabled="true" />


Gradle导入：

allprojects {
    repositories {
        //appnext
        maven { url "http://dl.appnext.com/" }
    }
}

dependencies {
     //appnext
    compile 'com.appnext.sdk:ads:2.+'
    compile 'com.appnext.sdk:banners:2.+'
    compile 'com.appnext.sdk:native-ads2:2.+' // New Native Ads
}

AndroidManifest配置：
<!--Necessary Permissions-->
android.gms.permission.ACTIVITY_RECOGNITION
android.permission.ACCESS_WIFI_STATE
android.permission.READ_PHONE_STATE
android.permission.ACCESS_FINE_LOCATION
android.permission.ACCESS_COARSE_LOCATION
android.permission.GET_TASKS
android.permission.REAL_GET_TASKS
android.permission.PACKAGE_USAGE_STATS
android.permission.WRITE_EXTERNAL_STORAGE
android.permission.CAMERA
android.permission.BLUETOOTH_ADMIN


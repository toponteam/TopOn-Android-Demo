Gradle导入：

dependencies {
    //admob
    compile 'com.google.android.gms:play-services-ads:17.2.0'
    //GDPR支持
    compile 'com.google.android.ads.consent:consent-library:1.0.6'
}

AndroidManifest增加配置：（如果没有增加会引起崩溃）
<meta-data
   android:name="com.google.android.gms.ads.AD_MANAGER_APP"
   android:value="true"/>
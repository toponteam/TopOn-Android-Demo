Gradle导入：

allprojects {
    repositories {
        ....
        //nend
        maven {
            url 'http://fan-adn.github.io/nendSDK-Android-lib/library'
        }
    }
}

dependencies {
    implementation 'net.nend.android:nend-sdk:5.1.0'
}


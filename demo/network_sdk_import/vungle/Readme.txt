通过Gradle导入：

allprojects {
    repositories {
        //vungle
        maven {
            url "https://jitpack.io"
        }
    }
}

dependencies {
     //Add Vungle SDK dependency
    compile 'com.github.vungle:vungle-android-sdk:6.3.12'
}

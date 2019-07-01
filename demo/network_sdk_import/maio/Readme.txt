Gradle导入：

allprojects {
    repositories {
        ....
        //maio
        maven{
            url "https://imobile-maio.github.io/maven"
        }
    }
}

dependencies {
    implementation 'com.maio:android-sdk:1.1.7@aar'
}


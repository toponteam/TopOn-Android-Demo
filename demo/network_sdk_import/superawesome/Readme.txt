Gradle导入：

allprojects {
    repositories {
        ....
        //awesome
        maven {
            url  "http://dl.bintray.com/sharkofmirkwood/maven"
        }
    }
}

dependencies {
    implementation 'tv.superawesome.sdk:sa-sdk:3.6.5@aar'
}


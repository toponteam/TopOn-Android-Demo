# TopOn与Firebase集成冲突解决方案



[1.原因](#1)

[2.定位冲突](#2)

[3.解决方法](#3)

[4.Admob与Firebase兼容列表](#4)



<h2 id='1'>1. 原因</h2>
TopOn 提供的**Admob**的aar包与**Firebase**的依赖发生冲突导致的



<h2 id='2'>2. 定位冲突</h2>
以**17.2.0**版本的**Admob**与**16.0.1**版本的**Firebase**冲突的情况为例，项目中通过gradle导入了16.0.1版本的Firebase依赖，并且导入了17.2.0版本的Admob的aar包。

libs：

```
play-service-measurement-base-sdk-api-16.3.0.jar
play-services-ads-17.2.0.aar
play-services-ads-base-17.2.0.aar
play-services-ads-identifier-16.0.0.aar
play-services-ads-lite-17.2.0.aar
play-services-basement-16.0.1.aar
play-services-gass-17.2.0.aar
play-services-mearsurement-base-16.3.0.aar
```

app build.gradle:

```java
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar','*.aar'])
    implementation 'com.google.firebase:firebase-core:16.0.1'    
}
```



运行时，项目会报错：（**假设只有此冲突存在，实际上此示例下会有很多冲突存在**）

```
Duplicate class com.google.android.gms.common.internal.ServiceSpecificExtraArgs$GamesExtraArgs found in modules classes.jar (com.google.android.gms:play-services-basement:15.0.1) and classes.jar (play-services-basement-16.0.1.aar)
Duplicate class com.google.android.gms.common.internal.ServiceSpecificExtraArgs$PlusExtraArgs found in modules classes.jar (com.google.android.gms:play-services-basement:15.0.1) and classes.jar (play-services-basement-16.0.1.aar)
Duplicate class com.google.android.gms.common.internal.ShowFirstParty found in modules classes.jar (com.google.android.gms:play-services-basement:15.0.1) and classes.jar (play-services-basement-16.0.1.aar)
Duplicate class com.google.android.gms.common.internal.StringResourceValueReader found in modules classes.jar (com.google.android.gms:play-services-basement:15.0.1) and classes.jar (play-services-basement-16.0.1.aar)
Duplicate class com.google.android.gms.common.internal.ViewUtils found in modules classes.jar (com.google.android.gms:play-services-basement:15.0.1) and classes.jar (play-services-basement-16.0.1.aar)
···
···

```



从报错信息中可以发现 **com.google.android.gms:play-services-basement:15.0.1** 与 **play-services-basement-16.0.1.aar** 冲突了，此时需要查看项目中的gradle的依赖关系，找到冲突的库是从哪里引进来的。

查找项目中的依赖关系：Terminal中输入 **gradlew :app:dependencies** （其中app为module）

```
+--- **com.google.firebase:firebase-core:16.0.1**
|    +--- com.google.firebase:firebase-analytics:16.0.1
|    |    +--- **com.google.android.gms:play-services-basement:15.0.1**
|    |    |    \--- androidx.legacy:legacy-support-v4:1.0.0
|    |    |         +--- androidx.core:core:1.0.0
|    |    |         |    +--- androidx.annotation:annotation:1.0.0
|    |    |         |    +--- androidx.collection:collection:1.0.0
|    |    |         |    |    \--- androidx.annotation:annotation:1.0.0
|    |    |         |    +--- androidx.lifecycle:lifecycle-runtime:2.0.0
|    |    |         |    |    +--- androidx.lifecycle:lifecycle-common:2.0.0
|    |    |         |    |    |    \--- androidx.annotation:annotation:1.0.0
|    |    |         |    |    +--- androidx.arch.core:core-common:2.0.0
|    |    |         |    |    |    \--- androidx.annotation:annotation:1.0.0
|    |    |         |    |    \--- androidx.annotation:annotation:1.0.0
|    |    |         |    \--- androidx.versionedparcelable:versionedparcelable:1.0.0
|    |    |         |         +--- androidx.annotation:annotation:1.0.0
|    |    |         |         \--- androidx.collection:collection:1.0.0 (*)
···

···
```


从依赖树中发现**com.google.firebase:firebase-core:16.0.1**依赖了**com.google.android.gms:play-services-basement:15.0.1**，此时项目中存在了**play-services-basement**的不同版本，一个是gradle依赖引进来的**15.0.1**版本，另一个是**play-services-basement-16.0.1.aar**引进来的**16.0.1**版本。



<h2 id='3'>3. 解决方法</h2>
1、您的项目中依赖了**support-v4/v7**，或者聚合了**AppNext、Vungle、Inmobi**等第三方平台：

删除**admob**文件夹下除**anythink_network_admob.aar**以外的所有aar、jar包，并修改build.gradle：

```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar','*.aar'])
    implementation 'com.google.firebase:firebase-core:16.0.7'
    implementation 'com.google.android.gms:play-services-ads:17.2.0'
    implementation 'com.google.android.ads.consent:consent-library:1.0.4'
}
```



2、您的项目中没有依赖**support-v4/v7**，即项目中依赖了**Androidx**：

删除**admob**文件夹下除**anythink_network_admob.aar**以外的所有aar、jar包，并修改build.gradle：

```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar','*.aar'])
    implementation 'com.google.firebase:firebase-core:17.2.1'
    implementation 'com.google.android.gms:play-services-ads:18.3.0'
    implementation 'com.google.android.ads.consent:consent-library:1.0.6'
}
```



<h2 id='4'>4. Admob与Firebase兼容列表</h2>
| Admob         | Firebase      |
| ------------- | ------------- |
| 17.2.0        | 16.0.7        |
| 18.2.0/18.3.0 | 17.0.0/17.2.1 |



# TopOn and Firebase integration conflict resolution



[1.The reason](#1)

[2.Positioning conflict](#2)

[3.Solution](#3)

[4.Admob and Firebase compatibility list](#4)



<h2 id='1'>1. The Reason</h2>

The aar package of **Admob** provided by TopOn conflicts with the dependency of **Firebase**.



<h2 id='2'>2. Positioning conflict</h2>

Take the case where the **17.2.0** version of **Admob** and the **16.0.1** version of **Firebase** conflict, for example, the 16.0.1 version of Firebase dependency is imported through gradle in the project And imported the aar package of Admob version 17.2.0.
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



When running, the project will report an error：（**Assuming only this conflict exists, in fact there will be many conflicts in this example**）

```
Duplicate class com.google.android.gms.common.internal.ServiceSpecificExtraArgs$GamesExtraArgs found in modules classes.jar (com.google.android.gms:play-services-basement:15.0.1) and classes.jar (play-services-basement-16.0.1.aar)
Duplicate class com.google.android.gms.common.internal.ServiceSpecificExtraArgs$PlusExtraArgs found in modules classes.jar (com.google.android.gms:play-services-basement:15.0.1) and classes.jar (play-services-basement-16.0.1.aar)
Duplicate class com.google.android.gms.common.internal.ShowFirstParty found in modules classes.jar (com.google.android.gms:play-services-basement:15.0.1) and classes.jar (play-services-basement-16.0.1.aar)
Duplicate class com.google.android.gms.common.internal.StringResourceValueReader found in modules classes.jar (com.google.android.gms:play-services-basement:15.0.1) and classes.jar (play-services-basement-16.0.1.aar)
Duplicate class com.google.android.gms.common.internal.ViewUtils found in modules classes.jar (com.google.android.gms:play-services-basement:15.0.1) and classes.jar (play-services-basement-16.0.1.aar)
···
···

```



Can be found from the error message: **com.google.android.gms: play-services-basement: 15.0.1** conflicts with **play-services-basement-16.0.1.aar**. At this time, you need to check the dependencies of gradle Relationship, find where the conflicting library was imported from.

Find dependencies in a project：Enter **gradlew: app: dependencies** in Terminal (where app is module)

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


It was found from the dependency tree that **com.google.firebase: firebase-core: 16.0.1** depends on **com.google.android.gms: play-services-basement: 15.0.1**, at this time in the project There are different versions of **play-services-basement**, one is the **15.0.1** version introduced by gradle, and the other is the **play-services-basement-16.0.1.aar** introduction **16.0.1** version coming.


<h2 id='3'>3. Solution</h2>

1、Your project depends on **support-v4 / v7**, or aggregates third-party platforms such as **AppNext, Vungle, Inmobi**:

Delete all aar and jar packages except **anythink_network_admob.aar** in the **admob** folder, and modify build.gradle:

```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar','*.aar'])
    implementation 'com.google.firebase:firebase-core:16.0.7'
    implementation 'com.google.android.gms:play-services-ads:17.2.0'
    implementation 'com.google.android.ads.consent:consent-library:1.0.4'
}
```



2、Your project does not depend on **support-v4 / v7**, that is, it depends on **Androidx**:

Delete all aar and jar packages except ** anythink_network_admob.aar ** in the ** admob ** folder, and modify build.gradle:

```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar','*.aar'])
    implementation 'com.google.firebase:firebase-core:17.2.1'
    implementation 'com.google.android.gms:play-services-ads:18.3.0'
    implementation 'com.google.android.ads.consent:consent-library:1.0.6'
}
```



<h2 id='4'>4. Admob and Firebase compatibility list</h2>
| Admob         | Firebase      |
| ------------- | ------------- |
| 17.2.0        | 16.0.7        |
| 18.2.0/18.3.0 | 17.0.0/17.2.1 |



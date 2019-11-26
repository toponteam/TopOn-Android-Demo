# The test guideline of TopOn SDK&FAQ

[0. Checklist](#0)<br>
[1. The test guideline of TopOn SDK](#1)<br>
[2. ErrorCode](#2)<br>
[3. FAQ](#3)<br>

<h2 id='0'>0. CheckList</h2>

**a、SDK core dependency package：**
```
anythink_core.aar
```

**b、Ad form dependency package：**
```
Native ad：anythink_native.aar 
Banner ad：anythink_banner.aar
Interstitial ad：anythink_interstitial.aar 
RewardVideo ad：anythink_rewardvideo.aar 
Splash ad：anythink_splash.aar 
```

**c、Head bidding function dependency package：**（Must be imported when using the head bidding function）
```
anythink_headbiding.aar<br>
```

**d、Ad platform dependence，in the corresponding network platform folder under the network directory:**
```
Aar, jar package in libs, extra folder
AndroidManifest.xml
proguard-android.txt
Resource in res folder
```
(For example: aggregating Facebook and Admob advertising platforms, all relevant resources in the facebook and admob folders in the network directory need to be imported and configured)

**e、Android-v7 library dependency：**
```java
dependencies {
    implementation 'com.android.support:appcompat-v7:28.0.0'
}
```

**f、AndroidManifest.xml<br>**
```java
 <application
        ...
        <!--Configuration for Http request for Android high version-->
        android:usesCleartextTraffic="true">
        ....
        <!--Configuration For Android 9.0 and above, it is used to adapt network request-->
        <uses-library android:name="org.apache.http.legacy" android:required="false"/>
        ....
<application>
```

**g、Proguard**
```java
-dontwarn com.anythink.**
-keep public class com.anythink.network.**
-keepclassmembers class com.anythink.network.** {
   public *;
}
```

<br>

Integration If there is a problem, please jump [Android_Integration_Document_For_TopOn_SDK](Android_Integration_Document_For_TopOn_SDK.md)


<h2 id='1'>1. The test guideline of TopOn SDK</h2>

**Please refer to [CheckList](#0) to check the TopOn SDK dependency package and the dependencies of the advertising platform you intend to aggregate before start testing, including aar, jar, AndroidManifest.xml, proguard-android.txt, res resources, etc.**

<h3>1.1 Open the logging function of the SDK</h3>

1. Add this code before initialization：**ATSDK.setNetworkLogDebug(true).**

2. Filter "**anythink_network**"  in Logcat in AndroidStudio to view logs.

<h3>1.2 Configure the ad source in the ad placement</h3>

1. Make sure there is a configured ad source under the ad placement in the TopOn background, add it via the **Add ad source** button, and the **status is on **.

![](The_configuration_of_ad_source.png)

For example, the **DemoBlack_RV** ad placement (id: **b5c4ad2f0de421**) is configured with the following ad sources:

​		1) **ironSource (At this point, the TopOn SDK will first request this ad source.)**
​		2) Applovin
​		3) Inmobi
​		4) Admob
​		5) Facebook

**Above ad sources are open**.

<h3>1.3 Start a request, observe logcat and troubleshoot</h3>

Take **DemoBlack_RV** as an example. TopOn SDK will first request the **ironSource which in first place, start an ad request and display it, then observe the output log of the SDK in logcat. The format looks like this:

```java
{
	"placemengId": "b5b449eefcab50", //Placementid
	"adType": "reward", //Ad type
	"action": "request_result", //Including request,request_result,impression, click,close
	"refresh": 0,
	"result": "success", //Result including success, fail
	"position": 0, //The position of currnt AdSource
	"networkType": 1,  //Third-party network id, you can view relation on this site（https://github.com/uparputeam/uparpu_demo_android）
	"networkUnit": "{\"unit_id\":\"1673989822661578_1846988512028374\"}", //App and placement information for third-party network
	"msg": "", //If a load failure occurs, a complete error message will be output here.
	"hourly_frequency": 0, //Current the number of impressions an hour
	"daily_frequency": 0, //Current the number of impressions one day
	"network_list": "1", //The id of the current third-party ad request list, interval by ","
	"request_network_num": 1 //Indicates how many third-party ad platforms can load at the same time
}

```

Observe the value of the **"result"** field

<h4>1.3.1 “success”</h4>

If **load, display, click, close**, etc. all processes are successful，Please step to [1.4 Test the next ad source](#1.4).

<h4>1.3.2 “fail”</h4>

Observe the output of the **"msg"** field, or the full error message obtained by **AdError.printStackTrace()** in the callback, The format looks like this:

```java
"code[ " + code + " ]
,desc[ " + desc + " ]
,platformCode[ " + platformCode + " ]
,platformMSG[ " + platformMSG + " ]"

code：TopOnSDK inner error code
desc：TopOnSDK inner error msg
platformCode：Third-party ad platform error code(need to check when the ad is not filled)
platformMsg：Third-party ad platform error message(need to check when the ad is not filled)
```

1. Please first query according to **code**, Please jump to  [2. ErrorCode](#2), query TopOnSDK error code and description.
2. If **code==4001**, please go to the corresponding third-party advertising platform to query **platformCode**, **platformMsg**.

<h3 id="1.4"> 1.4 Test the next ad source</h3>

1. Take the **DemoBlack_RV** ad placement as an example. When **the loading, display, click, and close** of the **ironSource** are successful, the ad source is test passed.
2. Then you need to test whether the **Applovin**, which is ranked second, is successful. You can click **the status switch** of the ad source in the TopOn background to close the status of the first **ironSource**. Like below:

![](Close_the_status_of_ad_source.png)

The closed ad source will automatically move down. **Changes take effect after about 1 minutes(try after clear the data of **, **The ad source for the ad placement at this time is as follows: **

​	1) **Applovin (At this point, the TopOn SDK will first request this ad source.)**
​	2) Inmobi
​	3) Admob
​	4) Facebook

There are 4 active ad sources in **DemoBlack_RV**.

3. **Repeat <a href="#1.3 Start a request, observe logcat and troubleshoot">1.3 Start a request, observe logcat and troubleshoot</a>, close the status of the ad source without problems and test Applovin.**

4. **Repeat <a href="#1.3 Start a request, observe logcat and troubleshoot">1.3 Start a request, observe logcat and troubleshoot</a>, close the status of the ad source without problems and test Inmobi.**

5. **Repeat <a href="#1.3 Start a request, observe logcat and troubleshoot">1.3 Start a request, observe logcat and troubleshoot</a>, close the status of the ad source without problems and test all remaining ad sources in DemoBlack_RV.**

After all the ad sources are verified successful, this ad placement has no exceptions.

<h3>1.5 Test the next ad placement</h3>

1. After Testing one ad placement, you need to test other ad placement (**if your app has multiple ad placements**). In the TopOn background, you can **click the ad placement button to switch the ad placement**, like this:

![](Switch_to_other_ad_placement.png)

2. **Repeat steps 1.2~1.4** until all ad placements in your app have been verified.



<h2 id='2'>2. ErrorCode</h2>

#### The description of error code

| ErrorCode | Description                                                  |
| --------- | ------------------------------------------------------------ |
| 9999      | Generally, there is an error in the network request, check if the network status is normal. |
| 9990      | The status returned by the Http interface request is incorrect. You need to contact the TopOn team to check the error message. |
| 9991      | The service code returned by the interface request is incorrect. You need to contact TopOn team to check the error message. |
| 9992      | The GDPR level is set too low, check if the FORBIDDEN level is set manually. |
| 2001      | The ad loads timeout, check if the current test ad source is an overseas platform, and whether the mobile phone network has overturned the wall. |
| 2002      | The TopOn SDK package is incompletely imported, and the Adapter package of the third-party network is missing. Please confirmed whether the SDK package of the third-party network is imported according to the guidelines. |
| 2003      | The current ad placement has reached the maximum number of impressions. You need to confirm that TopOn’s background configuration limits the number of impressions for this ad placement. |
| 2004      | The current ad placement is in the non-display time period. You need to confirm whether the background configuration of TopOn limits the display interval of the ad placement. |
| 2005      | The ad placement is in the loading stage, and the same ad placement cannot be loaded twice at the same time. |
| 2006      | Check whether the imported third-party SDK package is complete. Then, check whether the imported version matches the version specified on GitHub. Otherwise, you need to complete the third-party SDK package. |
| 3001      | Load Strategy gets an error, first check if the network is normal, and then check your appid, appkey, placementid. |
| 3002      | Appid, appkey, placementid cannot be null or empty.          |
| 4001      | No Fill，get all error information via AdError.printStackTrace() |
| 4002      | The context has been destroyed, you need to re-create the corresponding ad type object and re-initiate the ad loading. |
| 4003      | The status of this ad placement has been closed. You need to contact TopOn team to check the status of the ad placement. |
| 4004      | The ad placement does not have Ad Source information configured in the TopOn background. You need to configure Ad source information in the TopOn background . |



<h2 id='3'>3. FAQ</h2>

**Q**：How should I query the problem of no fill by the error code and error information of the platform that appears when requesting an advertisement?<br>
**A**：If the error code of TopOn is 4001, the error code of the platform needs to go to the corresponding third-party platform website to view the error code information (you can check the third-party advertising platform through the networkType field in the log)


|广告平台 | NetworkType | platformCode | platformMsg | 解决方法 |
| ------- | ----------- | ----------- | ----------- | ------- |
| Facebook | 1          | 1001        | No Fill     | There will be filled after going online, please add test device to the Facebook background for testing. |
| Admob    | 2          |             | 1           | The ad placement is abnormal, please check the Admob background and TopOn background|
|          |            |             | 2           | The network is abnormal, please check if the network is available and has turned over the wall|
|          |            |             | 3           | There will be filled after going online, please test with test id，[Admob test id](https://developers.google.com/admob/android/test-ads)。|
| toutiao  | 15         | 40025       | Unknown error or rendering error       | 请Please go to your toutiao background to download the corresponding toutiao SDK aar package and replace open\_ad\_sdk\_\*.aar|


<br>
<br>

**Q**：After adjusts the AdSource list of the ad placement on the TopOn background, how long will it take effect？<br>
**A**：The adjustment is expected to take effect in about 1 minutes. After 1 minutes, you need to kill the application and clear the data of application, then restart, and the SDK will get the latest strategy.
<br>
<br>

**Q**：Why the newly created Admob ads have not been filled？<br>
**A**：Because Admob has restrictions on new traffic, the filling will be low at the beginning. If you need to test whether the integration is correct, you can configure Admob's test ad id to verify:[Admob test id](https://developers.google.com/admob/android/test-ads).
<br>
<br>

**Q**：Toutiao ads return error code: **40025**, hint: **Unknown error or rendering error**<br>
**A**：The toutiao SDK downloaded by our GitHub: open\_ad\_sdk\_\*.aar does not match your ad placement usage. You must download the corresponding toutiao SDK aar package from your toutiao background and replace open\_ad_sdk\_\*.aar this package.
<br>
<br>

**Q**：Mintegral ad can be displayed normally in the debug mode of the application. If the proguard configuration is already configured, why can't it be displayed with the release package?<br>
**A**：First confirm whether the packaging is enabled for resource optimization.：**shrinkResource=true**,this requires the following actions：

```java
Add a keep.xml under the res/raw, then the content is：
<?xml version="1.0" encoding="utf-8"?>
<resources xmlns:tools="http://schemas.android.com/tools"
    tools:shrinkMode="safe" />
```

If you have add some third-party resource optimization frameworks, you need to whitelist the following resource paths:

```java
R.string.mintegral_*
R.drawable.mintegral_*
R.layout.mintegral_*
R.id.mintegral_*
```

<br>
<br>

**问**：How to resolve the conflict with Firebase after importing TopOn SDK？<br>
**答**：[TopOn and Firebase integration conflict resolution](TopOn_and_Firebase_integration_conflict_resolution.md)
<br>
<br>

**Q**：On some devices, the apk of the toutiao ad download can't be installed. How to solve it?<br>
**A**：In the anythink_bk_tt_file_path file in the toutiao directory we provide,add：<br>
```
<root-path name="root" path="" />
```

<br>
<br>

**Q**：The toutiao's reward video ad reported a null pointer exception during playback. How to solve it?<br>
**A**：If multiple dex will be played when packaging, please make sure that the related items of the toutiao are entered into classes.dex.。

<br>
<br>

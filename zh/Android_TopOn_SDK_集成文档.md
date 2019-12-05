# TopOn Android SDK

[1. 简介](#1)<br>
[2. Topon SDK的集成说明](#2)<br>
[3. SDK初始化](#3)<br>
[4. Native广告集成说明](#4)<br>
[5. NativeBanner广告集成说明](#5)<br>
[6. NativeSplash广告集成说明](#6)<br>
[7. RewardedVideo广告集成说明](#7)<br>
[8. Interstitial广告集成说明](#8)<br>
[9. Banner广告集成说明](#9)<br>
[10. Splash广告集成说明](#10)<br>
[11. GDPR说明](#11)<br>
[12. HeadBidding说明](#12)<br>
[13. 集成测试、错误码、FAQ](#13)<br>



<h2 id='1'>1. TopOn 简介</h2>

本文档主要介绍在Android项目中如何集成TopOn的SDK，TopOn的SDK的提供的集成方式均以AAR，Jar包的形式提供集成，包括第三方广告平台SDK提供，均是AAR，Jar包的形式。**在集成过程需要注意不要漏掉第三方SDK包的集成，否则会出现集成后获取不了广告资源。**<br>



<h2 id='2'>2. TopOn SDK的集成说明 </h2>

**在SDK的集成测试阶段，请打开TopOn SDK的日志功能，方便验证广告回调状态和排查错误。**<br>
在初始化SDK之前加上：**ATSDK.setNetworkLogDebug(true);**<br>

**SDK集成过程中遇到的问题**以及**集成完成后广告的验证流程**，请参考 [13. 集成测试、错误码、FAQ](#13)


<h3>2.1 SDK下载地址</h3>

[TopOn SDK下载地址](http://sdk-release.toponad.com/Android/5.2.0/TopOn_SDK_v5.2.0_20191203_Release.zip)

TopOn SDK的使用Demo可查看：[TopOn SDK Demo&SDK](https://github.com/uparputeam/uparpu_demo_android)

<h3>2.2 SDK包的目录说明</h3>

**libs：** 里面存放TopOn的基础SDK包，**按照需要集成的广告形式来引入** <br>
**network_sdk：** 里面存放全部聚合平台的SDK包，**按照需要聚合的广告平台来引入**

<h4>2.2.1 libs目录的SDK包集成说明</h4>

| SDK包 | 说明 | 是否必须|
| --- | --- |---|
|anythink_core.aar|TopOn的基础包，必须导入| **是**|
|anythink_native.aar|TopOn的Native广告集成包| 否|
|anythink_banner.aar|TopOn的Banner广告集成包|否|
|anythink_interstitial.aar|TopOn的插屏广告集成包|否|
|anythink_rewardvideo.aar|TopOn的激励视频广告集成包|否|
|anythink_splash.aar|TopOn的开屏广告集成包|否|
|anythink_headbiding.aar|TopOn的Headbidding插件包，是聚合第三方SDK的头部竞价的包，具体说明请查看[HeadBidding说明](#12)|否|

<h4>2.2.2 network_sdk目录的说明</h4>

**中国地区** （应用是发布中国大陆市场使用的广告平台）

| 目录            | 说明                              |      广告形式支持   |
| --------------- | -------------------------------  | ------------------|
| baidu           | 百度SDK相关文件（中国区）         | 原生广告，横幅广告，插屏广告，激励视频，开屏广告  |
| gdt             | 广点通SDK相关文件（中国区）       | 原生广告，横幅广告，插屏广告，激励视频，开屏广告|
| ks              | 快手SDK相关文件（中国区）         |插屏广告，激励视频 |
| ksyun           | 金山云SDK相关文件（中国区）       | 激励视频 |
| luomi           | 洛米SDK相关文件（中国区）         | 原生广告  |
| mintegral_china | Mintegral SDK相关文件（中国区）   |原生广告，横幅广告，插屏广告，激励视频 |
| oneway          | Oneway SDK相关文件（中国区）      |插屏广告，激励视频 |
| uniplay         | Uniplay SDK相关文件（中国区）     |横幅广告，插屏广告，激励视频 |
| sigmob          | Sigmob SDK相关文件（中国区）      | 插屏广告，激励视频，开屏广告 |
| toutiao         | 穿山甲SDK相关文件                 |原生广告，横幅广告，插屏广告，激励视频，开屏广告|

**非中国地区** （应用是发布Google Play使用的广告平台）

| 目录            | 说明                              |      广告形式支持   |
| --------------- | -------------------------------  | ------------------|
| adcolony        | AdColony SDK相关文件              | 插屏广告，激励视频|
| admob           | Admob SDK相关文件                 | 原生广告，横幅广告，插屏广告，激励视频|
| applovin        | Applovin SDK相关文件              | 原生广告，横幅广告，插屏广告，激励视频 |
| appnext         | Appnext SDK相关文件               | 原生广告，横幅广告，插屏广告，激励视频 |
| chartboost      | Chartboost SDK相关文件            | 插屏广告，激励视频 |
| facebook        | Facebook SDK相关文件              |原生广告，横幅广告，插屏广告，激励视频 |
| flurry          | Flurry SDK相关文件                |原生广告，横幅广告，插屏广告，激励视频 |
| inmobi          | Inmobi SDK相关文件                |原生广告，横幅广告，插屏广告，激励视频 |
| ironsource      | Ironsource SDK相关文件            | 插屏广告，激励视频 |
| maio            | Maio SDK相关文件                  |插屏广告，激励视频 |
| mintegral_international | Mintegral SDK相关文件（非中国区） |原生广告，横幅广告，插屏广告，激励视频 |
| mopub           | Mopub SDK相关文件                 |原生广告，横幅广告，插屏广告，激励视频 |
| nend            | Nend SDK相关文件                  |原生广告，横幅广告，插屏广告，激励视频 |
| startapp        | StartApp SDK相关文件              |插屏广告，激励视频 |
| superawesome    | SuperAwesome SDK相关文件          |激励视频 |
| tapjoy          | Tapjoy SDK相关文件                |插屏广告，激励视频 |
| unityads        | UnityAds SDK相关文件              |插屏广告，激励视频 |
| vungle          | Vungle SDK相关文件                | 插屏广告，激励视频 |

可集成需要聚合的平台SDK包到Android项目下，每个聚合的第三方SDK的文件夹内容说明：

| 目录/文件| 说明|
|---|---|
|libs|聚合第三方Network SDK必须集成的包目录(将里面的aar和jar放置开发工程的libs目录下)|
|extra|聚合第三方Network SDK必须的插件包目录(将里面的aar和jar放置开发工程的libs目录下)，如果工程已存在该插件包则不用再重复集成（不存在该文件夹则不需要导入）|
|AndroidManifest.xml|需将AndroidManifest里的组件信息添加到开发工程的AndroidManifest（文件不存在则不需要配置）|
|proguard-android.txt|聚合第三方Network SDK的混淆配置（文件不存在则不需要配置）|
|res|聚合第三方Network SDK必须导入的资源（有则将里面所有的文件夹复制到工程的res目录下，没有则不需要导入）|

**在Gradle的文件中需要补上android-v7包的配置：**

```java
dependencies {
    implementation 'com.android.support:appcompat-v7:28.0.0'
}
```

<h4>2.2.3 AndroidManifest配置说明 </h4>

```java
 <application
        ...
        <!--针对Android高版本下可以使用Http请求的配置-->
        android:usesCleartextTraffic="true">
        ....
        <!--针对Android 9.0以上的配置，用于适配9.0的网络请求-->
        <uses-library android:name="org.apache.http.legacy" android:required="false"/>
        ....
<application>
```

聚合第三方Network的AndroidManifest配置，请参考**network_sdk**目录下该平台文件夹下的AndroidManifest.xml

<h4>2.2.4 混淆配置</h4>

```java
-dontwarn com.anythink.**
-keep public class com.anythink.network.**
-keepclassmembers class com.anythink.network.** {
   public *;
}
```

聚合第三方Network的混淆配置，请参考**network_sdk**目录下该平台文件夹下的proguard-android.txt

<h4>2.2.5 广告测试说明</h4>

**在SDK的集成测试阶段，请打开TopOn SDK的日志功能，方便验证广告回调状态和排查错误。**<br>
在初始化SDK之前加上：**ATSDK.setNetworkLogDebug(true);**<br>

**SDK集成过程中遇到的问题**以及**集成完成后广告的验证流程**，请参考 [13. 集成测试、错误码、FAQ](#13)

**以下提供TopOn测试AppId信息和PlacementId信息，可通过以下的测试id先验证集成是否正常：**

| AppId | AppKey |
|----|----|
| a5aa1f9deda26d| 4f7b9ac17decb9babec83aac078742c7|


|广告平台| 原生广告位 |横幅广告位 | 插屏广告位| 激励视频广告位| 开屏广告位|
|----|----|----|----|----|----|
| AdColony        |-|-|b5bbdc92f49ce7|b5b449faa95391|-|
| Admob           | b5aa1fa501d9f6|b5baca41a2536f|b5baca54674522|b5b449f025ec7c|-|
| Applovin        |b5aa1fa7956158|b5bbdc59f88520|b5bbdc6fc65dd1|b5b449f20155a7|-|
| Appnext         | b5bc7f369610cd | b5bc7f3b034a2b |b5bc7f3ec5b952|b5bc7f38df0a73|-|
| 百度           |b5d148f9f2e47d|b5c0508c4c073f|b5c0508e2c84d4|b5c2c800fb3a52|b5c05090192a58|
| Chartboost      |-|- |b5bbdc8a68d901|b5b449f548e010|-|
| Facebook        | b5aa1fa4165ea3 |b5bbdc51a35e29|b5bbdc69a21187|b5b449eefcab50|-|
| Flurry          | b5aa1fa6c00d2f|b5bbdc584f1368|b5bbdc6d5e1362|b5b449f15d04ca|-|
| 广点通             |b5ab8590d44f82|b5baca43951901|b5baca561bc100|b5c2c880cb9d52|b5bea7bfd93f01|
| Inmobi          | b5aa1fa5d10190 |b5bbdc535a9d1a|b5bbdc6b63458f|b5b449f0c6b84a|-|
| Ironsource      |-|-|b5bbdc8e9ef916|b5b449f75948c5|-|
| 快手              |-|-|b5d6745b8133f2|b5d67459a3e535|-|
| 金山云           |-|-|-|b5bbd61d0aa571|-|
| 洛米           |b5d1ef61e17981|-|-|-|-|
| Maio            |-|-|b5cb961d9d3414|b5cb961e495a18|-|
| Mintegral |b5aa1fa85b86d5 |b5dd388839bf5e|b5bbdc725768fa|b5b449f2f58cd7|-|
| Mopub           | b5ab858fb0175f|b5bbdc5c857b2f|b5bbdc86dd8e3b|b5b449f4927359|-|
| Nend            | b5cb95ead9e60a |b5cb95ed13203c |b5cb95eeb7e908|b5cb95efa0c793|-|
| Oneway          |-|-|b5baca5e3d2b29|b5badf5b390201|-|
| StartApp        |-|-|b5d5e641d9c30a|b5cff0d063ac32|-|
| Superawesome    |-|-|- |b5cff0d2157805|-|
| Tapjoy          |-|-|b5bbdc8b6e9829|b5b449f66ceaf5|-|
| 穿山甲         |b5c2c97629da0d|b5baca45138428|b5baca585a8fef|b5b728e7a08cd4|b5bea7c1b653ef|
| Uniplay（玩转互联）         |-| b5baca4aebcb93 |b5baca5d16c597|b5badef36435e7|-|
| Unityads        |-|-|b5c21a303c25e0|b5b449f809139c|-|
| Vungle          |-|-|b5bbdc9182f9f2|b5b449f97e0b5f|-|
| Sigmob          |-|-|b5d7614ab30695|b5d7228c6c5d6a|b5d76150bab3ad|



<h2 id='3'>3. SDK初始化说明 </h2>

**注意：** 在任何的一个广告位加载广告执行之前，都必须先执行SDK的初始化方法，否则会出现无法加载广告的情况。

<h3>3.1 API说明</h3>

**ATSDK**

| API | 参数 | 说明|
| --- | --- |---|
| init | (Context context, String appId, String appKey) | TopOnSDK的初始化方法，其中appId和appKey需要在TopOn后台建立应用之后获取。<br>**该方法建议在Application onCreate的时候初始化**|
| setChannel | (String channel) | 设置渠道信息，用于TopOn后台区分广告数据，只允许设置字符的规则：**[A-Za-z0-9_]**|
| setSubChannel | (String subChannel) | 设置子渠道信息，只允许设置字符的规则：**[A-Za-z0-9_]** |
| initCustomMap | (Map<String, String> customMap)| 自定义key-value，可用于匹配后端下发的广告列表信息|
| setGDPRUploadDataLevel|(Context context, int level) |设置GDPR下数据的上报等级，level主要分以下三个等级：<br> **ATSDK.PERSONALIZED**：上报数据包含设备参数<br> **ATSDK.NONPERSONALIZED**：上报数据不包含设备参数<br> **ATSDK.FORBIDDEN**：不做任何上报，停止广告请求|
|getGDPRDataLevel|(Context context)| 获取当前的上报等级|
|isEUTraffic|(Context context)| 判断是否是欧盟地区|
|showGdprAuth|(Activity activity)|展示GDPR授权页面的Activity|
|setNetworkLogDebug|(boolean debug)|设置是否有Debug日志输出。<br>**建议在测试阶段把它打开，方便验证广告回调状态**|


<h3>3.2 示例代码</h3>

```java
ATSDK.init(getApplicationContext(), appid, appKey);
```



<h2 id='4'>4. Native广告集成说明</h2>

<h3>4.1 Native广告介绍</h3>

1.Native广告的集成，TopOn是以提供源素材的形式来提供给开发者进行渲染（除了部分广告平台会直接提供大图和视频的mediaview）；<br>
2.获取到Native广告只能同一时间在同一个地方展示，否则会导致最开始展示的Native广告无法正常展示或者点击；<br>
3.集成Native广告的过程中，部分通过api获取的素材是空的，这时可以不用展示该素材信息。


<h3>4.2 Native广告API说明</h3>

**ATNative:** Native广告加载的类

| API | 参数 | 说明|
| --- | --- | ---|
|ATNative|Context context, String placementId, ATNativeNetworkListener listener | 原生广告初始化方法，其中placementId是通过TopOn后台创建广告位获取的|
| makeAdRequest|-|发起Native广告请求|
| getNativeAd |-|获取已经加载完成的广告**（建议获取之后都要进行非null判断，因为可能会有部分情况出现null）**|


**ATNativeNetworkListener:** 广告加载的监听器

| 方法 | 参数 | 说明|
| --- | --- |---|
| onNativeAdLoaded|-|广告加载成功|
| onNativeAdLoadFail|(AdError error)| 广告加载失败，可通过AdError.printStackTrace()获取全部错误信息|

**NativeAd:** 通过**getNativeAd**获取到的广告对象，具体方法描述如下:



| 方法 | 参数 | 说明|
| --- | --- |---|
| setNativeEventListener|(ATNativeEventListener listener) | 设置广告事件监听，其中ATNativeEventListener是广告事件的接口类 |
| renderAdView|(ATNativeAdView view, ATNativeAdRenderer render)|用于广告渲染，其中view必须使用我们提供的ATNativeAdView，ATNativeAdRenderer则需要继承实现相应的接口功能 |
| prepare|(ATNativeAdView view)| 用于配置广告点击事件，在renderAdView方法之后调用（默认全部view可点击，存在广告标识的会有默认展示位置和大小）|
| prepare|(ATNativeAdView view, FrameLayout.LayoutParams layoutParams) |配置广告点击事件，在renderAdView方法之后调用:<br>layoutParams用于配置广告标识的位置和大小（目前仅Facebook，GDT有效，可为空）|
| prepare|(ATNativeAdView view, List<View> clickViewList, FrameLayout.LayoutParams layoutParams) |配置广告点击事件，在renderAdView方法之后调用:<br>clickViewList用于配置可点击的View <br>layoutParams用于配置广告标识的位置和大小（目前仅Facebook，GDT有效，可为空）|
| clear|(ATNativeAdView view)|移除广告对view的绑定|
|onResume|-|在Activity的onResume时调用（主要针对部分广告平台的视频广告）|
|onPause|-|在Activity的onPause时调用（主要针对部分广告平台的视频广告）|

**ATNativeEventListener:** 广告事件监听接口类

| 方法 | 参数 | 说明|
| --- | --- |---|
| onAdImpressed(ATNativeAdView view, ATAdInfo entity) | 广告展示回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|
| onAdClicked(ATNativeAdView view, ATAdInfo entity)| 广告点击回调|
| onAdVideoStart(ATNativeAdView view)| 广告视频播放开始（仅部分广告平台存在）|
| onAdVideoEnd(ATNativeAdView view) | 广告视频播放结束（仅部分广告平台存在）|
| onAdVideoProgress(ATNativeAdView view, int progress) | 广告视频播放进度（仅部分广告平台存在）|

**ATNativeAdRenderer:** 用于实现广告渲染的接口类

| 方法 | 参数 | 说明|
| --- | --- |---|
| createView|（Context contex, int networkType）|用于创建自定义的Native广告布局的View|
| renderAdView|(View view, CustomNativeAd customNativeAd)|用于实现广告内容渲染的方法，其中customNativeAd是广告素材的对象，可提供素材进行渲染|

**CustomNativeAd:** Native的广告素材对象（以下素材都可能会出现为null的情况，因为部分广告平台不一定存在所有素材信息）

| 方法 | 参数 | 说明|
| --- | --- |---|
| getIconImageUrl |-| 获取广告图标的url|
| getAdChoiceIconUrl|-| 获取广告商的标识的图标url |
| getAdIconView|- |获取广告IconView**（仅Facebook可用，且必须使用）** |
| getAdMediaView|(ViewGroup view, int width)| 获取广告大图的渲染容器（仅部分广告平台会存在），有可能是静态图和视频。参数描述：view：广告父容器, width: MediaView的宽度配置 (目前仅**Inmobi**需要这两个参数，其他可以传null) |
| getMainImageUrl |-| 获取大图Url|
| getTitle |-| 获取广告标题|
| getDescriptionText|-| 获取广告描述|
| getCallToActionText |-| 获取广告CTA按钮文字|
| getAdFrom |-| 获取广告来源（Nend广告平台必须渲染该信息）|
| getAdLogo |-|获取AdLogo的Bitmap（目前只有穿山甲能获取到）|
| getImageUrlList|-|获取图片的url列表|
|getStarRating|-|获取广告的评分|


<h3>4.3 Native广告示例代码</h3>

```java
if (atNativeAdView == null) {
	atNativeAdView = new ATNativeAdView(this);
}

ATNative atNatives = new ATNative(this, placementId, new ATNativeNetworkListener() {
                @Override
                public void onNativeAdLoaded() {
                    NativeAd nativeAd = atNatives[mCurrentSelectIndex].getNativeAd();
                		if (nativeAd != null) {
                    	nativeAd.renderAdView(ATNativeAdView, new NativeAdRender());
                       nativeAd.prepare(ATNativeAdView);
                		} else {
                    

                		}
                }

                @Override
                public void onNativeAdLoadFail(AdError adError) {
                  

                }
            });
atNatives.makeAdRequest();
```

```java

public class NativeAdRender implements ATNativeAdRenderer<CustomNativeAd> {

    Context mContext;
    List<View> mClickView = new ArrayList<>();

    public NativeAdRender(Context context) {
        mContext = context;
    }

    View mDevelopView;

    int mNetworkType;

    @Override
    public View createView(Context context, int networkType) {
        if (mDevelopView == null) {
            mDevelopView = LayoutInflater.from(context).inflate(R.layout.native_ad_item, null);
        }
        mNetworkType = networkType;
        if(mDevelopView.getParent() != null){
            ((ViewGroup)mDevelopView.getParent()).removeView(mDevelopView);
        }
        return mDevelopView;
    }

    @Override
    public void renderAdView(View view, CustomNativeAd ad) {
        TextView titleView = (TextView) view.findViewById(R.id.native_ad_title);
        TextView descView = (TextView) view.findViewById(R.id.native_ad_desc);
        TextView ctaView = (TextView) view.findViewById(R.id.native_ad_install_btn);
        TextView adFromView = (TextView) view.findViewById(R.id.native_ad_from);

        FrameLayout contentArea = (FrameLayout) view.findViewById(R.id.native_ad_content_image_area);
        View mediaView = ad.getAdMediaView(contentArea, contentArea.getWidth());
        View adiconView = ad.getAdIconView();

        FrameLayout iconArea = (FrameLayout) view.findViewById(R.id.native_ad_image);
        final SimpleDraweeView iconView = new SimpleDraweeView(mContext);
        iconView.setImageDrawable(null);
        if (adiconView == null) {
            iconArea.addView(iconView);
            iconView.setImageURI(ad.getIconImageUrl());
        } else {
            iconArea.addView(adiconView);
        }


        final SimpleDraweeView logoView = (SimpleDraweeView) view.findViewById(R.id.native_ad_logo);
        if (!TextUtils.isEmpty(ad.getAdChoiceIconUrl())) {
            logoView.setVisibility(View.VISIBLE);
            logoView.setImageURI(ad.getAdChoiceIconUrl());
        } else {
            logoView.setVisibility(View.GONE);
        }


        contentArea.removeAllViews();
        if (mediaView != null) {
            int height = contentArea.getWidth() == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : contentArea.getWidth() * 3 / 4;
            contentArea.addView(mediaView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        } else {

            final SimpleDraweeView imageView = new SimpleDraweeView(mContext);

            imageView.setImageURI(ad.getMainImageUrl());
            ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            contentArea.addView(imageView, params);
        }

        titleView.setText(ad.getTitle());
        descView.setText(ad.getDescriptionText());
        ctaView.setText(ad.getCallToActionText());
        if (!TextUtils.isEmpty(ad.getAdFrom())) {
            adFromView.setText(ad.getAdFrom() != null ? ad.getAdFrom() : "");
            adFromView.setVisibility(View.VISIBLE);
        } else {
            adFromView.setVisibility(View.GONE);
        }

        mClickView.clear();

        mClickView.add(ctaView);

    }

    public List<View> getClickView() {
        return mClickView;
    }
}

```



<h2 id='5'>5. NativeBanner广告集成说明</h2>

<h3>5.1 NativeBanner广告介绍</h3>

1.NativeBanner广告以View的形式提供广告的API，目前只支持640x150，320x50，Auto，其中Auto会自适应设置的NativeBanner View的宽高；<br>
2.NativeBanner需要使用Native广告的广告位来加载广告

<h3>5.2 NativeBanner广告API说明</h3>

**ATNativeBannerView:** NativeBanner广告的加载类，同时也是一个显示NativeBanner广告的View

| 方法            | 参数                                   | 说明                                                         |
| --------------- | -------------------------------------- | ------------------------------------------------------------ |
| setUnitId       | (String placementId)                   | 设置广告位id（必须设置Native广告的广告位id）                 |
| setAdListener   | (ATNaitveBannerListener listener)  | 设置NativeBanner广告监听回调，其中ATNaitveBannerListener是需要实现广告事件回调的接口类 |
| loadAd          | (Map<String, String> customRequestMap) | 其中customRequestMap设置为null即可，已经不再使用传入的参数   |
| setBannerConfig | (ATNativeBannerConfig config)      | 设置NativeBanner的本地配置，例如：字体颜色和字体大小         |

**ATNaitveBannerListener:** NativeBanner广告的事件回调监听

| 方法              | 参数                  | 说明                                                         |
| ----------------- | --------------------- | ------------------------------------------------------------ |
| onAdLoaded        | -                     | 广告加载成功回调                                             |
| onAdError         | (String error)        | 广告加载失败回调                                             |
| onAdClick         | (ATAdInfo entity) | 广告点击，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息 |
| onAdShow          | (ATAdInfo entity) | 广告展示回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息 |
| onAdClose         | -                     | 广告关闭回调（部分广告平台有该回调），可在此处执行移除view的操作 |
| onAutoRefresh     | (ATAdInfo entity) | 广告刷新回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息 |
| onAutoRefreshFail | (String error)        | 广告刷新失败回调                                             |

**ATNativeBannerConfig:** NativeBanner的本地设置项

| 设置项         | 类型                   | 说明                                                         |
| -------------- | ---------------------- | ------------------------------------------------------------ |
| titleColor     | int                    | 广告标题颜色                                                 |
| descColor      | int                    | 广告描述颜色                                                 |
| ctaColor       | int                    | CTA按钮文字颜色                                              |
| ctaBgColor     | int                    | CTA按钮背景颜色                                              |
| backgroupResId | int                    | 背景图片的资源id，默认没有                                   |
| isCloseBtnShow | boolean                | 是否展示关闭按钮                                             |
| isCtaBtnShow   | boolean                | 是否展示CTA按钮（注意海外平台的尽可能展示，否则可能会出现展示无效） |
| refreshTime    | long                   | 刷新时间（单位：毫秒）                                       |
| bannerSize     | ATNaitveBannerSize | NativeBanner的Size枚举：<br>**ATNaitveBannerSize.BANNER\_SIZE\_AUTO**:自适应NativeBanner View的宽高<br>**ATNaitveBannerSize.BANNER\_SIZE\_640x150**: NativeBanner 640x150的比例宽高<br>**ATNaitveBannerSize.BANNER\_SIZE\_320x50**: NativeBanner 320x50的比例宽高 |

<h3>5.3 NativeBanner广告示例代码</h3>

```java
ATBannerView  mBannerView = new ATBannerView(BannerAdActivity.this);
mBannerView.setUnitId(placementId);
mBannerView.loadAd();
frameLayout.addView(mBannerView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtil.dip2px(getApplicationContext(), 50)));
mBannerView.setBannerAdListener(new ATBannerListener() {
	@Override
	public void onBannerLoaded() { 
	}

	@Override
	public void onBannerFailed(AdError adError) {
	}

	@Override
	public void onBannerClicked(ATAdInfo entity) {
	}

	@Override
	public void onBannerShow(ATAdInfo entity) {
	}

	@Override
	public void onBannerClose() {
	}

	@Override
	public void onBannerAutoRefreshed(ATAdInfo entity) {
	}

	@Override
	public void onBannerAutoRefreshFail(AdError adError) {
	}
});
```

<h2 id='6'>6. NativeSplash广告集成说明</h2>

<h3>6.1 NativeSplash广告介绍</h3>

1.NativeSplash的广告是依赖开发者创建自己的Activity来实现的，只将需要展示广告的容器ViewGroup传给NativeSplash的api使用即可，NativeSplash会自动给ViewGroup渲染广告；<br>
2.NativeSplash广告的展示区域最好是屏幕高度的**75%以上**且**跳过秒数超过3秒以上**，否则可能会出现广告展示无效；<br>
3.NativeSplash**可支持竖屏和横屏**。

<h3>6.2 NativeSplash广告API说明</h3>

**ATNativeSplash:** NativeSplash广告的加载类

| 方法               | 参数                                                         | 说明                                                         |
| ------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ATNativeSplash | (Activity activity, ViewGroup container, View skipView, String placementId ATNativeSplashListener listener) | NativeSplash的初始化方法，以下是参数说明：<br> **activity**:展示广告的Activity<br> **container**:展示广告容器<br> **skipView**:容器skipView(事件自动绑定在SDK,开发者不能绑定事件的点击事件)<br> **placementId**:广告位id <br>**listener**:广告事件监听器 |
| ATNativeSplash | (Activity activity, ViewGroup container, View skipView, String placementId, long requestTimeOut, long fetchDelay, ATNativeSplashListener listener) | NativeSplash的初始化方法，以下是参数说明：<br> **activity**:展示广告的Activity<br> **container**:展示广告容器<br> **skipView**:容器skipView(事件自动绑定在SDK,开发者不能绑定事件的点击事件)<br> **placementId**:广告位id <br>**requestTimeOut**:请求超时时间<br> **fetchDelay**:广告展示的倒计时总时长<br> **listener**:广告事件监听器 |

**ATNativeSplashListener:** NativeSplash广告的事件回调类

| 方法         | 参数                        | 说明                                                         |
| ------------ | --------------------------- | ------------------------------------------------------------ |
| onAdLoaded   | -                           | 广告加载成功回调                                             |
| onNoAdError  | (AdError error)             | 广告加载失败回调，可通过AdError.printStackTrace()获取全部错误信息 |
| onAdShow     | (ATAdInfo entity)       | 广告展示回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息 |
| onAdClick    | (ATAdInfo entity)       | 广告点击回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息 |
| onAdSkip     | -                           | Skip按钮点击回调                                             |
| onAdTimeOver | -                           | 广告的倒计时结束，可在这里关闭NativeSplash广告的Activity     |
| onAdTick     | （long millisUtilFinished） | 广告的倒计时回调，用于倒计时秒数的刷新，返回单位：毫秒       |

<h3>6.3 NativeSplash广告示例代码</h3>

```java
public *** extends Activity {
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_ad_show);

        FrameLayout container = findViewById(R.id.splash_ad_container);

        ATNativeSplash splash = new ATNativeSplash(this, container, null, placementId, new ATNativeSplashListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onNoAdError(String msg) {
            }

            @Override
            public void onAdShow(ATAdInfo entity) {
            }

            @Override
            public void onAdClick(ATAdInfo entity) {
            }

            @Override
            public void onAdSkip() {
            }

            @Override
            public void onAdTimeOver() {
            }

            @Override
            public void onAdTick(long millisUtilFinished) {
            }
        });
	}
}
```



<h2 id='7'>7. RewardedVideo广告集成说明</h2>

<h3>7.1 RewardedVideo广告介绍</h3>
1.激励视频的激励下发是在视频广告关闭的事件回调中下发Boolean字段，告诉开发者是否可以下发激励给用户的。<br>
2.目前激励视频不支持S2S的激励下发机制


<h3>7.2 RewardedVideo广告API说明</h3>

**ATRewardVideoAd:** RewardedVideo广告的加载类

| 方法 | 参数 | 说明|
| --- | --- |---|
|ATRewardVideoAd|(Context context, String placementId)|RewardedVideo广告的初始化方法|
|load|-|发起广告加载|
|setAdListener|(ATRewardVideoListener listener)| 设置RewardedVideo广告监听回调，其中ATRewardVideoListener是需要实现广告事件回调的接口类|
|isAdReady|-|判断当前RewardedVideo是否存在可展示的广告|
|show|-|展示RewardedVideo的广告|
|setUserData|(String userId, String customData)|设置用户的信息，主要用于激励下发|

**ATRewardVideoListener:** RewardedVideo广告的事件回调监听:

| 方法 | 参数 | 说明|
| --- | --- |--- |
| onRewardedVideoAdLoaded|-|广告加载成功回调|
| onRewardedVideoAdFailed|(AdError error)| 广告加载失败回调，可通过AdError.printStackTrace()获取全部错误信息|
| onRewardedVideoAdPlayStart|(ATAdInfo entity)|广告刷新回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|
| onRewardedVideoAdPlayEnd|(ATAdInfo entity)|广告播放结束，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|
| onRewardedVideoAdPlayFailed|(AdError errorCode, ATAdInfo entity)|广告播放失败回调，可通过AdError.printStackTrace()获取全部错误信息，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|
| onReward |(ATAdInfo entity)| 下发激励的时候会回调该接口。其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|
| onRewardedVideoAdClosed|(ATAdInfo entity)| 广告关闭回调。其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|
| onRewardedVideoAdPlayClicked|(ATAdInfo entity)|广告点击，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|


<h3>7.3 RewardedVideo广告示例代码</h3>


```java
ATRewardVideoAd mRewardVideoAd = new ATRewardVideoAd(this,placementId);
mRewardVideoAd.setAdListener(new ATRewardVideoListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
            }

            @Override
            public void onRewardedVideoAdFailed(AdError errorCode) {
            }

            @Override
            public void onRewardedVideoAdPlayStart(ATAdInfo entity) {
            }

            @Override
            public void onRewardedVideoAdPlayEnd(ATAdInfo entity) {
            }

            @Override
            public void onRewardedVideoAdPlayFailed(AdError errorCode, ATAdInfo entity) {
            }

            @Override
            public void onRewardedVideoAdClosed(ATAdInfo entity) {
            }
	    
	    @Override
            public void onReward(ATAdInfo entity) {
            }

            @Override
            public void onRewardedVideoAdPlayClicked(ATAdInfo entity) {
            }
        });

 if(mRewardVideoAd.isAdReady()){
 	mRewardVideoAd.show();
 } else {
 	mRewardVideoAd.load();
 }
```



<h2 id='8'>8. Interstitial广告集成说明</h2>

<h3>8.1 Interstitial广告介绍</h3>
插屏广告是聚合了其他第三方广告平台的图片插屏广告和视频插屏广告，两种广告类型都是可以使用Interstitial的api来实现广告的加载和播放。


<h3>8.2 Interstitial广告API说明</h3>

**ATInterstitial:** Interstitial广告的加载类

| 方法 | 参数 | 说明|
| --- | --- |---|
|ATInterstitial|(Context context, String placementId)|Interstitial广告的初始化方法|
|load|-|发起广告加载|
|setAdListener|(ATInterstitialListener listener)| 设置Interstitial广告监听回调，其中ATInterstitialListener是需要实现广告事件回调的接口类|
|isAdReady|-|判断当前Interstitial是否存在可展示的广告|
|show|-|展示Interstitial的广告|

**ATInterstitialListener:** 是Interstitial广告的事件回调监听:

| 方法 | 参数 | 说明|
| --- | --- |--- |
| onInterstitialAdLoaded|-|广告加载成功回调|
| onInterstitialAdLoadFail|(AdError error)| 广告加载失败回调，可通过AdError.printStackTrace()获取全部错误信息|
| onInterstitialAdClicked|(ATAdInfo entity)|广告点击，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|
| onInterstitialAdShow|(ATAdInfo entity)| 广告展示回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|
| onInterstitialAdClose|(ATAdInfo entity)| 广告关闭回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|
| onInterstitialAdVideoStart|-|视频广告刷新回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|
| onInterstitialAdVideoEnd|-|视频广告播放结束|
| onInterstitialAdVideoError|-|Interstitial视频广告播放失败回调，可通过AdError.printStackTrace()获取全部错误信息|


<h3>8.3 Interstitial广告示例代码</h3>


```java
ATInterstitial mInterstitialAd = new ATInterstitial(this, placementId);
mInterstitialAd.setAdListener(new ATInterstitialListener() {
            @Override
            public void onInterstitialAdLoaded() {               
            }
            @Override
            public void onInterstitialAdLoadFail(AdError adError) {                
            }
            @Override
            public void onInterstitialAdClicked(ATAdInfo entity) {                
            }
            @Override
            public void onInterstitialAdShow(ATAdInfo entity) {                
            }
            @Override
            public void onInterstitialAdClose(ATAdInfo entity) {           
            }
            @Override
            public void onInterstitialAdVideoStart() {         
            }
            @Override
            public void onInterstitialAdVideoEnd() {            
            }
            @Override
            public void onInterstitialAdVideoError(AdError adError) {     
            }
});

 if(mInterstitialAd.isAdReady()){
 	mInterstitialAd.show();
 } else {
 	mInterstitialAd.load();
 }
```



<h2 id='9'>9. Banner广告集成说明</h2>

<h3>9.1 Banner广告介绍</h3>
1.Banner广告以View的形式提供广告的API，View可以自定义宽高，但是部分广告平台不一定能适配自定义的宽高，所以在设置宽高之前要与在TopOn后台配置的宽高比要一致，且需要经过测试看看实际效果来进行调整；<br>
2.Banner提供自动刷新的功能，默认是20秒一次，可通过TopOn后台配置刷新时间或者不刷新（Banner的View必须已经Add进Windows且可视状态才会自动刷新）<br>


<h3>9.2 Banner广告API说明</h3>

**ATBannerView:** Banner广告的加载类，同时也是一个显示Banner广告的View

| 方法 | 参数 | 说明|
| --- | --- |---|
| setUnitId|(String placementId)|设置广告位id（必须设置）|
| setBannerAdListener|(ATBannerListener listener)| 设置Banner广告监听回调，其中ATBannerListener是需要实现广告事件回调的接口类|
| loadAd|-|Banner广告加载|

**ATBannerListener:** 是Banner广告的事件回调监听

| 方法 | 参数 | 说明|
| --- | --- |--- |
| onBannerLoaded|-|广告加载成功回调|
| onBannerFailed|(AdError error)| 广告加载失败回调，可通过AdError.printStackTrace()获取全部错误信息|
| onBannerClicked|-|广告点击|
| onBannerShow|(ATAdInfo entity)| 广告展示回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|
| onBannerClose|-| 广告关闭回调（部分广告平台有该回调），可在此处执行移除view的操作|
| onBannerAutoRefreshed|(ATAdInfo entity)|广告自动刷新回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|
| onBannerAutoRefreshFail |(AdError error)|广告自动刷新失败回调，可通过AdError.printStackTrace()获取全部错误信息|


<h3>9.3 Banner广告示例代码</h3>

```java
ATBannerView  mBannerView = new ATBannerView(BannerAdActivity.this);
mBannerView.setUnitId(unitIds[mCurrentSelectIndex]);
mBannerView.loadAd();
frameLayout.addView(mBannerView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtil.dip2px(getApplicationContext(), 300)));
mBannerView.setBannerAdListener(new ATBannerListener() {
	@Override
	public void onBannerLoaded() { 
	}

	@Override
	public void onBannerFailed(AdError adError) {
	}

	@Override
	public void onBannerClicked(ATAdInfo entity) {
	}

	@Override
	public void onBannerShow(ATAdInfo entity) {
	}

	@Override
	public void onBannerClose() {
	}

	@Override
	public void onBannerAutoRefreshed(ATAdInfo entity) {
	}

	@Override
	public void onBannerAutoRefreshFail(AdError adError) {
	}
});
```



<h2 id='10'>10. Splash广告集成说明</h2>

<h3>10.1 Splash广告介绍</h3>

1.Splash的广告是依赖开发者创建自己的Activity来实现的，只将需要展示广告的容器ViewGroup传给Splash的api使用即可，Splash会自动给ViewGroup渲染广告；<br>
2.Splash广告的展示区域最好是屏幕高度的**75%以上**且**跳过秒数超过3秒以上**，否则可能会出现广告展示无效；<br>
3.确保初始化方法中的skipView是VISIBLE，不能配置是INVISIBLE或者GONE，否则会导致广告平台认为广告曝光失败；<br>
4.Splash**不支持横屏**的应用。


<h3>10.2 Splash广告API说明</h3>

**ATSplashAd:** Splash广告的加载类

| 方法 | 参数 | 说明|
| --- | --- |---|
|ATSplashAd|(Activity activity, ViewGroup container, View skipView, String placementId, ATSplashAdListener listener, long fetchDelay)|Splash的初始化方法，以下是参数说明：<br>**activity**:展示广告的Activity<br>**container**:展示广告容器<br>**skipView**:容器skipView(事件自动绑定在SDK,开发者不能绑定事件的点击事件)<br>**placementId**:广告位id <br>**listener**:广告事件监听器<br>**fetchDelay**:广告展示的倒计时总时长|
|ATSplashAd|(Activity activity, ViewGroup container, View skipView, String placementId, ATSplashAdListener listener)|Splash的初始化方法（里面默认展示时长3秒），以下是参数说明：<br>**activity**:展示广告的Activity<br>**container**:展示广告容器<br>**skipView**:容器skipView(事件自动绑定在SDK,开发者不能绑定事件的点击事件)<br>**placementId**:广告位id <br>**listener**:广告事件监听器<br>|

**ATSplashAdListener:** Splash广告的事件回调类

| 方法 | 参数 | 说明|
| --- | --- |--- |
| onAdLoaded|-|广告加载成功回调|
| onNoAdError|(AdError error)| 广告加载失败回调，可通过AdError.printStackTrace()获取全部错误信息|
| onAdShow|(ATAdInfo entity)|广告展示回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|
| onAdClick|(ATAdInfo entity)| 广告点击回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|
| onAdDismiss|(ATAdInfo entity)|广告关闭回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息|
|onAdTick|(long millisUtilFinished)|广告的倒计时回调，用于倒计时秒数的刷新|


<h3>10.3 Splash广告示例代码</h3>


```java
public *** extends Activity {
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_ad_show);

        FrameLayout container = findViewById(R.id.splash_ad_container);
        TextView skipView = findViewById(R.id.splash_ad_skip);

        ATSplashAd splashAd = new ATSplashAd(this, container, skipView, placementId, new ATSplashAdListener(){
		    @Override
		    public void onAdLoaded() {
		    }

		    @Override
		    public void onNoAdError(AdError adError) {
		        
		    }
		
		    @Override
		    public void onAdShow(ATAdInfo entity) {
		    }
		
		    @Override
		    public void onAdClick(ATAdInfo entity) {
		        
		    }
		
		    @Override
		    public void onAdDismiss(ATAdInfo entity) {
		        finish();
		    }
		
		    @Override
		    public void onAdTick(long millisUtilFinished) {
		        skipView.setText(String.valueOf(millisUtilFinished / 1000) + "| SKIP");
		    }
		        
        }, 5000L);	
		
	}
}
```



<h2 id='11'>11. GDPR说明</h2>

自2018年5月25日起，欧盟《一般数据保护条例》将正式生效。为了保护我们的开发人员和您的用户的利益和隐私，我们更新了我们的<a href="https://www.toponad.com/privacy-policy" target =" _blank">"TopOn privacy Policy"</a>。同时，我们在SDK添加了隐私权限设置。请检查以下配置并完成SDK集成.

1.设置TopOn GDPR等级方法如下**（设置之后，内部根据该等级去设置第三方广告平台GDPR上报等级）**:

```java
int level= { //level有以下选择
  ATSDK.PERSONALIZED //设备数据允许上报 
  ATSDK.NONPERSONALIZED //设备数据不允许上报
  ATSDK.FORBIDDEN //不做任何数据上报（所有广告请求停止）
}
ATSDK.setGDPRUploadDataLevel(context, level);
```

2.可以通过TopOn提供的授权的页面**（授权页会根据用户的选择设置上报等级）**：

```java
ATSDK.showGdprAuth(activity);
```

3.可以通过TopOn去单独设置第三方SDK的GDPR等级:

```java 
Map localMap = new HashMap<>();
//Admob配置
// true:同意上报个人信息， false:不同意上报个人信息
localMap.put(AdmobATConst.LOCATION_MAP_KEY_GDPR, true);   
ATSDK.addNetworkGDPRInfo(this, AdmobATConst.NETWORK_FIRM_ID, localMap);

//Inmob配置
//1:表示在欧盟地区，0:表示不在欧盟地区
localMap.put(InmobiATConst.LOCATION_MAP_KEY_GDPR_SCOPE, "1");//1|0
//true:同意上报个人信息，false:不同意上报个人信息
localMap.put(InmobiATConst.LOCATION_MAP_KEY_GDPR, true);//true | false
ATSDK.addNetworkGDPRInfo(this, InmobiATConst.NETWORK_FIRM_ID, localMap);

//Applovin配置
//true:同意上报个人信息，false:不同意上报个人信息
localMap.put(ApplovinATConst.LOCATION_MAP_KEY_GDPR, true);
ATSDK.addNetworkGDPRInfo(this, ApplovinATConst.NETWORK_FIRM_ID, localMap);

//Mintegral配置
//true:同意上报个人信息，false:不同意上报个人信息
localMap.put(MintegralATConst.LOCATION_MAP_KEY_GDPR, MIntegralConstans.IS_SWITCH_ON);
ATSDK.addNetworkGDPRInfo(this, MintegralATConst.NETWORK_FIRM_ID, localMap);

//Mopub配置
//true:同意上报个人信息，false:不同意上报个人信息
localMap.put(MopubATConst.LOCATION_MAP_KEY_GDPR, true);
ATSDK.addNetworkGDPRInfo(this, MopubATConst.NETWORK_FIRM_ID, localMap);

//Chartboost配置
//false:同意上报个人信息，true:不同意上报个人信息
localMap.put(ChartboostATConst.LOCATION_MAP_KEY_RESTRICTDATACONTROL, true);
ATSDK.addNetworkGDPRInfo(this, ChartboostATConst.NETWORK_FIRM_ID, localMap);

//Ironsource配置
//true:同意上报个人信息，false:不同意上报个人信息
localMap.put(IronsourceATConst.LOCATION_MAP_KEY_CONSENT, true);
ATSDK.addNetworkGDPRInfo(this, IronsourceATConst.NETWORK_FIRM_ID, localMap);

//UnityAds配置
//true:同意上报个人信息，false:不同意上报个人信息
localMap.put(UnityAdsATConst.LOCATION_MAP_KEY_GDPR_CONSENT, true);
ATSDK.addNetworkGDPRInfo(this, UnityAdsATConst.NETWORK_FIRM_ID, localMap);

//Adcolony配置
//1:同意上报个人信息，0:不同意上报个人信息
localMap.put(AdColonyATConst.LOCATION_MAP_KEY_GDPRCONTENT, "0"); 
//true:表示在欧盟地区，false:表示不在欧盟地区          localMap.put(AdColonyATConst.LOCATION_MAP_KEY_GDPRREQUEST, true);
ATSDK.addNetworkGDPRInfo(this, AdColonyATConst.NETWORK_FIRM_ID, localMap);
```



<h2 id='12'>12. Headbidding说明</h2>

应用内header bidding是一种先进的程序化广告竞价技术，允许所有需求方针对同一个广告展示同时竞价，最高出价者获得展示机会，这确保发布商的每次展示可以获得更高的收益。目前TopOn平台支持Mintegral和Facebook的应用内header bidding。

Mintegral和Facebook支持header bidding的应用版本如下：

| 广告平台  | 操作系统 | 支持广告类型             | 广告平台的SDK版本号 | 额外的SDK            |
| --------- | -------- | ------------------------ | ------------------- | -------------------- |
| Facebook  | Android  | 原生, 激励视频, 插屏     | >= 4.99.x           | BiddingKit.aar       |
| Mintegral | Android  | 原生, 激励视频, 插屏视频 | >= 9.12.4           | mintegral_mtgbid.aar |

注：以上额外的SDK包已经在对应平台的SDK目录已经存在，如果有需要直接导入即可。



<h2 id='13'>13. 集成测试、错误码、FAQ</h2>

**请打开TopOn SDK的日志功能，方便验证广告回调状态和排查错误。**<br>
在初始化SDK之前加上：**ATSDK.setNetworkLogDebug(true);**<br>

请跳转 [TopOn SDK 集成测试及FAQ说明](TopOnSDK集成测试及FAQ说明.md)

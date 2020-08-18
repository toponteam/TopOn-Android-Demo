package com.test.ad.demo;

import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import androidx.multidex.MultiDexApplication;

import com.anythink.core.api.ATSDK;
import com.anythink.core.api.NetTrafficeCallback;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Z on 2018/1/10.
 */

public class DemoApplicaion extends MultiDexApplication {
    public static final String appid = "a5aa1f9deda26d";
    public static final String appKey = "4f7b9ac17decb9babec83aac078742c7";
    //Native
    public static final String mPlacementId_native_all = "b5aa1fa2cae775";
    public static final String mPlacementId_native_facebook = "b5aa1fa4165ea3";
    public static final String mPlacementId_native_banner_facebook = "b5ee0cb54b17f8";
    public static final String mPlacementId_native_admob = "b5aa1fa501d9f6";
    public static final String mPlacementId_native_inmobi = "b5aa1fa5d10190";
    public static final String mPlacementId_native_flurry = "b5aa1fa6c00d2f";
    public static final String mPlacementId_native_applovin = "b5aa1fa7956158";
    public static final String mPlacementId_native_mintegral = "b5aa1fa85b86d5";
    public static final String mPLacementId_native_automatic_rending_mintegral= "b5ee8aeb8f3458";
    public static final String mPlacementId_native_mopub = "b5ab858fb0175f";
    public static final String mPlacementId_native_appnext = "b5bc7f369610cd";
    public static final String mPlacementId_native_nend = "b5cb95ead9e60a";
    public static final String mPlacementId_native_googleAdManager = "b5f1ea92c46353";

    //RewardedVideo
    public static final String mPlacementId_rewardvideo_all = "b5b449fb3d89d7";
    public static final String mPlacementId_rewardvideo_facebook = "b5b449eefcab50";
    public static final String mPlacementId_rewardvideo_admob = "b5b449f025ec7c";
    public static final String mPlacementId_rewardvideo_inmobi = "b5b449f0c6b84a";
    public static final String mPlacementId_rewardvideo_flurry = "b5b449f15d04ca";
    public static final String mPlacementId_rewardvideo_applovin = "b5b449f20155a7";
    public static final String mPlacementId_rewardvideo_mintegral = "b5b449f2f58cd7";
    public static final String mPlacementId_rewardvideo_mopub = "b5b449f4927359";
    public static final String mPlacementId_rewardvideo_CHARTBOOST = "b5b449f548e010";
    public static final String mPlacementId_rewardvideo_TAPJOY = "b5b449f66ceaf5";
    public static final String mPlacementId_rewardvideo_IRONSOURCE = "b5b449f75948c5";
    public static final String mPlacementId_rewardvideo_UNITYAD = "b5b449f809139c";
    public static final String mPlacementId_rewardvideo_vungle = "b5b449f97e0b5f";
    public static final String mPlacementId_rewardvideo_adcolony = "b5b449faa95391";
    public static final String mPlacementId_rewardvideo_appnext = "b5bc7f38df0a73";
    public static final String mPlacementId_rewardvideo_nend = "b5cb95efa0c793";
    public static final String mPlacementId_rewardvideo_maio = "b5cb961e495a18";
    public static final String mPlacementId_rewardvideo_startapp = "b5cff0d063ac32";
    public static final String mPlacementId_rewardvideo_superawesome = "b5cff0d2157805";
    public static final String mPlacementId_rewardvideo_myoffer = "b5db6c3764aea3";
    public static final String mPlacementId_rewardvideo_ogury = "b5dde267f73eb4";
    public static final String mPlacementId_rewardvideo_fyber = "b5e96f5e1ade5b";
    public static final String mPlacementId_rewardvideo_googleAdManager = "b5f1ea95c4594e";

    //Banner
    public static final String mPlacementId_banner_all = "b5baca4f74c3d8";
    public static final String mPlacementId_banner_facebook = "b5bbdc51a35e29";
    public static final String mPlacementId_banner_admob = "b5baca41a2536f";
    public static final String mPlacementId_banner_inmobi = "b5bbdc535a9d1a";
    public static final String mPlacementId_banner_flurry = "b5bbdc584f1368";
    public static final String mPlacementId_banner_applovin = "b5bbdc59f88520";
    public static final String mPlacementId_banner_mintegral = "b5dd388839bf5e";
    public static final String mPlacementId_banner_mopub = "b5bbdc5c857b2f";
    public static final String mPlacementId_banner_appnext = "b5bc7f3b034a2b";
    public static final String mPlacementId_banner_nend = "b5cb95ed13203c";
    public static final String mPlacementId_banner_fyber = "b5e96f5f2dc516";
    public static final String mPlacementId_banner_startapp = "b5ed47d37934a4";
    public static final String mPlacementId_banner_vungle = "b5ee8ae48f1578";
    public static final String mPlacementId_banner_adcolony = "b5ee8ae62b2f80";
    public static final String mPlacementId_banner_chartboost = "b5ee8ae6f9f5cf";
    public static final String mPlacementId_banner_googleAdManager = "b5f1ea93f1793b";

    //Interstitial
    public static final String mPlacementId_interstitial_all = "b5baca53984692";
    public static final String mPlacementId_interstitial_facebook = "b5bbdc69a21187";
    public static final String mPlacementId_interstitial_admob = "b5baca54674522";
    public static final String mPlacementId_interstitial_inmobi = "b5bbdc6b63458f";
    public static final String mPlacementId_interstitial_flurry = "b5bbdc6d5e1362";
    public static final String mPlacementId_interstitial_applovin = "b5bbdc6fc65dd1";
    public static final String mPlacementId_interstitial_mintegral = "b5bbdc725768fa";
    public static final String mPlacementId_interstitial_video_mintegral = "b5bbdc855a1506";
    public static final String mPlacementId_interstitial_mopub = "b5bbdc86dd8e3b";
    public static final String mPlacementId_interstitial_CHARTBOOST = "b5bbdc8a68d901";
    public static final String mPlacementId_interstitial_TAPJOY = "b5bbdc8b6e9829";
    public static final String mPlacementId_interstitial_IRONSOURCE = "b5bbdc8e9ef916";
    public static final String mPlacementId_interstitial_vungle = "b5bbdc9182f9f2";
    public static final String mPlacementId_interstitial_adcolony = "b5bbdc92f49ce7";
    public static final String mPlacementId_interstitial_appnext = "b5bc7f3ec5b952";
    public static final String mPlacementId_interstitial_UNITYAD = "b5c21a303c25e0";
    public static final String mPlacementId_interstitial_nend = "b5cb95eeb7e908";
    public static final String mPlacementId_interstitia_maio = "b5cb961d9d3414";
    public static final String mPlacementId_interstitia_startapp = "b5d5e641d9c30a";
    public static final String mPlacementId_interstitial_myoffer = "b5db6c39aed9c5";
    public static final String mPlacementId_interstitial_ogury = "b5dde269060938";
    public static final String mPlacementId_interstitial_fyber = "b5e96f607235f6";
    public static final String mPlacementId_interstitial_googleAdManager = "b5f1ea94f36790";

    //Splash
    public static final String mPlacementId_splash_mintegral = "b5ee8ae8611366";

    @Override
    public void onCreate() {
        super.onCreate();

//        JacocoHelper.Builder builder = new JacocoHelper.Builder();
//        builder.setApplication(this).setDebuggable(true);
//        JacocoHelper.initialize(builder.build());

        //Android 9 or above must be set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName();
            if (!getPackageName().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }


        Stetho.initializeWithDefaults(getApplicationContext());
        ATSDK.setNetworkLogDebug(true);
        ATSDK.integrationChecking(this);

        ATSDK.checkIsEuTraffic(this, new NetTrafficeCallback() {

            @Override
            public void onResultCallback(boolean isEU) {
                if (isEU && ATSDK.getGDPRDataLevel(DemoApplicaion.this) == ATSDK.UNKNOWN) {
                    ATSDK.showGdprAuth(DemoApplicaion.this);
                }

                Log.i("Demoapplication", "onResultCallback:" + isEU);
            }

            @Override
            public void onErrorCallback(String errorMsg) {
                Log.i("Demoapplication", "onErrorCallback:" + errorMsg);
            }
        });
        List excludelist = new ArrayList();
        excludelist.add("com.exclude.myoffer1");
        excludelist.add("com.exclude.myoffer2");
        ATSDK.setExcludeMyOfferPkgList(excludelist);

        Log.i("Demoapplication", "isChinaSDK:" + ATSDK.isChinaSDK());
        Log.i("Demoapplication", "SDKVersionName:" + ATSDK.getSDKVersionName());

        Map custommap = new HashMap();
        custommap.put("key1","initCustomMap1");
        custommap.put("key2","initCustomMap2");
        ATSDK.initCustomMap(custommap);

        Map subcustommap = new HashMap();
        subcustommap.put("key1","initPlacementCustomMap1");
        subcustommap.put("key2","initPlacementCustomMap2");
        ATSDK.initPlacementCustomMap("b5aa1fa4165ea3",subcustommap);//native  facebook


        ATSDK.setChannel("testChannle");
        ATSDK.setSubChannel("testSubChannle");
        ATSDK.init(DemoApplicaion.this, appid, appKey);


    }


}

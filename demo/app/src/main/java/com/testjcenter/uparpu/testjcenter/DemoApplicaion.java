package com.testjcenter.uparpu.testjcenter;

import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.qq.e.ads.cfg.MultiProcessFlag;
import com.uparpu.api.UpArpuSDK;

/**
 * Created by Z on 2018/1/10.
 */

public class DemoApplicaion extends MultiDexApplication {
    public static final String appid = "a5aa1f9deda26d";
    public static final String appKey = "4f7b9ac17decb9babec83aac078742c7";
    public static final String mPlacementId_native_all = "b5aa1fa2cae775";
    public static final String mPlacementId_native_facebook = "b5aa1fa4165ea3";
    public static final String mPlacementId_native_admob = "b5aa1fa501d9f6";
    public static final String mPlacementId_native_inmobi = "b5aa1fa5d10190";
    public static final String mPlacementId_native_flurry = "b5aa1fa6c00d2f";
    public static final String mPlacementId_native_applovin = "b5aa1fa7956158";
    public static final String mPlacementId_native_mobivsta = "b5aa1fa85b86d5";
    public static final String mPlacementId_native_GDT = "b5ab8590d44f82";
    public static final String mPlacementId_native_mopub = "b5ab858fb0175f";


    //rv线上
    public static final String mPlacementId_rewardvideo_all = "b5b449fb3d89d7";
    public static final String mPlacementId_rewardvideo_facebook = "b5b449eefcab50";
    public static final String mPlacementId_rewardvideo_admob = "b5b449f025ec7c";
    public static final String mPlacementId_rewardvideo_inmobi = "b5b449f0c6b84a";
    public static final String mPlacementId_rewardvideo_flurry = "b5b449f15d04ca";
    public static final String mPlacementId_rewardvideo_applovin = "b5b449f20155a7";
    public static final String mPlacementId_rewardvideo_mobivsta = "b5b449f2f58cd7";
    public static final String mPlacementId_rewardvideo_mopub = "b5b449f4927359";
    public static final String mPlacementId_rewardvideo_GDT = "";
    public static final String mPlacementId_rewardvideo_CHARTBOOST = "b5b449f548e010";
    public static final String mPlacementId_rewardvideo_TAPJOY = "b5b449f66ceaf5";
    public static final String mPlacementId_rewardvideo_IRONSOURCE = "b5b449f75948c5";
    public static final String mPlacementId_rewardvideo_UNITYAD = "b5b449f809139c";
    public static final String mPlacementId_rewardvideo_vungle = "b5b449f97e0b5f";
    public static final String mPlacementId_rewardvideo_adcolony = "b5b449faa95391";



    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(getApplicationContext());
        Fresco.initialize(getApplicationContext());
        UpArpuSDK.init(this, appid, appKey);
        MultiProcessFlag.setMultiProcess(true);
        UpArpuSDK.setNetworkLogDebug(true);

    }
}

package com.testjcenter.uparpu.testjcenter;

import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.uparpu.api.UpArpuSDK;

/**
 * Created by Z on 2018/1/10.
 */

public class DemoApplicaion extends MultiDexApplication {
    public static final String appid = "a5aa1f9deda26d";
    public static final String appKey = "4f7b9ac17decb9babec83aac078742c7";

    public static final String mPlacementId_all = "b5aa1fa2cae775";
    public static final String mPlacementId_facebook = "b5aa1fa4165ea3";
    public static final String mPlacementId_admob = "b5aa1fa501d9f6";
    public static final String mPlacementId_inmobi = "b5aa1fa5d10190";
    public static final String mPlacementId_flurry = "b5aa1fa6c00d2f";
    public static final String mPlacementId_applovin = "b5aa1fa7956158";
    public static final String mPlacementId_mobivsta = "b5aa1fa85b86d5";
    public static final String mPlacementId_GDT = "b5ab8590d44f82";
    public static final String mPlacementId_mopub = "b5ab858fb0175f";

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        UpArpuSDK.init(this, appid, appKey);

    }
}

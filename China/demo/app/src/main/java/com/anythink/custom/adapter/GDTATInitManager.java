/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATBidRequestInfo;
import com.anythink.core.api.ATBidRequestInfoListener;
import com.anythink.core.api.ATInitMediation;
import com.anythink.core.api.ATSDK;
import com.anythink.core.api.MediationInitCallback;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.comm.constants.LoadAdParams;
import com.qq.e.comm.managers.GDTAdSdk;
import com.qq.e.comm.managers.setting.GlobalSetting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GDTATInitManager extends ATInitMediation {

    public static final String TAG = GDTATInitManager.class.getSimpleName();
    //    private Map<String, Map<String, GDTATBiddingInfo>> mAdCacheMap;
    private volatile static GDTATInitManager sInstance;
    private final Object BUYER_ID_LOCK = new Object();
    int persionalizedAdStatus = 0;
    private Map<String, WeakReference> adObject = new ConcurrentHashMap<>();
    private Map<String, RewardVideoAD> gdtRVReference = new ConcurrentHashMap<>();
    private Map<String, UnifiedInterstitialAD> gdtIVReference = new ConcurrentHashMap<>();
    private boolean hasCallInit;
    private String mLocalInitAppId;

    private GDTATInitManager() {

    }

    public static GDTATInitManager getInstance() {
        if (sInstance == null) {
            synchronized (GDTATInitManager.class) {
                if (sInstance == null)
                    sInstance = new GDTATInitManager();
            }
        }
        return sInstance;
    }

    public void setGDTATCustomController(GDTATCustomController customController) {
        if (customController != null) {
            GlobalSetting.setAgreePrivacyStrategy(customController.getAgreePrivacyStrategy());

            try {
                Map<String, Boolean> params = new HashMap<>();
                params.put("mac_address", customController.isCanUseMacAddress());
                params.put("android_id", customController.isCanUseAndroidId());
                params.put("device_id", customController.isCanUseDeviceId());
                GlobalSetting.setAgreeReadPrivacyInfo(params);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    protected void put(String showId, WeakReference objectWeakReference) {
        try {
            adObject.put(showId, objectWeakReference);
        } catch (Throwable e) {

        }
    }

    protected void putRVReference(String unitId, RewardVideoAD rewardVideoAD) {
        gdtRVReference.clear();
        gdtRVReference.put(unitId, rewardVideoAD);
    }

    protected void clearRVReference() {
        gdtRVReference.clear();
    }

    protected void putIVReference(String unitId, UnifiedInterstitialAD unifiedInterstitialAD) {
        gdtIVReference.clear();
        gdtIVReference.put(unitId, unifiedInterstitialAD);
    }

    protected void clearIVReference() {
        gdtIVReference.clear();
    }

    protected void clearEmptyObject() {
        try {
            Iterator<Map.Entry<String, WeakReference>> entries = adObject.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, WeakReference> entry = entries.next();
                Object weakObject = entry.getValue().get();
                if (weakObject == null) {
                    adObject.remove(entry.getKey());
                }
            }
        } catch (Throwable e) {

        }
    }

    public synchronized void initSDK(Context context, Map<String, Object> serviceExtras) {
        initSDK(context, serviceExtras, null);
    }

    @Override
    public synchronized void initSDK(Context context, Map<String, Object> serviceExtras, MediationInitCallback onInitCallback) {
        try {
            persionalizedAdStatus = ATSDK.getPersionalizedAdStatus();
        } catch (Throwable e) {

        }
        if (persionalizedAdStatus == ATAdConst.PRIVACY.PERSIONALIZED_LIMIT_STATUS) {
            GlobalSetting.setPersonalizedState(1);
        } else {
            GlobalSetting.setPersonalizedState(0);
        }

        if (ATSDK.isNetworkLogDebug()) {
            Log.i(TAG, "GlobalSetting.getPersonalizedState():" + GlobalSetting.getPersonalizedState());
        }

        clearEmptyObject();

        boolean success;
//        String app_id = (String) serviceExtras.get("app_id");
        String app_id = getStringFromMap(serviceExtras, "app_id");

        if (serviceExtras.containsKey(ATInitMediation.KEY_LOCAL)) {
            mLocalInitAppId = app_id;
        } else if (mLocalInitAppId != null && !TextUtils.equals(mLocalInitAppId, app_id)) {
            checkToSaveInitData(getNetworkName(), serviceExtras, mLocalInitAppId);
            mLocalInitAppId = null;
        }

        if (!hasCallInit) {
            GDTAdSdk.init(context.getApplicationContext(), app_id);
            hasCallInit = true;
            success = true;
        } else {
            success = true;
        }

        if (onInitCallback != null) {
            if (success) {
                onInitCallback.onSuccess();
            } else {
                onInitCallback.onFail("GDT initSDK failed.");
            }
        }
    }

    @Override
    public String getNetworkName() {
        return "Tencent";
    }

    @Override
    public String getNetworkVersion() {
        return GDTATConst.getNetworkVersion();
    }

    @Override
    public String getNetworkSDKClass() {
        return "com.qq.e.ads.ADActivity";
    }

    @Override
    public List getActivityStatus() {
        ArrayList<String> list = new ArrayList<>();
        list.add("com.qq.e.ads.ADActivity");
        list.add("com.qq.e.ads.PortraitADActivity");
        list.add("com.qq.e.ads.LandscapeADActivity");
        list.add("com.qq.e.ads.RewardvideoPortraitADActivity");
        list.add("com.qq.e.ads.RewardvideoLandscapeADActivity");
        return list;
    }

    @Override
    public List getServiceStatus() {
        ArrayList<String> list = new ArrayList<>();
        list.add("com.qq.e.comm.DownloadService");
        return list;
    }


    protected int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
    }

    //VideoOption.VideoPlayPolicy Deprecated in GDT v4.441.1311
    /*int getVideoPlayPolicy(Context context, int autoPlayPolicy) {
        if (autoPlayPolicy == VideoOption.AutoPlayPolicy.ALWAYS) {
            return VideoOption.VideoPlayPolicy.AUTO;
        } else if (autoPlayPolicy == VideoOption.AutoPlayPolicy.WIFI) {
            try {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                return wifiNetworkInfo != null && wifiNetworkInfo.isConnected() ? VideoOption.VideoPlayPolicy.AUTO
                        : VideoOption.VideoPlayPolicy.MANUAL;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (autoPlayPolicy == VideoOption.AutoPlayPolicy.NEVER) {
            return VideoOption.VideoPlayPolicy.MANUAL;
        }
        return VideoOption.VideoPlayPolicy.UNKNOWN;
    }*/


    /**
     *
     */
    void getBidRequestInfo(Context applicationContext, final Map<String, Object> serverExtra, final Map<String, Object> localExtra, final ATBidRequestInfoListener bidRequestInfoListener) {
        GDTATInitManager.getInstance().initSDK(applicationContext, serverExtra, new MediationInitCallback() {
            @Override
            public void onSuccess() {

                runOnThreadPool(new Runnable() {
                    @Override
                    public void run() {
                        GDTBidRequestInfo bidRequestInfo;
                        synchronized (BUYER_ID_LOCK) {
                            bidRequestInfo = new GDTBidRequestInfo(serverExtra, localExtra);
                        }

                        if (!bidRequestInfo.isValid()) {
                            if (bidRequestInfoListener != null) {
                                bidRequestInfoListener.onFailed(ATBidRequestInfo.BIDTOKEN_EMPTY_ERROR_TYPE);
                            }
                            return;
                        }

                        if (bidRequestInfoListener != null) {
                            bidRequestInfoListener.onSuccess(bidRequestInfo);
                        }
                    }
                });

            }

            @Override
            public void onFail(String errorMsg) {
                if (bidRequestInfoListener != null) {
                    bidRequestInfoListener.onFailed(ATBidRequestInfo.INIT_ERROR_TYPE);
                }
            }
        });
    }

    protected LoadAdParams getLoadAdParams(Map<String, Object> serverMap) {
        LoadAdParams loadAdParams = new LoadAdParams();
        HashMap<String, String> loadMap = new HashMap<>();
        fillRequestMap(loadMap, serverMap);
        loadAdParams.setDevExtra(loadMap);
        return loadAdParams;
    }

    protected void fillRequestMap(Map fillMap, Map<String, Object> serverMap) {
        try {
            Object stkInfo = serverMap.get(ATAdConst.NETWORK_REQUEST_PARAMS_KEY.STACK_INFO);
            fillMap.put("staIn", stkInfo != null ? stkInfo.toString() : ""); // Stack
            fillMap.put("meSrc", GDTATConst.CHANNEL); //Mediation Id

            Object mediationWfId = serverMap.get(ATAdConst.NETWORK_REQUEST_PARAMS_KEY.MEDIATION_WF_ID);
            fillMap.put("thrmei", mediationWfId != null ? mediationWfId.toString() : ""); // Waterfall Id

//            printLog("fillRequestMap() >>> mediationId = " + mediationWfId +
//                    " stkInfo = " + stkInfo);
        } catch (Exception e) {
//            printLog("fillRequestMap() >>> " + e.getMessage());
        }
//        Log.i("GDTLoadParams", "LoadParams info:" + fillMap);
    }

//    public static void printLog(String msg) {
//        if (ATSDK.isNetworkLogDebug()) {
//            Log.d("GDTATInitManager", msg);
//        }
//    }

//    protected final static String C2S_PAYLOAD_PRE = "AT_GDT_C2S_";

    //C2S
//    protected synchronized String saveC2SOffer(String adUnitId, Object adCacheObject, double price, Object listenerObject) {
//        if (mAdCacheMap == null) {
//            mAdCacheMap = new ConcurrentHashMap<>(3);
//        }
//        Map<String, GDTATBiddingInfo> unitCacheMap = mAdCacheMap.get(adUnitId);
//        if (unitCacheMap == null) {
//            unitCacheMap = new ConcurrentHashMap<>(2);
//            mAdCacheMap.put(adUnitId, unitCacheMap);
//        }
//
//        String cacheId = C2S_PAYLOAD_PRE + "" + UUID.randomUUID().toString();
//        GDTATBiddingInfo biddingInfo = new GDTATBiddingInfo(adCacheObject, price, listenerObject);
//        unitCacheMap.put(cacheId, biddingInfo);
//
//        return cacheId;
//    }

//    protected synchronized GDTATBiddingInfo requestC2SOffer(String adUnitId, String cacheId) {
//        if (mAdCacheMap != null) {
//            Map<String, GDTATBiddingInfo> unitCacheMap = mAdCacheMap.get(adUnitId);
//            if (unitCacheMap != null) {
//                GDTATBiddingInfo gdtBiddingInfo = unitCacheMap.remove(cacheId);
//                return gdtBiddingInfo;
//            }
//        }
//        return null;
//    }

//    protected synchronized void removeCache(String adUnitId, String cacheId) {
//        if (mAdCacheMap != null) {
//            Map<String, GDTATBiddingInfo> unitCacheMap = mAdCacheMap.get(adUnitId);
//            if (unitCacheMap != null) {
//                unitCacheMap.remove(cacheId);
//            }
//        }
//    }
//    @Override
//    public String getAdapterVersion() {
//        return Const.ADAPTER_VERSION_NAME;
//    }
}

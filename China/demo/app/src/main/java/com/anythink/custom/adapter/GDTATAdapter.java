/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import static com.anythink.core.api.ATInitMediation.getIntFromMap;
import static com.anythink.core.api.ATInitMediation.getStringFromMap;

import android.content.Context;
import android.text.TextUtils;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATBidRequestInfoListener;
import com.anythink.core.api.ATBiddingListener;
import com.anythink.core.api.ATBiddingResult;
import com.anythink.core.api.ATInitMediation;
import com.anythink.core.api.MediationInitCallback;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.anythink.nativead.unitgroup.api.CustomNativeAdapter;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GDTATAdapter extends CustomNativeAdapter {

    String mAppId;
    String mUnitId;
    int mAdCount;
    String mPayload;
    int mUnitType;
    int mVideoMuted;
    int mVideoAutoPlay;
    int mVideoDuration;
    boolean isC2SBidding = false;
    private int mAdWidth = ADSize.FULL_WIDTH, mAdHeight = ADSize.AUTO_HEIGHT;

    private void startLoadAd(Context context, Map<String, Object> serverExtra) {
        try {
            switch (mUnitType) {
                case 2://Self Rendering 2.0
                case 4://Patch, Self Rendering 2.0
                    loadUnifiedAd(context.getApplicationContext(), serverExtra);
                    break;

                case 1: //Native Express
                case 3: //Patch, Express
                default:
                    GDTATNativeLoadListener loadListener = new GDTATNativeLoadListener() {

                        @Override
                        public void notifyLoaded(CustomNativeAd... customNativeAds) {
                            if (isC2SBidding && customNativeAds[0] instanceof GDTATNativeExpressAd) {
                                GDTATNativeExpressAd gdtatNativeExpressAd = (GDTATNativeExpressAd) customNativeAds[0];
                                if (mBiddingListener != null) {
                                    int ecpm = gdtatNativeExpressAd.mNativeExpressADView.getECPM();
                                    double price = ecpm;
                                    GDTATBiddingNotice gdtatBiddingNotice = new GDTATBiddingNotice(gdtatNativeExpressAd);
                                    mBiddingListener.onC2SBiddingResultWithCache(ATBiddingResult.success(price, System.currentTimeMillis() + "", gdtatBiddingNotice, ATAdConst.CURRENCY.RMB_CENT), gdtatNativeExpressAd);
                                }
                                return;
                            }

                            if (mLoadListener != null) {
                                mLoadListener.onAdCacheLoaded(customNativeAds);
                            }
                        }

                        @Override
                        public void notifyError(String errorCode, String errorMsg) {
                            notifyATLoadFail(errorCode, errorMsg);
                        }
                    };

                    if (mUnitType == 3) {//Patch, template
                        GDTATNativeExpressPatchAd gdtatNativeExpressPatchAd = new GDTATNativeExpressPatchAd(context, mUnitId, mAdWidth, mAdHeight,
                                mVideoMuted, mVideoAutoPlay, mVideoDuration, mPayload);
                        gdtatNativeExpressPatchAd.loadAD(loadListener, GDTATInitManager.getInstance().getLoadAdParams(serverExtra));
                    } else {
                        //Picture + video template
                        GDTATNativeExpressAd gdtatNativeExpressAd = new GDTATNativeExpressAd(context, mUnitId, mAdWidth, mAdHeight,
                                mVideoMuted, mVideoAutoPlay, mVideoDuration, mPayload);
                        gdtatNativeExpressAd.loadAD(loadListener, GDTATInitManager.getInstance().getLoadAdParams(serverExtra));
                    }
                    break;

            }
        } catch (Throwable e) {
            notifyATLoadFail("", e.getMessage());
        }


    }

    /**
     * Self-rendering 2.0
     */
    private void loadUnifiedAd(final Context context, Map<String, Object> serverExtra) {
        NativeADUnifiedListener nativeADUnifiedListener = new NativeADUnifiedListener() {
            @Override
            public void onADLoaded(List<NativeUnifiedADData> list) {
                List<CustomNativeAd> resultList = new ArrayList<>();
                if (list != null && list.size() > 0) {
                    GDTATNativePatchAd gdtatNativePatchAd = null;
                    GDTATNativeAd gdtNativeAd = null;
                    for (NativeUnifiedADData unifiedADData : list) {

                        if (mUnitType == 4) {//Patch, Self Rendering
                            gdtatNativePatchAd = new GDTATNativePatchAd(context, unifiedADData, mVideoMuted, mVideoAutoPlay, mVideoDuration);
                            resultList.add(gdtatNativePatchAd);
                        } else {
                            gdtNativeAd = new GDTATNativeAd(context, unifiedADData, mVideoMuted, mVideoAutoPlay, mVideoDuration);
                            resultList.add(gdtNativeAd);
                        }
                    }

                    CustomNativeAd[] customNativeAds = new CustomNativeAd[resultList.size()];
                    customNativeAds = resultList.toArray(customNativeAds);

                    if (isC2SBidding) {
                        if (mBiddingListener != null) {
                            if (mUnitType == 4 && gdtatNativePatchAd != null) {
                                int ecpm = gdtatNativePatchAd.mUnifiedAdData.getECPM();
                                double price = ecpm;
                                GDTATBiddingNotice gdtatBiddingNotice = new GDTATBiddingNotice(gdtatNativePatchAd);
                                mBiddingListener.onC2SBiddingResultWithCache(ATBiddingResult.success(price, System.currentTimeMillis() + "", gdtatBiddingNotice, ATAdConst.CURRENCY.RMB_CENT), gdtatNativePatchAd);
                            } else if (gdtNativeAd != null) {
                                int ecpm = gdtNativeAd.mUnifiedAdData.getECPM();
                                double price = ecpm;
                                GDTATBiddingNotice gdtatBiddingNotice = new GDTATBiddingNotice(gdtNativeAd);
                                mBiddingListener.onC2SBiddingResultWithCache(ATBiddingResult.success(price, System.currentTimeMillis() + "", gdtatBiddingNotice, ATAdConst.CURRENCY.RMB_CENT), gdtNativeAd);
                            }
                        }
                        return;
                    }

                    if (mLoadListener != null) {
                        mLoadListener.onAdCacheLoaded(customNativeAds);
                    }
                } else {
                    notifyATLoadFail("", "Ad list is empty");
                }
            }

            @Override
            public void onNoAD(com.qq.e.comm.util.AdError gdtAdError) {
                notifyATLoadFail(gdtAdError.getErrorCode() + "", gdtAdError.getErrorMsg());
            }
        };

        NativeUnifiedAD nativeUnifiedAd = null;
        if (TextUtils.isEmpty(mPayload)) {
            nativeUnifiedAd = new NativeUnifiedAD(context, mUnitId, nativeADUnifiedListener);
        } else {
            nativeUnifiedAd = new NativeUnifiedAD(context, mUnitId, nativeADUnifiedListener, mPayload);
        }


        if (mVideoDuration != -1) {
            nativeUnifiedAd.setMaxVideoDuration(mVideoDuration);
        }
        if (TextUtils.isEmpty(mPayload)) {
            nativeUnifiedAd.loadData(mAdCount, GDTATInitManager.getInstance().getLoadAdParams(serverExtra));
        } else {
            nativeUnifiedAd.loadData(mAdCount);
        }

    }


    @Override
    public String getNetworkName() {
        return GDTATInitManager.getInstance().getNetworkName();
    }

    @Override
    public void loadCustomNetworkAd(final Context context, final Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        initRequestParams(serverExtra, localExtra);

        if (TextUtils.isEmpty(mAppId) || TextUtils.isEmpty(mUnitId)) {
            notifyATLoadFail("", "GTD appid or unitId is empty.");
            return;
        }

        //Bidding Request
//        if (!TextUtils.isEmpty(mPayload) && mPayload.startsWith(GDTATInitManager.C2S_PAYLOAD_PRE)) {
//            GDTATBiddingInfo gdtATBiddingInfo = GDTATInitManager.getInstance().requestC2SOffer(mUnitId, mPayload);
//            if (gdtATBiddingInfo != null && gdtATBiddingInfo.adObject instanceof GDTATNativeExpressAd && ((GDTATNativeExpressAd) gdtATBiddingInfo.adObject) != null) {
//                GDTATNativeExpressAd gdtatNativeExpressAd = (GDTATNativeExpressAd) gdtATBiddingInfo.adObject;
//                if (mLoadListener != null) {
//                    mLoadListener.onAdCacheLoaded(gdtatNativeExpressAd);
//                }
//            }
//
//            if (gdtATBiddingInfo != null && gdtATBiddingInfo.adObject instanceof GDTATNativePatchAd && ((GDTATNativePatchAd) gdtATBiddingInfo.adObject) != null) {
//                GDTATNativePatchAd gdtatNativePatchAd = (GDTATNativePatchAd) gdtATBiddingInfo.adObject;
//                if (mLoadListener != null) {
//                    mLoadListener.onAdCacheLoaded(gdtatNativePatchAd);
//                }
//            }
//
//            if (gdtATBiddingInfo != null && gdtATBiddingInfo.adObject instanceof GDTATNativeAd && ((GDTATNativeAd) gdtATBiddingInfo.adObject) != null) {
//                GDTATNativeAd gdtNativeAd = (GDTATNativeAd) gdtATBiddingInfo.adObject;
//                if (mLoadListener != null) {
//                    mLoadListener.onAdCacheLoaded(gdtNativeAd);
//                }
//            }
//        }

        GDTATInitManager.getInstance().initSDK(context, serverExtra, new MediationInitCallback() {
            @Override
            public void onSuccess() {
                startLoadAd(context, serverExtra);
            }

            @Override
            public void onFail(String errorMsg) {
                notifyATLoadFail("", errorMsg);
            }
        });
    }

    void initRequestParams(Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        mAppId = getStringFromMap(serverExtra, "app_id");
        mUnitId = getStringFromMap(serverExtra, "unit_id");
        mUnitType = getIntFromMap(serverExtra, "unit_type");
        mPayload = getStringFromMap(serverExtra, "payload");

        mAdCount = isC2SBidding ? 1 : mRequestNum;

        //location story
        try {
            mAdWidth = getIntFromMap(localExtra, ATAdConst.KEY.AD_WIDTH, ADSize.FULL_WIDTH);

            if (localExtra.containsKey(GDTATConst.AD_HEIGHT)) {
                mAdHeight = getIntFromMap(localExtra, GDTATConst.AD_HEIGHT, ADSize.AUTO_HEIGHT);
            } else if (localExtra.containsKey(ATAdConst.KEY.AD_HEIGHT)) {
                mAdHeight = getIntFromMap(localExtra, ATAdConst.KEY.AD_HEIGHT, ADSize.AUTO_HEIGHT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int isVideoMuted = getIntFromMap(serverExtra, "video_muted", 0);
        ;
        int isVideoAutoPlay = getIntFromMap(serverExtra, "video_autoplay", 1);
        int videoDuration = getIntFromMap(serverExtra, "video_duration", -1);

        mVideoMuted = isVideoMuted;
        mVideoAutoPlay = isVideoAutoPlay;
        mVideoDuration = videoDuration;
    }

    @Override
    public void destory() {
    }

    @Override
    public String getNetworkPlacementId() {
        return mUnitId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return GDTATInitManager.getInstance().getNetworkVersion();
    }

    @Override
    public ATInitMediation getMediationInitManager() {
        return GDTATInitManager.getInstance();
    }

    @Override
    public void getBidRequestInfo(Context applicationContext, final Map<String, Object> serverExtra, final Map<String, Object> localExtra, final ATBidRequestInfoListener bidRequestInfoListener) {
        mUnitId = getStringFromMap(serverExtra, "unit_id");
        GDTATInitManager.getInstance().getBidRequestInfo(applicationContext, serverExtra, localExtra, bidRequestInfoListener);
    }

    @Override
    public boolean startBiddingRequest(Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra, ATBiddingListener biddingListener) {
        isC2SBidding = true;
        loadCustomNetworkAd(context, serverExtra, localExtra);
        return true;
    }
}

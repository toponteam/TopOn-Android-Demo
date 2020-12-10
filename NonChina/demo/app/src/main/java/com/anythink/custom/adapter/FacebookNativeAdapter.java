/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 *
 */

package com.anythink.custom.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.anythink.nativead.unitgroup.api.CustomNativeAdapter;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeBannerAd;

import java.util.Map;

public class FacebookNativeAdapter extends CustomNativeAdapter {

    String mPayload;
    String unitId = "";
    String unitType = "";
    String unitHeight = "";
    boolean isAutoPlay = false;

    @Override
    public void loadCustomNetworkAd(final Context context, final Map<String, Object> serverExtras, final Map<String, Object> localExtras) {
        try {
            if (serverExtras.containsKey("unit_id")) {
                unitId = serverExtras.get("unit_id").toString();
            }

            if (serverExtras.containsKey("unit_type")) {
                unitType = serverExtras.get("unit_type").toString();
            }

            if (serverExtras.containsKey("height")) {
                unitHeight = serverExtras.get("height").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(unitId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "facebook unitId is empty.");
            }
            return;
        }


        try {
            if (serverExtras != null) {
                isAutoPlay = Boolean.parseBoolean(serverExtras.get(CustomNativeAd.IS_AUTO_PLAY_KEY).toString());
            }
        } catch (Exception e) {

        }

        FacebookInitManager.getInstance().initSDK(context.getApplicationContext(), serverExtras);

        if (serverExtras.containsKey("payload")) {
            mPayload = serverExtras.get("payload").toString();
        }


        startAdLoad(context);

    }


    private void startAdLoad(final Context context) {
        switch (unitType) {
            case "1":
                NativeBannerAd nativeBanner = new NativeBannerAd(context, unitId);
                final FacebookNativeBannerAd facebookNativeBannerAd = new FacebookNativeBannerAd(context, nativeBanner, unitHeight);
                facebookNativeBannerAd.loadAd(mPayload, new FacebookNativeBannerAd.FBNativeBannerLoadListener() {
                    @Override
                    public void onLoadSuccess() {
                        if (mLoadListener != null) {
                            if (mLoadListener != null) {
                                mLoadListener.onAdCacheLoaded(facebookNativeBannerAd);
                            }
                        }
                    }

                    @Override
                    public void onLoadFail(String code, String message) {
                        if (mLoadListener != null) {
                            mLoadListener.onAdLoadError(code, message);
                        }
                    }
                });
                break;
            default:
                NativeAd nativeAd = new NativeAd(context, unitId);
                final FacebookNativeAd facebookNativeAd = new FacebookNativeAd(context, nativeAd);
                facebookNativeAd.loadAd(mPayload, new FacebookNativeAd.FBNativeLoadListener() {
                    @Override
                    public void onLoadSuccess() {
                        if (mLoadListener != null) {
                            if (mLoadListener != null) {
                                mLoadListener.onAdCacheLoaded(facebookNativeAd);
                            }
                        }
                    }

                    @Override
                    public void onLoadFail(String code, String message) {
                        if (mLoadListener != null) {
                            mLoadListener.onAdLoadError(code, message);
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void destory() {

    }

    @Override
    public String getNetworkName() {
        return FacebookInitManager.getInstance().getNetworkName();
    }

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        return false;
    }

    @Override
    public String getNetworkPlacementId() {
        return unitId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return FacebookInitManager.getInstance().getNetworkVersion();
    }
}

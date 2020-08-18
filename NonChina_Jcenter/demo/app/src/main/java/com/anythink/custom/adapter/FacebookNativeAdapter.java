package com.anythink.custom.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.anythink.nativead.unitgroup.api.CustomNativeAdapter;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;

import java.util.Map;

/**
 * Created by Z on 2018/1/12.
 */

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
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(adError.getErrorCode() + "", adError.getErrorMessage());
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                switch (unitType) {
                    case "1":
                        FacebookNativeBannerAd nativeBanner = new FacebookNativeBannerAd(context, (NativeBannerAd) ad, unitHeight);
                        if (mLoadListener != null) {
                            mLoadListener.onAdCacheLoaded(nativeBanner);
                        }
                        break;
                    default:
                        FacebookNativeAd nativeAd = new FacebookNativeAd(context, (NativeAd) ad);
                        if (mLoadListener != null) {
                            mLoadListener.onAdCacheLoaded(nativeAd);
                        }
                        break;
                }

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        switch (unitType) {
            case "1":
                NativeBannerAd nativeBanner = new NativeBannerAd(context, unitId);
                nativeBanner.setAdListener(nativeAdListener);
                nativeBanner.loadAd();

                break;
            default:
                NativeAd nativeAd = new NativeAd(context, unitId);
                nativeAd.setAdListener(nativeAdListener);
                nativeAd.loadAd();
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

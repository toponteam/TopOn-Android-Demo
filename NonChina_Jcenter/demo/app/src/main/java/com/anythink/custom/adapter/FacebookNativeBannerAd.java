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
import android.view.View;
import android.widget.FrameLayout;

import com.anythink.nativead.api.ATNativePrepareInfo;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.NativeBannerAdView;

public class FacebookNativeBannerAd extends CustomNativeAd implements NativeAdListener {
    private final String TAG = FacebookNativeBannerAd.class.getSimpleName();

    NativeBannerAd mFacebookNativeAd;
    Context mContext;

    NativeBannerAdView.Type mType = NativeBannerAdView.Type.HEIGHT_50;

    FBNativeBannerLoadListener mFBNativeBannerLoadListener;

    public FacebookNativeBannerAd(Context context
            , NativeBannerAd nativeBannerAd
            , String height) {
        mContext = context.getApplicationContext();
        mFacebookNativeAd = nativeBannerAd;
        switch (height) {
            case "50":
                mType = NativeBannerAdView.Type.HEIGHT_50;
                break;
            case "100":
                mType = NativeBannerAdView.Type.HEIGHT_100;
                break;
            case "120":
                mType = NativeBannerAdView.Type.HEIGHT_120;
                break;
        }
    }

    public void loadAd(String bidPayload, FBNativeBannerLoadListener fbNativeBannerLoadListener) {
        mFBNativeBannerLoadListener = fbNativeBannerLoadListener;
        if (TextUtils.isEmpty(bidPayload)) {
            NativeBannerAd.NativeLoadAdConfig nativeLoadAdConfig = mFacebookNativeAd.buildLoadAdConfig().withAdListener(this).build();
            mFacebookNativeAd.loadAd(nativeLoadAdConfig);
        } else {
            NativeBannerAd.NativeLoadAdConfig nativeLoadAdConfig = mFacebookNativeAd.buildLoadAdConfig().withAdListener(this).withBid(bidPayload).build();
            mFacebookNativeAd.loadAd(nativeLoadAdConfig);
        }

    }


    @Override
    public void clear(final View view) {
        if (mFacebookNativeAd != null) {
            mFacebookNativeAd.unregisterView();
        }
    }

    @Override
    public void prepare(View view, ATNativePrepareInfo nativePrepareInfo) {

    }

    @Override
    public View getAdMediaView(Object... object) {
        try {
            if (mFacebookNativeAd != null) {
                return NativeBannerAdView.render(mContext, mFacebookNativeAd, mType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public boolean isNativeExpress() {
        return true;
    }

    @Override
    public void destroy() {
        if (mFacebookNativeAd != null) {
            mFacebookNativeAd.unregisterView();
            mFacebookNativeAd.destroy();
            mFacebookNativeAd = null;
        }

        mContext = null;
    }

    /**
     * facebook listener--------------------------------------------------------------------------------
     **/

    @Override
    public void onError(Ad ad, AdError adError) {
        if (mFBNativeBannerLoadListener != null) {
            mFBNativeBannerLoadListener.onLoadFail(adError.getErrorCode() + "", adError.getErrorMessage());
        }
        mFBNativeBannerLoadListener = null;
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (mFBNativeBannerLoadListener != null) {
            mFBNativeBannerLoadListener.onLoadSuccess();
        }
        mFBNativeBannerLoadListener = null;
    }


    @Override
    public void onAdClicked(Ad ad) {
        notifyAdClicked();
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        notifyAdImpression();
    }


    @Override
    public void onMediaDownloaded(Ad ad) {

    }

    interface FBNativeBannerLoadListener {
        void onLoadSuccess();

        void onLoadFail(String code, String message);
    }
}

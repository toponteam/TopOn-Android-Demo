/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.MediationInitCallback;
import com.anythink.splashad.unitgroup.api.CustomSplashAdapter;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTSplashAd;

import java.util.Map;

public class PangleSplashAdapter extends CustomSplashAdapter implements TTSplashAd.AdInteractionListener {
    private final String TAG = getClass().getSimpleName();

    String appId = "";
    String slotId = "";
    String personalizedTemplate = "";

    TTSplashAd splashAd;

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtra, final Map<String, Object> localExtra) {
        if (serverExtra.containsKey("app_id") && serverExtra.containsKey("slot_id")) {
            appId = (String) serverExtra.get("app_id");
            slotId = (String) serverExtra.get("slot_id");

        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "app_id or slot_id is empty!");
            }
            return;
        }

        personalizedTemplate = "0";
        if (serverExtra.containsKey("personalized_template")) {
            personalizedTemplate = (String) serverExtra.get("personalized_template");
        }

        PangleInitManager.getInstance().initSDK(context, serverExtra, new MediationInitCallback() {
            @Override
            public void onSuccess() {
                startLoad(context, localExtra);
            }

            @Override
            public void onFail(String errorMsg) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError("", errorMsg);
                }
            }
        });
    }

    private void startLoad(Context context, Map<String, Object> localExtra) {
        TTAdManager ttAdManager = TTAdSdk.getAdManager();

        final TTAdNative mTTAdNative = ttAdManager.createAdNative(context);//baseContext is recommended for activity
        final AdSlot.Builder adSlotBuilder = new AdSlot.Builder().setCodeId(slotId);

        int width = 0;
        int height = 0;
        try {
            if (localExtra.containsKey(ATAdConst.KEY.AD_WIDTH)) {
                width = Integer.parseInt(localExtra.get(ATAdConst.KEY.AD_WIDTH).toString());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            if (localExtra.containsKey(ATAdConst.KEY.AD_HEIGHT)) {
                height = Integer.parseInt(localExtra.get(ATAdConst.KEY.AD_HEIGHT).toString());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        adSlotBuilder.setImageAcceptedSize(width, height); //Must be set

        if (TextUtils.equals("1", personalizedTemplate)) {// Native Express
            adSlotBuilder.setExpressViewAcceptedSize(px2dip(context, width), px2dip(context, height));
        }

        postOnMainThread(new Runnable() {
            @Override
            public void run() {
                AdSlot adSlot = adSlotBuilder.build();
                mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
                    @Override
                    public void onError(int i, String s) {
                        if (mLoadListener != null) {
                            mLoadListener.onAdLoadError(i + "", s);
                        }
                    }

                    @Override
                    public void onTimeout() {
                        if (mLoadListener != null) {
                            mLoadListener.onAdLoadError("", "onTimeout");
                        }
                    }

                    @Override
                    public void onSplashAdLoad(TTSplashAd ttSplashAd) {
                        splashAd = ttSplashAd;
                        if (mLoadListener != null) {
                            mLoadListener.onAdCacheLoaded();
                        }
                    }
                }, mFetchAdTimeout);
            }
        });

    }

    @Override
    public boolean isAdReady() {
        return splashAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup container) {
        if (splashAd != null) {
            splashAd.setSplashInteractionListener(PangleSplashAdapter.this);
            View splashView = splashAd.getSplashView();
            if (splashView != null) {
                container.addView(splashView);
            }
        }

    }

    @Override
    public String getNetworkName() {
        return PangleInitManager.getInstance().getNetworkName();
    }

    @Override
    public void destory() {

    }

    @Override
    public String getNetworkPlacementId() {
        return slotId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return PangleInitManager.getInstance().getNetworkVersion();
    }

    @Override
    public void onAdClicked(View view, int i) {
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdClicked();
        }

    }

    @Override
    public void onAdShow(View view, int i) {
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdShow();
        }

    }

    @Override
    public void onAdSkip() {
        mDismissType = ATAdConst.DISMISS_TYPE.SKIP;
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdDismiss();
        }
    }

    @Override
    public void onAdTimeOver() {
        mDismissType = ATAdConst.DISMISS_TYPE.TIMEOVER;
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdDismiss();
        }
    }

    private static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
    }

}

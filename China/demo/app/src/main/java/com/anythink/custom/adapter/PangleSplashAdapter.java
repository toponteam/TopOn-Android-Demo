package com.anythink.custom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra) {
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

        PangleInitManager.getInstance().initSDK(context, serverExtra, true, new PangleInitManager.InitCallback() {
            @Override
            public void onFinish() {
                startLoad(context);
            }
        });
    }

    private void startLoad(Context context) {
        TTAdManager ttAdManager = TTAdSdk.getAdManager();

        final TTAdNative mTTAdNative = ttAdManager.createAdNative(context);//baseContext is recommended for activity
        final AdSlot.Builder adSlotBuilder = new AdSlot.Builder().setCodeId(slotId);

        int width = 0;
        int height = 0;
        ViewGroup.LayoutParams layoutParams = mContainer.getLayoutParams();
        if (layoutParams != null) {
            width = layoutParams.width;
            height = layoutParams.height;
        }
        if (width <= 0) {
            width = context.getResources().getDisplayMetrics().widthPixels;
        }
        if (height <= 0) {
            height = context.getResources().getDisplayMetrics().heightPixels;
        }

        adSlotBuilder.setImageAcceptedSize(width, height); //Must be set

        if (TextUtils.equals("1", personalizedTemplate)) {// Native Express
            adSlotBuilder.setExpressViewAcceptedSize(width, height);
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
                        if (ttSplashAd != null) {
//                    ttSplashAd.setNotAllowSdkCountdown();
                            ttSplashAd.setSplashInteractionListener(PangleSplashAdapter.this);
                            View splashView = ttSplashAd.getSplashView();
                            if (splashView != null) {
                                if (mLoadListener != null) {
                                    mLoadListener.onAdCacheLoaded();
                                }
                                mContainer.removeAllViews();
                                mContainer.addView(splashView);
                            } else {
                                if (mLoadListener != null) {
                                    mLoadListener.onAdLoadError("", "");
                                }
                            }

                        } else {
                            if (mLoadListener != null) {
                                mLoadListener.onAdLoadError("", "");
                            }
                        }
                    }
                });
            }
        });

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
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdDismiss();
        }
    }

    @Override
    public void onAdTimeOver() {
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdDismiss();
        }
    }

}

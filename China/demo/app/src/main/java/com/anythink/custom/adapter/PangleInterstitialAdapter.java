package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.anythink.core.api.ATAdConst;
import com.anythink.interstitial.unitgroup.api.CustomInterstitialAdapter;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTInteractionAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.List;
import java.util.Map;

public class PangleInterstitialAdapter extends CustomInterstitialAdapter {

    String slotId = "";
    boolean isVideo = false;

    private TTInteractionAd mttInterstitialAd;
    private TTFullScreenVideoAd mTTFullScreenVideoAd;
    private TTNativeExpressAd mTTNativeExpressAd;


    //TT Ad load listener
    TTAdNative.InteractionAdListener ttInterstitialAdListener = new TTAdNative.InteractionAdListener() {
        @Override
        public void onError(int code, String message) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(String.valueOf(code), message);
            }
        }

        @Override
        public void onInteractionAdLoad(TTInteractionAd ttInteractionAd) {
            mttInterstitialAd = ttInteractionAd;
            if (mLoadListener != null) {
                mLoadListener.onAdCacheLoaded();
            }
        }

    };

    //TT Advertising event listener
    TTInteractionAd.AdInteractionListener interactionListener = new TTInteractionAd.AdInteractionListener() {

        @Override
        public void onAdClicked() {
            if (mImpressListener != null) {
                mImpressListener.onInterstitialAdClicked();
            }
        }

        @Override
        public void onAdShow() {
            if (mImpressListener != null) {
                mImpressListener.onInterstitialAdShow();
            }
        }

        @Override
        public void onAdDismiss() {
            if (mImpressListener != null) {
                mImpressListener.onInterstitialAdClose();
            }
        }

    };


    TTAdNative.FullScreenVideoAdListener ttFullScrenAdListener = new TTAdNative.FullScreenVideoAdListener() {
        @Override
        public void onError(int code, String message) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(String.valueOf(code), message);
            }
        }

        @Override
        public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
            mTTFullScreenVideoAd = ad;

            if (mLoadListener != null) {
                mLoadListener.onAdDataLoaded();
            }
        }

        @Override
        public void onFullScreenVideoCached() {
            if (mLoadListener != null) {
                mLoadListener.onAdCacheLoaded();
            }
        }

    };

    TTFullScreenVideoAd.FullScreenVideoAdInteractionListener ttFullScreenEventListener = new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

        @Override
        public void onAdShow() {
            if (mImpressListener != null) {
                mImpressListener.onInterstitialAdShow();
                mImpressListener.onInterstitialAdVideoStart();
            }
        }

        @Override
        public void onAdVideoBarClick() {
            if (mImpressListener != null) {
                mImpressListener.onInterstitialAdClicked();
            }
        }

        @Override
        public void onAdClose() {
            if (mImpressListener != null) {
                mImpressListener.onInterstitialAdClose();
            }
        }

        @Override
        public void onVideoComplete() {
            if (mImpressListener != null) {
                mImpressListener.onInterstitialAdVideoEnd();
            }
        }

        @Override
        public void onSkippedVideo() {
        }

    };


    TTAdNative.NativeExpressAdListener expressAdListener = new TTAdNative.NativeExpressAdListener() {
        @Override
        public void onError(int i, String s) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(String.valueOf(i), s);
            }
        }

        @Override
        public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
            mTTNativeExpressAd = list.get(0);
            mTTNativeExpressAd.render();
            if (mLoadListener != null) {
                mLoadListener.onAdCacheLoaded();
            }
        }
    };


    TTNativeExpressAd.AdInteractionListener adExpressInteractionListener = new TTNativeExpressAd.AdInteractionListener() {
        @Override
        public void onAdDismiss() {
            if (mImpressListener != null) {
                mImpressListener.onInterstitialAdClose();
            }
            if (mTTNativeExpressAd != null) {
                mTTNativeExpressAd.destroy();
            }
        }

        @Override
        public void onAdClicked(View view, int i) {
            if (mImpressListener != null) {
                mImpressListener.onInterstitialAdClicked();
            }
        }

        @Override
        public void onAdShow(View view, int i) {
            if (mImpressListener != null) {
                mImpressListener.onInterstitialAdShow();
            }
        }

        @Override
        public void onRenderFail(View view, String s, int i) {
        }

        @Override
        public void onRenderSuccess(View view, float v, float v1) {

        }
    };

    private void startLoad(Context context, Map<String, Object> localExtra, int layoutType, String personalized_template) {
        TTAdManager ttAdManager = TTAdSdk.getAdManager();

        /**Get the width set by the developer**/
        int developerSetExpressWidth = 0;
        try {
            if (localExtra != null) {
                developerSetExpressWidth = Integer.parseInt(localExtra.get(ATAdConst.KEY.AD_WIDTH).toString());
            }
        } catch (Exception e) {
        }

        TTAdNative mTTAdNative = ttAdManager.createAdNative(context);//baseContext is recommended for Activity
        AdSlot.Builder adSlotBuilder = new AdSlot.Builder().setCodeId(slotId);
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        adSlotBuilder.setImageAcceptedSize(width, height); //must be set
        adSlotBuilder.setAdCount(1);

        if (isVideo) {

            try {
                if (!TextUtils.isEmpty(personalized_template) && TextUtils.equals("1", personalized_template)) {
                    adSlotBuilder.setExpressViewAcceptedSize(px2dip(context, width), px2dip(context, height));
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            AdSlot adSlot = adSlotBuilder.build();
            mTTAdNative.loadFullScreenVideoAd(adSlot, ttFullScrenAdListener);
        } else {
            if (layoutType == 1) { //Native Express Interstitial
                float density = context.getResources().getDisplayMetrics().density;
                /**If developer width is set to 0, the default width is used**/
                int expressWidth = developerSetExpressWidth <= 0 ? (int) ((Math.min(width, height) - 30 * density) / density) : (int) (developerSetExpressWidth / density);
                adSlotBuilder.setExpressViewAcceptedSize(expressWidth, 0);
                AdSlot adSlot = adSlotBuilder.build();
                mTTAdNative.loadInteractionExpressAd(adSlot, expressAdListener);
            } else {
                AdSlot adSlot = adSlotBuilder.build();
                mTTAdNative.loadInteractionAd(adSlot, ttInterstitialAdListener);
            }

        }
    }

    @Override
    public boolean isAdReady() {
        return mttInterstitialAd != null || mTTFullScreenVideoAd != null || mTTNativeExpressAd != null;
    }

    @Override
    public void show(Activity activity) {
        try {
            if (mttInterstitialAd != null && activity != null) {
                mttInterstitialAd.setAdInteractionListener(interactionListener);
                mttInterstitialAd.showInteractionAd(activity);
            }

            if (mTTFullScreenVideoAd != null && activity != null) {
                mTTFullScreenVideoAd.setFullScreenVideoAdInteractionListener(ttFullScreenEventListener);
                mTTFullScreenVideoAd.showFullScreenVideoAd(activity);
            }

            if (mTTNativeExpressAd != null && activity != null) {
                mTTNativeExpressAd.setExpressInteractionListener(adExpressInteractionListener);
                mTTNativeExpressAd.showInteractionExpressAd(activity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getNetworkName() {
        return PangleInitManager.getInstance().getNetworkName();
    }

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtra, final Map<String, Object> localExtra) {
        String appId = (String) serverExtra.get("app_id");
        slotId = (String) serverExtra.get("slot_id");

        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "app_id or slot_id is empty!");
            }
            return;
        }

        if (serverExtra.containsKey("is_video")) {
            if (serverExtra.get("is_video").toString().equals("1")) {
                isVideo = true;
            }
        }

        int layoutType = 0;
        if (serverExtra.containsKey("layout_type")) {
            layoutType = Integer.parseInt(serverExtra.get("layout_type").toString());
        }

        final String personalized_template = (String) serverExtra.get("personalized_template");

        final int finalLayoutType = layoutType;
        PangleInitManager.getInstance().initSDK(context, serverExtra, new PangleInitManager.InitCallback() {
            @Override
            public void onFinish() {
                startLoad(context, localExtra, finalLayoutType, personalized_template);
            }
        });
    }

    @Override
    public void destory() {
        if (mTTFullScreenVideoAd != null) {
            mTTFullScreenVideoAd.setFullScreenVideoAdInteractionListener(null);
            mTTFullScreenVideoAd = null;
        }

        if (mttInterstitialAd != null) {
            mttInterstitialAd.setAdInteractionListener(null);
            mttInterstitialAd.setDownloadListener(null);
            mttInterstitialAd = null;
        }

        if (mTTNativeExpressAd != null) {
            mTTNativeExpressAd.setExpressInteractionListener(null);
            mTTNativeExpressAd.destroy();
            mTTNativeExpressAd = null;
        }

        interactionListener = null;
        ttInterstitialAdListener = null;
        ttFullScreenEventListener = null;
        ttFullScrenAdListener = null;
        expressAdListener = null;
        adExpressInteractionListener = null;
    }

    @Override
    public String getNetworkPlacementId() {
        return slotId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return PangleInitManager.getInstance().getNetworkVersion();
    }

    private static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
    }
}

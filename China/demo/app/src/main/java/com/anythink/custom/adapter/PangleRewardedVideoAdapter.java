package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.anythink.core.api.ATAdConst;
import com.anythink.rewardvideo.unitgroup.api.CustomRewardVideoAdapter;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;

import java.util.Map;

public class PangleRewardedVideoAdapter extends CustomRewardVideoAdapter {

    String slotId = "";
    private TTRewardVideoAd mttRewardVideoAd;

    //TT Ad load listener
    TTAdNative.RewardVideoAdListener ttRewardAdListener = new TTAdNative.RewardVideoAdListener() {
        @Override
        public void onError(int code, String message) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(String.valueOf(code), message);
            }
        }

        //Callback of cached video file resources to local after video ad loading
        @Override
        public void onRewardVideoCached() {
            if (mLoadListener != null) {
                mLoadListener.onAdCacheLoaded();
            }
        }

        //Video creatives are loaded, such as title, video url, etc., excluding video files
        @Override
        public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
            mttRewardVideoAd = ad;
            if (mLoadListener != null) {
                mLoadListener.onAdDataLoaded();
            }
        }
    };

    //TT Advertising event listener
    TTRewardVideoAd.RewardAdInteractionListener interactionListener = new TTRewardVideoAd.RewardAdInteractionListener() {

        @Override
        public void onAdShow() {
            if (mImpressionListener != null) {
                mImpressionListener.onRewardedVideoAdPlayStart();
            }
        }

        @Override
        public void onAdVideoBarClick() {
            if (mImpressionListener != null) {
                mImpressionListener.onRewardedVideoAdPlayClicked();
            }
        }

        @Override
        public void onAdClose() {
            if (mImpressionListener != null) {
                mImpressionListener.onRewardedVideoAdClosed();
            }
        }

        @Override
        public void onVideoComplete() {
            if (mImpressionListener != null) {
                mImpressionListener.onRewardedVideoAdPlayEnd();
            }

            if (mImpressionListener != null) {
                mImpressionListener.onReward();
            }
        }

        @Override
        public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {

        }

        @Override
        public void onSkippedVideo() {

        }

        public void onVideoError() {
            if (mImpressionListener != null) {
                mImpressionListener.onRewardedVideoAdPlayFailed("", "Callback VideoError");
            }
        }
    };

    private void startLoad(Context activity, Map<String, Object> localExtra, String personalized_template) {
        TTAdManager ttAdManager = TTAdSdk.getAdManager();

        TTAdNative mTTAdNative = ttAdManager.createAdNative(activity);//baseContext is recommended for activity
        AdSlot.Builder adSlotBuilder = new AdSlot.Builder().setCodeId(slotId);
        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;
        adSlotBuilder.setImageAcceptedSize(width, height); //must be set

        try {
            if (!TextUtils.isEmpty(personalized_template) && TextUtils.equals("1", personalized_template)) {
                adSlotBuilder.setExpressViewAcceptedSize(px2dip(activity, width), px2dip(activity, height));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }


        if (localExtra != null) {
            try {
                adSlotBuilder.setSupportDeepLink((Boolean) localExtra.get(ATAdConst.KEY.AD_IS_SUPPORT_DEEP_LINK));
            } catch (Exception e) {
            }

            try {
                int orientation = Integer.parseInt(localExtra.get(ATAdConst.KEY.AD_ORIENTATION).toString());
                switch (orientation) {
                    case ATAdConst.ORIENTATION_HORIZONTAL:
                        adSlotBuilder.setOrientation(TTAdConstant.HORIZONTAL);
                        break;
                    case ATAdConst.ORIENTATION_VERTICAL:
                        adSlotBuilder.setOrientation(TTAdConstant.VERTICAL);
                        break;
                }
            }  catch (Exception e) {
            }
        }

        if (!TextUtils.isEmpty(mUserId)) {
            adSlotBuilder.setUserID(mUserId);
        }

        if (!TextUtils.isEmpty(mUserData)) {
            adSlotBuilder.setMediaExtra(mUserData);
        }

        adSlotBuilder.setAdCount(1);

        AdSlot adSlot = adSlotBuilder.build();
        mTTAdNative.loadRewardVideoAd(adSlot, ttRewardAdListener);
    }

    @Override
    public boolean isAdReady() {
        return mttRewardVideoAd != null;
    }

    @Override
    public void show(Activity activity) {
        if (activity != null && mttRewardVideoAd != null) {
            mttRewardVideoAd.setRewardAdInteractionListener(interactionListener);
            mttRewardVideoAd.showRewardVideoAd(activity);
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
        final String personalized_template = (String) serverExtra.get("personalized_template");

        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "app_id or slot_id is empty!");
            }
            return;
        }

        PangleInitManager.getInstance().initSDK(context, serverExtra, new PangleInitManager.InitCallback() {
            @Override
            public void onFinish() {
                startLoad(context, localExtra, personalized_template);
            }
        });
    }

    @Override
    public void destory() {
        if (mttRewardVideoAd != null) {
            mttRewardVideoAd.setRewardAdInteractionListener(null);
            mttRewardVideoAd = null;
        }

        ttRewardAdListener = null;
        interactionListener = null;
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

package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.anythink.banner.unitgroup.api.CustomBannerAdapter;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTBannerAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.List;
import java.util.Map;

public class PangleBannerAdapter extends CustomBannerAdapter {

    String slotId = "";
    private TTNativeExpressAd mTTNativeExpressAd;
    Context mActivity;
    View mBannerView;
    int mBannerWidth;
    int mBannerHeight;
    int mRefreshTime;

    //TT Ad load listener
    TTAdNative.BannerAdListener ttBannerAdListener = new TTAdNative.BannerAdListener() {
        @Override
        public void onError(int i, String s) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(i + "", s);
            }
        }

        @Override
        public void onBannerAdLoad(TTBannerAd ttBannerAd) {
            if (ttBannerAd == null) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError("", "TTAD is null!");
                }
                return;
            }
            View bannerView = ttBannerAd.getBannerView();
            if (bannerView == null) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError("", "TTBannerView is null!");
                }
                return;
            }

            mBannerView = bannerView;
            mBannerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    try {
                        if (mBannerView != null && mBannerView.getParent() != null) {
                            int width = ((ViewGroup) mBannerView.getParent()).getMeasuredWidth();
                            int height = ((ViewGroup) mBannerView.getParent()).getMeasuredHeight();

                            if (mBannerView.getLayoutParams().width != width) {
                                mBannerView.getLayoutParams().width = width;
                                mBannerView.getLayoutParams().height = width * mBannerHeight / mBannerWidth;
                                if (mBannerView.getLayoutParams().height > height) {
                                    mBannerView.getLayoutParams().height = height;
                                    mBannerView.getLayoutParams().width = height * mBannerWidth / mBannerHeight;
                                }
                                ((ViewGroup) mBannerView.getParent()).requestLayout();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }
            });

            ttBannerAd.setBannerInteractionListener(interactionListener);

            if (mLoadListener != null) {
                mLoadListener.onAdCacheLoaded();
            }
        }
    };

    //TT Advertising event monitoring
    TTBannerAd.AdInteractionListener interactionListener = new TTBannerAd.AdInteractionListener() {

        @Override
        public void onAdClicked(View view, int i) {
            if (mImpressionEventListener != null) {
                mImpressionEventListener.onBannerAdClicked();
            }
        }

        @Override
        public void onAdShow(View view, int i) {
            if (mImpressionEventListener != null) {
                mImpressionEventListener.onBannerAdShow();
            }
        }
    };


    //Native Express
    TTAdNative.NativeExpressAdListener expressAdListener = new TTAdNative.NativeExpressAdListener() {
        @Override
        public void onError(int i, String s) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(i + "", s);
            }
        }

        @Override
        public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
            if (list != null && list.size() > 0) {
                mTTNativeExpressAd = list.get(0);
                if (mRefreshTime > 0) {
                    mTTNativeExpressAd.setSlideIntervalTime(mRefreshTime);
                } else {
                    mTTNativeExpressAd.setSlideIntervalTime(0);
                }
                mTTNativeExpressAd.setExpressInteractionListener(expressAdInteractionListener);
                mTTNativeExpressAd.render();

                if (mActivity instanceof Activity) {
                    bindDislike((Activity) mActivity, mTTNativeExpressAd, false);
                }


            } else {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError("", "Return Ad list is empty.");
                }
            }
        }
    };


    TTNativeExpressAd.ExpressAdInteractionListener expressAdInteractionListener = new TTNativeExpressAd.ExpressAdInteractionListener() {
        @Override
        public void onAdClicked(View view, int type) {
            if (mImpressionEventListener != null) {
                mImpressionEventListener.onBannerAdClicked();
            }
        }

        @Override
        public void onAdShow(View view, int type) {
            if (mImpressionEventListener != null) {
                mImpressionEventListener.onBannerAdShow();
            }
        }

        @Override
        public void onRenderFail(View view, String msg, int code) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(code + "", msg);
            }
        }

        @Override
        public void onRenderSuccess(View view, float width, float height) {
            mBannerView = view;
            if (mLoadListener != null) {
                mLoadListener.onAdCacheLoaded();
            }
        }
    };

    private void startLoadBanner(Context activity, Map<String, Object> serverExtra) {
        TTAdManager ttAdManager = TTAdSdk.getAdManager();

        String size = "";
        if (serverExtra.containsKey("size")) {
            size = serverExtra.get("size").toString();
        }

        int layoutType = 0;
        if (serverExtra.containsKey("layout_type")) {
            layoutType = Integer.parseInt(serverExtra.get("layout_type").toString());
        }

        int mediaSize = 0;
        if (serverExtra.containsKey("media_size")) {
            mediaSize = Integer.parseInt(serverExtra.get("media_size").toString());
        }

        int bannerWidth = 0;
        int bannerHeight = 0;

        //Layout Type
        if (layoutType == 1) {
            switch (mediaSize) {
                case 0:
                    bannerWidth = 600;
                    bannerHeight = 90;
                    break;
                case 1:
                    bannerWidth = 600;
                    bannerHeight = 100;
                    break;
                case 2:
                    bannerWidth = 600;
                    bannerHeight = 150;
                    break;
                case 3:
                    bannerWidth = 600;
                    bannerHeight = 250;
                    break;
                case 4:
                    bannerWidth = 600;
                    bannerHeight = 286;
                    break;
                case 5:
                    bannerWidth = 600;
                    bannerHeight = 200;
                    break;
                case 6:
                    bannerWidth = 600;
                    bannerHeight = 388;
                    break;
                case 7:
                    bannerWidth = 600;
                    bannerHeight = 400;
                    break;
                case 8:
                    bannerWidth = 600;
                    bannerHeight = 500;
                    break;

            }
        } else {
            try {
                if (!TextUtils.isEmpty(size)) {
                    String[] bannerSizes = size.split("x");
                    bannerWidth = Integer.parseInt(bannerSizes[0]);
                    bannerHeight = Integer.parseInt(bannerSizes[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (bannerWidth == 0 || bannerHeight == 0) {
            bannerWidth = 640;
            bannerHeight = 100;
        }

        mBannerWidth = bannerWidth;
        mBannerHeight = bannerHeight;

        //If BannerView has been configured for width, then use it directly in the template
        int viewWidth = (mATBannerView != null && mATBannerView.getLayoutParams() != null) ? (int) (mATBannerView.getLayoutParams().width / activity.getResources().getDisplayMetrics().density) : 0;
        int viewHeight = (mATBannerView != null && mATBannerView.getLayoutParams() != null) ? (int) (mATBannerView.getLayoutParams().height / activity.getResources().getDisplayMetrics().density) : 0;

        TTAdNative mTTAdNative = ttAdManager.createAdNative(activity);//baseContext is recommended for Activity
        AdSlot.Builder adSlotBuilder = new AdSlot.Builder().setCodeId(slotId);
        adSlotBuilder.setImageAcceptedSize(bannerWidth, bannerHeight); //must be set
        adSlotBuilder.setAdCount(1);


        if (layoutType == 1) {
            adSlotBuilder.setExpressViewAcceptedSize(viewWidth <= 0 ? bannerWidth / 2 : viewWidth, viewHeight <= 0 ? 0 : viewHeight);
            AdSlot adSlot = adSlotBuilder.build();
            mTTAdNative.loadBannerExpressAd(adSlot, expressAdListener);
        } else {
            AdSlot adSlot = adSlotBuilder.build();
            mTTAdNative.loadBannerAd(adSlot, ttBannerAdListener);
        }
    }

    private void bindDislike(Activity activity, TTNativeExpressAd ad, boolean customStyle) {
        //Use the default dislike popup style in the default personalization template
        ad.setDislikeCallback(activity, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onSelected(int position, String value) {
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdClose();
                }
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onRefuse() {

            }
        });
    }


    @Override
    public View getBannerView() {
        return mBannerView;
    }

    @Override
    public String getNetworkName() {
        return PangleInitManager.getInstance().getNetworkName();
    }

    @Override
    public void loadCustomNetworkAd(final Context context, final Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        String appId = (String) serverExtra.get("app_id");
        slotId = (String) serverExtra.get("slot_id");

        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "app_id or slot_id is empty!");
            }
            return;
        }

        if (!(context instanceof Activity)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "Context must be activity.");
            }
            return;
        }

        mActivity = context;

        mRefreshTime = 0;
        try {
            if (serverExtra.containsKey("nw_rft")) {
                mRefreshTime = Integer.valueOf((String) serverExtra.get("nw_rft"));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        PangleInitManager.getInstance().initSDK(context, serverExtra, new PangleInitManager.InitCallback() {
            @Override
            public void onFinish() {
                startLoadBanner(context, serverExtra);
            }
        });
    }

    @Override
    public void destory() {
        mBannerView = null;

        if (mTTNativeExpressAd != null) {
            mTTNativeExpressAd.setExpressInteractionListener(null);
            mTTNativeExpressAd.destroy();
            mTTNativeExpressAd = null;
        }

        interactionListener = null;
        ttBannerAdListener = null;
        expressAdInteractionListener = null;
        expressAdListener = null;
        mActivity = null;
    }

    @Override
    public String getNetworkPlacementId() {
        return slotId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return PangleInitManager.getInstance().getNetworkVersion();
    }
}

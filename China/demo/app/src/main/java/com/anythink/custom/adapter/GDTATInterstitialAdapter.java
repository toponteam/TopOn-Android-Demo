/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import static com.anythink.core.api.ATInitMediation.getBooleanFromMap;
import static com.anythink.core.api.ATInitMediation.getIntFromMap;
import static com.anythink.core.api.ATInitMediation.getStringFromMap;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATBidRequestInfoListener;
import com.anythink.core.api.ATBiddingListener;
import com.anythink.core.api.ATBiddingResult;
import com.anythink.core.api.ATInitMediation;
import com.anythink.core.api.MediationInitCallback;
import com.anythink.interstitial.unitgroup.api.CustomInterstitialAdapter;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.ads.interstitial2.UnifiedInterstitialMediaListener;
import com.qq.e.comm.compliance.DownloadConfirmCallBack;
import com.qq.e.comm.compliance.DownloadConfirmListener;

import java.util.Map;


public class GDTATInterstitialAdapter extends CustomInterstitialAdapter implements UnifiedInterstitialMediaListener {
    public static String TAG = GDTATInterstitialAdapter.class.getSimpleName();
    UnifiedInterstitialAD mUnifiedInterstitialAd;

    String mAppId;
    String mUnitId;
    String mPayload;

    int mUnitVersion = 0;
    String mIsFullScreen;// 0： normal， 1：full screen

    boolean isUseDownloadDialogFrame;
    boolean isC2SBidding;

    private void startLoadAd(Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        //2.0
        mIsFullScreen = getStringFromMap(serverExtra, "is_fullscreen", "0");
        isUseDownloadDialogFrame = getBooleanFromMap(localExtra, ATAdConst.KEY.AD_CLICK_CONFIRM_STATUS, false);

        requestUnifiedInterstitial(context, serverExtra);
    }

    private void requestUnifiedInterstitial(Context context, Map<String, Object> serverExtra) {
        if (!(context instanceof Activity)) {
            notifyATLoadFail("", "GDT UnifiedInterstitial's context must be activity.");
            return;
        }

        UnifiedInterstitialADListener unifiedInterstitialADListener = new UnifiedInterstitialADListener() {
            @Override
            public void onADReceive() {

                if (mUnifiedInterstitialAd != null && isUseDownloadDialogFrame) {
                    mUnifiedInterstitialAd.setDownloadConfirmListener(new DownloadConfirmListener() {
                        @Override
                        public void onDownloadConfirm(Activity activity, int i, String s, DownloadConfirmCallBack downloadConfirmCallBack) {
                            if (mImpressListener != null) {
                                GDTDownloadFirmInfo gdtDownloadFirmInfo = new GDTDownloadFirmInfo();
                                gdtDownloadFirmInfo.appInfoUrl = s;
                                gdtDownloadFirmInfo.scenes = i;
                                gdtDownloadFirmInfo.confirmCallBack = downloadConfirmCallBack;
                                mImpressListener.onDownloadConfirm(activity, gdtDownloadFirmInfo);
                            }
                        }
                    });
                }

                if (isC2SBidding) {
                    if (mBiddingListener != null) {
                        int ecpm = mUnifiedInterstitialAd.getECPM();
                        double price = ecpm;
                        GDTATBiddingNotice biddingNotice = new GDTATBiddingNotice(mUnifiedInterstitialAd);
                        mBiddingListener.onC2SBiddingResultWithCache(ATBiddingResult.success(price, System.currentTimeMillis() + "", biddingNotice, ATAdConst.CURRENCY.RMB_CENT), null);
                    }
                } else {
                    if (mLoadListener != null) {
                        mLoadListener.onAdDataLoaded();
                    }
                }
            }

            @Override
            public void onVideoCached() {

            }

            @Override
            public void onNoAD(com.qq.e.comm.util.AdError adError) {
                notifyATLoadFail(String.valueOf(adError.getErrorCode()), adError.getErrorMsg());
            }

            @Override
            public void onADOpened() {
            }

            @Override
            public void onADExposure() {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdShow();
                }
            }

            @Override
            public void onADClicked() {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClicked();
                }
            }

            @Override
            public void onADLeftApplication() {

            }

            @Override
            public void onADClosed() {
                GDTATInitManager.getInstance().clearIVReference();
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClose();
                }
                if (mUnifiedInterstitialAd != null) {
                    mUnifiedInterstitialAd.destroy();
                }
            }

            @Override
            public void onRenderSuccess() {
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            }

            @Override
            public void onRenderFail() {
                notifyATLoadFail("", "GDT: onRenderFail()");
            }
        };
        if (TextUtils.isEmpty(mPayload) || isC2SBidding) {
            mUnifiedInterstitialAd = new UnifiedInterstitialAD((Activity) context, mUnitId, unifiedInterstitialADListener);
            mUnifiedInterstitialAd.setLoadAdParams(GDTATInitManager.getInstance().getLoadAdParams(serverExtra));
        } else {
            mUnifiedInterstitialAd = new UnifiedInterstitialAD((Activity) context, mUnitId, unifiedInterstitialADListener, null, mPayload);
        }

        // set video option
        setVideoOption(context, serverExtra);

        if (TextUtils.equals("1", mIsFullScreen)) {//full screen
            mUnifiedInterstitialAd.loadFullScreenAD();
        } else {
            mUnifiedInterstitialAd.loadAD();
        }
    }


    @Override
    public boolean isAdReady() {
        if (mUnifiedInterstitialAd != null) {
            return mUnifiedInterstitialAd.isValid();
        }
        return false;
    }

    @Override
    public void show(Activity activity) {
        if (mUnifiedInterstitialAd != null) {
            // Interstitial video or full screen
            mUnifiedInterstitialAd.setMediaListener(GDTATInterstitialAdapter.this);

            if (TextUtils.equals("1", mIsFullScreen)) {//full screen
                if (activity != null) {
                    GDTATInitManager.getInstance().putIVReference(mUnitId, mUnifiedInterstitialAd);
                    mUnifiedInterstitialAd.showFullScreenAD(activity);
                } else {
                    Log.e(TAG, "Gdt (Full Screen) show fail: context need be Activity");
                }
            } else {
                GDTATInitManager.getInstance().putIVReference(mUnitId, mUnifiedInterstitialAd);
                if (activity != null) {
                    mUnifiedInterstitialAd.show(activity);
                } else {
                    mUnifiedInterstitialAd.show();
                }
            }
        }
    }

    @Override
    public String getNetworkName() {
        return GDTATInitManager.getInstance().getNetworkName();
    }

    @Override
    public void loadCustomNetworkAd(final Context context, final Map<String, Object> serverExtra, final Map<String, Object> localExtra) {
        initRequestParams(serverExtra, localExtra);

        if (TextUtils.isEmpty(mAppId) || TextUtils.isEmpty(mUnitId)) {
            notifyATLoadFail("", "GDT appid or unitId is empty.");
            return;
        }

        //Bidding Request
//        if (!TextUtils.isEmpty(mPayload) && mPayload.startsWith(GDTATInitManager.C2S_PAYLOAD_PRE)) {
//            GDTATBiddingInfo gdtATBiddingInfo = GDTATInitManager.getInstance().requestC2SOffer(mUnitId, mPayload);
//            if (gdtATBiddingInfo != null && gdtATBiddingInfo.adObject instanceof UnifiedInterstitialAD && ((UnifiedInterstitialAD) gdtATBiddingInfo.adObject).isValid()) {
//                mGDTATBiddingInfo = gdtATBiddingInfo;
//                mUnifiedInterstitialAd = (UnifiedInterstitialAD) gdtATBiddingInfo.adObject;
//                if (mLoadListener != null) {
//                    mLoadListener.onAdCacheLoaded();
//                }
//            } else {
//                if (mLoadListener != null) {
//                    mLoadListener.onAdLoadError("", "GDT: Bidding Cache is Empty or not ready.");
//                }
//            }
//            return;
//        }

        GDTATInitManager.getInstance().initSDK(context, serverExtra, new MediationInitCallback() {
            @Override
            public void onSuccess() {
                if (getMixedFormatAdType() == ATAdConst.ATMixedFormatAdType.NATIVE) {
                    thirdPartyLoad(new GDTATAdapter(), context, serverExtra, localExtra);
                } else {
                    startLoadAd(context, serverExtra, localExtra);
                }
            }

            @Override
            public void onFail(String errorMsg) {
                notifyATLoadFail("", errorMsg);
            }
        });
    }

    private void initRequestParams(Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        mAppId = getStringFromMap(serverExtra, "app_id");
        mUnitId = getStringFromMap(serverExtra, "unit_id");
        mUnitVersion = getIntFromMap(serverExtra, "unit_version");
        mPayload = getStringFromMap(serverExtra, "payload");
    }

    @Override
    public void destory() {
        if (mUnifiedInterstitialAd != null) {
            mUnifiedInterstitialAd.setMediaListener(null);
            mUnifiedInterstitialAd.destroy();
            mUnifiedInterstitialAd = null;
        }
    }

    @Override
    public String getNetworkPlacementId() {
        return mUnitId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return GDTATInitManager.getInstance().getNetworkVersion();
    }

    /**
     * set video option
     */
    private void setVideoOption(Context context, Map<String, Object> serverExtra) {

        int isVideoMuted = getIntFromMap(serverExtra, "video_muted", 0);
        ;
        int isVideoAutoPlay = getIntFromMap(serverExtra, "video_autoplay", 1);
        int videoDuration = getIntFromMap(serverExtra, "video_duration", -1);

        if (mUnifiedInterstitialAd != null) {
            VideoOption option = new VideoOption.Builder()
                    .setAutoPlayMuted(isVideoMuted == 1)
                    .setDetailPageMuted(isVideoMuted == 1)
                    .setAutoPlayPolicy(isVideoAutoPlay)
                    .build();
            mUnifiedInterstitialAd.setVideoOption(option);
            if (videoDuration != -1) {
                mUnifiedInterstitialAd.setMaxVideoDuration(videoDuration);
            }
        }
    }

    @Override
    public void onVideoInit() {

    }

    @Override
    public void onVideoLoading() {

    }

    @Override
    public void onVideoReady(long l) {
    }

    @Override
    public void onVideoStart() {
        if (mImpressListener != null) {
            mImpressListener.onInterstitialAdVideoStart();
        }
    }

    @Override
    public void onVideoPause() {

    }

    @Override
    public void onVideoComplete() {
        if (mImpressListener != null) {
            mImpressListener.onInterstitialAdVideoEnd();
        }
    }

    @Override
    public void onVideoError(com.qq.e.comm.util.AdError adError) {
        GDTATInitManager.getInstance().clearIVReference();
        if (mImpressListener != null) {
            mImpressListener.onInterstitialAdVideoError(adError.getErrorCode() + "", adError.getErrorMsg());
        }
    }

    @Override
    public void onVideoPageOpen() {

    }

    @Override
    public void onVideoPageClose() {

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
    public boolean startBiddingRequest(final Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra, ATBiddingListener biddingListener) {
        isC2SBidding = true;
        loadCustomNetworkAd(context, serverExtra, localExtra);
        return true;
    }

}

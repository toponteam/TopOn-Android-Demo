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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.anythink.banner.unitgroup.api.CustomBannerAdapter;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATBidRequestInfoListener;
import com.anythink.core.api.ATBiddingListener;
import com.anythink.core.api.ATBiddingResult;
import com.anythink.core.api.ATInitMediation;
import com.anythink.core.api.MediationInitCallback;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.comm.compliance.DownloadConfirmCallBack;
import com.qq.e.comm.compliance.DownloadConfirmListener;

import java.util.Map;

public class GDTATBannerAdapter extends CustomBannerAdapter {
    private final String TAG = GDTATBannerAdapter.class.getSimpleName();

    String mAppId;
    String mUnitId;
    String mPayload;
    UnifiedBannerView mBannerView;

    int mUnitVersion = 0;
    int mRefreshTime;

    boolean isUseDownloadDialogFrame;

    boolean isC2SBidding;

    DownloadConfirmListener downloadConfirmCallBack = new DownloadConfirmListener() {
        @Override
        public void onDownloadConfirm(Activity activity, int i, String s, DownloadConfirmCallBack downloadConfirmCallBack) {
            if (mImpressionEventListener != null) {
                GDTDownloadFirmInfo gdtDownloadFirmInfo = new GDTDownloadFirmInfo();
                gdtDownloadFirmInfo.appInfoUrl = s;
                gdtDownloadFirmInfo.scenes = i;
                gdtDownloadFirmInfo.confirmCallBack = downloadConfirmCallBack;
                mImpressionEventListener.onDownloadConfirm(activity, gdtDownloadFirmInfo);
            }
        }
    };

    private void startLoadAd(Activity activity, Map<String, Object> serverMap) {
        UnifiedBannerView unifiedBannerView = null;
        //2.0
        UnifiedBannerADListener unifiedBannerADListener = new UnifiedBannerADListener() {
            @Override
            public void onNoAD(com.qq.e.comm.util.AdError adError) {
                mBannerView = null;
                notifyATLoadFail(String.valueOf(adError.getErrorCode()), adError.getErrorMsg());
            }

            @Override
            public void onADReceive() {
                if (isUseDownloadDialogFrame && mBannerView != null) {
                    mBannerView.setDownloadConfirmListener(downloadConfirmCallBack);
                }

                if (isC2SBidding) {
                    if (mBiddingListener != null) {
                        if (mBannerView != null) {
                            int ecpm = mBannerView.getECPM();
                            double price = ecpm;
                            GDTATBiddingNotice biddingNotice = new GDTATBiddingNotice(mBannerView);
                            mBiddingListener.onC2SBiddingResultWithCache(ATBiddingResult.success(price, System.currentTimeMillis() + "", biddingNotice, ATAdConst.CURRENCY.RMB_CENT), null);

                        } else {
                            notifyATLoadFail("", "GDT: Offer had been destroy.");
                        }
                    }
                } else {
                    if (mLoadListener != null) {
                        mLoadListener.onAdCacheLoaded();
                    }
                }
            }

            @Override
            public void onADExposure() {
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdShow();
                }
            }

            @Override
            public void onADClosed() {
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdClose();
                }
            }

            @Override
            public void onADClicked() {
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdClicked();
                }
            }

            @Override
            public void onADLeftApplication() {

            }

        };


        if (TextUtils.isEmpty(mPayload) || isC2SBidding) {
            unifiedBannerView = new UnifiedBannerView(activity, mUnitId, unifiedBannerADListener);
            unifiedBannerView.setLoadAdParams(GDTATInitManager.getInstance().getLoadAdParams(serverMap));
        } else {
            unifiedBannerView = new UnifiedBannerView(activity, mUnitId, unifiedBannerADListener, null, mPayload);
        }


        if (mRefreshTime > 0) {
            unifiedBannerView.setRefresh(mRefreshTime);
        } else {
            unifiedBannerView.setRefresh(0);
        }

        if (unifiedBannerView.getLayoutParams() == null) {
            unifiedBannerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        mBannerView = unifiedBannerView;

        unifiedBannerView.loadAD();
    }

    @Override
    public View getBannerView() {
        return mBannerView;
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

        if (!(context instanceof Activity)) {
            notifyATLoadFail("", "Context must be activity.");
            return;
        }

        //Bidding Request
//        if (!TextUtils.isEmpty(mPayload) && mPayload.startsWith(GDTATInitManager.C2S_PAYLOAD_PRE)) {
//            GDTATBiddingInfo gdtATBiddingInfo = GDTATInitManager.getInstance().requestC2SOffer(mUnitId, mPayload);
//            if (gdtATBiddingInfo != null && gdtATBiddingInfo.adObject instanceof UnifiedBannerView && ((UnifiedBannerView) gdtATBiddingInfo.adObject) != null) {
//                mGDTATBiddingInfo = gdtATBiddingInfo;
//                mBannerView = (UnifiedBannerView) gdtATBiddingInfo.adObject;
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

        runOnNetworkRequestThread(new Runnable() {
            @Override
            public void run() {
                GDTATInitManager.getInstance().initSDK(context, serverExtra, new MediationInitCallback() {
                    @Override
                    public void onSuccess() {
                        startLoadAd((Activity) context, serverExtra);
                    }

                    @Override
                    public void onFail(String errorMsg) {
                        notifyATLoadFail("", errorMsg);
                    }
                });
            }
        });
    }

    private void initRequestParams(Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        mAppId = getStringFromMap(serverExtra, "app_id");
        mUnitId = getStringFromMap(serverExtra, "unit_id");
        mUnitVersion = getIntFromMap(serverExtra, "unit_version");

        mPayload = getStringFromMap(serverExtra, "payload");

        isUseDownloadDialogFrame = getBooleanFromMap(localExtra, ATAdConst.KEY.AD_CLICK_CONFIRM_STATUS, false);

        mRefreshTime = 0;
        try {
            if (serverExtra.containsKey("nw_rft")) {
                mRefreshTime = getIntFromMap(serverExtra, "nw_rft");
                mRefreshTime /= 1000f;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destory() {
        if (mBannerView != null) {
            if (mBannerView instanceof UnifiedBannerView) {
                ((UnifiedBannerView) mBannerView).destroy();
            }
            mBannerView = null;
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

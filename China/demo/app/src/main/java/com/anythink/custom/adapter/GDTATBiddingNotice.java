package com.anythink.custom.adapter;

import static com.anythink.core.api.ATInitMediation.getIntFromMap;

import android.util.Log;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATBiddingNotice;
import com.anythink.core.api.ATSDK;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.comm.constants.BiddingLossReason;
import com.qq.e.comm.pi.IBidding;

import java.util.HashMap;
import java.util.Map;

public class GDTATBiddingNotice implements ATBiddingNotice {
    Object adObject;

    protected GDTATBiddingNotice(Object adObject) {
        this.adObject = adObject;
    }

    @Override
    public void notifyBidWin(double costPrice, double secondPrice, Map<String, Object> extra) {
        //RMB cents

        Map<String, Object> map = new HashMap<>(4);

        map.put(IBidding.HIGHEST_LOSS_PRICE, (int) Math.round(secondPrice));

        try {
            if (adObject instanceof RewardVideoAD) {
                RewardVideoAD rewardVideoAD = (RewardVideoAD) adObject;

                map.put(IBidding.EXPECT_COST_PRICE, rewardVideoAD.getECPM());

                if (ATSDK.isNetworkLogDebug()) {
                    Log.i("GDTATBiddingNotice", (adObject != null ? adObject.toString() : "") + ": notifyBidWin: " + map.toString());
                }

                rewardVideoAD.sendWinNotification(map);
                return;
            }
        } catch (Throwable e) {

        }

        try {
            if (adObject instanceof UnifiedInterstitialAD) {
                UnifiedInterstitialAD unifiedInterstitialAD = (UnifiedInterstitialAD) adObject;

                map.put(IBidding.EXPECT_COST_PRICE, unifiedInterstitialAD.getECPM());

                if (ATSDK.isNetworkLogDebug()) {
                    Log.i("GDTATBiddingNotice", (adObject != null ? adObject.toString() : "") + ": notifyBidWin: " + map.toString());
                }

                unifiedInterstitialAD.sendWinNotification(map);

                return;
            }
        } catch (Throwable e) {

        }

        try {
            if (adObject instanceof SplashAD) {
                SplashAD splashAD = (SplashAD) adObject;

                map.put(IBidding.EXPECT_COST_PRICE, splashAD.getECPM());

                if (ATSDK.isNetworkLogDebug()) {
                    Log.i("GDTATBiddingNotice", (adObject != null ? adObject.toString() : "") + ": notifyBidWin: " + map.toString());
                }

                splashAD.sendWinNotification(map);

                return;
            }
        } catch (Throwable e) {

        }

        try {
            if (adObject instanceof UnifiedBannerView) {
                UnifiedBannerView unifiedBannerView = (UnifiedBannerView) adObject;

                map.put(IBidding.EXPECT_COST_PRICE, unifiedBannerView.getECPM());

                if (ATSDK.isNetworkLogDebug()) {
                    Log.i("GDTATBiddingNotice", (adObject != null ? adObject.toString() : "") + ": notifyBidWin: " + map.toString());
                }

                unifiedBannerView.sendWinNotification(map);

                return;
            }
        } catch (Throwable e) {

        }

        try {
            if (adObject instanceof GDTATNativeExpressAd) {
                GDTATNativeExpressAd gdtatNativeExpressAd = (GDTATNativeExpressAd) adObject;

                map.put(IBidding.EXPECT_COST_PRICE, gdtatNativeExpressAd.mNativeExpressADView.getECPM());

                if (ATSDK.isNetworkLogDebug()) {
                    Log.i("GDTATBiddingNotice", (adObject != null ? adObject.toString() : "") + ": notifyBidWin: " + map.toString());
                }

                gdtatNativeExpressAd.mNativeExpressADView.sendWinNotification(map);
                return;
            }

            if (adObject instanceof GDTATNativePatchAd) {
                GDTATNativePatchAd gdtatNativePatchAd = (GDTATNativePatchAd) adObject;

                map.put(IBidding.EXPECT_COST_PRICE, gdtatNativePatchAd.mUnifiedAdData.getECPM());

                if (ATSDK.isNetworkLogDebug()) {
                    Log.i("GDTATBiddingNotice", (adObject != null ? adObject.toString() : "") + ": notifyBidWin: " + map.toString());
                }

                gdtatNativePatchAd.mUnifiedAdData.sendWinNotification(map);
                return;
            }

            if (adObject instanceof GDTATNativeAd) {
                GDTATNativeAd gdtNativeAd = (GDTATNativeAd) adObject;

                map.put(IBidding.EXPECT_COST_PRICE, gdtNativeAd.mUnifiedAdData.getECPM());

                if (ATSDK.isNetworkLogDebug()) {
                    Log.i("GDTATBiddingNotice", (adObject != null ? adObject.toString() : "") + ": notifyBidWin: " + map.toString());
                }

                gdtNativeAd.mUnifiedAdData.sendWinNotification(map);
                return;
            }
        } catch (Throwable e) {
        }

        adObject = null;
    }

    @Override
    public void notifyBidLoss(String lossCode, double winPrice, Map<String, Object> extra) {
        //RMB cents
        Map<String, Object> map = new HashMap<>(4);

        int gdtLossReason = BiddingLossReason.OTHER;
        switch (lossCode) {
            case ATAdConst
                    .BIDDING_TYPE.BIDDING_LOSS_WITH_BIDDING_TIMEOUT:
                gdtLossReason = BiddingLossReason.NO_AD;
                break;
            case ATAdConst
                    .BIDDING_TYPE.BIDDING_LOSS_WITH_LOW_PRICE_IN_HB:
            case ATAdConst
                    .BIDDING_TYPE.BIDDING_LOSS_WITH_LOW_PRICE_IN_NORMAL:
                gdtLossReason = BiddingLossReason.LOW_PRICE;
                break;
        }

        int winnerPriceCent = (int) Math.round(winPrice);


        map.put(IBidding.WIN_PRICE, winnerPriceCent);
        map.put(IBidding.LOSS_REASON, gdtLossReason);

        try {
            int adnId = getIntFromMap(extra, ATBiddingNotice.ADN_ID, -1);
            switch (adnId) {
                case ATAdConst.BIDDING_ADN_ID.LOSE_TO_NORMAL_IN_SAME_ADN:
                    adnId = 1;
                    break;
                case ATAdConst.BIDDING_ADN_ID.LOSE_TO_HB_IN_SAME_ADN:
                    adnId = 4;
                    break;
                case ATAdConst.BIDDING_ADN_ID.LOSE_TO_OWN_ADN:
                    adnId = 3;
                    break;
                case ATAdConst.BIDDING_ADN_ID.LOSE_TO_OTHER_ADN:
                    adnId = 2;
                    break;
            }

            if (adnId != -1) {
                map.put(IBidding.ADN_ID, adnId);
            }
        } catch (Throwable e) {
        }


        if (ATSDK.isNetworkLogDebug()) {
            Log.i("GDTATBiddingNotice", (adObject != null ? adObject.toString() : "") + ": notifyBidLoss lossCode:" + lossCode + ",lossReason:" + gdtLossReason
                    + "\n" + map.toString());
        }

        try {
            if (adObject instanceof RewardVideoAD) {
                RewardVideoAD rewardVideoAD = (RewardVideoAD) adObject;
                rewardVideoAD.sendLossNotification(map);
                return;
            }
        } catch (Throwable e) {

        }

        try {
            if (adObject instanceof UnifiedInterstitialAD) {
                UnifiedInterstitialAD unifiedInterstitialAD = (UnifiedInterstitialAD) adObject;
                unifiedInterstitialAD.sendLossNotification(map);
                return;
            }
        } catch (Throwable e) {

        }

        try {
            if (adObject instanceof SplashAD) {
                SplashAD splashAD = (SplashAD) adObject;
                splashAD.sendLossNotification(map);
                return;
            }
        } catch (Throwable e) {

        }


        try {
            if (adObject instanceof UnifiedBannerView) {
                UnifiedBannerView unifiedBannerView = (UnifiedBannerView) adObject;
                unifiedBannerView.sendLossNotification(map);
                return;
            }
        } catch (Throwable e) {

        }


        try {
            if (adObject instanceof GDTATNativeExpressAd) {
                GDTATNativeExpressAd gdtatNativeExpressAd = (GDTATNativeExpressAd) adObject;
                gdtatNativeExpressAd.mNativeExpressADView.sendLossNotification(map);
                return;
            }

            if (adObject instanceof GDTATNativePatchAd) {
                GDTATNativePatchAd gdtatNativePatchAd = (GDTATNativePatchAd) adObject;
                gdtatNativePatchAd.mUnifiedAdData.sendLossNotification(map);
                return;
            }

            if (adObject instanceof GDTATNativeAd) {
                GDTATNativeAd gdtNativeAd = (GDTATNativeAd) adObject;
                gdtNativeAd.mUnifiedAdData.sendLossNotification(map);
                return;
            }
        } catch (Throwable e) {

        }

        adObject = null;
    }

    @Override
    public void notifyBidDisplay(boolean isWinner, double displayPrice) {

    }

    @Override
    public ATAdConst.CURRENCY getNoticePriceCurrency() {
        return ATAdConst.CURRENCY.RMB_CENT;
    }

}

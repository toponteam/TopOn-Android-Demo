package com.anythink.custom.adapter;


import android.content.Context;
import android.util.Log;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATInitMediation;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;

import java.util.Map;

public class AdmobConst {

    public static String TAG = AdmobConst.class.getSimpleName();

    public static final String ADAPTIVE_TYPE = "adaptive_type";
    public static final String ADAPTIVE_ORIENTATION = "adaptive_orientation";
    public static final String ADAPTIVE_WIDTH = "adaptive_width";

    public static final int ADAPTIVE_ANCHORED = 0;
    public static final int ADAPTIVE_INLINE = 1;
    public static final int ORIENTATION_CURRENT = 0;
    public static final int ORIENTATION_PORTRAIT = 1;
    public static final int ORIENTATION_LANDSCAPE = 2;


    public static class REWARD_EXTRA {
        public static final String REWARD_EXTRA_KEY_REWARD_AMOUNT = "admob_reward_amount";
        public static final String REWARD_EXTRA_KEY_REWARD_TYPE = "admob_reward_type";
    }


    private static String version;
    private static boolean disableAutoUseAdaptiveBanner = false;

    public static String getNetworkVersion() {
        if (version != null) {
            return version;
        }
        try {
            version = MobileAds.getVersion().toString();
            return version;
        } catch (Throwable e) {
            version = "";
        }
        return version;
    }

    protected static AdSize getAdaptiveBannerAdSize(Context context, Map<String, Object> localExtras, Map<String, Object> serviceExtras) {
        try {
            int localExtraWidth = ATInitMediation.getIntFromMap(localExtras, ATAdConst.KEY.AD_WIDTH, 0);
            if (!disableAutoUseAdaptiveBanner && localExtraWidth > 0 && !localExtras.containsKey(AdmobConst.ADAPTIVE_TYPE)) {
                localExtras.put(AdmobConst.ADAPTIVE_TYPE, AdmobConst.ADAPTIVE_ANCHORED);
                localExtras.put(AdmobConst.ADAPTIVE_ORIENTATION, AdmobConst.ORIENTATION_CURRENT);
            }
            if (localExtraWidth <= 0) {
                localExtraWidth = ATInitMediation.getIntFromMap(localExtras, AdmobConst.ADAPTIVE_WIDTH, 0);
            }
            String size = ATInitMediation.getStringFromMap(serviceExtras, "size");
            AdSize adSize;
            if (localExtraWidth > 0) {
                int minWidth = 300;
                int minWidthPx = dip2px(context, minWidth);
                if ("300x250".equals(size)) {
                    adSize = getAdSizeFromServiceSize(context, size);
                } else if (localExtraWidth < minWidthPx) {
                    Log.e("anythink_network", TAG + ": The width is too small. It is recommended to be larger than " + minWidth + "dp to avoid affecting the ad fill rate.");
                    adSize = getAdSizeFromLocalExtra(context, localExtras, minWidthPx);
                } else {
                    adSize = getAdSizeFromLocalExtra(context, localExtras, localExtraWidth);
                }
            } else {
                adSize = getAdSizeFromServiceSize(context, size);
            }
            if (adSize == AdSize.INVALID) {
                adSize = AdSize.BANNER;
            }
            return adSize;
        } catch (Throwable e) {
            Log.e(TAG, "getAdaptiveBannerAdSize() >>> failed: " + e.getMessage());
        }
        return AdSize.BANNER;
    }

    private static AdSize getAdSizeFromLocalExtra(Context context, Map<String, Object> localExtras, int localExtraWidth) {
        AdSize adSize;
        int adaptiveType = ATInitMediation.getIntFromMap(localExtras, AdmobConst.ADAPTIVE_TYPE, 0);
        int orientation = ATInitMediation.getIntFromMap(localExtras, AdmobConst.ADAPTIVE_ORIENTATION, 0);
        int width = px2dip(context, localExtraWidth);
        switch (orientation) {
            case AdmobConst.ORIENTATION_PORTRAIT:
                if (adaptiveType == AdmobConst.ADAPTIVE_INLINE) {
                    adSize = AdSize.getPortraitInlineAdaptiveBannerAdSize(context, width);
                } else {
                    adSize = AdSize.getPortraitAnchoredAdaptiveBannerAdSize(context, width);
                }
                break;
            case AdmobConst.ORIENTATION_LANDSCAPE:
                if (adaptiveType == AdmobConst.ADAPTIVE_INLINE) {
                    adSize = AdSize.getLandscapeInlineAdaptiveBannerAdSize(context, width);
                } else {
                    adSize = AdSize.getLandscapeAnchoredAdaptiveBannerAdSize(context, width);
                }
                break;
            default:
                if (adaptiveType == AdmobConst.ADAPTIVE_INLINE) {
                    adSize = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(context, width);
                } else {
                    adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width);
                }
                break;
        }
        return adSize;
    }

    private static AdSize getAdSizeFromServiceSize(Context context, String size) {
        AdSize adSize;
        switch (size) {
            case "320x100":
                adSize = AdSize.LARGE_BANNER;
                break;
            case "300x250":
                adSize = AdSize.MEDIUM_RECTANGLE;
                break;
            case "468x60":
                adSize = AdSize.FULL_BANNER;
                break;
            case "728x90":
                adSize = AdSize.LEADERBOARD;
                break;
            case "adaptive":
                int localExtraWidth = Math.min(context.getResources().getDisplayMetrics().widthPixels,
                        context.getResources().getDisplayMetrics().heightPixels);
                int localExtraWidthDp = px2dip(context, localExtraWidth);
                adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, localExtraWidthDp);
                break;
            case "320x50":
            default:
                adSize = AdSize.BANNER;
                break;
        }

        return adSize;
    }

    protected static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
    }

    protected static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static void setDisableAutoUseAdaptiveBanner(boolean disableAutoUseAdaptive) {
        Log.e(TAG, "setDisableAutoUseAdaptiveBanner: " + disableAutoUseAdaptive);
        disableAutoUseAdaptiveBanner = disableAutoUseAdaptive;
    }
}

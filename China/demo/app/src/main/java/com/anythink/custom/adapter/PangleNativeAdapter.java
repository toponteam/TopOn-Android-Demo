package com.anythink.custom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.anythink.core.api.ATAdConst;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.anythink.nativead.unitgroup.api.CustomNativeAdapter;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTDrawFeedAd;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PangleNativeAdapter extends CustomNativeAdapter {
    private final String TAG = getClass().getSimpleName();

    String slotId;

    /**
     * //Is Native Feeds? "0"：yes， "1"：no
     */
    String layoutType;
    String nativeType;

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtra, final Map<String, Object> localExtra) {

        String appId = (String) serverExtra.get("app_id");
        slotId = (String) serverExtra.get("slot_id");

        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "toutiao app_id or slot_id is empty!");
            }
            return;
        }

        layoutType = "1";

        if (serverExtra.containsKey("layout_type")) {
            layoutType = (String) serverExtra.get("layout_type");
        }

        int requestNum = 1;
        try {
            requestNum = Integer.parseInt(serverExtra.get(CustomNativeAd.AD_REQUEST_NUM).toString());
        } catch (Exception e) {
        }

        if (serverExtra.containsKey("is_video")) {
            nativeType = serverExtra.get("is_video").toString();
        }

        int mediaSize = 0;
        try {
            if (serverExtra.containsKey("media_size")) {
                mediaSize = Integer.parseInt(serverExtra.get("media_size").toString());
            }
        } catch (Exception e) {

        }

        final int finalRequestNum = requestNum;
        final int finalMediaSize = mediaSize;
        PangleInitManager.getInstance().initSDK(context, serverExtra, new PangleInitManager.InitCallback() {
            @Override
            public void onFinish() {
                startLoad(context, localExtra, finalRequestNum, finalMediaSize);
            }
        });
    }

    private void startLoad(final Context context, Map<String, Object> localExtra, int requestNum, int mediaSize) {
        TTAdManager ttAdManager = TTAdSdk.getAdManager();

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        if (localExtra != null) {

            Object widthObject = null;
           if (localExtra.containsKey(ATAdConst.KEY.AD_WIDTH)) {
                widthObject = localExtra.get(ATAdConst.KEY.AD_WIDTH);
            }

            Object heightObject = null;
            if (localExtra.containsKey(ATAdConst.KEY.AD_HEIGHT)) {
                heightObject = localExtra.get(ATAdConst.KEY.AD_HEIGHT);
            }

            if (mediaSize == 1) { //690*388
                width = 690;
                height = 388;
            } else if (mediaSize == 2) { //228*150
                width = 228;
                height = 150;
            } else {
                try {
                    if (widthObject instanceof Integer || widthObject instanceof String) {
                        width = Integer.parseInt(widthObject.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (heightObject instanceof Integer || heightObject instanceof String) {
                        height = Integer.parseInt(heightObject.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }


        TTAdNative mTTAdNative = ttAdManager.createAdNative(context);//baseContext建议为activity
        AdSlot.Builder adSlotBuilder = new AdSlot.Builder().setCodeId(slotId);

        if (width > 0 && height > 0) {
            adSlotBuilder.setImageAcceptedSize(width, height); //必须设置
        } else {
            adSlotBuilder.setImageAcceptedSize(640, 320); //必须设置
        }
        adSlotBuilder.setAdCount(requestNum);
        adSlotBuilder.setSupportDeepLink(true);


        //Native Express
        if (TextUtils.equals("0", nativeType) && TextUtils.equals("0", layoutType)) {
            Log.i(TAG, "load Native Express Ad");
            // set size, unit: dp
            adSlotBuilder.setExpressViewAcceptedSize(px2dip(context, width), px2dip(context, height)); //Must be set
            mTTAdNative.loadNativeExpressAd(adSlotBuilder.build(), new TTAdNative.NativeExpressAdListener() {
                @Override
                public void onError(int i, String s) {
                    if (mLoadListener != null) {
                        mLoadListener.onAdLoadError(String.valueOf(i), s);
                    }
                }

                @Override
                public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
                    final List<PangleNativeExpressAd> customNativeAds = new ArrayList<>();
                    for (final TTNativeExpressAd ttNativeExpressAd : list) {
                        PangleNativeExpressAd ttNativeAd = new PangleNativeExpressAd(context, slotId, ttNativeExpressAd, false);
                        customNativeAds.add(ttNativeAd);
                    }

                    handleExpressAdRender(customNativeAds);
                }
            });
            return;
        }

        //Native Express Video
        if (TextUtils.equals("1", nativeType)) {
            Log.i(TAG, "load Native Express Video");
            // set size, unity: dp
            adSlotBuilder.setExpressViewAcceptedSize(px2dip(context, width), px2dip(context, height)); //Must be set
            mTTAdNative.loadExpressDrawFeedAd(adSlotBuilder.build(), new TTAdNative.NativeExpressAdListener() {
                @Override
                public void onError(int i, String s) {
                    if (mLoadListener != null) {
                        mLoadListener.onAdLoadError(String.valueOf(i), s);
                    }
                }

                @Override
                public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
                    List<PangleNativeExpressAd> customNativeAds = new ArrayList<>();
                    for (TTNativeExpressAd ttNativeExpressAd : list) {
                        PangleNativeExpressAd ttNativeAd = new PangleNativeExpressAd(context, slotId, ttNativeExpressAd, true);
                        customNativeAds.add(ttNativeAd);
                    }

                    handleExpressAdRender(customNativeAds);
                }
            });
            return;
        }

        //  Custom rendering-------------------------------------------------------------------------------------------------------------------------------------
        /**Load different ads based on Native type**/
        switch (nativeType) {
            case "0": //Information Flow
                mTTAdNative.loadFeedAd(adSlotBuilder.build(), new TTAdNative.FeedAdListener() {
                    @Override
                    public void onError(int i, String s) {
                        if (mLoadListener != null) {
                            mLoadListener.onAdLoadError(String.valueOf(i), s);
                        }

                    }

                    @Override
                    public void onFeedAdLoad(List<TTFeedAd> list) {
                        List<CustomNativeAd> resultList = new ArrayList<>();
                        for (TTFeedAd ttFeedAd : list) {
                            PangleNativeAd ttNativeAd = new PangleNativeAd(context, slotId, ttFeedAd);
                            resultList.add(ttNativeAd);
                        }

                        if (mLoadListener != null) {
                            CustomNativeAd[] customNativeAds = new CustomNativeAd[resultList.size()];
                            customNativeAds = resultList.toArray(customNativeAds);
                            mLoadListener.onAdCacheLoaded(customNativeAds);
                        }
                    }
                });
                break;
            case "1": //Video stream
                mTTAdNative.loadDrawFeedAd(adSlotBuilder.build(), new TTAdNative.DrawFeedAdListener() {
                    @Override
                    public void onError(int i, String s) {
                        if (mLoadListener != null) {
                            mLoadListener.onAdLoadError(String.valueOf(i), s);
                        }

                    }

                    @Override
                    public void onDrawFeedAdLoad(List<TTDrawFeedAd> list) {
                        List<CustomNativeAd> resultList = new ArrayList<>();
                        for (TTFeedAd ttFeedAd : list) {
                            PangleNativeAd ttNativeAd = new PangleNativeAd(context, slotId, ttFeedAd);
                            resultList.add(ttNativeAd);
                        }
                        if (mLoadListener != null) {
                            CustomNativeAd[] customNativeAds = new CustomNativeAd[resultList.size()];
                            customNativeAds = resultList.toArray(customNativeAds);
                            mLoadListener.onAdCacheLoaded(customNativeAds);
                        }
                    }
                });
                break;

            case "2": //Native Banner
                adSlotBuilder.setNativeAdType(AdSlot.TYPE_BANNER);
                mTTAdNative.loadNativeAd(adSlotBuilder.build(), new TTAdNative.NativeAdListener() {
                    @Override
                    public void onError(int i, String s) {
                        if (mLoadListener != null) {
                            mLoadListener.onAdLoadError(String.valueOf(i), s);
                        }
                    }

                    @Override
                    public void onNativeAdLoad(List<TTNativeAd> list) {
                        List<CustomNativeAd> resultList = new ArrayList<>();
                        for (TTNativeAd ttFeedAd : list) {
                            PangleNativeAd ttNativeAd = new PangleNativeAd(context, slotId, ttFeedAd);
                            resultList.add(ttNativeAd);
                        }

                        if (mLoadListener != null) {
                            CustomNativeAd[] customNativeAds = new CustomNativeAd[resultList.size()];
                            customNativeAds = resultList.toArray(customNativeAds);
                            mLoadListener.onAdCacheLoaded(customNativeAds);
                        }
                    }
                });
                break;
            case "3": //Native Interstitial
                adSlotBuilder.setNativeAdType(AdSlot.TYPE_INTERACTION_AD);
                mTTAdNative.loadNativeAd(adSlotBuilder.build(), new TTAdNative.NativeAdListener() {
                    @Override
                    public void onError(int i, String s) {
                        if (mLoadListener != null) {
                            mLoadListener.onAdLoadError(String.valueOf(i), s);
                        }
                    }

                    @Override
                    public void onNativeAdLoad(List<TTNativeAd> list) {
                        List<CustomNativeAd> resultList = new ArrayList<>();
                        for (TTNativeAd ttFeedAd : list) {
                            PangleNativeAd ttNativeAd = new PangleNativeAd(context, slotId, ttFeedAd);
                            resultList.add(ttNativeAd);
                        }

                        if (mLoadListener != null) {
                            CustomNativeAd[] customNativeAds = new CustomNativeAd[resultList.size()];
                            customNativeAds = resultList.toArray(customNativeAds);
                            mLoadListener.onAdCacheLoaded(customNativeAds);
                        }
                    }
                });
                break;
            default:
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError("", "The Native type is not exit.");
                }
                break;
        }
    }

    private static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
    }

    private void handleExpressAdRender(List<PangleNativeExpressAd> customNativeAds) {
        final List<CustomNativeAd> resultList = new ArrayList<>();
        final int[] size = new int[]{customNativeAds.size()};
        for (final PangleNativeExpressAd customNativeAd : customNativeAds) {
            customNativeAd.renderExpressAd(new TTNativeExpressAd.ExpressAdInteractionListener() {
                @Override
                public void onAdClicked(View view, int i) {
                }

                @Override
                public void onAdShow(View view, int i) {
                }

                @Override
                public void onRenderFail(View view, String s, int i) {
                    size[0] = size[0] - 1;
                    if (size[0] == 0) {
                        if (resultList.size() == 0) {
                            if (mLoadListener != null) {
                                mLoadListener.onAdLoadError(String.valueOf(i), s);
                            }
                        } else {
                            if (mLoadListener != null) {
                                CustomNativeAd[] customNativeAds = new CustomNativeAd[resultList.size()];
                                customNativeAds = resultList.toArray(customNativeAds);
                                mLoadListener.onAdCacheLoaded(customNativeAds);
                            }
                        }
                    }
                }

                @Override
                public void onRenderSuccess(View view, float v, float v1) {
                    resultList.add(customNativeAd);
                    size[0] = size[0] - 1;
                    if (size[0] == 0) {
                        if (mLoadListener != null) {
                            CustomNativeAd[] customNativeAds = new CustomNativeAd[resultList.size()];
                            customNativeAds = resultList.toArray(customNativeAds);
                            mLoadListener.onAdCacheLoaded(customNativeAds);
                        }
                    }

                }
            });
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
}

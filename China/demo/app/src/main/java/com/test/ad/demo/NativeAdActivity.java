/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anythink.china.api.ATAppDownloadListener;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdSourceStatusListener;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.nativead.api.ATNative;
import com.anythink.nativead.api.ATNativeAdView;
import com.anythink.nativead.api.ATNativeDislikeListener;
import com.anythink.nativead.api.ATNativeEventExListener;
import com.anythink.nativead.api.ATNativeMaterial;
import com.anythink.nativead.api.ATNativeNetworkListener;
import com.anythink.nativead.api.ATNativePrepareExInfo;
import com.anythink.nativead.api.ATNativePrepareInfo;
import com.anythink.nativead.api.NativeAd;
import com.anythink.nativead.api.NativeAdInteractionType;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.anythink.network.gdt.GDTATConst;
import com.anythink.network.toutiao.TTATConst;
import com.test.ad.demo.util.PlacementIdUtil;
import com.test.ad.demo.utils.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdActivity extends Activity {

    private static final String TAG = NativeAdActivity.class.getSimpleName();

    ATNative mATNative;
    ATNativeAdView anyThinkNativeAdView;
    NativeAd mNativeAd;
    ATNativePrepareInfo mNativePrepareInfo;


    private Spinner mSpinner;
    private ViewGroup mAdContainer;

    private RelativeLayout rlNative;
    private RelativeLayout rlDraw;
    private RelativeLayout rlPatch;

    private TextView tvLoadAdBtn;
    private TextView tvIsAdReadyBtn;
    private TextView tvShowAdBtn;
    private TextView tvShowLog;

    private RecyclerView rvButtonList;

    int mType;
    final int TYPE_NATIVE = 0;
    final int TYPE_NATIVE_LIST = 1;
    private View mPanel;

    private Map<String, String> mPlacementIdMap;
    private String mCurrentNetworkName;
    private List<String> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_native);

        tvShowLog = findViewById(R.id.tv_show_log);
        tvShowLog.setMovementMethod(ScrollingMovementMethod.getInstance());

        TitleBar titleBar = (TitleBar) findViewById(R.id.title_bar);
        titleBar.setTitle(R.string.anythink_title_native);
        titleBar.setListener(new TitleBarClickListener() {
            @Override
            public void onBackClick(View v) {
                finish();
            }
        });

        rlNative = findViewById(R.id.rl_native);
        rlDraw = findViewById(R.id.rl_draw);
        rlPatch = findViewById(R.id.rl_patch);
        tvLoadAdBtn = findViewById(R.id.load_ad_btn);
        tvIsAdReadyBtn = findViewById(R.id.is_ad_ready_btn);
        tvShowAdBtn = findViewById(R.id.show_ad_btn);

        initTypeSpinner();

        mPlacementIdMap = PlacementIdUtil.getNativePlacements(this);
        List<String> placementNameList = new ArrayList<>(mPlacementIdMap.keySet());


        mSpinner = (Spinner) findViewById(R.id.spinner_1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                NativeAdActivity.this, android.R.layout.simple_spinner_dropdown_item,
                placementNameList);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mCurrentNetworkName = parent.getItemAtPosition(position).toString();
//                Toast.makeText(getApplicationContext(), mCurrentNetworkName, Toast.LENGTH_SHORT).show();

                ATNative.entryAdScenario(mPlacementIdMap.get(mCurrentNetworkName), "");

                init(mPlacementIdMap.get(mCurrentNetworkName));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mCurrentNetworkName = placementNameList.get(0);
        init(mPlacementIdMap.get(mCurrentNetworkName));

        int padding = dip2px(10);
        final int containerHeight = dip2px(340);
        final int adViewWidth = getResources().getDisplayMetrics().widthPixels - 2 * padding;
        final int adViewHeight = containerHeight - 2 * padding;


        tvLoadAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAd(adViewWidth, adViewHeight);
            }
        });

        tvIsAdReadyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAdReady();
            }
        });

        tvShowAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType == TYPE_NATIVE) {

                    showAd(mAdContainer, adViewWidth, adViewHeight);

                } else if (mType == TYPE_NATIVE_LIST) {

                    Intent intent = new Intent(NativeAdActivity.this, NativeListActivity.class);
                    intent.putExtra("placementId", mPlacementIdMap.get(mCurrentNetworkName));

                    startActivity(intent);
                }
            }
        });

//        findViewById(R.id.iv_close_panel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mData.clear();
//                destroyAd();
//                mPanel.setVisibility(View.GONE);
//            }
//        });

        initPanel();
    }

    private void initTypeSpinner() {
        Spinner typeSpinner = (Spinner) findViewById(R.id.spinner_2);
        typeSpinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NativeAdActivity.this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{
                        "Native",
                        "Native List"
                });
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String typeName = parent.getItemAtPosition(position).toString();
//                Toast.makeText(NativeAdActivity.this, typeName, Toast.LENGTH_SHORT).show();

                switch (position) {
                    case 0:
                        mType = TYPE_NATIVE;
                        break;
                    case 1:
                        mType = TYPE_NATIVE_LIST;
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void init(String placementId) {
        mATNative = new ATNative(this, placementId, new ATNativeNetworkListener() {
            @Override
            public void onNativeAdLoaded() {
                Log.i(TAG, "onNativeAdLoaded");
                ViewUtil.printLog(tvShowLog, "load success...");
            }

            @Override
            public void onNativeAdLoadFail(AdError adError) {
                Log.i(TAG, "onNativeAdLoadFail, " + adError.getFullErrorInfo());
                ViewUtil.printLog(tvShowLog, "load fail...：" + adError.getFullErrorInfo());
            }
        });

        mATNative.setAdSourceStatusListener(new ATAdSourceStatusListener() {
            @Override
            public void onAdSourceBiddingAttempt(ATAdInfo adInfo) {
                Log.i(TAG, "onAdSourceBiddingAttempt: " + adInfo.toString());
            }

            @Override
            public void onAdSourceBiddingFilled(ATAdInfo adInfo) {
                Log.i(TAG, "onAdSourceBiddingFilled: " + adInfo.toString());
            }

            @Override
            public void onAdSourceBiddingFail(ATAdInfo adInfo, AdError adError) {
                Log.i(TAG, "onAdSourceBiddingFail Info: " + adInfo.toString());
                Log.i(TAG, "onAdSourceBiddingFail error: " + adError.getFullErrorInfo());
            }

            @Override
            public void onAdSourceAttempt(ATAdInfo adInfo) {
                Log.i(TAG, "onAdSourceAttempt: " + adInfo.toString());
            }

            @Override
            public void onAdSourceLoadFilled(ATAdInfo adInfo) {
                Log.i(TAG, "onAdSourceLoadFilled: " + adInfo.toString());
            }

            @Override
            public void onAdSourceLoadFail(ATAdInfo adInfo, AdError adError) {
                Log.i(TAG, "onAdSourceLoadFail Info: " + adInfo.toString());
                Log.i(TAG, "onAdSourceLoadFail error: " + adError.getFullErrorInfo());
            }
        });
    }

    private void loadAd(int adViewWidth, int adViewHeight) {
        Map<String, Object> localMap = new HashMap<>();

        // since v5.6.4
        localMap.put(ATAdConst.KEY.AD_WIDTH, adViewWidth);
        localMap.put(ATAdConst.KEY.AD_HEIGHT, adViewHeight);
        localMap.put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, 0);
        localMap.put(GDTATConst.AD_HEIGHT, -2);

        mATNative.setLocalExtra(localMap);

        mATNative.makeAdRequest();
    }

    private void isAdReady() {
        boolean isReady = mATNative.checkAdStatus().isReady();
        Log.i(TAG, "isAdReady: " + isReady);
        ViewUtil.printLog(tvShowLog, "isAdReady：" + isReady);
    }

    private void showAd(ViewGroup adContainer, int adViewWidth, int adViewHeight) {
        if (adContainer == null) {
            Log.e(TAG, "showAd: adContainer = null");
            return;
        }

        NativeAd nativeAd = mATNative.getNativeAd();
        if (nativeAd != null) {

            if (mNativeAd != null) {
                mNativeAd.destory();
            }
            mNativeAd = nativeAd;

            mNativeAd.setNativeEventListener(new ATNativeEventExListener() {
                @Override
                public void onDeeplinkCallback(ATNativeAdView view, ATAdInfo adInfo, boolean isSuccess) {
                    Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
                }

                @Override
                public void onAdImpressed(ATNativeAdView view, ATAdInfo entity) {
                    Log.i(TAG, "native ad onAdImpressed:\n" + entity.toString());
                    ViewUtil.printLog(tvShowLog, "onAdImpressed");
                }

                @Override
                public void onAdClicked(ATNativeAdView view, ATAdInfo entity) {
                    Log.i(TAG, "native ad onAdClicked:\n" + entity.toString());
                    ViewUtil.printLog(tvShowLog, "onAdClicked");
                }

                @Override
                public void onAdVideoStart(ATNativeAdView view) {
                    Log.i(TAG, "native ad onAdVideoStart");
                    ViewUtil.printLog(tvShowLog, "onAdVideoStart");
                }

                @Override
                public void onAdVideoEnd(ATNativeAdView view) {
                    Log.i(TAG, "native ad onAdVideoEnd");
                    ViewUtil.printLog(tvShowLog, "onAdVideoEnd");
                }

                @Override
                public void onAdVideoProgress(ATNativeAdView view, int progress) {
                    Log.i(TAG, "native ad onAdVideoProgress:" + progress);
                    ViewUtil.printLog(tvShowLog, "onAdVideoProgress");
                }
            });

            mNativeAd.setDislikeCallbackListener(new ATNativeDislikeListener() {
                @Override
                public void onAdCloseButtonClick(ATNativeAdView view, ATAdInfo entity) {
                    Log.i(TAG, "native ad onAdCloseButtonClick");
                    ViewUtil.printLog(tvShowLog, "native ad onAdCloseButtonClick");
                    if (view.getParent() != null) {
                        ((ViewGroup) view.getParent()).removeView(view);
                        view.removeAllViews();
                    }

                    exitNativePanel();
                }
            });

            mNativeAd.setAdDownloadListener(new ATAppDownloadListener() {

                @Override
                public void onDownloadStart(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                    Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadStart: totalBytes: " + totalBytes
                            + "\ncurrBytes:" + currBytes
                            + "\nfileName:" + fileName
                            + "\nappName:" + appName);
                }

                @Override
                public void onDownloadUpdate(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                    Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadUpdate: totalBytes: " + totalBytes
                            + "\ncurrBytes:" + currBytes
                            + "\nfileName:" + fileName
                            + "\nappName:" + appName);
                }

                @Override
                public void onDownloadPause(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                    Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadPause: totalBytes: " + totalBytes
                            + "\ncurrBytes:" + currBytes
                            + "\nfileName:" + fileName
                            + "\nappName:" + appName);
                }

                @Override
                public void onDownloadFinish(ATAdInfo adInfo, long totalBytes, String fileName, String appName) {
                    Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadFinish: totalBytes: " + totalBytes
                            + "\nfileName:" + fileName
                            + "\nappName:" + appName);
                }

                @Override
                public void onDownloadFail(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                    Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadFail: totalBytes: " + totalBytes
                            + "\ncurrBytes:" + currBytes
                            + "\nfileName:" + fileName
                            + "\nappName:" + appName);
                }

                @Override
                public void onInstalled(ATAdInfo adInfo, String fileName, String appName) {
                    Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onInstalled:"
                            + "\nfileName:" + fileName
                            + "\nappName:" + appName);
                }
            });


            if (anyThinkNativeAdView == null) {
                anyThinkNativeAdView = new ATNativeAdView(this);
            } else {
                anyThinkNativeAdView.removeAllViews();
            }
            if (anyThinkNativeAdView.getParent() == null) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(adViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
//                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(adViewWidth, adViewHeight);
                layoutParams.gravity = Gravity.CENTER;
                adContainer.addView(anyThinkNativeAdView, layoutParams);
            }

            //log
            print(mNativeAd.getAdMaterial());


            int height = adViewWidth * 600 / 1024;
            height = height <= 0 ? FrameLayout.LayoutParams.WRAP_CONTENT : height;

            mNativePrepareInfo = null;

            try {
                if (mNativeAd.isNativeExpress()) {
                    mNativeAd.renderAdContainer(anyThinkNativeAdView, null);
                } else {
                    View selfRenderView = LayoutInflater.from(this).inflate(R.layout.native_ad_item, null);
                    mNativePrepareInfo = new ATNativePrepareExInfo();

                    SelfRenderViewUtil.bindSelfRenderView(this, mNativeAd.getAdMaterial(), selfRenderView, mNativePrepareInfo, height);

                    mNativeAd.renderAdContainer(anyThinkNativeAdView, selfRenderView);

                }
            } catch (Exception e) {

            }

            mNativeAd.prepare(anyThinkNativeAdView, mNativePrepareInfo);


            anyThinkNativeAdView.setVisibility(View.VISIBLE);

            mPanel.setVisibility(View.VISIBLE);
            initPanelButtonList(mNativeAd.getAdMaterial().getAdType());
        } else {
            ViewUtil.printLog(tvShowLog, "this placement no cache!");
        }
    }

    private void print(ATNativeMaterial adMaterial) {
        String adType = adMaterial.getAdType();
        switch (adType) {
            case CustomNativeAd.NativeAdConst.VIDEO_TYPE:
                Log.i(TAG, "Ad source type: Video" + ", video duration: " + adMaterial.getVideoDuration());
                break;
            case CustomNativeAd.NativeAdConst.IMAGE_TYPE:
                Log.i(TAG, "Ad source type: Image");
                break;
            default:
                Log.i(TAG, "Ad source type: Unknown");
                break;
        }
        switch (adMaterial.getNativeType()) {
            case CustomNativeAd.NativeType.FEED:
                Log.i(TAG, "Native type: Feed");
                break;
            case CustomNativeAd.NativeType.PATCH:
                Log.i(TAG, "Native type: Patch");
                break;
        }

        Log.i(TAG, "show native material:" +
                "getTitle:" + adMaterial.getTitle() + "\n" +
                "getDescriptionText:" + adMaterial.getDescriptionText() + "\n" +
                "getNativeType:" + adMaterial.getNativeType() + "\n" +
                "getAdMediaView:" + adMaterial.getAdMediaView(anyThinkNativeAdView, anyThinkNativeAdView.getWidth()) + "\n" +
                "getAdIconView:" + adMaterial.getAdIconView() + "\n" +
                "getMainImageUrl:" + adMaterial.getMainImageUrl() + "\n" +
                "getMainImageWidth:" + adMaterial.getMainImageWidth() + "\n" +
                "getMainImageHeight:" + adMaterial.getMainImageHeight() + "\n" +
                "getIconImageUrl:" + adMaterial.getIconImageUrl() + "\n" +
                "getCallToActionText:" + adMaterial.getCallToActionText() + "\n" +
                "getStarRating:" + adMaterial.getStarRating() + "\n" +
                "getVideoUrl:" + adMaterial.getVideoUrl() + "\n" +
                "getAdChoiceIconUrl:" + adMaterial.getAdChoiceIconUrl() + "\n" +
                "getAdFrom:" + adMaterial.getAdFrom() + "\n" +
                "getImageUrlList:" + adMaterial.getImageUrlList() + "\n" +
                "getNetworkInfoMap:" + adMaterial.getNetworkInfoMap() + "\n" +
                "getAdAppInfo:" + adMaterial.getAdAppInfo() + "\n" +
                "getNativeAdInteractionType:" + (adMaterial.getNativeAdInteractionType() == NativeAdInteractionType.APP_TYPE ? "Application" : "UNKNOWN") + "\n" +
                "getVideoDuration:" + adMaterial.getVideoDuration()
        );
    }

//    public void changeBg(View view,boolean selected) {
//        view.setBackgroundResource(selected ? R.drawable.bg_white_selected : R.drawable.bg_white);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tvShowLog = null;
        destroyAd();
        if (mATNative != null) {
            mATNative.setAdListener(null);
            mATNative.setAdSourceStatusListener(null);
        }
    }

    private void destroyAd() {
        if (mNativeAd != null) {
            mNativeAd.destory();
        }
    }

    @Override
    protected void onPause() {
        if (mNativeAd != null) {
            mNativeAd.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mNativeAd != null) {
            mNativeAd.onResume();
        }
        super.onResume();
    }

    public int dip2px(float dipValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private void initPanel() {
        mPanel = findViewById(R.id.rl_panel);
        mAdContainer = findViewById(R.id.ad_container);
        rvButtonList = findViewById(R.id.rv_button);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        rvButtonList.setLayoutManager(manager);

        final boolean isMute[] = new boolean[]{true};
        NativeVideoButtonAdapter adapter = new NativeVideoButtonAdapter(mData, new NativeVideoButtonAdapter.OnNativeVideoButtonCallback() {
            @Override
            public void onClick(String action) {
                if (action == VideoAction.VOICE_CHANGE) {
                    if (mNativeAd != null) {
                        mNativeAd.setVideoMute(!isMute[0]);
                        isMute[0] = !isMute[0];
                    }
                } else if (action == VideoAction.VIDEO_RESUME) {
                    if (mNativeAd != null) {
                        mNativeAd.resumeVideo();
                    }
                } else if (action == VideoAction.VIDEO_PAUSE) {
                    if (mNativeAd != null) {
                        mNativeAd.pauseVideo();
                    }
                } else if (action == VideoAction.VIDEO_PROGRESS) {
                    if (mNativeAd != null) {
                        String tips = "video duration: " + mNativeAd.getVideoDuration() + ", progress: " + mNativeAd.getVideoProgress();
                        Log.i(TAG, tips);
                        Toast.makeText(NativeAdActivity.this, tips, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        rvButtonList.setAdapter(adapter);
    }

    private void initPanelButtonList(String adType) {
        if (adType == CustomNativeAd.NativeAdConst.VIDEO_TYPE) {
            boolean isNativeExpress = true;
            if (mNativeAd != null) {
                isNativeExpress = mNativeAd.isNativeExpress();
            }

            if (isNativeExpress) {
                return;
            }

            ATAdInfo atAdInfo = mNativeAd.getAdInfo();
            int networkId = atAdInfo.getNetworkFirmId();

            switch (networkId) {
                case 8:
                    //for GDT
                    mData.add(VideoAction.VOICE_CHANGE);
                    mData.add(VideoAction.VIDEO_RESUME);
                    mData.add(VideoAction.VIDEO_PAUSE);
                    mData.add(VideoAction.VIDEO_PROGRESS);
                    break;
//                case 15:
//                    //for CSJ
//                    mData.add(VideoAction.VOICE_CHANGE);
//                    break;
                case 22:
                    //for BaiDu
                    mData.add(VideoAction.VIDEO_PROGRESS);
                    break;
                case 28:
                    //for KuaiShou
                    mData.add(VideoAction.VIDEO_PROGRESS);
                    break;
            }
        }
    }

    private void exitNativePanel() {
        mData.clear();
        destroyAd();
        mPanel.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mPanel.getVisibility() == View.VISIBLE) {
            exitNativePanel();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

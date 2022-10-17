/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdSourceStatusListener;
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
import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.test.ad.demo.util.PlacementIdUtil;
import com.test.ad.demo.utils.ViewUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdActivity extends Activity {

    private static final String TAG = NativeAdActivity.class.getSimpleName();

    ATNative mATNative;
    NativeAd mNativeAd;
    ATNativePrepareInfo mNativePrepareInfo;


    private Spinner mSpinner;
    private ATNativeAdView mATNativeAdView;
    private View mSelfRenderView;

    private TextView tvLoadAdBtn;
    private TextView tvIsAdReadyBtn;
    private TextView tvShowAdBtn;
    private TextView tvShowLog;

    private RecyclerView rvButtonList;

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
        titleBar.setListener(new TitleBarClickListener() {
            @Override
            public void onBackClick(View v) {
                finish();
            }
        });

        tvLoadAdBtn = findViewById(R.id.load_ad_btn);
        tvIsAdReadyBtn = findViewById(R.id.is_ad_ready_btn);
        tvShowAdBtn = findViewById(R.id.show_ad_btn);
        mSpinner = findViewById(R.id.spinner_1);

        String nativeType = getIntent().getStringExtra("native_type");

        if (nativeType.equals("1")) {
            mPlacementIdMap = PlacementIdUtil.getNativeSelfrenderPlacements(this);
            titleBar.setTitle(R.string.anythink_native_self);
        } else {
            mPlacementIdMap = PlacementIdUtil.getNativeExpressPlacements(this);
            titleBar.setTitle(R.string.anythink_native_express);
        }

        List<String> placementNameList = new ArrayList<>(mPlacementIdMap.keySet());

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

                init(mPlacementIdMap.get(mCurrentNetworkName));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mCurrentNetworkName = placementNameList.get(0);
        init(mPlacementIdMap.get(mCurrentNetworkName));

        initPanel();

        final int adViewWidth = getResources().getDisplayMetrics().widthPixels;
        final int adViewHeight = adViewWidth * 3 / 4;


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
               /*
                 To collect scene arrival rate statistics, you can view related information "https://docs.toponad.com/#/en-us/android/NetworkAccess/scenario/scenario"
                 Call the "Enter AD scene" method when an AD trigger condition is met, such as:
                 ** The scenario is a pop-up AD after the cleanup, which is called at the end of the cleanup.
                 * 1、Call "entryAdScenario" to report the arrival of the scene.
                 * 2、Call "ATNative#checkAdStatus#isReady".
                 * 3、Call "getNativeAd" to show AD view.
                 */
                ATNative.entryAdScenario(mPlacementIdMap.get(mCurrentNetworkName), "");
                if(isAdReady()){
                    showAd();
                }
            }
        });
    }

    private void initPanel() {
        mPanel = findViewById(R.id.rl_panel);
        mATNativeAdView = findViewById(R.id.native_ad_view);
        mSelfRenderView = findViewById(R.id.native_selfrender_view);
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
        Map<String, Object> localExtra = new HashMap<>();
        localExtra.put(ATAdConst.KEY.AD_WIDTH, adViewWidth);
        localExtra.put(ATAdConst.KEY.AD_HEIGHT, adViewHeight);

        mATNative.setLocalExtra(localExtra);
        mATNative.makeAdRequest();
    }

    private boolean isAdReady() {
        boolean isReady = mATNative.checkAdStatus().isReady();
        Log.i(TAG, "isAdReady: " + isReady);
        ViewUtil.printLog(tvShowLog, "isAdReady：" + isReady);
        return isReady;
    }

    private void showAd() {
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

                    exitNativePanel();
                }
            });

            mATNativeAdView.removeAllViews();
            //log
            print(mNativeAd.getAdMaterial());

            mNativePrepareInfo = null;

            try {
                mNativePrepareInfo = new ATNativePrepareExInfo();

                if (mNativeAd.isNativeExpress()) {
                    mNativeAd.renderAdContainer(mATNativeAdView, null);
                } else {
                    SelfRenderViewUtil.bindSelfRenderView(this, mNativeAd.getAdMaterial(), mSelfRenderView, mNativePrepareInfo);
                    mNativeAd.renderAdContainer(mATNativeAdView, mSelfRenderView);
                }

            } catch (Exception e) {

            }

            mNativeAd.prepare(mATNativeAdView, mNativePrepareInfo);
            mATNativeAdView.setVisibility(View.VISIBLE);
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

        Log.i(TAG, "show native material:" + "\n" +
                "getTitle:" + adMaterial.getTitle() + "\n" +
                "getDescriptionText:" + adMaterial.getDescriptionText() + "\n" +
                "getNativeType:" + adMaterial.getNativeType() + "\n" +
                "getAdMediaView:" + adMaterial.getAdMediaView() + "\n" +
                "getAdIconView:" + adMaterial.getAdIconView() + "\n" +
                "getIconImageUrl:" + adMaterial.getIconImageUrl() + "\n" +
                "getMainImageUrl:" + adMaterial.getMainImageUrl() + "\n" +
                "getMainImageWidth:" + adMaterial.getMainImageWidth() + "\n" +
                "getMainImageHeight:" + adMaterial.getMainImageHeight() + "\n" +
                "getVideoWidth:" + adMaterial.getVideoWidth() + "\n" +
                "getVideoHeight:" + adMaterial.getVideoHeight() + "\n" +
                "getAppPrice:" + adMaterial.getAppPrice() + "\n" +
                "getAppCommentNum:" + adMaterial.getAppCommentNum() + "\n" +
                "getCallToActionText:" + adMaterial.getCallToActionText() + "\n" +
                "getStarRating:" + adMaterial.getStarRating() + "\n" +
                "getVideoUrl:" + adMaterial.getVideoUrl() + "\n" +
                "getAdChoiceIconUrl:" + adMaterial.getAdChoiceIconUrl() + "\n" +
                "getAdFrom:" + adMaterial.getAdFrom() + "\n" +
                "getImageUrlList:" + adMaterial.getImageUrlList() + "\n" +
                "getNetworkInfoMap:" + adMaterial.getNetworkInfoMap() + "\n" +
                "getAdAppInfo:" + adMaterial.getAdAppInfo() + "\n" +
                "getNativeAdInteractionType:" + (adMaterial.getNativeAdInteractionType()) + "\n" +
                "getVideoDuration:" + adMaterial.getVideoDuration() + "\n" +
                "getAdvertiserName:" + adMaterial.getAdvertiserName() + "\n" +
                "getNativeType:" + adMaterial.getNativeType() + "\n" +
                "getAdType:" + adMaterial.getAdType() + "\n" +
                "getNativeCustomVideo:" + adMaterial.getNativeCustomVideo() + "\n" +
                "getAdLogo:" + adMaterial.getAdLogo() + "\n" +
                "getNativeExpressWidth:" + adMaterial.getNativeExpressWidth() + "\n" +
                "getNativeExpressHeight" + adMaterial.getNativeExpressHeight() + "\n"
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

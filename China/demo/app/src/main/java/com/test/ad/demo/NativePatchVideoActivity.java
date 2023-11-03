package com.test.ad.demo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.nativead.api.ATNative;
import com.anythink.nativead.api.ATNativeAdView;
import com.anythink.nativead.api.ATNativeDislikeListener;
import com.anythink.nativead.api.ATNativeEventExListener;
import com.anythink.nativead.api.ATNativeImageView;
import com.anythink.nativead.api.ATNativeMaterial;
import com.anythink.nativead.api.ATNativeNetworkListener;
import com.anythink.nativead.api.ATNativePrepareExInfo;
import com.anythink.nativead.api.ATNativePrepareInfo;
import com.anythink.nativead.api.ATNativeView;
import com.anythink.nativead.api.NativeAd;
import com.test.ad.demo.util.PlacementIdUtil;

import java.util.HashMap;
import java.util.Map;

//Use self-rendering ads as much as possible for patch ads, which can control the closing time of ads
public class NativePatchVideoActivity extends Activity {

    public final String TAG = getClass().getSimpleName();

    private String placementId;
    VideoView videoView;
    private ATNative mATNative;
    private ATNativeView anyThinkNativeView;
    private View mSelfRenderView;
    private View mAdLogoView;
    private TextView mAdTitleView;
    private TextView mPatchCountDownView;

    private ATNativePrepareInfo mNativePrepareInfo;
    private int adViewWidth;
    private int adViewHeight;

    ATNativeNetworkListener nativeNetworkListener = new ATNativeNetworkListener() {
        @Override
        public void onNativeAdLoaded() {
            Log.i(TAG, "onNativeAdLoaded");
            if (!hadBeenShowAd) {
                NativeAd nativeAd = mATNative.getNativeAd();
                impressionNativeAd = nativeAd;
                showAd(impressionNativeAd);
            }
        }

        @Override
        public void onNativeAdLoadFail(AdError adError) {
            videoView.start();
        }
    };

    NativeAd impressionNativeAd;

    CountDownTimer countDownTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_native_patch_video);

        placementId = PlacementIdUtil.getPatchPlacementId(this);

        videoView = findViewById(R.id.video_view);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_1));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
            }
        });

        mPatchCountDownView = findViewById(R.id.patch_count_down_text);
        anyThinkNativeView = findViewById(R.id.patch_native_view);
        mSelfRenderView = findViewById(R.id.patch_self_render_view);
        mAdLogoView = findViewById(R.id.patch_ad_logo);
        mAdTitleView = mSelfRenderView.findViewById(R.id.patch_title);

        adViewWidth = getResources().getDisplayMetrics().widthPixels;
        adViewHeight = getResources().getDisplayMetrics().widthPixels * 600 / 1024;

        videoView.getLayoutParams().height = adViewHeight;

        mATNative = new ATNative(this, placementId, nativeNetworkListener);
        impressionNativeAd = mATNative.getNativeAd();
        if (impressionNativeAd != null) {
            showAd(impressionNativeAd);
        }

        Map<String, Object> localMap = new HashMap<>();

        // since v5.6.4
        localMap.put(ATAdConst.KEY.AD_WIDTH, adViewWidth);
        localMap.put(ATAdConst.KEY.AD_HEIGHT, adViewHeight);
//        localMap.put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, 0);
//        localMap.put(GDTATConst.AD_HEIGHT, -2);

        mATNative.setLocalExtra(localMap);

        mATNative.makeAdRequest();
    }


    boolean hadBeenShowAd = false;

    private void showAd(NativeAd nativeAd) {
        hadBeenShowAd = true;
        nativeAd.setNativeEventListener(new ATNativeEventExListener() {
            @Override
            public void onDeeplinkCallback(ATNativeAdView view, ATAdInfo adInfo, boolean isSuccess) {
                Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
            }

            @Override
            public void onAdImpressed(ATNativeAdView view, ATAdInfo entity) {
                Log.i(TAG, "native ad onAdImpressed:\n" + entity.toString());
            }

            @Override
            public void onAdClicked(ATNativeAdView view, ATAdInfo entity) {
                Log.i(TAG, "native ad onAdClicked:\n" + entity.toString());
            }

            @Override
            public void onAdVideoStart(ATNativeAdView view) {
                Log.i(TAG, "native ad onAdVideoStart");
            }

            @Override
            public void onAdVideoEnd(ATNativeAdView view) {
                Log.i(TAG, "native ad onAdVideoEnd");
            }

            @Override
            public void onAdVideoProgress(ATNativeAdView view, int progress) {
                Log.i(TAG, "native ad onAdVideoProgress:" + progress);
            }
        });

        nativeAd.setDislikeCallbackListener(new ATNativeDislikeListener() {
            @Override
            public void onAdCloseButtonClick(ATNativeAdView view, ATAdInfo entity) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }

                mPatchCountDownView.setVisibility(View.GONE);

                if (videoView != null) {
                    videoView.start();
                }
            }
        });

        mNativePrepareInfo = null;

        try {
            if (nativeAd.isNativeExpress()) {
                mSelfRenderView.setVisibility(View.GONE);
                anyThinkNativeView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                nativeAd.renderAdContainer(anyThinkNativeView, null);
            } else {
                anyThinkNativeView.getLayoutParams().height = adViewHeight;
                mNativePrepareInfo = new ATNativePrepareExInfo();
                mSelfRenderView.setVisibility(View.VISIBLE);

                bindSelfRenderView(nativeAd.getAdMaterial());
                nativeAd.renderAdContainer(anyThinkNativeView, mSelfRenderView);
            }
        } catch (Exception e) {

        }
        nativeAd.prepare(anyThinkNativeView, mNativePrepareInfo);
        anyThinkNativeView.setVisibility(View.VISIBLE);

        startAdDismissCountdown(nativeAd);
    }

    private void startAdDismissCountdown(NativeAd nativeAd) {
        double dismissAdTime = nativeAd.getVideoDuration() * 1000;
        if (dismissAdTime <= 3000) {
            dismissAdTime = 10000;
        } else {
            dismissAdTime += 2000;
        }

        countDownTimer = new CountDownTimer((long) dismissAdTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mPatchCountDownView.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                anyThinkNativeView.setVisibility(View.GONE);
                if (impressionNativeAd != null) {
                    impressionNativeAd.destory();
                    impressionNativeAd = null;
                }
                mPatchCountDownView.setVisibility(View.GONE);
                if (videoView != null) {
                    videoView.start();
                }

            }
        };

        mPatchCountDownView.setText(String.valueOf(dismissAdTime / 1000));
        mPatchCountDownView.setVisibility(View.VISIBLE);
        countDownTimer.start();
    }

    public void bindSelfRenderView(ATNativeMaterial adMaterial) {
        FrameLayout contentArea = (FrameLayout) mSelfRenderView.findViewById(R.id.patch_main_image_content);


        String title = adMaterial.getTitle();
        // title
        if (!TextUtils.isEmpty(title)) {
            mAdTitleView.setText(title);
            mAdTitleView.setVisibility(View.VISIBLE);
        } else {
            mAdTitleView.setVisibility(View.GONE);
        }

        contentArea.removeAllViews();
        View mediaView = adMaterial.getAdMediaView(contentArea);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        // media view
        if (mediaView != null) {
            if (mediaView.getParent() != null) {
                ((ViewGroup) mediaView.getParent()).removeView(mediaView);
            }

            mediaView.setLayoutParams(params);
            contentArea.addView(mediaView, params);
        } else {
            ATNativeImageView imageView = new ATNativeImageView(this);
            imageView.setImage(adMaterial.getMainImageUrl());
            contentArea.addView(imageView, params);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mATNative != null) {
            mATNative.setAdListener(null);
            mATNative.setAdSourceStatusListener(null);
        }
        if (impressionNativeAd != null) {
            impressionNativeAd.destory();
            impressionNativeAd = null;
        }

        if (videoView != null) {
            videoView.stopPlayback();
        }
    }


    public int dip2px(float dipValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


}

package com.test.ad.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdRevenueListener;
import com.anythink.core.api.ATAdSourceStatusListener;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.ATShowConfig;
import com.anythink.core.api.AdError;
import com.anythink.splashad.api.ATSplashAd;
import com.anythink.splashad.api.ATSplashAdExtraInfo;
import com.anythink.splashad.api.ATSplashExListener;
import com.anythink.splashad.api.ATSplashSkipAdListener;
import com.anythink.splashad.api.ATSplashSkipInfo;
import com.test.ad.demo.base.BaseActivity;
import com.test.ad.demo.zoomout.SplashEyeAdHolder;
import com.test.ad.demo.zoomout.SplashZoomOutManager;

public class SplashAdShowActivity extends Activity implements ATSplashExListener {

    private static final String TAG = SplashAdShowActivity.class.getSimpleName();

    private ATSplashAd splashAd;
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_ad_show);
        initView();
        initData();
    }

    private void initView() {
        container = findViewById(R.id.splash_ad_container);
    }

    private void initData() {

        String placementId = getIntent().getStringExtra("placementId");
        splashAd = new ATSplashAd(this, placementId, this, 5000);
        splashAd.setNativeAdCustomRender(new BaseActivity.NativeAdCustomRender(this));
        splashAd.setAdRevenueListener(new ATAdRevenueListener() {
            @Override
            public void onAdRevenuePaid(ATAdInfo adInfo) {
                Log.i(TAG, "onAdRevenuePaid() >>> " + adInfo.toString());
            }
        });
        splashAd.setAdSourceStatusListener(new ATAdSourceStatusListener() {
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

        if (splashAd.isAdReady()) {
            Log.i(TAG, "SplashAd is ready to show.");
            //splashAd.show(SplashAdShowActivity.this, container);
            //showAdWithCustomSkipView();//show with customSkipView
            splashAd.show(SplashAdShowActivity.this, container, null, getATShowConfig());
        } else {
            Log.i(TAG, "SplashAd isn't ready to show, start to request.");
            splashAd.loadAd();
        }
    }

    @Override
    public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
        Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
    }

    private void showAdWithCustomSkipView() {
        TextView skipView = findViewById(R.id.splash_ad_skip);

        long countDownDuration = 5000;
        long callbackInterval = 1000;
        skipView.setText(((int) (countDownDuration / 1000)) + "s | Skip");

//        splashAd.show(this, container, new ATSplashSkipInfo(skipView, countDownDuration, callbackInterval, new ATSplashSkipAdListener() {
//            @Override
//            public void onAdTick(long duration, long remainder) {
//                skipView.setText(((int) (remainder / 1000)) + "s | Skip");
//            }
//
//            @Override
//            public void isSupportCustomSkipView(boolean isSupport) {
//                Log.i(TAG, "isSupportCustomSkipView: " + isSupport);
//                if (isSupport) {
//                    skipView.setVisibility(View.VISIBLE);
//                }
//            }
//        }));

        splashAd.show(this, container, new ATSplashSkipInfo(skipView, countDownDuration, callbackInterval, new ATSplashSkipAdListener() {
            @Override
            public void onAdTick(long duration, long remainder) {
                skipView.setText(((int) (remainder / 1000)) + "s | Skip");
            }

            @Override
            public void isSupportCustomSkipView(boolean isSupport) {
                Log.i(TAG, "isSupportCustomSkipView: " + isSupport);
                if (isSupport) {
                    skipView.setVisibility(View.VISIBLE);
                }
            }
        }), getATShowConfig());
    }

    @Override
    public void onAdLoaded(boolean isTimeout) {
        Log.i(TAG, "onAdLoaded---------isTimeout:" + isTimeout);

        if (!inForeBackground) {
            needShowSplashAd = true;
            return;
        }

        if (!splashAd.isAdReady()) {
            Log.e(TAG, "onAdLoaded: no cache");
            jumpToMainActivity();
            return;
        }

//        splashAd.show(this, container);
//        showAdWithCustomSkipView();//show with customSkipView
        splashAd.show(this, container, null, getATShowConfig());
    }

    @Override
    public void onAdLoadTimeout() {
        Log.i(TAG, "onAdLoadTimeout---------");
        Toast.makeText(getApplicationContext(), "onAdLoadTimeout", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onNoAdError(AdError adError) {
        Log.i(TAG, "onNoAdError---------:" + adError.getFullErrorInfo());
        jumpToMainActivity();
    }

    @Override
    public void onAdShow(ATAdInfo entity) {
        Log.i(TAG, "onAdShow:\n" + entity.toString());
    }

    @Override
    public void onAdClick(ATAdInfo entity) {
        Log.i(TAG, "onAdClick:\n" + entity.toString());
    }

    @Override
    public void onAdDismiss(ATAdInfo entity, ATSplashAdExtraInfo splashAdExtraInfo) {
        Log.i(TAG, "onAdDismiss type:" + splashAdExtraInfo.getDismissType() + "\n" + entity.toString());
        SplashEyeAdHolder.splashEyeAd = splashAdExtraInfo.getAtSplashEyeAd();
        jumpToMainActivity();
    }

    boolean hasHandleJump = false;
    boolean needJump;

    boolean inForeBackground;
    boolean needShowSplashAd;

    public void jumpToMainActivity() {

        if (!needJump) {
            needJump = true;
            return;
        }

        if (!hasHandleJump) {
            hasHandleJump = true;

            if (SplashEyeAdHolder.splashEyeAd != null) {
                try {
                    SplashZoomOutManager zoomOutManager = SplashZoomOutManager.getInstance(getApplicationContext());
                    zoomOutManager.setSplashInfo(container.getChildAt(0),
                            getWindow().getDecorView());
                } catch (Throwable e) {
                    Log.e(TAG, "jumpToMainActivity: ------------------------------------------ error");
                    e.printStackTrace();
                }

                Intent intent = new Intent(this, TestMainActivity.class);
                startActivity(intent);

                overridePendingTransition(0, 0);
            }
            Toast.makeText(this.getApplicationContext(), "start your MainActivity.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        inForeBackground = true;

        if (needJump) {
            jumpToMainActivity();
        }

        needJump = true;

        if (needShowSplashAd) {
            needShowSplashAd = false;

            if (splashAd.isAdReady()) {
//                splashAd.show(this, container);
                splashAd.show(this, container, AdConst.SCENARIO_ID.SPLASH_AD_SCENARIO);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        inForeBackground = false;
        needJump = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (container != null) {
            container.removeAllViews();
        }
        if (splashAd != null) {
            splashAd.setAdListener(null);
            splashAd.setAdDownloadListener(null);
            splashAd.setAdSourceStatusListener(null);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {

    }

    private ATShowConfig getATShowConfig() {
        ATShowConfig.Builder builder = new ATShowConfig.Builder();
        builder.scenarioId(AdConst.SCENARIO_ID.SPLASH_AD_SCENARIO);
        builder.showCustomExt(AdConst.SHOW_CUSTOM_EXT.SPLASH_AD_SHOW_CUSTOM_EXT);

        return builder.build();
    }
}

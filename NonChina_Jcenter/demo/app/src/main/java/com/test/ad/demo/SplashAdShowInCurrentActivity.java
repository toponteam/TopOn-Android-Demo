package com.test.ad.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdSourceStatusListener;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.splashad.api.ATSplashAd;
import com.anythink.splashad.api.ATSplashAdExtraInfo;
import com.anythink.splashad.api.ATSplashExListener;
import com.anythink.splashad.api.ATSplashEyeAdListener;
import com.anythink.splashad.api.IATSplashEyeAd;
import com.test.ad.demo.zoomout.SplashZoomOutManager;

import java.util.HashMap;
import java.util.Map;

public class SplashAdShowInCurrentActivity extends Activity {

    private static final String TAG = SplashAdShowInCurrentActivity.class.getSimpleName();

    ATSplashAd splashAd;
    FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ad_show_in_current);

        String placementId = getIntent().getStringExtra("placementId");
        container = findViewById(R.id.splash_ad_container);

        ATSplashAd.entryAdScenario(placementId, "");

        initSplash(placementId);


        findViewById(R.id.show_ad_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (splashAd.isAdReady()) {
                    if (container != null) {
                        container.setVisibility(View.VISIBLE);
                    }
                    splashAd.show(SplashAdShowInCurrentActivity.this, container);
                } else {
                    Toast.makeText(getApplicationContext(), "splash no cache.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.loadAd_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> localMap = new HashMap<>();
                localMap.put(ATAdConst.KEY.AD_WIDTH, getResources().getDisplayMetrics().widthPixels);
                localMap.put(ATAdConst.KEY.AD_HEIGHT, getResources().getDisplayMetrics().heightPixels);

                splashAd.setLocalExtra(localMap);

                splashAd.loadAd();
            }
        });

        findViewById(R.id.is_ad_ready_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                boolean isReady = splashAd.isAdReady();
                ATAdStatusInfo atAdStatusInfo = splashAd.checkAdStatus();
                Toast.makeText(getApplicationContext(), "splash ad ready status:" + atAdStatusInfo.isReady(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSplash(String placementId) {
        splashAd = new ATSplashAd(this, placementId, new ATSplashExListener() {

            @Override
            public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
                Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
            }

            @Override
            public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {

            }

//            @Override
//            public void onAdLoaded() {
//                Log.i(TAG, "onAdLoaded---------");
//                Toast.makeText(getApplicationContext(), "onAdLoaded", Toast.LENGTH_SHORT).show();
//            }

            @Override
            public void onAdLoaded(boolean isTimeout) {
                Log.i(TAG, "onAdLoaded---------isTimeout:" + isTimeout);
                Toast.makeText(getApplicationContext(), "onAdLoaded,isTimeout:" + isTimeout, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoadTimeout() {
                Log.i(TAG, "onAdLoadTimeout---------");
                Toast.makeText(getApplicationContext(), "onAdLoadTimeout", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoAdError(AdError adError) {
                Log.i(TAG, "onNoAdError---------:" + adError.getFullErrorInfo());
                Toast.makeText(getApplicationContext(), "onNoAdError: " + adError.getFullErrorInfo(), Toast.LENGTH_SHORT).show();
                if (container != null) {
                    container.removeAllViews();
                    container.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAdShow(ATAdInfo entity) {
                Log.i(TAG, "onAdShow:\n" + entity.toString());
                Toast.makeText(getApplicationContext(), "onAdShow", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClick(ATAdInfo entity) {
                Log.i(TAG, "onAdClick:\n" + entity.toString());
                Toast.makeText(getApplicationContext(), "onAdClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdDismiss(ATAdInfo entity, ATSplashAdExtraInfo splashAdExtraInfo) {
                Log.i(TAG, "onAdDismiss type:" + splashAdExtraInfo.getDismissType() + "\n" + entity.toString());
                Toast.makeText(getApplicationContext(), "onAdDismiss", Toast.LENGTH_SHORT).show();
                if (container != null) {
                    container.removeAllViews();
                    container.setVisibility(View.GONE);
                }

                showSplashEyeAd(splashAdExtraInfo.getAtSplashEyeAd());

            }

        }, 5000);

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
    }

    private void showSplashEyeAd(IATSplashEyeAd splashEyeAd) {
        if (splashEyeAd == null) {
            return;
        }

        splashEyeAd.show(SplashAdShowInCurrentActivity.this, null, new ATSplashEyeAdListener() {
            @Override
            public void onAnimationStart(View splashView) {

                SplashZoomOutManager zoomOutManager = SplashZoomOutManager.getInstance(getApplicationContext());

                int[] suggestedSize = splashEyeAd.getSuggestedSize(getApplicationContext());
                if (suggestedSize != null) {
                    zoomOutManager.setSplashEyeAdViewSize(suggestedSize[0], suggestedSize[1]);
                }

                zoomOutManager.setSplashInfo(splashView, getWindow().getDecorView());
                ViewGroup content = findViewById(android.R.id.content);
                zoomOutManager.startZoomOut(splashView, content, content, new SplashZoomOutManager.AnimationCallBack() {

                    @Override
                    public void animationStart(int animationTime) {

                    }

                    @Override
                    public void animationEnd() {
                        Log.i(TAG, "animationEnd---------: eye");
                        splashEyeAd.onFinished();
                    }
                });


            }

//            @Override
//            public void onAdClick() {
//                Log.i(TAG, "onAdClick---------: eye");
//            }

            @Override
            public void onAdDismiss(boolean isSupportEyeSplash, String errorMsg) {
                Log.i(TAG, "onAdDismiss---------: eye");
                SplashZoomOutManager zoomOutManager = SplashZoomOutManager.getInstance(getApplicationContext());
                zoomOutManager.clearStaticData();
                splashEyeAd.destroy();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        splashAd = null;
        container = null;
    }
}
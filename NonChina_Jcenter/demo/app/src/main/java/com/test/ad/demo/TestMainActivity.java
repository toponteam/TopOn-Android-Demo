package com.test.ad.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.anythink.splashad.api.ATSplashEyeAdListener;
import com.test.ad.demo.zoomout.SplashEyeAdHolder;
import com.test.ad.demo.zoomout.SplashZoomOutManager;

public class TestMainActivity extends Activity {

    public static final String TAG = TestMainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout rootLayout = getRootLayout();

        setContentView(rootLayout);
    }

    private FrameLayout getRootLayout() {
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setBackgroundColor(Color.WHITE);


        TextView textView = new TextView(this);

        textView.setText("Test Main Activity");
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(25);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        rootLayout.addView(textView, layoutParams);
        return rootLayout;
    }


    private void showSplashEyeAd() {

        if (SplashEyeAdHolder.splashEyeAd == null) {
            return;
        }

        SplashEyeAdHolder.splashEyeAd.show(TestMainActivity.this, null, new ATSplashEyeAdListener() {
            @Override
            public void onAnimationStart(View splashView) {
                SplashZoomOutManager zoomOutManager = SplashZoomOutManager.getInstance(getApplicationContext());


                int[] suggestedSize = SplashEyeAdHolder.splashEyeAd.getSuggestedSize(getApplicationContext());
                if (suggestedSize != null) {
                    zoomOutManager.setSplashEyeAdViewSize(suggestedSize[0], suggestedSize[1]);
                }
                zoomOutManager.startZoomOut((ViewGroup) getWindow().getDecorView(),
                        findViewById(android.R.id.content), new SplashZoomOutManager.AnimationCallBack() {

                            @Override
                            public void animationStart(int animationTime) {

                            }

                            @Override
                            public void animationEnd() {
                                Log.i(TAG, "animationEnd---------: eye");
                                SplashEyeAdHolder.splashEyeAd.onFinished();
                            }
                        });

            }

            @Override
            public void onAdDismiss(boolean isSupportEyeSplash, String errorMsg) {
                Log.i(TAG, "onAdDismiss---------: close eye ad");
                SplashZoomOutManager zoomOutManager = SplashZoomOutManager.getInstance(getApplicationContext());
                zoomOutManager.clearStaticData();

                SplashEyeAdHolder.splashEyeAd.destroy();
                SplashEyeAdHolder.splashEyeAd = null;
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        showSplashEyeAd();

    }
}

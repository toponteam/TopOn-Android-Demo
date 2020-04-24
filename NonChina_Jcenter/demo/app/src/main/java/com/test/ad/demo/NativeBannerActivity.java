package com.test.ad.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.anythink.core.api.ATAdInfo;
import com.anythink.nativead.banner.api.ATNativeBannerConfig;
import com.anythink.nativead.banner.api.ATNativeBannerListener;
import com.anythink.nativead.banner.api.ATNativeBannerSize;
import com.anythink.nativead.banner.api.ATNativeBannerView;

import java.util.HashMap;
import java.util.Map;

public class NativeBannerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_native_banner);


        final LinearLayout frameLayout = findViewById(R.id.native_banner_frame);

        final ATNativeBannerView bannerView = new ATNativeBannerView(this);
        ATNativeBannerConfig config640 = new ATNativeBannerConfig();
        config640.bannerSize = ATNativeBannerSize.BANNER_SIZE_640x150;
//        config640.backgroupResId = R.drawable.uparpu_test_bg;
        config640.ctaBgColor = 0xff000000;
        bannerView.setBannerConfig(config640);

        bannerView.setUnitId(DemoApplicaion.mPlacementId_native_all);
        bannerView.setVisibility(View.GONE);
        LinearLayout.LayoutParams params640 = new LinearLayout.LayoutParams(dip2px(this, 360), dip2px(this, 75));
        params640.topMargin = dip2px(this, 10);
        bannerView.setBackgroundColor(0xffffffff);
        frameLayout.addView(bannerView, params640);
        bannerView.setAdListener(new ATNativeBannerListener() {
            @Override
            public void onAdLoaded() {
                bannerView.setVisibility(View.VISIBLE);
                Log.i("BannerActivity", "640---onAdLoaded----");
            }

            @Override
            public void onAdError(String errorMsg) {
                Log.i("BannerActivity", "640---onAdError----:" + errorMsg);
            }

            @Override
            public void onAdClick(ATAdInfo entity) {
                Log.i("BannerActivity", "640---onAdClick----:\n" + entity.toString());
            }

            @Override
            public void onAdClose() {
                Log.i("BannerActivity", "640---onAdClose----");
            }

            @Override
            public void onAdShow(ATAdInfo entity) {
                Log.i("BannerActivity", "640---onAdShow----\n" + entity.toString());
            }

            @Override
            public void onAutoRefresh(ATAdInfo entity) {
                Log.i("BannerActivity", "640---onAutoRefresh----\n" + entity.toString());
            }

            @Override
            public void onAutoRefreshFail(String errorMsg) {
                Log.i("BannerActivity", "640---onAutoRefreshFail----:" + errorMsg);
            }
        });

        final ATNativeBannerView bannerView320 = new ATNativeBannerView(this);

        ATNativeBannerConfig config320 = new ATNativeBannerConfig();
        config320.bannerSize = ATNativeBannerSize.BANNER_SIZE_320x50;
        config320.ctaBgColor = 0xff000000;
        bannerView320.setBannerConfig(config320);

        bannerView320.setUnitId(DemoApplicaion.mPlacementId_native_facebook);
        bannerView320.setVisibility(View.GONE);
        bannerView320.setBackgroundColor(0xffffffff);
        LinearLayout.LayoutParams params320 = new LinearLayout.LayoutParams(dip2px(this, 320), dip2px(this, 50));
        params320.topMargin = dip2px(this, 10);
        frameLayout.addView(bannerView320, params320);

        bannerView320.setAdListener(new ATNativeBannerListener() {
            @Override
            public void onAdLoaded() {
                bannerView320.setVisibility(View.VISIBLE);
                Log.i("BannerActivity", "320---onAdLoaded");
            }

            @Override
            public void onAdError(String errorMsg) {
                Log.i("BannerActivity", "320---onAdError:" + errorMsg);
            }

            @Override
            public void onAdClick(ATAdInfo entity) {
                Log.i("BannerActivity", "320---onAdClick:n" + entity.toString());
            }

            @Override
            public void onAdClose() {
                Log.i("BannerActivity", "320---onAdClose----");
            }

            @Override
            public void onAdShow(ATAdInfo entity) {
                Log.i("BannerActivity", "320---onAdShow:\n" + entity.toString());
            }

            @Override
            public void onAutoRefresh(ATAdInfo entity) {
                Log.i("BannerActivity", "320---onAutoRefresh:\n" + entity.toString());
            }

            @Override
            public void onAutoRefreshFail(String errorMsg) {
                Log.i("BannerActivity", "auto---onAutoRefreshFail:" + errorMsg);
            }
        });


        final ATNativeBannerView bannerViewAuto = new ATNativeBannerView(this);
        bannerViewAuto.setUnitId(DemoApplicaion.mPlacementId_native_admob);
        bannerViewAuto.setVisibility(View.GONE);
        ATNativeBannerConfig configAuto = new ATNativeBannerConfig();
        configAuto.bannerSize = ATNativeBannerSize.BANNER_SIZE_AUTO;
        configAuto.isCtaBtnShow = true;
        configAuto.ctaBgColor = 0xff000000;
        configAuto.ctaColor = 0xff00ff00;
        configAuto.titleColor = 0xffffffff;
        bannerViewAuto.setBannerConfig(configAuto);
        final Map<String, Object> localMapAuto = new HashMap<>();
        bannerViewAuto.setLocalExtra(localMapAuto);

        bannerViewAuto.setBackgroundColor(0xffffffff);
        LinearLayout.LayoutParams paramsAuto = new LinearLayout.LayoutParams(dip2px(getApplicationContext(), 320), dip2px(getApplicationContext(), 205));
        paramsAuto.topMargin = dip2px(getApplicationContext(), 10);
        frameLayout.addView(bannerViewAuto, paramsAuto);

        bannerViewAuto.setAdListener(new ATNativeBannerListener() {
            @Override
            public void onAdLoaded() {
                bannerViewAuto.setVisibility(View.VISIBLE);
                Log.i("BannerActivity", "auto---onAdLoaded----");
            }

            @Override
            public void onAdError(String errorMsg) {
                Log.i("BannerActivity", "auto---onAdError----:" + errorMsg);
            }

            @Override
            public void onAdClick(ATAdInfo entity) {
                Log.i("BannerActivity", "auto---onAdClick:\n" + entity.toString());
            }

            @Override
            public void onAdClose() {
                Log.i("BannerActivity", "auto---onAdClose----");
            }

            @Override
            public void onAdShow(ATAdInfo entity) {
                Log.i("BannerActivity", "auto---onAdShow:\n" + entity.toString());
            }

            @Override
            public void onAutoRefresh(ATAdInfo entity) {
                Log.i("BannerActivity", "auto---onAutoRefresh:\n" + entity.toString());
            }

            @Override
            public void onAutoRefreshFail(String errorMsg) {
                Log.i("BannerActivity", "auto---onAutoRefreshFail:" + errorMsg);
            }
        });

        findViewById(R.id.load_native_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bannerView.loadAd(null);
                bannerView320.loadAd(null);
                bannerViewAuto.loadAd(null);
            }
        });

        findViewById(R.id.remove_native_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bannerView.setVisibility(View.INVISIBLE);
                bannerView320.setVisibility(View.INVISIBLE);
                bannerViewAuto.setVisibility(View.INVISIBLE);

            }
        });


    }

    public int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}

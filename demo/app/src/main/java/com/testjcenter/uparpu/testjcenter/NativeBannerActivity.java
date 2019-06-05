package com.testjcenter.uparpu.testjcenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.uparpu.nativead.banner.api.UpArpuNaitveBannerListener;
import com.uparpu.nativead.banner.api.UpArpuNaitveBannerSize;
import com.uparpu.nativead.banner.api.UpArpuNativeBannerConfig;
import com.uparpu.nativead.banner.api.UpArpuNativeBannerView;

import java.util.HashMap;
import java.util.Map;

public class NativeBannerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_native_banner);

        final Map<String, Object> localConfigMap = new HashMap<>();

        final LinearLayout frameLayout = findViewById(R.id.native_banner_frame);

        final UpArpuNativeBannerView bannerView = new UpArpuNativeBannerView(this);
        UpArpuNativeBannerConfig config640 = new UpArpuNativeBannerConfig();
        config640.bannerSize = UpArpuNaitveBannerSize.BANNER_SIZE_640x150;
//        config640.backgroupResId = R.drawable.uparpu_test_bg;
        config640.ctaBgColor = 0xff000000;
        bannerView.setBannerConfig(config640);

        bannerView.setUnitId(DemoApplicaion.mPlacementId_native_all);
        bannerView.setVisibility(View.GONE);
        LinearLayout.LayoutParams params640 = new LinearLayout.LayoutParams(dip2px(this, 360), dip2px(this, 75));
        params640.topMargin = dip2px(this, 10);
        bannerView.setBackgroundColor(0xffffffff);
        frameLayout.addView(bannerView, params640);
        bannerView.setAdListener(new UpArpuNaitveBannerListener() {
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
            public void onAdClick() {
                Log.i("BannerActivity", "640---onAdClick----");
            }

            @Override
            public void onAdClose() {
                Log.i("BannerActivity", "640---onAdClose----");
            }

            @Override
            public void onAdShow() {
                Log.i("BannerActivity", "640---onAdShow----");
            }

            @Override
            public void onAutoRefresh() {
                Log.i("BannerActivity", "640---onAutoRefresh----");
            }

            @Override
            public void onAutoRefreshFail(String errorMsg) {
                Log.i("BannerActivity", "640---onAutoRefreshFail----" + errorMsg);
            }
        });

        final UpArpuNativeBannerView bannerView320 = new UpArpuNativeBannerView(this);

        UpArpuNativeBannerConfig config320 = new UpArpuNativeBannerConfig();
        config320.bannerSize = UpArpuNaitveBannerSize.BANNER_SIZE_320x50;
//        config320.backgroupResId = R.drawable.uparpu_test_bg;
        config320.ctaBgColor = 0xff000000;
        bannerView320.setBannerConfig(config320);

        bannerView320.setUnitId(DemoApplicaion.mPlacementId_native_facebook);
        bannerView320.setVisibility(View.GONE);
        bannerView320.setBackgroundColor(0xffffffff);
        LinearLayout.LayoutParams params320 = new LinearLayout.LayoutParams(dip2px(this, 320), dip2px(this, 50));
        params320.topMargin = dip2px(this, 10);
        frameLayout.addView(bannerView320, params320);

        bannerView320.setAdListener(new UpArpuNaitveBannerListener() {
            @Override
            public void onAdLoaded() {
                bannerView320.setVisibility(View.VISIBLE);
                Log.i("BannerActivity", "320---onAdLoaded----");
            }

            @Override
            public void onAdError(String errorMsg) {
                Log.i("BannerActivity", "320---onAdError----:" + errorMsg);
            }

            @Override
            public void onAdClick() {
                Log.i("BannerActivity", "320---onAdClick----");
            }

            @Override
            public void onAdClose() {
                Log.i("BannerActivity", "320---onAdClose----");
            }

            @Override
            public void onAdShow() {
                Log.i("BannerActivity", "320---onAdShow----");
            }

            @Override
            public void onAutoRefresh() {
                Log.i("BannerActivity", "320---onAutoRefresh----");
            }

            @Override
            public void onAutoRefreshFail(String errorMsg) {
                Log.i("BannerActivity", "auto---onAutoRefreshFail----" + errorMsg);
            }
        });


        final UpArpuNativeBannerView bannerViewAuto = new UpArpuNativeBannerView(this);
        bannerViewAuto.setUnitId(DemoApplicaion.mPlacementId_native_admob);
        bannerViewAuto.setVisibility(View.GONE);
        UpArpuNativeBannerConfig configAuto = new UpArpuNativeBannerConfig();
        configAuto.bannerSize = UpArpuNaitveBannerSize.BANNER_SIZE_AUTO;
//                configAuto.backgroupResId = R.drawable.uparpu_test_bg;
        configAuto.isCtaBtnShow = true;
        configAuto.ctaBgColor = 0xff000000;
        configAuto.ctaColor = 0xff00ff00;
        configAuto.titleColor = 0xffffffff;
        bannerViewAuto.setBannerConfig(configAuto);

        bannerViewAuto.setBackgroundColor(0xffffffff);
        LinearLayout.LayoutParams paramsAuto = new LinearLayout.LayoutParams(dip2px(getApplicationContext(), 360), dip2px(getApplicationContext(), 35));
        paramsAuto.topMargin = dip2px(getApplicationContext(), 10);
        frameLayout.addView(bannerViewAuto, paramsAuto);

        bannerViewAuto.setAdListener(new UpArpuNaitveBannerListener() {
            @Override
            public void onAdLoaded() {
                Log.i("BannerActivity", "320---onAdLoaded----");
            }

            @Override
            public void onAdError(String errorMsg) {
                Log.i("BannerActivity", "320---onAdError----:" + errorMsg);
            }

            @Override
            public void onAdClick() {
                Log.i("BannerActivity", "320---onAdClick----");
            }

            @Override
            public void onAdClose() {
                Log.i("BannerActivity", "320---onAdClose----");
            }

            @Override
            public void onAdShow() {
                Log.i("BannerActivity", "320---onAdShow----");
            }

            @Override
            public void onAutoRefresh() {
                Log.i("BannerActivity", "320---onAutoRefresh----");
            }

            @Override
            public void onAutoRefreshFail(String errorMsg) {
                Log.i("BannerActivity", "320---onAutoRefreshFail----" + errorMsg);
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

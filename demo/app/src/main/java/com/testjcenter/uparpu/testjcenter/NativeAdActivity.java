package com.testjcenter.uparpu.testjcenter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.qq.e.ads.nativ.ADSize;
import com.uparpu.api.AdError;
import com.uparpu.nativead.api.NativeAd;
import com.uparpu.nativead.api.UpArpuNative;
import com.uparpu.nativead.api.UpArpuNativeAdView;
import com.uparpu.nativead.api.UpArpuNativeNetworkListener;
import com.uparpu.network.gdt.GDTUpArpuConst;

import java.util.HashMap;
import java.util.Map;

public class NativeAdActivity extends Activity {

    String unitIds[] = new String[]{
            DemoApplicaion.mPlacementId_native_all
            , DemoApplicaion.mPlacementId_native_facebook
            , DemoApplicaion.mPlacementId_native_admob
            , DemoApplicaion.mPlacementId_native_inmobi
            , DemoApplicaion.mPlacementId_native_flurry
            , DemoApplicaion.mPlacementId_native_applovin
            , DemoApplicaion.mPlacementId_native_mobivsta
            , DemoApplicaion.mPlacementId_native_mopub
            , DemoApplicaion.mPlacementId_native_GDT

    };

    String unitGroupName[] = new String[]{
            "All network",
            "facebook",
            "admob",
            "inmobi",
            "flurry",
            "applovin",
            "mintegral",
            "mopub",
            "gdt"
    };

    UpArpuNative upArapuNatives[] = new UpArpuNative[unitIds.length];
    UpArpuNativeAdView upArpuNativeAdView;
    NativeAd mNativeAd;

    RadioGroup mRadioGroup;

    int mCurrentSelectIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_native);

        mRadioGroup = (RadioGroup) findViewById(R.id.placement_select_group);

        for (int i = 0; i < unitIds.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setPadding(20, 20, 20, 20);                 // 设置文字距离按钮四周的距离
            radioButton.setText(unitGroupName[i]);
            radioButton.setId(i);
            mRadioGroup.addView(radioButton);
        }

        mRadioGroup.check(0);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mCurrentSelectIndex = i;
            }
        });

        final UpArpuRender upArpuRender = new UpArpuRender(this);

        Map<String, Object> localMap = null;
        for (int i = 0; i < unitIds.length; i++) {
            upArapuNatives[i] = new UpArpuNative(this, unitIds[i], new UpArpuNativeNetworkListener() {
                @Override
                public void onNativeAdLoaded(NativeAd nativeAd) {
                    if (nativeAd != null) {
                        mNativeAd = nativeAd;
                        mNativeAd.renderAdView(upArpuNativeAdView, upArpuRender);
                        upArpuNativeAdView.setVisibility(View.VISIBLE);
                        mNativeAd.prepare(upArpuNativeAdView);
                    }
                }

                @Override
                public void onNativeAdLoadFail(AdError adError) {
                    Toast.makeText(NativeAdActivity.this, "load fail...：" + adError.getDesc(), Toast.LENGTH_LONG).show();

                }
            });

            //如果是广点通的 需要配置额外配置
            if (i == GDTUpArpuConst.getGDTType()) {
                localMap = new HashMap<>();
                localMap.put(GDTUpArpuConst.ADTYPE, "3");
                localMap.put(GDTUpArpuConst.AD_WIDTH, ADSize.FULL_WIDTH);//
                localMap.put(GDTUpArpuConst.AD_HEIGHT, ADSize.FULL_WIDTH);//
                upArapuNatives[i].setLocalExtra(localMap);
            }

            if (upArpuNativeAdView == null) {
                upArpuNativeAdView = new UpArpuNativeAdView(this);
            }
        }


        findViewById(R.id.loadAd_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> maps = new HashMap<>();
                maps.put("age", "22");
                maps.put("sex", "lady");
                upArapuNatives[mCurrentSelectIndex].makeAdRequest(maps);
            }
        });

        findViewById(R.id.loadcache_ad_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NativeAd nativeAd = upArapuNatives[mCurrentSelectIndex].showAds();
                if (nativeAd != null) {
                    mNativeAd = nativeAd;
                    mNativeAd.renderAdView(upArpuNativeAdView, upArpuRender);
                    upArpuNativeAdView.setVisibility(View.VISIBLE);
                    mNativeAd.prepare(upArpuNativeAdView);
                } else {
                    Toast.makeText(NativeAdActivity.this, "this placement no cache!", Toast.LENGTH_LONG).show();

                }

            }
        });

        upArpuNativeAdView.setVisibility(View.GONE);
        ((FrameLayout) findViewById(R.id.ad_container)).addView(upArpuNativeAdView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNativeAd != null) {
            mNativeAd.destory();
        }
    }
}

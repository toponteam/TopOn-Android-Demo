package com.testjcenter.uparpu.testjcenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.uparpu.nativead.api.UpArpuNativeEventListener;
import com.uparpu.nativead.api.UpArpuNativeNetworkListener;
import com.uparpu.network.gdt.GDTUpArpuConst;
import com.uparpu.network.toutiao.TTUpArpuConst;

import java.util.HashMap;
import java.util.Map;

public class NativeAdActivity extends Activity {

    private static String TAG = "NativeAdActivity";
    String unitIds[] = new String[]{
            DemoApplicaion.mPlacementId_native_all
            , DemoApplicaion.mPlacementId_native_facebook
            , DemoApplicaion.mPlacementId_native_admob
            , DemoApplicaion.mPlacementId_native_inmobi
            , DemoApplicaion.mPlacementId_native_flurry
            , DemoApplicaion.mPlacementId_native_applovin
            , DemoApplicaion.mPlacementId_native_mintegral
            , DemoApplicaion.mPlacementId_native_mopub
            , DemoApplicaion.mPlacementId_native_GDT
            , DemoApplicaion.mPlacementId_native_mobpower
            , DemoApplicaion.mPlacementId_native_appnext
            , DemoApplicaion.mPlacementId_native_toutiao
            , DemoApplicaion.mPlacementId_native_toutiao_drawer
            , DemoApplicaion.mPlacementId_native_nend

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
            "gdt",
            "mobpower",
            "appnext",
            "toutiao",
            "toutiao_drawer",
            "nend"
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
                public void onNativeAdLoaded() {
                    Toast.makeText(NativeAdActivity.this, "load success...", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "onNativeAdLoaded: ");
                }

                @Override
                public void onNativeAdLoadFail(AdError adError) {
                    Toast.makeText(NativeAdActivity.this, "load fail...：" + adError.getDesc(), Toast.LENGTH_LONG).show();
                    Log.i(TAG, "onNativeAdLoadFail: "+adError.printStackTrace());

                }
            });

            localMap = new HashMap<>();
            //如果是广点通的 需要配置额外配置
            if (i == GDTUpArpuConst.NETWORK_FIRM_ID) {
                localMap.put(GDTUpArpuConst.ADTYPE, "3");
                localMap.put(GDTUpArpuConst.AD_WIDTH, ADSize.FULL_WIDTH);//
                localMap.put(GDTUpArpuConst.AD_HEIGHT, ADSize.FULL_WIDTH);//
            }

            localMap.put(TTUpArpuConst.NATIVE_AD_IMAGE_WIDTH, dip2px(this, 250));
            localMap.put(TTUpArpuConst.NATIVE_AD_IMAGE_HEIGHT, dip2px(this, 170));
            localMap.put(TTUpArpuConst.NATIVE_AD_INTERRUPT_VIDEOPLAY, true);

            upArapuNatives[i].setLocalExtra(localMap);

            if (upArpuNativeAdView == null) {
                upArpuNativeAdView = new UpArpuNativeAdView(this);
            }
        }


        findViewById(R.id.loadAd_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upArapuNatives[mCurrentSelectIndex].makeAdRequest();
            }
        });

        findViewById(R.id.loadcache_ad_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NativeAd nativeAd = upArapuNatives[mCurrentSelectIndex].getNativeAd();
                if (nativeAd != null) {
                    mNativeAd = nativeAd;
                    mNativeAd.setNativeEventListener(new UpArpuNativeEventListener() {
                        @Override
                        public void onAdImpressed(UpArpuNativeAdView upArpuNativeAdView) {
                            Log.i(TAG, "onAdImpressed: -----------");
                        }

                        @Override
                        public void onAdClicked(UpArpuNativeAdView upArpuNativeAdView) {
                            Log.i(TAG, "onAdClicked: -----------");
                        }

                        @Override
                        public void onAdVideoStart(UpArpuNativeAdView upArpuNativeAdView) {
                            Log.i(TAG, "onAdVideoStart: -----------");
                        }

                        @Override
                        public void onAdVideoEnd(UpArpuNativeAdView upArpuNativeAdView) {
                            Log.i(TAG, "onAdVideoEnd: -----------");
                        }

                        @Override
                        public void onAdVideoProgress(UpArpuNativeAdView upArpuNativeAdView, int i) {
                            Log.i(TAG, "onAdVideoProgress: -----------");
                        }
                    });

                    try {
                        mNativeAd.renderAdView(upArpuNativeAdView, upArpuRender);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}

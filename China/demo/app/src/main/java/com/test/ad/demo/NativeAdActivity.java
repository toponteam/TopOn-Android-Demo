package com.test.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.nativead.api.ATNative;
import com.anythink.nativead.api.ATNativeAdView;
import com.anythink.nativead.api.ATNativeDislikeListener;
import com.anythink.nativead.api.ATNativeEventListener;
import com.anythink.nativead.api.ATNativeNetworkListener;
import com.anythink.nativead.api.NativeAd;

import java.util.HashMap;
import java.util.Map;

public class NativeAdActivity extends Activity {

    private static String TAG = "NativeAdActivity";

    String unitIds[] = new String[]{
            DemoApplicaion.mPlacementId_native_all
            , DemoApplicaion.mPlacementId_native_mintegral
            , DemoApplicaion.mPLacementId_native_automatic_rending_mintegral
            , DemoApplicaion.mPlacementId_native_GDT
            , DemoApplicaion.mPlacementId_native_toutiao
            , DemoApplicaion.mPlacementId_native_toutiao_drawer
            , DemoApplicaion.mPlacementId_native_baidu
            , DemoApplicaion.mPlacementId_native_kuaishou
            , DemoApplicaion.mPlacementId_native_kuaishou_drawer
            , DemoApplicaion.mPlacementId_native_oneway

    };

    String unitGroupName[] = new String[]{
            "All network",
            "Mintegral",
            "Mintegral auto-rending",
            "GDT",
            "Toutiao",
            "Toutiao_drawer",
            "Baidu",
            "Kuaishou",
            "Kuaishou-draw",
            "Oneway"
    };

    ATNative atNatives[] = new ATNative[unitIds.length];
    ATNativeAdView anyThinkNativeAdView;
    NativeAd mNativeAd;

    int mCurrentSelectIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_native);

        Spinner spinner = (Spinner) findViewById(R.id.native_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                NativeAdActivity.this, android.R.layout.simple_spinner_dropdown_item,
                unitGroupName);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(NativeAdActivity.this,
                        parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                mCurrentSelectIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        int padding = dip2px(10);
        final int containerHeight = dip2px(340);
        final int adViewWidth = getResources().getDisplayMetrics().widthPixels - 2 * padding;
        final int adViewHeight = containerHeight - 2 * padding;

        final NativeDemoRender anyThinkRender = new NativeDemoRender(this);

        for (int i = 0; i < unitIds.length; i++) {
            atNatives[i] = new ATNative(this, unitIds[i], new ATNativeNetworkListener() {
                @Override
                public void onNativeAdLoaded() {
                    Log.i(TAG, "onNativeAdLoaded");
                    Toast.makeText(NativeAdActivity.this, "load success..."
                            , Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNativeAdLoadFail(AdError adError) {
                    Log.i(TAG, "onNativeAdLoadFail, " + adError.printStackTrace());
                    Toast.makeText(NativeAdActivity.this, "load fail...：" + adError.printStackTrace(), Toast.LENGTH_LONG).show();

                }
            });


            Map<String, Object> localMap = new HashMap<>();

            // since v5.6.4
            localMap.put(ATAdConst.KEY.AD_WIDTH, adViewWidth);
            localMap.put(ATAdConst.KEY.AD_HEIGHT, adViewHeight);

            // since v5.6.2
//            localMap.put(ATNative.KEY_WIDTH, adViewWidth);
//            localMap.put(ATNative.KEY_HEIGHT, adViewHeight);
//
//            // before v5.6.2
//            //Pangle
//            localMap.put(TTATConst.NATIVE_AD_IMAGE_WIDTH, adViewWidth);
//            localMap.put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, adViewHeight);
//            //Mintegral
//            localMap.put(MintegralATConst.AUTO_RENDER_NATIVE_WIDTH, adViewWidth);
//            localMap.put(MintegralATConst.AUTO_RENDER_NATIVE_HEIGHT, adViewHeight);
            // before v5.6.2

            atNatives[i].setLocalExtra(localMap);

            if (anyThinkNativeAdView == null) {
                anyThinkNativeAdView = new ATNativeAdView(this);
            }
        }


        findViewById(R.id.loadAd_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (anyThinkNativeAdView != null && anyThinkNativeAdView.getParent() == null) {
                    ((FrameLayout) findViewById(R.id.ad_container)).addView(anyThinkNativeAdView, new FrameLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, containerHeight));
                }

                atNatives[mCurrentSelectIndex].makeAdRequest();
            }
        });

        findViewById(R.id.loadcache_ad_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NativeAd nativeAd = atNatives[mCurrentSelectIndex].getNativeAd();
                if (nativeAd != null) {
                    if (mNativeAd != null) {
                        mNativeAd.destory();
                    }
                    mNativeAd = nativeAd;
                    mNativeAd.setNativeEventListener(new ATNativeEventListener() {
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
                    mNativeAd.setDislikeCallbackListener(new ATNativeDislikeListener() {
                        @Override
                        public void onAdCloseButtonClick(ATNativeAdView view, ATAdInfo entity) {
                            Log.i(TAG, "native ad onAdCloseButtonClick");
                            if (view.getParent() != null) {
                                ((ViewGroup) view.getParent()).removeView(view);
                            }
                        }
                    });
                    try {
                        mNativeAd.renderAdView(anyThinkNativeAdView, anyThinkRender);
                    } catch (Exception e) {

                    }

                    anyThinkNativeAdView.setVisibility(View.VISIBLE);
                    mNativeAd.prepare(anyThinkNativeAdView, anyThinkRender.getClickView(), null);
                } else {
                    Toast.makeText(NativeAdActivity.this, "this placement no cache!", Toast.LENGTH_LONG).show();

                }

            }
        });
        anyThinkNativeAdView.setPadding(padding, padding, padding, padding);

        anyThinkNativeAdView.setVisibility(View.GONE);
        ((FrameLayout) findViewById(R.id.ad_container)).addView(anyThinkNativeAdView, new FrameLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, containerHeight));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}

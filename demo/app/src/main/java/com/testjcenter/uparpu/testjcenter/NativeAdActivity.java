package com.testjcenter.uparpu.testjcenter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.qq.e.ads.nativ.ADSize;
import com.uparpu.api.AdError;
import com.uparpu.api.UpArpuSDK;
import com.uparpu.nativead.api.NativeAd;
import com.uparpu.nativead.api.UpArpuNative;
import com.uparpu.nativead.api.UpArpuNativeAdView;
import com.uparpu.nativead.api.UpArpuNativeNetworkListener;
import com.uparpu.network.admob.AdmobUpArpuAdapter;
import com.uparpu.network.applovin.ApplovinUpArpuAdapter;
import com.uparpu.network.gdt.GDTLocationKeyMaps;
import com.uparpu.network.inmobi.InmobiUpArpuAdapter;
import com.uparpu.network.mobvista.MobvistaUpArpuAdapter;
import com.uparpu.network.mopub.MopubUpArpuAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Z on 2018/1/11.
 */
public class NativeAdActivity extends Activity {

    String unitIds[] = new String[]{
            DemoApplicaion.mPlacementId_all
            , DemoApplicaion.mPlacementId_facebook
            , DemoApplicaion.mPlacementId_admob
            , DemoApplicaion.mPlacementId_inmobi
            , DemoApplicaion.mPlacementId_flurry
            , DemoApplicaion.mPlacementId_applovin
            , DemoApplicaion.mPlacementId_mobivsta
            , DemoApplicaion.mPlacementId_mopub
            , DemoApplicaion.mPlacementId_GDT};

    String unitGroupName[] = new String[]{
            "All network",
            "facebook",
            "admob",
            "inmobi",
            "flurry",
            "applovin",
            "mobvista",
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

        setContentView(R.layout.main_nativead_layout);
        Fresco.initialize(this);

        mRadioGroup = (RadioGroup)findViewById(R.id.placement_select_group);

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

        //        UpArpuNative upArpuNative = new UpArpuNative(Context,placementid,UpArpuNativeNetworkListener);

        UpArpuRender upArpuRender = new UpArpuRender(this);

        //        upArapuNatives[0].coerceCleanAllAdCache();
        Map<String,Object> localMap = null;
        for (int i = 0; i < unitIds.length; i++) {
            upArapuNatives[i] = new UpArpuNative(this, unitIds[i], new UpArpuNativeNetworkListener() {
                @Override
                public void onNativeAdLoaded(NativeAd nativeAd) {
                    if (nativeAd != null) {
                        mNativeAd = nativeAd;
                        try{
                            mNativeAd.renderAdView(upArpuNativeAdView);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        upArpuNativeAdView.setVisibility(View.VISIBLE);
                        mNativeAd.prepare(upArpuNativeAdView);
                    }
                }

                @Override
                public void onNativeAdLoadFail(AdError adError) {
                    Toast.makeText(NativeAdActivity.this, "load fail...：" + adError.getDesc(),Toast.LENGTH_LONG).show();

                }
            }, upArpuRender);

            //需要配置额外配置
            localMap = new HashMap<>();


            /***
             * 如果针对每个平台进行私有设置，可参考如下：
             */
            //
            //            if(i==1){//facebook 本地使用测试 --不支持GDPR
            //
            //            }
            //
            //            if(i==2){//admob 本地使用测试
            //
            //
            //                Log.e("localMap","admob setting.....");
            //                localMap.put(AdmobUpArpuAdapter.LOCATION_MAP_KEY_GDPR, true);// true 同意| false 不同意
            //
            //            }
            //            if(i==3){//inmob 本地使用测试
            //                //是否GDPR地区
            //                localMap.put(InmobiUpArpuAdapter.LOCATION_MAP_KEY_GDPR_SCOPE, "1");//1|0
            //
            //                //是否同意GDPR
            //                localMap.put(InmobiUpArpuAdapter.LOCATION_MAP_KEY_GDPR,true);//true | false
            //            }
            //            if(i==4){//furrly 本地使用测试--不支持GDPR
            //
            //            }
            //            if(i==5){//applovin 本地使用测试
            //                //是否同意GDPR
            //                localMap.put(ApplovinUpArpuAdapter.LOCATION_MAP_KEY_GDPR, true);
            //            }
            //            if(i==6){//mv 本地使用测试
            //                //是否同意GDPR协议
            //                localMap.put(MobvistaUpArpuAdapter.LOCATION_MAP_KEY_GDPR, MobVistaConstans.IS_SWITCH_ON);
            //                //授权信息的级别
            //                localMap.put(MobvistaUpArpuAdapter.LOCATION_MAP_KEY_GDPR_LEVEL,MobVistaConstans.AUTHORITY_ALL_INFO);
            //            }
            //            if(i==7){//MOPUB 本地使用测试
            //
            //                //是否同意GDPR
            //                localMap.put(MopubUpArpuAdapter.LOCATION_MAP_KEY_GDPR, true);
            //
            //            }
            if(i==8){//GDT 本地使用测试 不支持GDPR
                localMap.put(GDTLocationKeyMaps.ADTYPE,"3");
                localMap.put(GDTLocationKeyMaps.AD_WIDTH, ADSize.FULL_WIDTH);//
                localMap.put(GDTLocationKeyMaps.AD_HEIGHT,ADSize.FULL_WIDTH);//
            }

            upArapuNatives[i].setLocalExtra(localMap);

            if (upArpuNativeAdView == null) {
                upArpuNativeAdView = new UpArpuNativeAdView(this);
            }
        }




        findViewById(R.id.loadAd_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    HashMap<String,String> maps = new HashMap<>();
                    maps.put("age", "22");
                    maps.put("sex", "lady");
                    upArapuNatives[mCurrentSelectIndex].makeAdRequest(maps);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        findViewById(R.id.loadcache_ad_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NativeAd nativeAd = upArapuNatives[mCurrentSelectIndex].showAds();
                if (nativeAd != null) {
                    mNativeAd = nativeAd;
                    try {
                        mNativeAd.renderAdView(upArpuNativeAdView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    upArpuNativeAdView.setVisibility(View.VISIBLE);
                    mNativeAd.prepare(upArpuNativeAdView);
                } else {
                    Toast.makeText(NativeAdActivity.this, "this placement no cache!",Toast.LENGTH_LONG).show();

                }

            }
        });

        upArpuNativeAdView.setVisibility(View.GONE);
        ((FrameLayout)findViewById(R.id.ad_container)).addView(upArpuNativeAdView);


        findViewById(R.id.show_gdpr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpArpuSDK.showGdprAuth(NativeAdActivity.this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNativeAd != null) {
            mNativeAd.destory();
        }
        if (upArapuNatives != null) {
            for (UpArpuNative upArpuNative : upArapuNatives) {
                upArpuNative.coerceCleanAllAdCache();
                upArpuNative.destory();
            }
        }
        Fresco.shutDown();
        upArpuNativeAdView.removeAllViews();
    }
}

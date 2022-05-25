/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.splashad.api.ATSplashAd;
import com.anythink.splashad.api.ATSplashAdExtraInfo;
import com.anythink.splashad.api.ATSplashExListener;
import com.test.ad.demo.util.PlacementIdUtil;
import com.test.ad.demo.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SplashAdActivity extends Activity implements ATSplashExListener {

    private static final String TAG = SplashAdActivity.class.getSimpleName();

    String mCurrentPlacementName;
    ATSplashAd splashAd;

    private TextView tvShowLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        findViewById(R.id.rl_type).setSelected(true);
        tvShowLog = findViewById(R.id.tv_show_log);
        tvShowLog.setMovementMethod(ScrollingMovementMethod.getInstance());

        TitleBar titleBar = (TitleBar) findViewById(R.id.title_bar);
        titleBar.setTitle(R.string.anythink_title_splash);
        titleBar.setListener(new TitleBarClickListener() {
            @Override
            public void onBackClick(View v) {
                finish();
            }
        });

        String defaultConfig = "";

        //Mintegral
//        defaultConfig = "{\"unit_id\":1333033,\"nw_firm_id\":6,\"adapter_class\":\"com.anythink.network.mintegral.MintegralATSplashAdapter\",\"content\":\"{\\\"placement_id\\\":\\\"210169\\\",\\\"unitid\\\":\\\"276803\\\",\\\"countdown\\\":\\\"5\\\",\\\"allows_skip\\\":\\\"1\\\",\\\"orientation\\\":\\\"1\\\",\\\"appkey\\\":\\\"ef13ef712aeb0f6eb3d698c4c08add96\\\",\\\"suport_video\\\":\\\"1\\\",\\\"appid\\\":\\\"100947\\\"}\"}";

        //Tencent Ads
//        defaultConfig = "{\"unit_id\":1333176,\"nw_firm_id\":8,\"adapter_class\":\"com.anythink.network.gdt.GDTATSplashAdapter\",\"content\":\"{\\\"unit_id\\\":\\\"8863364436303842593\\\",\\\"zoomoutad_sw\\\":\\\"1\\\",\\\"app_id\\\":\\\"1101152570\\\"}\"}";

        //CSJ
//        defaultConfig = "{\"unit_id\":1333195,\"nw_firm_id\":15,\"adapter_class\":\"com.anythink.network.toutiao.TTATSplashAdapter\",\"content\":\"{\\\"personalized_template\\\":\\\"0\\\",\\\"zoomoutad_sw\\\":\\\"2\\\",\\\"button_type\\\":\\\"1\\\",\\\"dl_type\\\":\\\"2\\\",\\\"slot_id\\\":\\\"801121648\\\",\\\"app_id\\\":\\\"5001121\\\"}\"}";

        //Sigmob
//        defaultConfig = "{\"unit_id\":1333222,\"nw_firm_id\":29,\"adapter_class\":\"com.anythink.network.sigmob.SigmobATSplashAdapter\",\"content\":\"{\\\"placement_id\\\":\\\"ea1f8f21300\\\",\\\"app_id\\\":\\\"6878\\\",\\\"app_key\\\":\\\"8ebc1fd1c27e650c\\\"}\"}";

        //Baidu
//        defaultConfig = "{\"unit_id\":1329553,\"nw_firm_id\":22,\"adapter_class\":\"com.anythink.network.baidu.BaiduATSplashAdapter\",\"content\":\"{\\\"button_type\\\":\\\"0\\\",\\\"ad_place_id\\\":\\\"7854679\\\",\\\"app_id\\\":\\\"a7dd29d3\\\"}\"}";

        //Kuaishou
//        defaultConfig = "{\"unit_id\":1333246,\"nw_firm_id\":28,\"adapter_class\":\"com.anythink.network.ks.KSATSplashAdapter\",\"content\":\"{\\\"zoomoutad_sw\\\":\\\"1\\\",\\\"position_id\\\":\\\"4000000042\\\",\\\"app_id\\\":\\\"90009\\\",\\\"app_name\\\":\\\"90009\\\"}\"}";

        //Klevin
//        defaultConfig = "{\"unit_id\":1333253,\"nw_firm_id\":51,\"adapter_class\":\"com.anythink.network.klevin.KlevinATSplashAdapter\",\"content\":\"{\\\"pos_id\\\":\\\"30029\\\",\\\"app_id\\\":\\\"30008\\\"}\"}";

        Map<String, String> placementIdMap = PlacementIdUtil.getSplashPlacements(this);
        List<String> placementNameList = new ArrayList<>(placementIdMap.keySet());

        Spinner spinner = (Spinner) findViewById(R.id.spinner_1);
//        CheckBox isCustomSkipViewCheckBox = findViewById(R.id.splash_is_custom_skip);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                SplashAdActivity.this, android.R.layout.simple_spinner_dropdown_item,
                placementNameList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
//                Toast.makeText(getApplicationContext(),
//                        parent.getItemAtPosition(position).toString(),
//                        Toast.LENGTH_SHORT).show();
                mCurrentPlacementName = parent.getSelectedItem().toString();

                String placementName = placementIdMap.get(mCurrentPlacementName);
                init(placementName, defaultConfig);

//                if (TextUtils.equals(mCurrentPlacementName, "MyOffer")
//                        || TextUtils.equals(mCurrentPlacementName, "Adx(internal)")
//                        || TextUtils.equals(mCurrentPlacementName, "OnlineApi(internal)")
//                        || TextUtils.equals(mCurrentPlacementName, "Toutiao")) {
//                    isCustomSkipViewCheckBox.setVisibility(View.VISIBLE);
//                } else {
//                    isCustomSkipViewCheckBox.setVisibility(View.GONE);
//                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        findViewById(R.id.is_ad_ready_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAdReady();
            }
        });

        findViewById(R.id.load_ad_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd();
            }
        });

        findViewById(R.id.show_ad_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashAdActivity.this, SplashAdShowActivity.class);
                intent.putExtra("placementId", placementIdMap.get(mCurrentPlacementName));
//                intent.putExtra("custom_skip_view", isCustomSkipViewCheckBox.isChecked());
                startActivity(intent);
            }
        });

//        findViewById(R.id.show_in_current_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SplashAdActivity.this, SplashAdShowInCurrentActivity.class);
//                intent.putExtra("placementId", placementIdMap.get(mCurrentPlacementName));
//                startActivity(intent);
//            }
//        });


//        mCurrentPlacementName = placementNameList.get(0);
//        String placementName = placementIdMap.get(mCurrentPlacementName);
//        init(placementName,defaultConfig);
    }

    private void loadAd() {
        splashAd.loadAd();
    }

    private void isAdReady() {
        if (splashAd.isAdReady()) {
            Log.i(TAG, "SplashAd is ready to show.");
            ViewUtil.printLog(tvShowLog, "SplashAd is ready to show.");
        } else {
            Log.i(TAG, "SplashAd isn't ready to show.");
            ViewUtil.printLog(tvShowLog, "SplashAd isn't ready to show.");
        }
    }

    private void init(String placementId, String defaultConfig) {
        splashAd = new ATSplashAd(this, placementId, this, 5000, defaultConfig);
        ATSplashAd.entryAdScenario(placementId, "");
    }

    @Override
    public void onAdLoaded(boolean isTimeout) {
        Log.i(TAG, "onAdLoaded---------isTimeout:" + isTimeout);
        ViewUtil.printLog(tvShowLog, "onAdLoaded---------isTimeout:" + isTimeout);
    }

    @Override
    public void onAdLoadTimeout() {
        Log.i(TAG, "onAdLoadTimeout---------");
        ViewUtil.printLog(tvShowLog, "onAdLoadTimeout---------");
    }

    @Override
    public void onNoAdError(AdError adError) {
        Log.i(TAG, "onNoAdError---------:" + adError.getFullErrorInfo());
        ViewUtil.printLog(tvShowLog, "onNoAdError---------:" + adError.getFullErrorInfo());
    }

    @Override
    public void onAdShow(ATAdInfo entity) {

    }

    @Override
    public void onAdClick(ATAdInfo entity) {

    }

    @Override
    public void onAdDismiss(ATAdInfo entity, ATSplashAdExtraInfo splashAdExtraInfo) {

    }

    @Override
    public void onDeeplinkCallback(ATAdInfo entity, boolean isSuccess) {

    }

    @Override
    public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {

    }

    @Override
    protected void onDestroy() {
        tvShowLog = null;
        if (splashAd != null) {
            splashAd.setAdListener(null);
            splashAd.setAdDownloadListener(null);
            splashAd.setAdSourceStatusListener(null);
        }
        super.onDestroy();
    }
}

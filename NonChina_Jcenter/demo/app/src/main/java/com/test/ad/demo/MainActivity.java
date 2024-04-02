package com.test.ad.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anythink.core.api.ATSDK;
import com.anythink.core.api.DeviceInfoCallback;
import com.test.ad.demo.util.PlacementIdUtil;

import org.json.JSONObject;

public class MainActivity extends Activity implements View.OnClickListener {
    private RelativeLayout mRlNativeAd;
    private RelativeLayout mRlRewardVideoAd;
    private RelativeLayout mRlInterstitialAd;
    private RelativeLayout mRlBannerAd;
    private RelativeLayout mRlSplashAd;
    private RelativeLayout mRlMediaVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
        initData();
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_version)).setText(getResources().getString(R.string.anythink_sdk_version, ATSDK.getSDKVersionName()) + PlacementIdUtil.MODE);
        ((TextView) findViewById(R.id.tv_sdk_demo)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mRlNativeAd = findViewById(R.id.nativeBtn);
        mRlSplashAd = findViewById(R.id.splashBtn);
        mRlBannerAd = findViewById(R.id.bannerBtn);
        mRlInterstitialAd = findViewById(R.id.interstitialBtn);
        mRlRewardVideoAd = findViewById(R.id.rewardedVideoBtn);
        mRlMediaVideoAd = findViewById(R.id.media_videoBtn);
    }

    private void initListener() {
        mRlNativeAd.setOnClickListener(this);
        mRlSplashAd.setOnClickListener(this);
        mRlBannerAd.setOnClickListener(this);
        mRlInterstitialAd.setOnClickListener(this);
        mRlRewardVideoAd.setOnClickListener(this);
        mRlMediaVideoAd.setOnClickListener(this);
    }

    private void initData() {
        ATSDK.testModeDeviceInfo(this, new DeviceInfoCallback() {
            @Override
            public void deviceInfo(String deviceInfo) {
                if (!TextUtils.isEmpty(deviceInfo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(deviceInfo);
                        String gaid = jsonObject.optString("GAID");

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                TextView deviceIdTextView = (TextView) findViewById(R.id.tv_device_id);
                                deviceIdTextView.setText(getResources().getString(R.string.anythink_click_to_copy_device_id, gaid));
                                deviceIdTextView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        copyContentToClipboard(MainActivity.this, gaid);

                                        Toast.makeText(MainActivity.this, "Gaid：" + gaid, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void startAdPage(Class<?> adPageClass) {
        startActivity(new Intent(MainActivity.this, adPageClass));
    }

    public void copyContentToClipboard(Context context, String content) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", content);
        cm.setPrimaryClip(mClipData);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Class<?> adPageClass = null;
        switch (v.getId()) {
            case R.id.nativeBtn:
                adPageClass = NativeMainActivity.class;
                break;
            case R.id.splashBtn:
                adPageClass = SplashAdActivity.class;
                break;
            case R.id.interstitialBtn:
                adPageClass = InterstitialAdActivity.class;
                break;
            case R.id.bannerBtn:
                adPageClass = BannerAdActivity.class;
                break;
            case R.id.rewardedVideoBtn:
                adPageClass = RewardVideoAdActivity.class;
                break;
            case R.id.media_videoBtn:
                adPageClass = MediaVideoActivity.class;
                break;
        }
        startAdPage(adPageClass);
    }
}
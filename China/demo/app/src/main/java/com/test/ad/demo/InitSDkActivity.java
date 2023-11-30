package com.test.ad.demo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATInitConfig;
import com.anythink.core.api.ATNetworkConfig;
import com.anythink.core.api.ATSDK;
import com.test.ad.demo.util.SDKUtil;

import java.util.ArrayList;
import java.util.List;

public class InitSDkActivity extends Activity {

    private RelativeLayout mRlInitSDK;
    private RelativeLayout mRlShowAd;
    private RelativeLayout mRlPrivacyContainer;
    private WebView mPrivacyWebView;
    private TextView mTVAgree, mTVNoAgree, mTVPersonAdDesc;
    private CheckBox mCBPersonAd;
    boolean mHasInitSdk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_sdk);

        initView();
        initListener();
    }

    private void initView() {
        mRlInitSDK = findViewById(R.id.initSdk);
        mRlShowAd = findViewById(R.id.showAd);
        mRlPrivacyContainer = findViewById(R.id.privacy_container);
        mTVAgree = findViewById(R.id.privaty_agree);
        mTVNoAgree = findViewById(R.id.privaty_no_agree);

        mPrivacyWebView = findViewById(R.id.privacy_webview);

        mCBPersonAd = findViewById(R.id.cb_personad_switch);
        mTVPersonAdDesc = findViewById(R.id.tv_desc_personad_switch);
        initializeWebView(mPrivacyWebView, this);

        initSetting();
    }

    private void initListener() {
        mRlInitSDK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出隐私协议弹窗，用户同意后再进行SDK初始化
                if (!mHasInitSdk) {
                    //隐私协议url
                    if (TextUtils.isEmpty(mPrivacyWebView.getUrl())) {
                        mPrivacyWebView.loadUrl("https://www.toponad.com/zh-cn/privacy-policy");
                    }
                    mRlPrivacyContainer.setVisibility(View.VISIBLE);
                } else {
                    showToast("SDK已初始化");
                }
            }
        });

        mRlShowAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHasInitSdk) {
                    startActivity(new Intent(InitSDkActivity.this, MainActivity.class));
//                    finish();
                } else {
                    showToast("请先初始化SDK");
                }
            }
        });

        mRlPrivacyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mTVAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //初始化SDK
                SDKUtil.initSDK(InitSDkActivity.this);
                mHasInitSdk = true;
                mRlPrivacyContainer.setVisibility(View.GONE);
                showToast("初始化成功");
            }
        });

        mTVNoAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlPrivacyContainer.setVisibility(View.GONE);
            }
        });

        mCBPersonAd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    putSPString(InitSDkActivity.this, "ad_setting", "person_ad", "1");
                    mTVPersonAdDesc.setText("当前状态：开启");
                    ATSDK.setPersonalizedAdStatus(ATAdConst.PRIVACY.PERSIONALIZED_ALLOW_STATUS);
                } else {
                    putSPString(InitSDkActivity.this, "ad_setting", "person_ad", "2");
                    mTVPersonAdDesc.setText("当前状态：关闭");
                    ATSDK.setPersonalizedAdStatus(ATAdConst.PRIVACY.PERSIONALIZED_LIMIT_STATUS);
                }
            }
        });
    }

    private void initSetting() {
        //"1":同意，”2“:不同意
        String spPersonAd = getSPString(InitSDkActivity.this, "ad_setting", "person_ad", "2");
        boolean allowPersonAd = TextUtils.equals("1", spPersonAd);
        if (allowPersonAd) {
            ATSDK.setPersonalizedAdStatus(ATAdConst.PRIVACY.PERSIONALIZED_ALLOW_STATUS);
            mCBPersonAd.setChecked(true);
        } else {
            ATSDK.setPersonalizedAdStatus(ATAdConst.PRIVACY.PERSIONALIZED_LIMIT_STATUS);
            mCBPersonAd.setChecked(false);
        }
    }

    private void showToast(String msg) {
        try {
            Toast.makeText(InitSDkActivity.this, msg, Toast.LENGTH_SHORT)
                    .show();
        } catch (Throwable t) {

        }
    }

    private void initializeWebView(WebView mWebView, Context context) {
        WebSettings webSettings = mWebView.getSettings();


        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.requestFocus();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSavePassword(false);
        webSettings.setDatabaseEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (Build.VERSION.SDK_INT >= 17) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }



    private ATNetworkConfig getAtNetworkConfig() {
        List<ATInitConfig> atInitConfigs = new ArrayList<>();

//        ATInitConfig gdtatInitConfig = new GDTATInitConfig("1200028501");
//        ATInitConfig mintegralATInitConfig = new MintegralATInitConfig("100947", "ef13ef712aeb0f6eb3d698c4c08add96");
//
//        atInitConfigs.add(gdtatInitConfig);
//        atInitConfigs.add(mintegralATInitConfig);

        ATNetworkConfig.Builder builder = new ATNetworkConfig.Builder();
        builder.withInitConfigList(atInitConfigs);
        return builder.build();
    }

    private void putSPString(Context context, String name, String key, String value) {
        if (context == null) {
            return;
        }
        try {
            SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, String.valueOf(value));
            editor.apply();
        } catch (Exception e) {
        } catch (Error e) {
        }
    }

    private String getSPString(Context context, String name, String key, String defut) {
        if (context == null) {
            return null;
        }
        try {
            SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            return sp.getString(key, defut);
        } catch (Exception e) {

        } catch (Error e) {

        }
        return defut;

    }
}
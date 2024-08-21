package com.anythink.custom.adapter;

import android.content.Context;
import android.os.Bundle;

import com.anythink.core.api.ATInitMediation;
import com.anythink.core.api.MediationInitCallback;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

import java.util.Map;


public class AdMobInitManager extends ATInitMediation {

    private static final String TAG = AdMobInitManager.class.getSimpleName();

    private boolean mIsInit;
    private volatile static AdMobInitManager sInstance;


    Context applicationContext;


    private AdMobInitManager() {
        mIsInit = false;
    }

    public static AdMobInitManager getInstance() {
        if (sInstance == null) {
            synchronized (AdMobInitManager.class) {
                if (sInstance == null)
                    sInstance = new AdMobInitManager();
            }
        }
        return sInstance;
    }


    public void initSDK(Context context, final Map<String, Object> serviceExtras, MediationInitCallback initListener) {
        if (mIsInit) {
            if (initListener != null) {
                initListener.onSuccess();
            }
            return;
        }

        applicationContext = context.getApplicationContext();

        runOnThreadPool(new Runnable() {
            @Override
            public void run() {
                initAdmobSDK(initListener, serviceExtras, applicationContext);
            }
        });
    }

    private void initAdmobSDK(MediationInitCallback initListener, Map<String, Object> serviceExtras, Context applicationContext) {
        try {
            synchronized (this) {
                if (mIsInit) {
                    if (initListener != null) {
                        initListener.onSuccess();
                    }
                    return;
                }
                MobileAds.disableMediationAdapterInitialization(applicationContext);

                MobileAds.initialize(applicationContext);
                mIsInit = true;
            }
            if (initListener != null) {
                initListener.onSuccess();
            }
        } catch (Throwable e) {
            mIsInit = false;
            if (initListener != null) {
                initListener.onFail(e.getMessage());
            }
        }
    }

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        //gdpr
        return false;
    }

    @Override
    public String getNetworkName() {
        return "Custom Admob";
    }

    @Override
    public String getNetworkVersion() {
        return AdmobConst.getNetworkVersion();
    }

    protected AdRequest.Builder getRequestBuilder() {
        AdRequest.Builder requestBuilder = new AdRequest.Builder();

        Bundle requestBundle = new Bundle();
        requestBuilder.addNetworkExtrasBundle(AdMobAdapter.class, requestBundle);

        return requestBuilder;
    }

}


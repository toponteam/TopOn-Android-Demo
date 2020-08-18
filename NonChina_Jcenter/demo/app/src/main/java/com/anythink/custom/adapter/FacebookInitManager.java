package com.anythink.custom.adapter;

import android.content.Context;

import com.anythink.core.api.ATInitMediation;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.BuildConfig;

import java.util.Map;

public class FacebookInitManager extends ATInitMediation {

    private boolean mIsInit;
    private static FacebookInitManager sInstance;

    private FacebookInitManager() {

    }

    public synchronized static FacebookInitManager getInstance() {
        if (sInstance == null) {
            sInstance = new FacebookInitManager();
        }
        return sInstance;
    }


    @Override
    public synchronized void initSDK(Context context, Map<String, Object> serviceExtras) {
        try {
            if (!mIsInit) {
                AudienceNetworkAds.initialize(context.getApplicationContext());
                mIsInit = true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNetworkName() {
        return "Custom Facebook";
    }


    public static String getNetworkVersion() {
        try {
            return BuildConfig.VERSION_NAME;
        } catch (Throwable e) {

        }
        return "";
    }

}

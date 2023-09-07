/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import android.content.Context;
import android.graphics.Rect;

import com.anythink.core.api.ATBaseAdAdapter;
import com.anythink.splashad.unitgroup.api.CustomSplashEyeAd;
import com.qq.e.ads.splash.SplashAD;

public class GDTATSplashEyeAd extends CustomSplashEyeAd {

    SplashAD mSplashAD;

    public GDTATSplashEyeAd(ATBaseAdAdapter adapter, SplashAD splashAD) {
        super(adapter);
        this.mAtBaseAdAdapter = adapter;
        this.mSplashAD = splashAD;
    }


    @Override
    public void show(Context context, Rect rect) {
        try {
            if (mATSplashEyeAdListener != null) {
                mATSplashEyeAdListener.onAnimationStart(this.mSplashView);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public int[] getSuggestedSize(Context context) {
        return null;
    }

    @Override
    public void onFinished() {
        if (mSplashAD != null) {
            mSplashAD.zoomOutAnimationFinish();
        }
    }

    @Override
    public void customResourceDestory() {
        mSplashAD = null;
    }

}

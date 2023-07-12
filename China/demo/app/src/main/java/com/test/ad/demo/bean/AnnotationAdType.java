package com.test.ad.demo.bean;

import android.support.annotation.IntDef;

import com.anythink.core.api.ATAdConst.ATMixedFormatAdType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description:
 * Created by Quin on 2023/2/9.
 **/
@IntDef(value = {ATMixedFormatAdType.NATIVE, ATMixedFormatAdType.INTERSTITIAL, ATMixedFormatAdType.REWARDED_VIDEO,
        ATMixedFormatAdType.BANNER, ATMixedFormatAdType.SPLASH})
@Retention(RetentionPolicy.SOURCE)
public @interface AnnotationAdType {

}

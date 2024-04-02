package com.test.ad.demo.bean;

import androidx.annotation.IntDef;

import com.anythink.core.api.ATAdConst.ATMixedFormatAdType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef(value = {ATMixedFormatAdType.NATIVE, ATMixedFormatAdType.INTERSTITIAL, ATMixedFormatAdType.REWARDED_VIDEO,
        ATMixedFormatAdType.BANNER, ATMixedFormatAdType.SPLASH, ATMixedFormatAdType.MEDIA_VIDEO})
@Retention(RetentionPolicy.SOURCE)
public @interface AnnotationAdType {
    
}

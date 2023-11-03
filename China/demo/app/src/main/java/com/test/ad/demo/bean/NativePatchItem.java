package com.test.ad.demo.bean;

import com.anythink.nativead.api.NativeAd;

public class NativePatchItem {

    public int type = 0;

    public NativeAd nativeAd;
    public String videoUrl;


    public NativePatchItem(int type, NativeAd nativeAd, String videoUrl) {
        this.type = type;
        this.nativeAd = nativeAd;
        this.videoUrl = videoUrl;
    }
}

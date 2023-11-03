package com.test.ad.demo.bean;

import com.anythink.nativead.api.NativeAd;

public class NativeDrawItem {

    public static final int NORMAL_ITME = 1;
    public static final int AD_ITEM = 2;

    public int type = 0;

    public NativeAd nativeAd;
    public int videoId;
    public int ImgId;


    public NativeDrawItem(int type, NativeAd nativeAd, int videoId, int imgId) {
        this.type = type;
        this.nativeAd = nativeAd;
        this.videoId = videoId;
        ImgId = imgId;
    }
}

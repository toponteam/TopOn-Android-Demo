package com.anythink.custom.adapter;

import android.content.Context;

public class GDTATNativeExpressPatchAd extends GDTATNativeExpressAd {

    protected GDTATNativeExpressPatchAd(Context context, String unitId, int localWidth, int localHeight, int videoMuted, int videoAutoPlay, int videoDuration, String payload) {
        super(context, unitId, localWidth, localHeight, videoMuted, videoAutoPlay, videoDuration, payload);
    }

    @Override
    public int getNativeType() {
        return NativeType.PATCH;
    }
}

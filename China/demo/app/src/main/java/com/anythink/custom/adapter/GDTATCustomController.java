package com.anythink.custom.adapter;

public abstract class GDTATCustomController {
    public boolean getAgreePrivacyStrategy() {
        return true;
    }

    public boolean isCanUseMacAddress() {
        return true;
    }

    public boolean isCanUseAndroidId() {
        return true;
    }

    public boolean isCanUseDeviceId() {
        return true;
    }

}

package com.test.ad.demo;

/**
 * Description:
 * Created by Quin on 2023/8/1.
 **/
public class AdConst {
    /*
     * To collect scene arrival rate statistics, you can refer to the link below:
     * en: "https://docs.toponad.com/#/en-us/android/NetworkAccess/scenario/scenario"
     * chinese: "https://docs.toponad.com/#/zh-cn/android/NetworkAccess/scenario/scenario"
     * Call the "Enter AD scene" method when an AD trigger condition is met, such as:
     * The scenario is a pop-up AD after the cleanup, which is called at the end of the cleanup.
     * The scenarioId support custom.
     * 1、Call "entryAdScenario" to report the arrival of the scene.
     * 2、Call "isAdReady".
     * 3、Call "show" to show AD view.
     * (Note the difference between auto and manual)
     */
    public static class SCENARIO_ID {
        public static final String SPLASH_AD_SCENARIO = "splash_ad_show_1";
        public static final String INTERSTITIAL_AD_SCENARIO = "interstitial_ad_show_1";
        public static final String NATIVE_AD_SCENARIO = "native_ad_show_1";
        public static final String BANNER_AD_SCENARIO = "banner_ad_show_1";
        public static final String REWARD_VIDEO_AD_SCENARIO = "reward_video_ad_show_1";
    }
}

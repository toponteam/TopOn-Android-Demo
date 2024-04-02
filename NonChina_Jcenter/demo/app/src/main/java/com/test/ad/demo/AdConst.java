package com.test.ad.demo;


public class AdConst {
    /*
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
        public static final String MEDIA_VIDEO_AD_SCENARIO = "media_video_ad_show_1";
    }

    public static class SHOW_CUSTOM_EXT {
        public static final String SPLASH_AD_SHOW_CUSTOM_EXT = "splash_ad_show_custom_ext";
        public static final String INTERSTITIAL_AD_SHOW_CUSTOM_EXT = "interstitial_ad_show_custom_ext";
        public static final String NATIVE_AD_SHOW_CUSTOM_EXT = "native_ad_show_custom_ext";
        public static final String BANNER_AD_SHOW_CUSTOM_EXT = "banner_ad_show_custom_ext";
        public static final String REWARD_VIDEO_AD_SHOW_CUSTOM_EXT = "reward_video_ad_show_custom_ext";
        public static final String MEDIA_VIDEO_AD_SHOW_CUSTOM_EXT = "media_video_ad_show_custom_ext";
    }
}

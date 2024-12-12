package com.test.ad.demo;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATShowConfig;
import com.anythink.core.api.AdError;
import com.anythink.mediavideo.api.ATMediaVideo;
import com.anythink.mediavideo.api.ATMediaVideoConfig;
import com.anythink.mediavideo.api.ATMediaVideoEventListener;
import com.anythink.mediavideo.api.MediaVideoAd;
import com.anythink.mediavideo.api.OnIMAEventListener;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.test.ad.demo.base.BaseActivity;
import com.test.ad.demo.bean.CommonViewBean;
import com.test.ad.demo.mediavideo.VideoPlayerWithAdPlayback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vicent
 * @date 2024/2/21
 * @apiNote
 */
public class MediaVideoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = MediaVideoActivity.class.getSimpleName();
    private ATMediaVideo mATMediaVideo;
    private MediaVideoAd mMediaVideoAd;
    private TextView mTVLoadAdBtn;
    private TextView mTVIsAdReadyBtn;
    private TextView mTVShowAdBtn;
    private VideoPlayerWithAdPlayback mVideoPlayerWithAdPlayback;
    final String VIDEO_CONTENT_URL = "https://storage.googleapis.com/gvabox/media/samples/stock.mp4";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_media_video;
    }

    @Override
    protected int getAdType() {
        return ATAdConst.ATMixedFormatAdType.MEDIA_VIDEO;
    }

    @Override
    protected void onSelectPlacementId(String placementId) {
        initMediaVideoAd(placementId);
    }

    @Override
    protected CommonViewBean getCommonViewBean() {
        final CommonViewBean commonViewBean = new CommonViewBean();
        commonViewBean.setTitleBar(findViewById(R.id.title_bar));
        commonViewBean.setSpinnerSelectPlacement(findViewById(R.id.spinner_1));
        commonViewBean.setTvLogView(findViewById(R.id.tv_show_log));
        commonViewBean.setTitleResId(R.string.anythink_title_media_video);
        commonViewBean.setVideoPlayerWithAdPlayback(mVideoPlayerWithAdPlayback);
        return commonViewBean;
    }

    @Override
    protected void initView() {
        mVideoPlayerWithAdPlayback = findViewById(R.id.video_player_with_ad_playback);
        super.initView();
        mTVLoadAdBtn = findViewById(R.id.load_ad_btn);
        mTVIsAdReadyBtn = findViewById(R.id.is_ad_ready_btn);
        mTVShowAdBtn = findViewById(R.id.show_ad_btn);
    }

    @Override
    protected void initData() {
        super.initData();
        mVideoPlayerWithAdPlayback.setContentVideoPath(VIDEO_CONTENT_URL);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTVLoadAdBtn.setOnClickListener(this);
        mTVIsAdReadyBtn.setOnClickListener(this);
        mTVShowAdBtn.setOnClickListener(this);
    }

    private void initMediaVideoAd(String placementId) {
        ATMediaVideoConfig.Builder atMediaVideoConfigBuilder = new ATMediaVideoConfig.Builder();

        //Ad Url Tag Params
        Map<String, String> adTagUrlPrams = new HashMap<>();
        //ad tag:description_url
        adTagUrlPrams.put("description_url", "desc_url");
        //ad tag:cust_params,
        //adTagUrlPrams.put("cust_params", "section%3Dblog%26anotherKey%3Dvalue1%2Cvalue2");
        //more ad tags see https://support.google.com/admanager/answer/10678356?sjid=13614313059636716551-AP
        //adTagUrlPrams.put("ad_type","ad_type=audio_video");
        atMediaVideoConfigBuilder.setUrlTagParams(adTagUrlPrams);

        //other setting
        //atMediaVideoConfigBuilder.setVideoMute(false);
        //atMediaVideoConfigBuilder.setHideUICountDown(false);
        //atMediaVideoConfigBuilder.setLoadVideoTimeout(12000);
        //atMediaVideoConfigBuilder.setAutoPlay(true);
        //atMediaVideoConfigBuilder.setEnablePreload(true);
        //atMediaVideoConfigBuilder.setSettingLanguage("fr");
        //atMediaVideoConfigBuilder.setEnableDebugMode(true);

        mATMediaVideo = new ATMediaVideo(this, placementId, mVideoPlayerWithAdPlayback.getVideoAdPlayer(), atMediaVideoConfigBuilder.build());

        //mMediaVideo = new ATMediaVideo(this, "", null);
        mATMediaVideo.setAdRevenueListener(new AdRevenueListenerImpl());
        mATMediaVideo.setAdListener(getATMediaVideoEventListener());
        //IMA Event Listener
        mATMediaVideo.setIMAEventListener(new OnIMAEventListener() {
            @Override
            public void onEvent(Object adEvent) {
                if (((AdEvent) adEvent).getType() != AdEvent.AdEventType.AD_PROGRESS) {
                    Log.i(TAG, "[ATMediaVideo]IMAAdEvent: " + ((AdEvent) adEvent).getType());
                    //printLogOnUI("IMAAdEvent: " + ((AdEvent) adEvent).getType());
                }
            }
        });

        mATMediaVideo.setAdSourceStatusListener(new BaseActivity.ATAdSourceStatusListenerImpl());
    }

    private ATMediaVideoEventListener getATMediaVideoEventListener() {
        return new ATMediaVideoEventListener() {
            @Override
            public void onMediaVideoAdLoaded() {
                Log.i(TAG, "onMediaVideoAdLoaded:");
                printLogOnUI("onMediaVideoAdLoaded");
            }

            @Override
            public void onMediaVideoAdLoadFailed(AdError adError) {
                Log.i(TAG, "onMediaVideoAdLoadFailed:\n" + adError.getFullErrorInfo());
                printLogOnUI("onMediaVideoAdLoadFailed:" + adError.getFullErrorInfo());
            }

            @Override
            public void onMediaVideoAdClick(ATAdInfo adInfo) {
                Log.i(TAG, "onMediaVideoAdClick:" + adInfo.toString());
                printLogOnUI("onMediaVideoAdClick");
            }

            @Override
            public void onMediaVideoAdResume(ATAdInfo adInfo) {
                Log.i(TAG, "onMediaVideoAdResume:" + adInfo.toString());
                printLogOnUI("onMediaVideoAdResume");
            }

            @Override
            public void onMediaVideoAdPause(ATAdInfo adInfo) {
                Log.i(TAG, "onMediaVideoAdPause:" + adInfo.toString());
                printLogOnUI("onMediaVideoAdPause");
            }

            @Override
            public void onMediaVideoAdStart(ATAdInfo adInfo) {
                Log.i(TAG, "onMediaVideoAdStart:" + adInfo.toString());
                printLogOnUI("onMediaVideoAdStart");
            }

            @Override
            public void onMediaVideoAdEnd(ATAdInfo adInfo) {
                Log.i(TAG, "onMediaVideoAdEnd:" + adInfo.toString());
                printLogOnUI("onMediaVideoAdEnd");
            }

            @Override
            public void onMediaVideoAdPlayError(AdError adError, ATAdInfo adInfo) {
                Log.i(TAG, "onMediaVideoAdPlayError:" + adError.getFullErrorInfo());
                printLogOnUI("onMediaVideoAdPlayError");
            }

            @Override
            public void onMediaVideoAdSkiped(ATAdInfo adInfo) {
                Log.i(TAG, "onMediaVideoAdSkiped:" + adInfo.toString());
                printLogOnUI("onMediaVideoAdSkiped");
            }

            @Override
            public void onMediaVideoAdTapped(ATAdInfo adInfo) {
                Log.i(TAG, "onMediaVideoAdTapped:" + adInfo.toString());
                printLogOnUI("onMediaVideoAdTapped");
            }

            @Override
            public void onMediaVideoAdProgress(float progress, double totalTime) {
                //no need to log
            }
        };
    }

    private OnIMAEventListener getMediaVideoAdUseOnIMAEventListener() {
        return new OnIMAEventListener() {
            @Override
            public void onEvent(Object adEvent) {
                /**
                 * do content resume、pause and start、destroy MediaVideoAd
                 */
                if (adEvent instanceof AdEvent) {
                    if (((AdEvent) adEvent).getType() != AdEvent.AdEventType.AD_PROGRESS) {
                        Log.i(TAG, "[MediaVideoAd]IMAAdEvent: " + ((AdEvent) adEvent).getType());
                        //printLogOnUI("IMAAdEvent: " + ((AdEvent) adEvent).getType());
                    }

                    switch (((AdEvent) adEvent).getType()) {
                        case LOADED:
                            // AdEventType.LOADED will be fired when ads are ready to be
                            // played. AdsManager.start() begins ad playback. This method is
                            // ignored for VMAP or ad rules playlists, as the SDK will
                            // automatically start executing the playlist.
                            //if (mMediaVideoAd != null) {
                            //    mMediaVideoAd.start();
                            //}
                            break;
                        case CONTENT_PAUSE_REQUESTED:
                            printLogOnUI("ima event CONTENT_PAUSE_REQUESTED");
                            // AdEventType.CONTENT_PAUSE_REQUESTED is fired immediately before
                            // a video ad is played.
                            pauseContent();
                            break;
                        case CONTENT_RESUME_REQUESTED:
                            printLogOnUI("ima event CONTENT_RESUME_REQUESTED");
                            // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad is
                            // completed and you should start playing your content.
                            resumeContent();
                            break;
                        case PAUSED:
                            break;
                        case RESUMED:
                            break;
                        case AD_BREAK_READY:
                            // AdEventType.AD_BREAK_READY will be fired when VMAP ads are ready to be played.
                            // For VAMP ads, mMediaVideoAd.start() must be called after AdEventType.AD_BREAK_READY
                            printLogOnUI("ima event AD_BREAK_READY");
                            if (mMediaVideoAd != null) {
                                mMediaVideoAd.start();
                                printLogOnUI("start MediaVideoAd with AD_BREAK_READY");
                            }
                            break;
                        case ALL_ADS_COMPLETED:
                            mMediaVideoAd = null;//should set null,Avoid affecting other caches
                            destroyShowingMediaVideoAd();
                            printLogOnUI("destroy MediaVideoAd with ALL_ADS_COMPLETED");
                            resumeContent();
                            setReadyToPlayContent(false);
                            break;
                        case AD_BREAK_FETCH_ERROR:
                            Log.w(TAG, "Ad Fetch Error. Resuming content.");
                            // A CONTENT_RESUME_REQUESTED event should follow to trigger content playback.
                            destroyShowingMediaVideoAd();
                            printLogOnUI("destroy MediaVideoAd with AD_BREAK_FETCH_ERROR");
                            resumeContent();
                            setReadyToPlayContent(false);
                            break;
                        default:
                            break;
                    }
                }
            }
        };
    }

    private void loadAd() {
        if (mATMediaVideo == null) {
            printLogOnUI("ATMediaVideo create." + getString(R.string.anythink_ad_status_loading));
            initMediaVideoAd(mCurrentPlacementId);
            //return;
        } else {
            printLogOnUI("ATMediaVideo create new." + getString(R.string.anythink_ad_status_loading));
            initMediaVideoAd(mCurrentPlacementId);
        }
        //if (mATMediaVideo.checkAdStatus().isLoading()) {
        //    printLogOnUI("ATMediaVideo is loading.");
        //    return;
        //}
        //printLogOnUI(getString(R.string.anythink_ad_status_loading));

        Map<String, Object> localMap = new HashMap<>();

//        localMap.put(ATAdConst.KEY.AD_WIDTH, getResources().getDisplayMetrics().widthPixels);
//        localMap.put(ATAdConst.KEY.AD_HEIGHT, getResources().getDisplayMetrics().heightPixels);

        mATMediaVideo.setLocalExtra(localMap);
        mATMediaVideo.loadAd();
    }

    private void isAdReady() {
        if(mATMediaVideo == null){
            printLogOnUI("MediaVideo ad not init,please click load");
            return;
        }
        ATAdStatusInfo atAdStatusInfo = mATMediaVideo.checkAdStatus();
        printLogOnUI("MediaVideo ad ready status:" + atAdStatusInfo.isReady());
        List<ATAdInfo> atAdInfoList = mATMediaVideo.checkValidAdCaches();
        Log.i(TAG, "Valid Cahce size:" + (atAdInfoList != null ? atAdInfoList.size() : 0));
        if (atAdInfoList != null) {
            for (ATAdInfo adInfo : atAdInfoList) {
                //Log.i(TAG, "\nCahce detail:" + adInfo.getUrlTagParams());//return ATMediaVideoConfig.urlTagParams
                Log.i(TAG, "\nCahce detail:" + adInfo.toString());
            }
        }
    }

    private void showAd() {
        if (mATMediaVideo.isAdReady()) {
            if (mMediaVideoAd != null) {
                //destroy showing media video ad
                destroyShowingMediaVideoAd();
            }
            //reset player state and content video play position
            mVideoPlayerWithAdPlayback.resetInnerVideoPlayerAndParams();

            mMediaVideoAd = mATMediaVideo.getMediaVideoAd(getATShowConfig());
            mVideoPlayerWithAdPlayback.setReadyToPlayContent(true);

            //must setContainer(container) before start()
            if (mVideoPlayerWithAdPlayback.getAdUiContainer() != null) {
                mVideoPlayerWithAdPlayback.getAdUiContainer().removeAllViews();
            }
            //need setContainer
            mMediaVideoAd.setContainer(mVideoPlayerWithAdPlayback.getAdUiContainer());
            //setContentProgressProvider(VMAP need)
            mMediaVideoAd.setContentProgressProvider(getContentProgressProvider());
            //Call setOnIMAEventListener to bind MediaVideoAd and OnIMAEventListener
            mMediaVideoAd.setOnIMAEventListener(getMediaVideoAdUseOnIMAEventListener());

            if (mMediaVideoAd != null) {
                if (!mMediaVideoAd.isVMAPOffer()) {
                    //VAST just load start
                    printLogOnUI("start MediaVideoAd with Show Ad click");
                    mMediaVideoAd.start();
                } else {
                    //VAMP wait OnIMAEventListener#AD_BREAK_READY call
                    printLogOnUI("show VMAP wait AD_BREAK_READY");
                }
            }
        } else {
            printLogOnUI("MediaVideo ad show fail,because of ready status:false");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyShowingMediaVideoAd();

        //stop timmer
        if (mVideoPlayerWithAdPlayback != null) {
            mVideoPlayerWithAdPlayback.stopTracking();
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.load_ad_btn:
                loadAd();
                break;
            case R.id.is_ad_ready_btn:
                isAdReady();
                break;
            case R.id.show_ad_btn:
                ATMediaVideo.entryAdScenario(mCurrentPlacementId, AdConst.SCENARIO_ID.MEDIA_VIDEO_AD_SCENARIO);
                if (mATMediaVideo != null) {
                    showAd();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    /**
     * Save position of the video, whether content or ad. Can be called when the app is paused, for
     * example.
     */
    public void pause() {
        mVideoPlayerWithAdPlayback.savePosition();
        if (mMediaVideoAd != null && mVideoPlayerWithAdPlayback.getIsAdDisplayed()) {
            mMediaVideoAd.pause();
        } else {
            mVideoPlayerWithAdPlayback.pause();
        }
    }

    /**
     * Restore the previously saved progress location of the video. Can be called when the app is
     * resumed.
     */
    public void resume() {
        mVideoPlayerWithAdPlayback.restorePosition();
        if (mMediaVideoAd != null && mVideoPlayerWithAdPlayback.getIsAdDisplayed()) {
            mMediaVideoAd.resume();
        } else {
            mVideoPlayerWithAdPlayback.play();
        }
    }

    public void destroyShowingMediaVideoAd() {
        if (mMediaVideoAd != null) {
            mMediaVideoAd.onDestroy();
            mMediaVideoAd = null;
        }
    }

    private ATShowConfig getATShowConfig() {
        ATShowConfig.Builder builder = new ATShowConfig.Builder();
        builder.scenarioId(AdConst.SCENARIO_ID.MEDIA_VIDEO_AD_SCENARIO);
        builder.showCustomExt(AdConst.SHOW_CUSTOM_EXT.MEDIA_VIDEO_AD_SHOW_CUSTOM_EXT);

        return builder.build();
    }
}

package com.test.ad.demo.mediavideo;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.anythink.mediavideo.api.videoadplayer.ATAdMediaInfo;
import com.anythink.mediavideo.api.videoadplayer.ATAdPodInfo;
import com.anythink.mediavideo.api.videoadplayer.ATVideoAdPlayer;
import com.anythink.mediavideo.api.videoadplayer.ATVideoProgressUpdate;
import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.test.ad.demo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Vicent
 * @date 2024/2/22
 * @apiNote
 */
public class VideoPlayerWithAdPlayback extends RelativeLayout {

    private final String TAG = VideoPlayerWithAdPlayback.class.getSimpleName();

    // The wrapped video player.
    private VideoPlayer videoPlayer;

    // A Timer to help track media updates
    private Timer timer;

    // Track the currently playing media file. If doing preloading, this will need to be an
    // array or other data structure.
    private ATAdMediaInfo adMediaInfo;

    // The SDK will render ad playback UI elements into this ViewGroup.
    private ViewGroup adUiContainer;

    // Used to track if the current video is an ad (as opposed to a content video).
    private boolean isAdDisplayed;

    // Used to track the current content video URL to resume content playback.
    private String contentVideoUrl;

    // The saved position in the ad to resume if app is backgrounded during ad playback.
    private int savedAdPosition;
    private int lastContentPosition;

    // The saved position in the content to resume to after ad playback or if app is backgrounded
    // during content playback.
    private int savedContentPosition;

    // Used to track if the content has completed.
    private boolean contentHasCompleted;

    // VideoAdPlayer interface implementation for the SDK to send ad play/pause type events.
    private ATVideoAdPlayer videoAdPlayer;

    private String lastAdInfoUrl;

    // ContentProgressProvider interface implementation for the SDK to check content progress.
    private ContentProgressProvider contentProgressProvider;

    private boolean readyToPlayContent;

    private final List<ATVideoAdPlayer.VideoAdPlayerCallback> adCallbacks = new ArrayList<ATVideoAdPlayer.VideoAdPlayerCallback>(1);

    public VideoPlayerWithAdPlayback(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VideoPlayerWithAdPlayback(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoPlayerWithAdPlayback(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void startTracking() {
        if (timer != null) {
            return;
        }
        timer = new Timer();
        TimerTask updateTimerTask = new TimerTask() {
            @Override
            public void run() {
                // Tell the current video progress. A better implementation would be
                // reactive to events from the media player, instead of polling.
                for (ATVideoAdPlayer.VideoAdPlayerCallback callback : adCallbacks) {
                    callback.onAdProgress(adMediaInfo, videoAdPlayer.getAdProgress());
                }
            }
        };
        int initialDelayMs = 250;
        int pollingTimeMs = 250;
        timer.schedule(updateTimerTask, pollingTimeMs, initialDelayMs);
    }

    public void stopTracking() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void init() {
        isAdDisplayed = false;
        contentHasCompleted = false;
        readyToPlayContent = false;
        savedAdPosition = 0;
        savedContentPosition = 0;
        videoPlayer = (VideoPlayer) this.getRootView().findViewById(R.id.video_player);
        adUiContainer = (ViewGroup) this.getRootView().findViewById(R.id.ad_ui_container);

        // Define VideoAdPlayer connector.
        videoAdPlayer = new ATVideoAdPlayer() {
            @Override
            public void playAd(ATAdMediaInfo info) {
                adMediaInfo = info;
                startTracking();
                Log.d(TAG, "playAd isAdDisplayed:" + isAdDisplayed + ",lastAdInfoUrl:" + lastAdInfoUrl + ",info.getUrl():" + info.getUrl());
                if (isAdDisplayed && TextUtils.equals(lastAdInfoUrl,info.getUrl())) {
                    Log.d(TAG, "playAd: videoPlayer.resume()");
                    videoPlayer.resume();
                } else {
                    isAdDisplayed = true;
                    Log.d(TAG, "playAd: videoPlayer.play() is equal:" + (TextUtils.equals(lastAdInfoUrl, info.getUrl())) + ",lastAdInfoUrl:" + lastAdInfoUrl);
                    if (!TextUtils.equals(lastAdInfoUrl, info.getUrl())) {
                        lastAdInfoUrl = info.getUrl();
                        videoPlayer.setVideoPath(info.getUrl());
                    }
                    videoPlayer.play();
                }
            }

            @Override
            public void loadAd(ATAdMediaInfo info, ATAdPodInfo api) {
                Log.d(TAG, "loadAd: ");

            }

            @Override
            public void stopAd(ATAdMediaInfo info) {
                Log.d(TAG, "stopAd: ");
                isAdDisplayed = false;
                stopTracking();
                videoPlayer.stopPlayback();
            }

            @Override
            public void pauseAd(ATAdMediaInfo info) {
                Log.d(TAG, "pauseAd: ");
                stopTracking();
                videoPlayer.pause();
            }

            @Override
            public void release() {
                // any clean up that needs to be done
            }

            @Override
            public void addCallback(VideoAdPlayerCallback videoAdPlayerCallback) {
                Log.d(TAG, "addCallback: ");
                adCallbacks.add(videoAdPlayerCallback);
            }

            @Override
            public void removeCallback(VideoAdPlayerCallback videoAdPlayerCallback) {
                Log.d(TAG, "removeCallback: ");
                adCallbacks.remove(videoAdPlayerCallback);
            }

            @Override
            public ATVideoProgressUpdate getAdProgress() {
                if (!isAdDisplayed || videoPlayer.getDuration() <= 0) {
                    return ATVideoProgressUpdate.VIDEO_TIME_NOT_READY;
                }
                return new ATVideoProgressUpdate(videoPlayer.getCurrentPosition(), videoPlayer.getDuration());
            }
        };

        contentProgressProvider = new ContentProgressProvider() {
            @Override
            public VideoProgressUpdate getContentProgress() {
                return getVideoContentProgress();
            }
        };

        // Set player callbacks for delegating major video events.
        videoPlayer.addPlayerCallback(new VideoPlayer.PlayerCallback() {
            @Override
            public void onPlay() {
                Log.d(TAG, "videoPlayer onPlay: isAdDisplayed:" + isAdDisplayed);
                if (isAdDisplayed) {
                    for (ATVideoAdPlayer.VideoAdPlayerCallback callback : adCallbacks) {
                        callback.onPlay(adMediaInfo);
                    }
                }
            }

            @Override
            public void onPause() {
                Log.d(TAG, "videoPlayer onPause: isAdDisplayed:" + isAdDisplayed);
                if (isAdDisplayed) {
                    for (ATVideoAdPlayer.VideoAdPlayerCallback callback : adCallbacks) {
                        callback.onPause(adMediaInfo);
                    }
                }
            }

            @Override
            public void onResume() {
                Log.d(TAG, "videoPlayer onResume: isAdDisplayed:" + isAdDisplayed);
                if (isAdDisplayed) {
                    for (ATVideoAdPlayer.VideoAdPlayerCallback callback : adCallbacks) {
                        callback.onResume(adMediaInfo);
                    }
                }
            }

            @Override
            public void onError() {
                Log.d(TAG, "videoPlayer onError: isAdDisplayed:" + isAdDisplayed + ",lastAdInfoUrl:" + lastAdInfoUrl);
                if (isAdDisplayed) {
                    for (ATVideoAdPlayer.VideoAdPlayerCallback callback : adCallbacks) {
                        callback.onError(adMediaInfo);
                    }
                }
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "videoPlayer onComplete: isAdDisplayed:" + isAdDisplayed + ",lastAdInfoUrl:" + lastAdInfoUrl);
                if (isAdDisplayed) {
                    for (ATVideoAdPlayer.VideoAdPlayerCallback callback : adCallbacks) {
                        callback.onEnded(adMediaInfo);
                    }
                } else {
                    contentHasCompleted = true;
                    for (ATVideoAdPlayer.VideoAdPlayerCallback callback : adCallbacks) {
                        callback.onContentComplete();
                    }
                }
            }
        });
    }

    private void resetInnerPlayerParams() {
        isAdDisplayed = false;
        contentHasCompleted = false;
        readyToPlayContent = false;
        savedAdPosition = 0;
        savedContentPosition = 0;
        lastContentPosition = 0;
        restorePosition();
    }

    /**
     * Set the path of the video to be played as content.
     */
    public void setContentVideoPath(String contentVideoUrl) {
        this.contentVideoUrl = contentVideoUrl;
        contentHasCompleted = false;
    }

    /**
     * Save the playback progress state of the currently playing video. This is called when content is
     * paused to prepare for ad playback or when app is backgrounded.
     */
    public void savePosition() {
        if (isAdDisplayed) {
            savedAdPosition = videoPlayer.getCurrentPosition();
        } else {
            readyToPlayContent = true;
            savedContentPosition = videoPlayer.getCurrentPosition();
        }
    }

    /**
     * Restore the currently loaded video to its previously saved playback progress state. This is
     * called when content is resumed after ad playback or when focus has returned to the app.
     */
    public void restorePosition() {
        if (isAdDisplayed) {
            videoPlayer.seekTo(savedAdPosition);
        } else {
            videoPlayer.seekTo(savedContentPosition);
        }
    }

    /**
     * Pauses the content video.
     */
    public void pause() {
        videoPlayer.pause();
    }

    /**
     * Plays the content video.
     */
    public void play() {
        videoPlayer.play();
    }

    /**
     * Seeks the content video.
     */
    public void seek(int time) {
        // Seek only if an ad is not playing. Save the content position either way.
        if (!isAdDisplayed) {
            videoPlayer.seekTo(time);
        }
        savedContentPosition = time;
    }

    /**
     * Returns current content video play time.
     */
    public int getCurrentContentTime() {
        if (isAdDisplayed) {
            return savedContentPosition;
        } else {
            return videoPlayer.getCurrentPosition();
        }
    }

    /**
     * Pause the currently playing content video in preparation for an ad to play, and disables the
     * media controller.
     */
    public void pauseContentForAdPlayback() {
        videoPlayer.disablePlaybackControls();
        savePosition();
        videoPlayer.stopPlayback();
    }

    /**
     * Resume the content video from its previous playback progress position after an ad finishes
     * playing. Re-enables the media controller.
     */
    public void resumeContentAfterAdPlayback() {
        if (!readyToPlayContent) {
            return;
        }
        if (contentHasCompleted) {
            return;
        }
        isAdDisplayed = false;
        videoPlayer.setVideoPath(contentVideoUrl);
        videoPlayer.enablePlaybackControls(/* timeout= */ 3000);
        videoPlayer.seekTo(savedContentPosition);
        videoPlayer.play();

    }

    /**
     * Returns the UI element for rendering video ad elements.
     */
    public ViewGroup getAdUiContainer() {
        return adUiContainer;
    }

    /**
     * Returns an implementation of the SDK's VideoAdPlayer interface.
     */
    public ATVideoAdPlayer getVideoAdPlayer() {
        return videoAdPlayer;
    }

    /**
     * Returns if an ad is displayed.
     */
    public boolean getIsAdDisplayed() {
        return isAdDisplayed;
    }


    public void enableControls() {
        // Calling enablePlaybackControls(0) with 0 milliseconds shows the controls until
        // disablePlaybackControls() is called.
        videoPlayer.enablePlaybackControls(/* timeout= */ 0);
    }

    public void disableControls() {
        videoPlayer.disablePlaybackControls();
    }

    public void resetInnerVideoPlayerAndParams() {
        videoPlayer.pause();
        resetInnerPlayerParams();
    }

    public void setReadyToPlayContent(boolean readyToPlayContent) {
        this.readyToPlayContent = readyToPlayContent;
    }

    private VideoProgressUpdate getVideoContentProgress(){
        if (!readyToPlayContent || isAdDisplayed || videoPlayer.getDuration() <= 0) {
            return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }
        //add lastContentPosition to avoid return VideoProgressUpdate with wrong position

        int curVideoPosition = videoPlayer.getCurrentPosition();
        if (lastContentPosition == 0) {
            lastContentPosition = curVideoPosition;
            return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }
        if (lastContentPosition >= curVideoPosition) {
            lastContentPosition = curVideoPosition;
            return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }
        lastContentPosition = curVideoPosition;
        return new VideoProgressUpdate(curVideoPosition, videoPlayer.getDuration());
    }

    public ContentProgressProvider getContentProgressProvider(){
        return contentProgressProvider;
    }
}


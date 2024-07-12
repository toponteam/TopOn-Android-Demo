package com.test.ad.demo.bean;

import android.widget.Spinner;
import android.widget.TextView;

import com.test.ad.demo.TitleBar;
import com.test.ad.demo.mediavideo.VideoPlayerWithAdPlayback;

/**
 * Description:The public view bean of the page.
 **/
public class CommonViewBean {
    private TitleBar titleBar;
    private TextView tvLogView;
    private Spinner spinnerSelectPlacement;
    private int titleResId;
    private VideoPlayerWithAdPlayback videoPlayerWithAdPlayback;

    public CommonViewBean() {

    }

    public TitleBar getTitleBar() {
        return titleBar;
    }

    public void setTitleBar(TitleBar titleBar) {
        this.titleBar = titleBar;
    }

    public Spinner getSpinnerSelectPlacement() {
        return spinnerSelectPlacement;
    }

    public void setSpinnerSelectPlacement(Spinner spinnerSelectPlacement) {
        this.spinnerSelectPlacement = spinnerSelectPlacement;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public void setTitleResId(int titleResId) {
        this.titleResId = titleResId;
    }

    public TextView getTvLogView() {
        return tvLogView;
    }

    public void setTvLogView(TextView tvLogView) {
        this.tvLogView = tvLogView;
    }

    public VideoPlayerWithAdPlayback getVideoPlayerWithAdPlayback() {
        return videoPlayerWithAdPlayback;
    }

    public void setVideoPlayerWithAdPlayback(VideoPlayerWithAdPlayback videoPlayerWithAdPlayback) {
        this.videoPlayerWithAdPlayback = videoPlayerWithAdPlayback;
    }
}

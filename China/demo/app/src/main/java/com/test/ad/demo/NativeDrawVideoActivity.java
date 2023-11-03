package com.test.ad.demo;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.AdError;
import com.anythink.nativead.api.ATNative;
import com.anythink.nativead.api.ATNativeNetworkListener;
import com.anythink.nativead.api.NativeAd;
import com.dingmouren.layoutmanagergroup.viewpager.OnViewPagerListener;
import com.dingmouren.layoutmanagergroup.viewpager.ViewPagerLayoutManager;
import com.test.ad.demo.bean.NativeDrawItem;
import com.test.ad.demo.util.PlacementIdUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeDrawVideoActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();

    private int[] imgs = {R.mipmap.testvideo1_thumb, R.mipmap.testvideo2_thumb, R.mipmap.testvideo2_thumb};
    private int[] videos = {R.raw.testvideo1, R.raw.testvideo2, R.raw.testvideo2};

    private RecyclerView mRecyclerView;
    private ViewPagerLayoutManager mLayoutManager;
    private NativeDrawAdapter mAdapter;

    private String placementId;
    private ATNative mATNative;
    private List<NativeDrawItem> datas = new ArrayList<>();
    private int adViewWidth;
    private int adViewHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Throwable ignore) {
        }
        setContentView(R.layout.activity_native_draw_video);

        adViewWidth = getResources().getDisplayMetrics().widthPixels;
        adViewHeight = getResources().getDisplayMetrics().heightPixels;

        placementId = PlacementIdUtil.getDrawPlacementId(this);
        initDrawAdRequest();
        initView();
        initListener();
        createMockDataAndShow();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler);

        mLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL);
        mAdapter = new NativeDrawAdapter(this, datas, mATNative);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListener() {
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {
                onLayoutComplete();
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                Log.i(TAG, "position:" + position + " isNext:" + isNext);
                int index = 0;
                if (isNext) {
                    index = 0;
                } else {
                    index = 1;
                }
                if (datas.get(position).type == NativeDrawItem.NORMAL_ITME)
                    releaseVideo(index);
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                Log.i(TAG, "position:" + position + "  isBottom:" + isBottom);
                if (datas.get(position).type == NativeDrawItem.NORMAL_ITME) {
                    playVideo();
                }
                if (isBottom) {
                    //next page
                    createMockDataAndShow();
                }
            }

            private void onLayoutComplete() {
                if (datas.get(0).type == NativeDrawItem.NORMAL_ITME) {
                    playVideo();
                }
            }

        });
    }


    private void initDrawAdRequest() {
        mATNative = new ATNative(this, placementId, new ATNativeNetworkListener() {
            @Override
            public void onNativeAdLoaded() {
                Log.i(TAG, "onNativeAdLoaded");
            }

            @Override
            public void onNativeAdLoadFail(AdError adError) {
                Log.i(TAG, "onNativeAdLoadFail, " + adError.getFullErrorInfo());
            }
        });

        Map<String, Object> localMap = new HashMap<>();

        // since v5.6.4
        localMap.put(ATAdConst.KEY.AD_WIDTH, adViewWidth);
        localMap.put(ATAdConst.KEY.AD_HEIGHT, adViewHeight);
//        localMap.put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, 0);
//        localMap.put(GDTATConst.AD_HEIGHT, -2);

        mATNative.setLocalExtra(localMap);
        mATNative.makeAdRequest();
    }

    private void createMockDataAndShow() {
        List<NativeDrawItem> tempList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            int random = (int) (Math.random() * 100);
            int index = random % videos.length;
            tempList.add(new NativeDrawItem(NativeDrawItem.NORMAL_ITME, null, videos[index], imgs[index]));
        }

        NativeAd nativeAd = mATNative.getNativeAd();
        if (nativeAd != null) {
            tempList.add(new NativeDrawItem(NativeDrawItem.AD_ITEM, nativeAd, -1, -1));
            Log.i(TAG, "add nativeAd:" + nativeAd.getAdMaterial().getDescriptionText());
        }
        mATNative.makeAdRequest();
        datas.addAll(tempList);
        mAdapter.notifyItemRangeChanged(datas.size() - tempList.size(), tempList.size());
        mAdapter.notifyDataSetChanged();
    }

    private void playVideo() {
        if (isFinishing()) {
            return;
        }

        View itemView = mRecyclerView.getChildAt(0);
        if (itemView == null) {
            return;
        }
        final FrameLayout videoLayout = itemView.findViewById(R.id.video_layout);
        if (videoLayout == null) {
            return;
        }
        View view = videoLayout.getChildAt(0);
        if (!(view instanceof VideoView)) {
            Log.i(TAG, "view not instanceof VideoView");
            return;
        }
        final VideoView videoView = (VideoView) videoLayout.getChildAt(0);
        final ImageView imgPlay = itemView.findViewById(R.id.img_play);
        final ImageView imgThumb = itemView.findViewById(R.id.img_thumb);
        final MediaPlayer[] mediaPlayer = new MediaPlayer[1];
        videoView.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    mediaPlayer[0] = mp;
                    Log.i(TAG, "onInfo");
                    mp.setLooping(true);
                    imgThumb.animate().alpha(0).setDuration(200).start();
                    if (mp != null && videoView != null) {
                        int mVideoWidth = mp.getVideoWidth();
                        int mVideoHeight = mp.getVideoHeight();
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) videoView.getLayoutParams();
                        if (mVideoWidth > 0 && mVideoHeight > 0 && layoutParams != null) {
                            int[] size = getScreenSize(NativeDrawVideoActivity.this.getApplicationContext());
                            layoutParams.width = mVideoWidth * size[1] / mVideoHeight;
                            layoutParams.height = size[1];
                            layoutParams.leftMargin = -(layoutParams.width - size[0]) / 2;
                            videoView.setLayoutParams(layoutParams);
                        }
                    }
                    return false;
                }
            });
        }
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i(TAG, "onPrepared");
            }
        });

        imgPlay.setOnClickListener(new View.OnClickListener() {
            boolean isPlaying = true;

            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    Log.i(TAG, "isPlaying:" + videoView.isPlaying());
                    imgPlay.animate().alpha(1f).start();
                    videoView.pause();
                    isPlaying = false;
                } else {
                    Log.i(TAG, "isPlaying:" + videoView.isPlaying());
                    imgPlay.animate().alpha(0f).start();
                    videoView.start();
                    isPlaying = true;
                }
            }
        });
    }

    private void releaseVideo(int index) {
        if (isFinishing()) {
            return;
        }

        View itemView = mRecyclerView.getChildAt(index);
        if (itemView != null) {
            final FrameLayout videoLayout = itemView.findViewById(R.id.video_layout);
            if (videoLayout == null) return;
            View view = videoLayout.getChildAt(0);
            if (view instanceof VideoView) {
                final VideoView videoView = (VideoView) videoLayout.getChildAt(0);
                final ImageView imgThumb = itemView.findViewById(R.id.img_thumb);
                final ImageView imgPlay = itemView.findViewById(R.id.img_play);
                videoView.stopPlayback();
                imgThumb.animate().alpha(1).start();
                imgPlay.animate().alpha(0f).start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLayoutManager != null) {
            mLayoutManager.setOnViewPagerListener(null);
        }
        if (datas != null) {
            for (NativeDrawItem drawItem : datas) {
                if (drawItem.nativeAd != null) {
                    drawItem.nativeAd.destory();
                    drawItem.nativeAd = null;
                }
            }
        }
    }

    public int dip2px(float dipValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int[] getScreenSize(Context context) {
        int[] size = new int[]{0, 0};
        if (context == null) {
            return size;
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        } else {
            display.getMetrics(dm);
        }
        size[0] = dm.widthPixels;
        size[1] = dm.heightPixels;
        return size;
    }

}

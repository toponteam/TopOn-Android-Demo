package com.test.ad.demo.zoomout;

import android.animation.Animator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

public class SplashZoomOutManager {
    private static final String TAG = "SplashZoomOutManager";
    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private int zoomOutWidth;//悬浮窗的宽度
    private int zoomOutHeight;//悬浮窗的高度
    private int zoomOutMargin;//悬浮窗最小离屏幕边缘的距离
    private int zoomOutMarginBottom;//悬浮窗默认距离屏幕底端的高度
    private int zoomOutPos;//悬浮窗默认位于屏幕左面或右面
    private int zoomOutAnimationTime;//悬浮窗缩放动画的，单位ms

    private View splashView;

    private int originSplashWidth;
    private int originSplashHeight;
    private int[] originSplashPos = new int[2];
    private int decorViewWidth;
    private int decorViewHeight;

    private volatile static SplashZoomOutManager instance;

    public interface AnimationCallBack {
        void animationStart(int animationTime);

        void animationEnd();
    }

    public static SplashZoomOutManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SplashZoomOutManager.class) {
                if (instance == null)
                    instance = new SplashZoomOutManager(context);
            }
        }
        return instance;
    }

    private SplashZoomOutManager(Context context) {
        int deviceWidth = Math.min(PxUtils.getDeviceHeightInPixel(context), PxUtils.getDeviceWidthInPixel(context));
        zoomOutWidth = Math.round(deviceWidth * 0.3f);//屏幕宽度的30%，之前使用PxUtils.dpToPx(context, 90);
        zoomOutHeight = Math.round(zoomOutWidth * 16 / 9);//根据宽度计算高度，之前使用PxUtils.dpToPx(context, 160);

        zoomOutMargin = PxUtils.dpToPx(context, 6);
        zoomOutMarginBottom = PxUtils.dpToPx(context, 100);
        zoomOutPos = RIGHT;
        zoomOutAnimationTime = 300;
    }

    /**
     * 用于开屏v+在两个activity之间传递数据
     *
     * @param splashView 开屏对应显示view，外部提供开屏container的子view
     * @param decorView  因为在另一个单独的activity启动时获取不到view尺寸，在这里获取下decorView的尺寸，在展示悬挂的
     *                   activity使用该尺寸布局
     */
    public void setSplashInfo(View splashView, View decorView) {
        this.splashView = splashView;
        splashView.getLocationOnScreen(originSplashPos);
        originSplashWidth = splashView.getWidth();
        originSplashHeight = splashView.getHeight();
        decorViewWidth = decorView.getWidth();
        decorViewHeight = decorView.getHeight();
    }

    public void setSplashEyeAdViewSize(int width, int height) {
        zoomOutWidth = width;
        zoomOutHeight = height;
    }


    public void clearStaticData() {
        splashView = null;
    }


    /**
     * 开屏采用单独的activity时候，悬浮窗显示在另外一个activity使用该函数进行动画
     * 调用前要先调用setSplashInfo设置数据，该函数会使用setSplashInfo设置的数据，并会清除对设置数据的引用
     *
     * @param animationContainer 一般是decorView
     * @param zoomOutContainer   最终浮窗所在的父布局
     * @param callBack           动画完成的回调
     */
    public ViewGroup startZoomOut(final ViewGroup animationContainer,
                                  final ViewGroup zoomOutContainer,
                                  final AnimationCallBack callBack) {
        Log.d(TAG, "zoomOut startZoomOut activity");
        if (animationContainer == null || zoomOutContainer == null) {
            Log.d(TAG, "zoomOut animationContainer or zoomOutContainer is null");
            return null;
        }

        if (splashView == null) {
            Log.d(TAG, "zoomOut splashAD or splashView is null");
            return null;
        }
        //先把view按照原来的尺寸显示出来
        int[] animationContainerPos = new int[2];
        animationContainer.getLocationOnScreen(animationContainerPos);
        int x = originSplashPos[0] - animationContainerPos[0];
        int y = originSplashPos[1] - animationContainerPos[1];

        ViewUtils.removeFromParent(splashView);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(originSplashWidth,
                originSplashHeight);
        animationContainer.addView(splashView, layoutParams);
        splashView.setX(x);
        splashView.setY(y);
        return startZoomOut(splashView, animationContainer, zoomOutContainer, callBack);
    }

    /**
     * 开屏显示和悬浮窗显示在同一个activity中
     * 使用该函数会清除setSplashInfo设置的数据
     * * 动画步骤：
     * 1、把需要动画的view从父布局中移除出来，目的是在动画时可以隐藏其他开屏的view
     * 2、把splash对应的view加到动画的view里开始动画，因为动画窗口可能比较最终的布局要大
     * 3、在动画结束把splash view加到zoomOutContainer里
     *
     * @param splash             开屏对应的view;
     * @param animationContainer 开屏动画所在的layout
     * @param zoomOutContainer   动画结束时，最终悬浮窗所在的父布局
     * @param callBack           动画结束时的回调，splashAdView无法感知动画的执行时间，需要使用该函数通知动画结束了
     */
    public ViewGroup startZoomOut(final View splash, final ViewGroup animationContainer,
                                  final ViewGroup zoomOutContainer,
                                  final AnimationCallBack callBack) {
        clearStaticData();//单例清除下引用的view和ad数据，免得内存泄漏
        if (splash == null || zoomOutContainer == null) {
            return null;
        }
        final Context context = zoomOutContainer.getContext();
        final int[] splashScreenPos = new int[2];
        splash.getLocationOnScreen(splashScreenPos);

        int fromWidth = splash.getWidth();
        int fromHeight = splash.getHeight();
        int animationContainerWidth = animationContainer.getWidth();
        int animationContainerHeight = animationContainer.getHeight();

        if (animationContainerWidth == 0) {
            animationContainerWidth = decorViewWidth;
        }
        if (animationContainerHeight == 0) {
            animationContainerHeight = decorViewHeight;
        }
        float xScaleRatio = (float) zoomOutWidth / fromWidth;
        float yScaleRation = (float) zoomOutHeight / fromHeight;
        final float animationDistX = zoomOutPos == LEFT ? zoomOutMargin :
                animationContainerWidth - zoomOutMargin - zoomOutWidth;
        final float animationDistY = animationContainerHeight - zoomOutMarginBottom - zoomOutHeight;  //最终位于container的y坐标

        Log.d(TAG, "zoomOut animationContainerWidth:" + animationContainerWidth + " " +
                "animationContainerHeight:" + animationContainerHeight);
        Log.d(TAG, "zoomOut splashScreenX:" + splashScreenPos[0] + " splashScreenY:" + splashScreenPos[1]);
        Log.d(TAG, "zoomOut splashWidth:" + fromWidth + " splashHeight:" + fromHeight);
        Log.d(TAG, "zoomOut width:" + zoomOutWidth + " height:" + zoomOutHeight);
        Log.d(TAG, "zoomOut animationDistX:" + animationDistX + " animationDistY:" + animationDistY);

        ViewUtils.removeFromParent(splash);
        FrameLayout.LayoutParams animationParams = new FrameLayout.LayoutParams(fromWidth, fromHeight);
        animationContainer.addView(splash, animationParams);

        final ViewGroup zoomOutView = new SplashZoomOutLayout(context, zoomOutMargin);

        splash.setPivotX(0);
        splash.setPivotY(0);
        splash.animate()
                .scaleX(xScaleRatio)
                .scaleY(yScaleRation)
                .x(animationDistX)
                .y(animationDistY)
                .setInterpolator(new OvershootInterpolator(0))
                .setDuration(zoomOutAnimationTime)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        Log.d(TAG, "zoomOut onAnimationStart");
                        if (callBack != null) {
                            callBack.animationStart(zoomOutAnimationTime);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Log.d(TAG, "zoomOut onAnimationEnd");
                        ViewUtils.removeFromParent(splash);
                        splash.setScaleX(1);
                        splash.setScaleY(1);
                        splash.setX(0);
                        splash.setY(0);
                        int[] zoomOutContainerScreenPos = new int[2];
                        zoomOutContainer.getLocationOnScreen(zoomOutContainerScreenPos);
                        float distX = animationDistX - zoomOutContainerScreenPos[0] + splashScreenPos[0];
                        float distY = animationDistY - zoomOutContainerScreenPos[1] + splashScreenPos[1];
                        Log.d(TAG, "zoomOut distX:" + distX + " distY:" + distY);
                        Log.d(TAG, "zoomOut containerScreenX:" + zoomOutContainerScreenPos[0] + " " +
                                "containerScreenY:" + zoomOutContainerScreenPos[1]);
                        zoomOutView.addView(splash, FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT);
                        FrameLayout.LayoutParams zoomOutParams = new FrameLayout.LayoutParams(zoomOutWidth,
                                zoomOutHeight);
                        zoomOutContainer.addView(zoomOutView, zoomOutParams);
                        zoomOutView.setTranslationX(distX);
                        zoomOutView.setTranslationY(distY);
                        if (callBack != null) {
                            callBack.animationEnd();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
        return zoomOutView;
    }

}

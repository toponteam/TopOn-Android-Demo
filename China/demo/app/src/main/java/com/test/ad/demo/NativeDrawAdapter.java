package com.test.ad.demo;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.anythink.core.api.ATAdInfo;
import com.anythink.nativead.api.ATNative;
import com.anythink.nativead.api.ATNativeAdView;
import com.anythink.nativead.api.ATNativeDislikeListener;
import com.anythink.nativead.api.ATNativeEventListener;
import com.anythink.nativead.api.ATNativeImageView;
import com.anythink.nativead.api.ATNativeMaterial;
import com.anythink.nativead.api.ATNativePrepareExInfo;
import com.anythink.nativead.api.ATNativePrepareInfo;
import com.anythink.nativead.api.ATNativeView;
import com.anythink.nativead.api.NativeAd;
import com.test.ad.demo.bean.NativeDrawItem;
import com.test.ad.demo.util.SelfRenderViewUtil;
import com.test.ad.demo.view.FullScreenVideoView;

import java.util.ArrayList;
import java.util.List;


public class NativeDrawAdapter extends RecyclerView.Adapter<NativeDrawAdapter.ViewHolder> {

    private String TAG = getClass().getSimpleName();

    private Context mContext;
    private List<NativeDrawItem> datas;
    ATNative mNativeAdHandler;

    public NativeDrawAdapter(Context context, List<NativeDrawItem> datas, ATNative nativeAdHandler) {
        this.mContext = context;
        this.datas = datas;
        this.mNativeAdHandler = nativeAdHandler;
    }

//    public void addData(List<NativeDrawItem> data) {
//        if (data != null) {
//            int oldSize = datas.size();
//            int InsertSize = data.size();
//            this.datas.addAll(data);
//
//            this.notifyItemRangeChanged(oldSize, InsertSize);
//        }
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == NativeDrawItem.AD_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_draw_ad_item, parent, false);
            return new AdViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_pager, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        View view = null;
        NativeDrawItem item = null;
        if (datas != null) {
            item = datas.get(position);
            if (item.type == NativeDrawItem.NORMAL_ITME) {
                holder.img_thumb.setImageResource(item.ImgId);
                view = getView();
                ((VideoView) view).setVideoURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + item.videoId));

                holder.videoLayout.removeAllViews();
                holder.videoLayout.addView(view);
                changeUIVisibility(holder, item.type);
            } else if (item.type == NativeDrawItem.AD_ITEM) {
                renderAd(item.nativeAd, (AdViewHolder) holder, position);
            }
        }
    }

    private void renderAd(final NativeAd nativeAd, AdViewHolder adViewHolder, int position) {
        nativeAd.setNativeEventListener(new ATNativeEventListener() {
            @Override
            public void onAdImpressed(ATNativeAdView view, ATAdInfo entity) {
                Log.i(TAG, "native ad onAdImpressed--------\n" + entity.toString());
            }

            @Override
            public void onAdClicked(ATNativeAdView view, ATAdInfo entity) {
                Log.i(TAG, "native ad onAdClicked--------\n" + entity.toString());
            }

            @Override
            public void onAdVideoStart(ATNativeAdView view) {
                Log.i(TAG, "native ad onAdVideoStart--------");
            }

            @Override
            public void onAdVideoEnd(ATNativeAdView view) {
                Log.i(TAG, "native ad onAdVideoEnd--------");
            }

            @Override
            public void onAdVideoProgress(ATNativeAdView view, int progress) {
                Log.i(TAG, "native ad onAdVideoProgress--------:" + progress);
            }
        });

        adViewHolder.atNativeView.removeAllViews();

        try {
            Log.i(TAG, "native ad start to render ad------------- ");

            ATNativePrepareInfo nativePrepareInfo = null;
            if (nativeAd.isNativeExpress()) {
                adViewHolder.atNativeView.getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
                nativeAd.renderAdContainer(adViewHolder.atNativeView, null);
            } else {
                adViewHolder.atNativeView.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
                nativePrepareInfo = new ATNativePrepareInfo();

                bindSelfRenderView(nativeAd.getAdMaterial(), adViewHolder.selfRenderView, nativePrepareInfo);
                nativeAd.renderAdContainer(adViewHolder.atNativeView, adViewHolder.selfRenderView);
            }
            nativeAd.setDislikeCallbackListener(new ATNativeDislikeListener() {
                @Override
                public void onAdCloseButtonClick(ATNativeAdView view, ATAdInfo entity) {
                    datas.remove(position);
                    notifyItemRemoved(position);
                    nativeAd.destory();
                }
            });
            nativeAd.prepare(adViewHolder.atNativeView, nativePrepareInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bindSelfRenderView(ATNativeMaterial adMaterial, View selfRenderView, ATNativePrepareInfo nativePrepareInfo) {
        FrameLayout contentArea = selfRenderView.findViewById(R.id.native_draw_main_image_area);
        ATNativeImageView icon = selfRenderView.findViewById(R.id.icon_view);
        TextView title = selfRenderView.findViewById(R.id.tv_title);
        TextView desc = selfRenderView.findViewById(R.id.tv_desc);
        Button action = selfRenderView.findViewById(R.id.button_creative);

        contentArea.removeAllViews();
        List<View> clickViewList = new ArrayList<>();//click views
        View mediaView = adMaterial.getAdMediaView(contentArea);

        if (!TextUtils.isEmpty(adMaterial.getIconImageUrl())) {
            icon.setImage(adMaterial.getIconImageUrl());
        } else {
            icon.setImageBitmap(null);
        }

        if (!TextUtils.isEmpty(adMaterial.getTitle())) {
            title.setText(adMaterial.getTitle());
            title.setVisibility(View.VISIBLE);
        } else {
            title.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(adMaterial.getDescriptionText())) {
            desc.setText(adMaterial.getDescriptionText());
            desc.setVisibility(View.VISIBLE);
        } else {
            desc.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(adMaterial.getCallToActionText())) {
            action.setText(adMaterial.getCallToActionText());
            action.setVisibility(View.VISIBLE);
        } else {
            action.setVisibility(View.GONE);
        }

        clickViewList.add(title);
        clickViewList.add(desc);
        clickViewList.add(action);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        // media view
        if (mediaView != null) {
            if (mediaView.getParent() != null) {
                ((ViewGroup) mediaView.getParent()).removeView(mediaView);
            }
            params.gravity = Gravity.CENTER;
            mediaView.setLayoutParams(params);
            contentArea.addView(mediaView, params);
        } else if (!TextUtils.isEmpty(adMaterial.getMainImageUrl())) {
            ATNativeImageView imageView = new ATNativeImageView(mContext);
            imageView.setImage(adMaterial.getMainImageUrl());
            nativePrepareInfo.setMainImageView(imageView);//bind main image
            contentArea.addView(imageView, params);
            contentArea.setVisibility(View.VISIBLE);
        } else {
            contentArea.removeAllViews();
            contentArea.setVisibility(View.GONE);
        }

        nativePrepareInfo.setClickViewList(clickViewList);//bind click view list

        if (nativePrepareInfo instanceof ATNativePrepareExInfo) {
            List<View> creativeClickViewList = new ArrayList<>();//click views
            ((ATNativePrepareExInfo) nativePrepareInfo).setCreativeClickViewList(creativeClickViewList);//bind custom view list
        }

        int adChoiceSize = SelfRenderViewUtil.dip2px(mContext, 30);
        FrameLayout.LayoutParams adChoickLayoutParams = new FrameLayout.LayoutParams(adChoiceSize, ViewGroup.LayoutParams.WRAP_CONTENT);
        adChoickLayoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        adChoickLayoutParams.bottomMargin = SelfRenderViewUtil.dip2px(mContext, 7);
        adChoickLayoutParams.rightMargin = SelfRenderViewUtil.dip2px(mContext, 12);

        nativePrepareInfo.setChoiceViewLayoutParams(adChoickLayoutParams);
    }


    private View getView() {
        FullScreenVideoView videoView = new FullScreenVideoView(mContext);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        videoView.setLayoutParams(layoutParams);
        return videoView;
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).type;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_thumb;
//        CircleImageView img_head_icon;
//        ImageView img_play;
        RelativeLayout rootView;
        FrameLayout videoLayout;
//        LinearLayout verticalIconLauout;

        public ViewHolder(View itemView) {
            super(itemView);
            img_thumb = itemView.findViewById(R.id.img_thumb);
            videoLayout = itemView.findViewById(R.id.video_layout);
//            img_play = itemView.findViewById(R.id.img_play);
            rootView = itemView.findViewById(R.id.root_view);
//            verticalIconLauout = itemView.findViewById(R.id.vertical_icon);
//            img_head_icon = itemView.findViewById(R.id.head_icon);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    private void changeUIVisibility(NativeDrawAdapter.ViewHolder holder, int type) {
        boolean visibilable = true;
        if (type == NativeDrawItem.AD_ITEM) {
            visibilable = false;
        }
        Log.d(TAG, "visibilable=" + visibilable);
//        holder.img_play.setVisibility(visibilable ? View.VISIBLE : View.GONE);
        holder.img_thumb.setVisibility(visibilable ? View.VISIBLE : View.GONE);

    }

    protected class AdViewHolder extends NativeDrawAdapter.ViewHolder {
        protected ATNativeView atNativeView;
        protected View selfRenderView;

        public AdViewHolder(View itemView) {
            super(itemView);
            atNativeView = (ATNativeView) itemView.findViewById(R.id.native_draw_ad_view);
            selfRenderView = itemView.findViewById(R.id.native_draw_selfrender_view);
        }

    }


}

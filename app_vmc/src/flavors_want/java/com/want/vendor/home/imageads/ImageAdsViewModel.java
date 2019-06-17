package com.want.vendor.home.imageads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vmc.core.model.ads.AdList;
import com.vmc.core.model.ads.Ads;
import com.vmc.core.utils.AdsUtils;
import com.vmc.core.worker.ads.AdsUpdaterWorker;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.core.log.lg;
import com.want.vendor.home.bigimgads.BigImgAdsActivity;
import com.want.vendor.ui.view.GlideRoundTransform;
import com.want.vmc.R;


import java.util.ArrayList;

import vmc.core.log;
import vmc.project.ui.view.PhotoCarouselView;

import static de.greenrobot.event.EventBus.TAG;

/**
 * <b>Create Date:</b> 2016/11/14<br>
 * <b>Author:</b> huyunqiang<br>
 * <b>Description:</b>中间图片广告模块 <br>
 */
public class ImageAdsViewModel extends AbsViewModel {
    Context mContext;
    private PhotoCarouselView view;
    private BroadcastReceiver mAdsUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            log.d(TAG, "mAdsUpdateReceiver, 广告更新广播 ");
            if (AdsUpdaterWorker.ACTION_ADS_UPDATE.equals(action)) {
                getImageList();
                log.d(TAG, "mAdsUpdateReceiver  更新绑定数据 ");
            }
        }

    };

    public ImageAdsViewModel(Context context) {
        super();
        this.mContext = context;
    }

    public ImageAdsViewModel(final PhotoCarouselView view, Context context) {
        super();
        this.mContext = context;
        this.view = view;
        this.view.setImageLoader(new PhotoCarouselView.ImageLoaderListener() {
            @Override
            public void onLoadImage(ImageView imageView, String url) {
                Glide.with(mContext.getApplicationContext())
                     .load(url)
                     .placeholder(R.drawable.vendor_home_banner_loading)
                     .error(R.drawable.vendor_home_banner_loading)
                     .transform(new GlideRoundTransform(mContext, 20))
                     .into(imageView);
            }
        });
        view.setBackgroundResource(R.drawable.vendor_home_banner_loading);
        getImageList();
    }

    public void onResume() {
        view.startPhotoCarousel();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AdsUpdaterWorker.ACTION_ADS_UPDATE);//广告
        mContext.registerReceiver(mAdsUpdateReceiver, filter);

    }

    public void onDestroy() {
        mContext.unregisterReceiver(mAdsUpdateReceiver);
        view.stopPhotoCarousel();
        view.removeCallbacksAndMessages();
    }


    /**
     * 获取图片
     */
    public void getImageList() {
        final AdList list = AdsUtils.getAdList(mContext, Ads.AdType.IMAGE);
        if (null == list||(list.records!=null&&list.records.size()==0)||null==list.records) {
            view.setBackgroundResource(R.drawable.vendor_home_banner_loading);
            view.setImageRes(new ArrayList<Ads>());//设置数据源
            view.setCarouselLayout();//设置适配器
            view.setIsTouchScroll(true);//设置不能手动滑动
            return;
        }


//                      view.setBackgroundResource(0);//删除设置加载中图片
        view.setBackgroundResource(android.R.color.transparent);//设置下载完成后，背景图为透明
        view.setImageRes(list.records);//设置数据源
        view.setCarouselLayout();//设置适配器
        view.setIsTouchScroll(true);//设置不能手动滑动

        view.setOnItemClickLinsener(new PhotoCarouselView.OnItemClickLinsener() {
            @Override
            public void onClick(int pos) {
                BigImgAdsActivity.start(mContext, list.records.get(pos).ad_detail);
            }
        });


    }

}

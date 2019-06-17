package vmc.project.ui.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vmc.core.model.ads.Ads;
import com.want.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import vmc.project.R;

/**
 * <b>Create Date:</b> 16/9/23<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:图片轮播自定义控件</b>
 */

public class PhotoCarouselView extends FrameLayout {

    private static final int DELAY_SECONDS = 10000;//轮播间隔时间
    private static final int DOTS_WIDTH = 8;//指示点宽度
    private static final int DOTS_HEIGHT = 8;//指示点高度
    OnItemClickLinsener mItemClickLinsener;
    private List<ImageView> mPhotoIVList;//轮播图片集合
    private ViewPager mViewPager;
    private Context mContext;
    private LinearLayout mDotLinearLayout;//轮播指示点
    private int mCountInt;
    private Handler mHandler;
    private CarouselPagerAdapter mCarouselPagerAdapter;
    private MyOnPageChangeListener myOnPageChangeListener;
    private ImageView imageDots;//指示点的ImageView
    private ImageView imageCarouse;//图片轮播的ImageView
    private LinearLayout.LayoutParams mDotsLayoutParams;
    private boolean isTouchScroll;//是否触摸滚动
    private IViewPagerScrollToEnd iViewPagerScrollToEnd;
    private ImageLoaderListener mImageLoader = new ImageLoaderListener() {
        @Override
        public void onLoadImage(ImageView imageView, String url) {
            new ImageLoader.Builder().view(imageCarouse)
                                     .with(mContext)
                                     .url(url)
                                     .error(R.drawable.default_image)
                                     .build()
                                     .load();
        }
    };
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mViewPager.getCurrentItem() == mCountInt-1) {
                mViewPager.setCurrentItem(0);
                mHandler.postDelayed(mRunnable,DELAY_SECONDS);
            } else {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                mHandler.postDelayed(mRunnable, DELAY_SECONDS);
            }
        }
    };

    public PhotoCarouselView(Context context) {
        this(context, null);
    }

    public PhotoCarouselView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoCarouselView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initData();
    }

    public void setImageLoader(ImageLoaderListener imageLoader) {
        mImageLoader = imageLoader;
    }

    public void setiViewPagerScrollToEnd(IViewPagerScrollToEnd iViewPagerScrollToEnd) {
        this.iViewPagerScrollToEnd = iViewPagerScrollToEnd;
    }

    /**
     * 初始化
     */
    private void initData() {
        mPhotoIVList = new ArrayList<>();
        mHandler = new Handler();
        initLayout();
    }

    /**
     * 提供给外部的方法用于设置图片资源
     *
     * @param imageList 轮播图片集合
     */
    public void setImageRes(List<Ads> imageList) {
        mCountInt = imageList.size();
        mDotLinearLayout.removeAllViews();
        mPhotoIVList.clear();
        /**
         * 添加Dots
         */
//        for (int i = 0; i < mCountInt; i++) {
//            imageDots = new ImageView(mContext);
//            mDotsLayoutParams = new LinearLayout.LayoutParams(DOTS_WIDTH, DOTS_HEIGHT);
//            mDotsLayoutParams.leftMargin = 5;
//            mDotsLayoutParams.rightMargin = 5;
//            imageDots.setLayoutParams(mDotsLayoutParams);
//            imageDots.setBackgroundResource(R.drawable.dot_selector);
//            mDotLinearLayout.addView(imageDots);
//        }

        for (int i = 0; i < mCountInt; i++) {
            imageCarouse = new ImageView(mContext);
            imageCarouse.setScaleType(ImageView.ScaleType.FIT_XY);
            if(null != mImageLoader){
                mImageLoader.onLoadImage(imageCarouse, imageList.get(i).ad_url);
            }
            mPhotoIVList.add(imageCarouse);
        }
//        /**
//         * 添加图片
//         */
//        for (int i = 1; i <= mCountInt + 1; i++) {
//            imageCarouse = new ImageView(mContext);
//            imageCarouse.setScaleType(ImageView.ScaleType.FIT_XY);
//            if (i == mCountInt + 1) {
//                new ImageLoader.Builder().view(imageCarouse).with(mContext).url(imageList.get(0).ad_url).error(R.drawable.default_image).build().load();
//            } else {
//                new ImageLoader.Builder().view(imageCarouse).with(mContext).url(imageList.get(i - 1).ad_url).error(R.drawable.default_image).build().load();
//            }
//            mPhotoIVList.add(imageCarouse);
//        }
    }

    /**
     * 初始化布局控件
     */
    private void initLayout() {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.carousel_layout, this, true);
        mViewPager = (ViewPager) mView.findViewById(R.id.carousel_viewpager);
        mViewPager.setEnabled(false);
        mDotLinearLayout = (LinearLayout) mView.findViewById(R.id.dot_linearlayout);
        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isTouchScroll;
            }
        });
        mCarouselPagerAdapter = new CarouselPagerAdapter();
        myOnPageChangeListener = new MyOnPageChangeListener();
        mViewPager.setFocusable(true);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(myOnPageChangeListener);

    }

    /**
     * 从网络获取图片并且设置轮播图片和轮播指示点
     */
    public void setCarouselLayout() {
        mViewPager.setAdapter(mCarouselPagerAdapter);
//        if (mCarouselPagerAdapter.getCount()>0){
//            updateDot(0);
//        }

    }

    /**
     * 开始轮播
     */
    public void startPhotoCarousel() {
        mHandler.postDelayed(mRunnable, DELAY_SECONDS);
    }

    /**
     * 停止轮播
     */
    public void stopPhotoCarousel() {
        mHandler.removeCallbacks(mRunnable);
    }

    /**
     * 将Handler所有的Callbacks和Messages全部清除,避免内存泄漏
     */
    public void removeCallbacksAndMessages() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 更新指示点
     */
//    private void updateDot(int position) {
//        for (int i = 0; i <mCountInt; i++) {
//            mDotLinearLayout.getChildAt(i).setEnabled(false);
//        }
//        mDotLinearLayout.getChildAt(position).setEnabled(true);
//    }

    /**
     * 设置是否能触摸滑动
     *
     * @param isTouchScroll
     */
    public void setIsTouchScroll(boolean isTouchScroll) {
        this.isTouchScroll = !isTouchScroll;
    }

    public void setOnItemClickLinsener(OnItemClickLinsener linsener) {
        this.mItemClickLinsener = linsener;
    }

    public interface ImageLoaderListener {
        void onLoadImage(ImageView imageView, String url);
    }

    /**
     * 定义一个接口,当滚动到最后一页时,通知页面进行相应的操作
     */
    public interface IViewPagerScrollToEnd {
        void viewPagerScrollToEnd();
    }

    public interface OnItemClickLinsener {
        void onClick(int pos);

    }

    /**
     * 重写PagerAdapter
     */

    class CarouselPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPhotoIVList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mPhotoIVList.get(position));

            mPhotoIVList.get(position).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickLinsener!=null){
                        mItemClickLinsener.onClick(mViewPager.getCurrentItem());
                    }
                }
            });



            return mPhotoIVList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if(position < mPhotoIVList.size()) {
                container.removeView(mPhotoIVList.get(position));
            }
        }
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
//            updateDot(position);
            /**
             * 滚动到最后一页时通知页面
             */

            if (null!=iViewPagerScrollToEnd&&position == mPhotoIVList.size() - 1) {
                iViewPagerScrollToEnd.viewPagerScrollToEnd();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }




    }
}

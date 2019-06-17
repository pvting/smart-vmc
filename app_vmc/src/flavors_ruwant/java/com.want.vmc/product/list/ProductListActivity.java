package com.want.vmc.product.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;

import com.vmc.core.utils.ConfigUtils;
import com.want.vmc.product.list.category.ProductCategoryContract;
import com.want.vmc.product.list.category.ProductCategoryFragment;
import com.want.vmc.product.list.category.ProductCategoryPresenter;
import com.want.vmc.product.list.list.ProductListContract;
import com.want.vmc.product.list.list.ProductListFragment;
import com.want.vmc.product.list.list.ProductListPresenter;

import com.want.vmc.R;

import vmc.vendor.VActivity;
import vmc.vendor.common.back.BackContract;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.common.back.BackPresenter;
import vmc.vendor.common.back.BackViewModel;

/**
 * <b>Create Date:</b> 10/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class ProductListActivity extends VActivity {

    private static final String FRAGMENT_TAG_LIST = "list";
    private static final String FRAGMENT_TAG_CATEGORY = "category";
    private BackPresenter mBackPresenter;


    public static void start(Context context) {
        Intent starter = new Intent(context, ProductListActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestToolbar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_product_list_activity);

        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = null;

        // 商品列表内容区
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_LIST);
        if (null == fragment && null != findViewById(R.id.product_list_content)) {
            ft = fm.beginTransaction();
            fragment = ProductListFragment.newInstance(getResources().getConfiguration().orientation);
            ft.add(R.id.product_list_content, fragment, FRAGMENT_TAG_LIST);
        }
        if (null != fragment) {
            new ProductListPresenter((ProductListContract.View) fragment);
        }

        //产品分类
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_CATEGORY);
        if(null == fragment && null != findViewById(R.id.product_list_tabs)){
            if (null == ft) {
                ft = fm.beginTransaction();
            }
            fragment = ProductCategoryFragment.newInstance();
            ft.add(R.id.product_list_tabs, fragment, FRAGMENT_TAG_CATEGORY);
        }
        if (null != fragment) {
            new ProductCategoryPresenter((ProductCategoryContract.View) fragment);
        }

        // 返回
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_BACK);
        if (null == fragment && null != findViewById(R.id.vendor_back)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }
            fragment = BackFragment.newInstance();
            ft.add(R.id.vendor_back, fragment, FRAGMENT_TAG_BACK);
        }
        if (null != fragment) {
            mBackPresenter = new BackPresenter((BackContract.View) fragment) {

                @Override
                public void onBack() {
                    super.onBack();
                    ProductListActivity.this.finish();
                }

                @Override
                public void onTimerEnd() {
                    super.onTimerEnd();
                    ProductListActivity.this.finish();
                }
            };
        }

        if (null != ft) {
            ft.commit();
        }

    }

//    private long firstTouch;
//
//    /**
//     * 应对快速点击
//     * @param ev 事件
//     * @return 拦截状态
//     */
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_UP) {
//            if (System.currentTimeMillis() - firstTouch < 2000) {
//                return true;
//            } else {
//                firstTouch = System.currentTimeMillis();
//            }
//        }
//        return super.dispatchTouchEvent(ev);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConfigUtils.getConfig(ProductListActivity.this) != null &&
            ConfigUtils.getConfig(ProductListActivity.this).vmc_count_down_time_settings != null) {
            if(ConfigUtils.getConfig(ProductListActivity.this).vmc_count_down_time_settings.general_page_countdown != 0) {
                mBackPresenter.setTimeLeft(ConfigUtils.getConfig(ProductListActivity.this).vmc_count_down_time_settings.general_page_countdown);
            }else{
                mBackPresenter.setTimeLeft(BackViewModel.DEFAULT_TIMELEFT);
            }
        }
    }
}

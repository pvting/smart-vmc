package com.want.vmc.guide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.want.vmc.guide.shopping.ShoppingGuideContract;
import com.want.vmc.guide.shopping.ShoppingGuideFragment;
import com.want.vmc.guide.shopping.ShoppingGuidePresenter;
import com.want.vmc.home.info.InfoContract;
import com.want.vmc.home.info.InfoFragment;
import com.want.vmc.home.info.InfoPresenter;

import com.want.vmc.R;
import vmc.vendor.VActivity;
import vmc.vendor.common.back.BackContract;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.common.back.BackPresenter;

/**
 * <b>Create Date:</b> 10/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class ShoppingGuideActivity extends VActivity {

    private static final String FRAGMENT_TAG_INFO = "info";
    private static final String FRAGMENT_TAG_BUYGUIDE = "shoppingguide";
    private static final String FRAGMENT_TAG_SHOPPING = "shopping";
    private BackPresenter mBackPresenter;

    public static void start(Context context) {
        Intent starter = new Intent(context, ShoppingGuideActivity.class);
        context.startActivity(starter);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestToolbar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_shoppingguide_activity);

        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = null;

        // TODO: 10/22/16 处理Activity内存重启

        // 信息展示
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_INFO);
        if (null == fragment && null != findViewById(R.id.home_info)) {
            ft = fm.beginTransaction();
            fragment = InfoFragment.newInstance();
            ft.add(R.id.home_info, fragment, FRAGMENT_TAG_INFO);
        }

        if (null != fragment) {
            new InfoPresenter((InfoContract.View) fragment);
        }

        // 购物引导页面
         fragment = fm.findFragmentByTag(FRAGMENT_TAG_BUYGUIDE);
        if (null == fragment && null != findViewById(R.id.home_shopping_guide)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }

            fragment = ShoppingGuideFragment.newInstance();

            ft.add(R.id.home_shopping_guide, fragment, FRAGMENT_TAG_BUYGUIDE);
        }

        if (null != fragment) {
            new ShoppingGuidePresenter((ShoppingGuideContract.View) fragment);
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
            new BackPresenter((BackContract.View) fragment) {

                @Override
                public void onBack() {
                    super.onBack();
                    ShoppingGuideActivity.this.finish();
                }

                @Override
                public void onTimerEnd() {
                    super.onTimerEnd();
                    ShoppingGuideActivity.this.finish();
                }
            };
        }


        if (null != ft) {
            ft.commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

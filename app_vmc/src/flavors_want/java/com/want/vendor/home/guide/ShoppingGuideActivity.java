package com.want.vendor.home.guide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.vmc.core.utils.ConfigUtils;
import com.want.vmc.R;
import com.want.vendor.common.help.HelpFragment;
import com.want.vendor.home.guide.shopping.ShoppingGuideContract;
import com.want.vendor.home.guide.shopping.ShoppingGuideFragment;
import com.want.vendor.home.guide.shopping.ShoppingGuidePresenter;

import vmc.vendor.VActivity;
import vmc.vendor.common.back.BackContract;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.common.back.BackPresenter;
import vmc.vendor.common.back.BackViewModel;

/**
 * <b>Create Date:</b> 10/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class ShoppingGuideActivity extends VActivity {

    private static final String FRAGMENT_TAG_INFO = "info";
    private static final String FRAGMENT_TAG_BUYGUIDE = "shoppingguide";
    private static final String FRAGMENT_TAG_SHOPPING = "shopping";
    protected static final String FRAGMENT_TAG_HELP = "f_t_help";
    private BackPresenter mBackPresenter;

    public static void start(Context context) {
        Intent starter = new Intent(context, ShoppingGuideActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestToolbar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_home_shopping_guide_activity);

        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = null;

        // TODO: 10/22/16 处理Activity内存重启

        // 购物引导页面
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_BUYGUIDE);
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

            mBackPresenter = new BackPresenter((BackContract.View) fragment) {

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
        //问题帮助
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_HELP);
        if (null == fragment && null != findViewById(R.id.vendor_help)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }
            fragment = HelpFragment.newInstance();
            ft.add(R.id.vendor_help, fragment, FRAGMENT_TAG_HELP);
        }
        if (null != ft) {
            ft.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConfigUtils.getConfig(ShoppingGuideActivity.this) != null &&
            ConfigUtils.getConfig(ShoppingGuideActivity.this).vmc_count_down_time_settings != null) {
            if (ConfigUtils.getConfig(ShoppingGuideActivity.this).vmc_count_down_time_settings.general_page_countdown !=
                0) {
                mBackPresenter.setTimeLeft(ConfigUtils.getConfig(ShoppingGuideActivity.this).vmc_count_down_time_settings.general_page_countdown);
            } else {
                mBackPresenter.setTimeLeft(BackViewModel.DEFAULT_TIMELEFT);
            }
        }
    }

    @Override
    public int getThrottleTime() {
        return 800;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

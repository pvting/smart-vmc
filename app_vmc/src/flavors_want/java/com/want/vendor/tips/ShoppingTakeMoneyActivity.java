package com.want.vendor.tips;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.vmc.core.utils.ConfigUtils;
import com.want.vmc.R;
import com.want.vendor.home.guide.shopping.ShoppingGuideContract;
import com.want.vendor.home.guide.shopping.ShoppingGuideFragment;
import com.want.vendor.home.guide.shopping.ShoppingGuidePresenter;

import vmc.vendor.VActivity;
import vmc.vendor.common.back.BackContract;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.common.back.BackPresenter;
import vmc.vendor.common.back.BackViewModel;
import vmc.vendor.utils.IntentHelper;

/**
 * <b>Create Date:</b> 2016/11/22<br>
 * <b>Author:</b> ZhongWenjie<br>
 * <b>Description:</b> <br>
 */
public class ShoppingTakeMoneyActivity extends VActivity {
    protected static final String FRAGMENT_TAG_TAKE_MONEY = "f_t_take_money";
    private BackPresenter mBackPresenter;

    public static void start(Context context) {
        Intent starter = new Intent(context, ShoppingTakeMoneyActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestToolbar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_shopping_takemoney_activity);
        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = null;


        // 错误提示跳转的请取出现金页面
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_TAKE_MONEY);
        if (null == fragment && null != findViewById(R.id.shopping_problem_money)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }

            fragment = ShoppingGuideFragment.newInstance();

            ft.add(R.id.shopping_problem_money, fragment, FRAGMENT_TAG_TAKE_MONEY);
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
                    ShoppingTakeMoneyActivity.this.finish();
                }

                @Override
                public void onTimerEnd() {
                    super.onTimerEnd();
                    IntentHelper.startHome(ShoppingTakeMoneyActivity.this);
                }
            };
        }

        if (null != ft) {
            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConfigUtils.getConfig(ShoppingTakeMoneyActivity.this) != null &&
            ConfigUtils.getConfig(ShoppingTakeMoneyActivity.this).vmc_count_down_time_settings != null) {
            if (ConfigUtils.getConfig(ShoppingTakeMoneyActivity.this).vmc_count_down_time_settings.general_page_countdown !=
                0) {
                mBackPresenter.setTimeLeft(ConfigUtils.getConfig(ShoppingTakeMoneyActivity.this).vmc_count_down_time_settings.general_page_countdown);
            }else{
                mBackPresenter.setTimeLeft(BackViewModel.DEFAULT_TIMELEFT);
            }
        }
    }
}
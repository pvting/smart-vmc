package com.want.vendor.home.guide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.vmc.core.utils.ConfigUtils;
import com.want.base.sdk.framework.eventbus.MEventBus;
import com.want.vendor.deliver.fai.ClearEditTextEventBus;
import com.want.vmc.R;
import com.want.vendor.home.guide.problem.ProblemHelpContract;
import com.want.vendor.home.guide.problem.ProblemHelpFragment;
import com.want.vendor.home.guide.problem.ProblemHelpPresenter;

import vmc.vendor.VActivity;
import vmc.vendor.common.back.BackContract;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.common.back.BackPresenter;
import vmc.vendor.common.back.BackViewModel;
import vmc.vendor.utils.IntentHelper;


public class ShoppingGuideProblemActivity extends VActivity {


    protected static final String FRAGMENT_TAG_PROBLEMHELP = "f_t_problemhelp";
    private BackPresenter mBackPresenter;

    public static void start(Context context) {
        Intent starter = new Intent(context, ShoppingGuideProblemActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_guide_hint_problem_activity);

        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = null;


        // 问题求助页面
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_PROBLEMHELP);
        if (null == fragment && null != findViewById(com.want.vmc.R.id.home_hint_problem)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }

            fragment = ProblemHelpFragment.newInstance();

            ft.add(com.want.vmc.R.id.home_hint_problem, fragment, FRAGMENT_TAG_PROBLEMHELP);
        }

        if (null != fragment) {
            new ProblemHelpPresenter((ProblemHelpContract.View) fragment);
        }

        // 返回
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_BACK);
        if (null == fragment && null != findViewById(com.want.vmc.R.id.vendor_back)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }
            fragment = BackFragment.newInstance();
            ft.add(com.want.vmc.R.id.vendor_back, fragment, FRAGMENT_TAG_BACK);
        }
        if (null != fragment) {
            mBackPresenter = new BackPresenter((BackContract.View) fragment) {

                @Override
                public void onBack() {
                    super.onBack();
                    MEventBus.getDefault().post(new ClearEditTextEventBus());
                    ShoppingGuideProblemActivity.this.finish();
                }

                @Override
                public void onTimerEnd() {
                    super.onTimerEnd();
                    IntentHelper.startHome(ShoppingGuideProblemActivity.this);
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
        if (ConfigUtils.getConfig(ShoppingGuideProblemActivity.this) != null &&
            ConfigUtils.getConfig(ShoppingGuideProblemActivity.this).vmc_count_down_time_settings != null) {
            if (ConfigUtils.getConfig(ShoppingGuideProblemActivity.this).vmc_count_down_time_settings.help_page_countdown !=
                0) {
                mBackPresenter.setTimeLeft(ConfigUtils.getConfig(ShoppingGuideProblemActivity.this).vmc_count_down_time_settings.help_page_countdown);
            }else{
                mBackPresenter.setTimeLeft(BackViewModel.DEFAULT_TIMELEFT);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

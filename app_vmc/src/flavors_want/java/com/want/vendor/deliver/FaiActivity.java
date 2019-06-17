package com.want.vendor.deliver;

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
import com.want.vendor.deliver.fai.FaiContract;
import com.want.vendor.deliver.fai.FaiFragment;
import com.want.vendor.deliver.fai.FaiPresenter;

import vmc.machine.core.VMCContoller;
import vmc.vendor.VActivity;
import vmc.vendor.common.back.BackContract;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.common.back.BackPresenter;
import vmc.vendor.common.back.BackViewModel;
import vmc.vendor.utils.IntentHelper;

/**
 * <b>Create Date:</b> 2016/11/14<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b> <br>
 */
public class FaiActivity extends VActivity {

    private static final String FRAGMENT_FAIL_SHOW = "FaiActivity";
    private BackPresenter mBackPresenter;

    public static void start(Context context,String str_no,String extra) {
        Intent starter = new Intent(context, FaiActivity.class);
        starter.putExtra("str_no",str_no);
        starter.putExtra("extra",extra);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestContentView();
        setContentView(R.layout.vendor_deliver_fillout_activity);
        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = null;
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_FAIL_SHOW);
        if (null == fragment) {
            ft = fm.beginTransaction();
            fragment = FaiFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("str_no",getIntent().getStringExtra("str_no"));
            bundle.putString("extra",getIntent().getStringExtra("extra"));

            fragment.setArguments(bundle);
            ft.add(R.id.vendor_deliver_show, fragment, FRAGMENT_FAIL_SHOW);
        }
        if (null != fragment){
            new FaiPresenter((FaiContract.View)fragment);
        }

        fragment = fm.findFragmentByTag(FRAGMENT_TAG_BACK);
        if (null == fragment && null != findViewById(R.id.vendor_deliver_back)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }
            fragment = BackFragment.newInstance();
            ft.add(R.id.vendor_deliver_back, fragment,FRAGMENT_TAG_BACK);
        }
        if (null != fragment) {
            mBackPresenter = new BackPresenter((BackContract.View) fragment) {
                @Override
                public void onBack() {
                    super.onBack();
                    VMCContoller.getInstance().cancelDeal();
                    MEventBus.getDefault().post(new ClearEditTextEventBus());
                    FaiActivity.this.finish();
                    IntentHelper.startHome(FaiActivity.this);
                }

                @Override
                public void onTimerEnd() {
                    super.onTimerEnd();
                    VMCContoller.getInstance().cancelDeal();
                    FaiActivity.this.finish();
                    IntentHelper.startHome(FaiActivity.this);
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
        if (ConfigUtils.getConfig(FaiActivity.this) != null &&
            ConfigUtils.getConfig(FaiActivity.this).vmc_count_down_time_settings != null) {
            if (ConfigUtils.getConfig(FaiActivity.this).vmc_count_down_time_settings.general_page_countdown !=
                0) {
                mBackPresenter.setTimeLeft(ConfigUtils.getConfig(FaiActivity.this).vmc_count_down_time_settings.general_page_countdown);
            } else {
                mBackPresenter.setTimeLeft(BackViewModel.DEFAULT_TIMELEFT);
            }
        }
    }
}

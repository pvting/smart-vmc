package com.want.vendor.deliver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.vmc.core.model.product.DeliverProduct;
import com.vmc.core.utils.ConfigUtils;
import com.want.base.sdk.framework.eventbus.MEventBus;
import com.want.vendor.deliver.fai.ClearEditTextEventBus;
import com.want.vmc.R;
import com.want.vendor.deliver.success.SuccessContract;
import com.want.vendor.deliver.success.SuccessFragment;
import com.want.vendor.deliver.success.SuccessPresenter;

import vmc.machine.core.VMCContoller;
import vmc.vendor.VActivity;
import vmc.vendor.common.back.BackContract;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.common.back.BackPresenter;
import vmc.vendor.common.back.BackViewModel;
import vmc.vendor.utils.IntentHelper;


/**
 * <b>Create Date:</b> 2016/11/15<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b> <br>
 */
public class SuccessActivity extends VActivity {

    private static final String FRAGMENT_SUCCCESS_SHOW = "SuccessActivity";
    private BackPresenter mBackPresenter;

    public static void start(Context context,DeliverProduct result) {
        Intent starter = new Intent(context, SuccessActivity.class);
        starter.putExtra(Extras.DATA, (Parcelable) result);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestContentView();
        setContentView(R.layout.vendor_deliver_fillout_activity);
        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = null;
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_SUCCCESS_SHOW);
        if (null == fragment) {
            ft = fm.beginTransaction();
            DeliverProduct result = getIntent().getParcelableExtra(Extras.DATA);

            fragment = SuccessFragment.newInstance(result);
            ft.add(R.id.vendor_deliver_show, fragment, FRAGMENT_SUCCCESS_SHOW);
        }

        if (null != fragment) {
            new SuccessPresenter((SuccessContract.View) fragment);
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
                    SuccessActivity.this.finish();
                }

                @Override
                public void onTimerEnd() {
                    super.onTimerEnd();
                    VMCContoller.getInstance().cancelDeal();
                    IntentHelper.startHome(SuccessActivity.this);
                    SuccessActivity.this.finish();
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
        if (ConfigUtils.getConfig(SuccessActivity.this) != null &&
            ConfigUtils.getConfig(SuccessActivity.this).vmc_count_down_time_settings != null) {
            if (ConfigUtils.getConfig(SuccessActivity.this).vmc_count_down_time_settings.general_page_countdown !=
                0) {
                mBackPresenter.setTimeLeft(ConfigUtils.getConfig(SuccessActivity.this).vmc_count_down_time_settings.general_page_countdown);
            } else {
                mBackPresenter.setTimeLeft(BackViewModel.DEFAULT_TIMELEFT);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

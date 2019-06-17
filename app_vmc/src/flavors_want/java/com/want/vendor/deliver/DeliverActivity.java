package com.want.vendor.deliver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;

import com.vmc.core.BLLController;
import com.vmc.core.utils.ConfigUtils;
import com.want.vendor.deliver.fillout.FillOutContract;
import com.want.vendor.deliver.fillout.FillOutFragment;
import com.want.vendor.deliver.fillout.FillOutPresenter;
import com.want.vendor.tips.GuideProblemCodeActivity;
import com.want.vmc.R;

import vmc.core.log;
import vmc.vendor.VActivity;
import vmc.vendor.common.back.BackContract;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.common.back.BackPresenter;
import vmc.vendor.common.back.BackViewModel;

/**
 * <b>Create Date:</b> 2016/11/14<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b> <br>
 */
public class DeliverActivity extends VActivity implements FillOutContract {

    private static final String FRAGMENT_DELIVER_SHOW = "DeliverActivity";
    private BackPresenter mBackPresenter;
    private final String TAG = "DeliverActivity";
    public static boolean isCanBack = true;


    //启动自身acticity
    public static void start(Context context) {
        Intent starter = new Intent(context, DeliverActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestContentView();
        setContentView(R.layout.vendor_deliver_fillout_activity);
        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = null;
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_DELIVER_SHOW);
        if (null == fragment) {
            ft = fm.beginTransaction();
            fragment = FillOutFragment.newInstance();
            ft.add(R.id.vendor_deliver_show, fragment, FRAGMENT_DELIVER_SHOW);
        }

        if (null != fragment) {
            new FillOutPresenter((FillOutContract.View) fragment);
        }

        fragment = fm.findFragmentByTag(FRAGMENT_TAG_BACK);
        if (null == fragment && null != findViewById(R.id.vendor_deliver_back)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }
            fragment = BackFragment.newInstance();
            ft.add(R.id.vendor_deliver_back, fragment, FRAGMENT_TAG_BACK);
        }
        if (null != fragment) {
            mBackPresenter = new BackPresenter((BackContract.View) fragment) {
                @Override
                public void onBack() {
                    super.onBack();
                    log.d(TAG, "isCanBack=" + isCanBack);
                    if (isCanBack) {
                        DeliverActivity.this.finish();
                    }
                }

                @Override
                public void onTimerEnd() {
                    super.onTimerEnd();
                    if (isCanBack) {
                        DeliverActivity.this.finish();
                        log.d(TAG, "DeliverActivity---被干掉啦");
                    }
                }
            };
        }

        if (null != ft) {
            ft.commit();
        }
    }

    public static Activity getActivity() {
        return getActivity();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
        super.onResume();

        restTime();

        if (BLLController.getInstance().isDoorOpen()){
            GuideProblemCodeActivity.start(this, 6);
            log.e(TAG,"onResume: 门已开 无法提货");
            return;
        }

        if (BLLController.getInstance().isDriveError()){
            GuideProblemCodeActivity.start(this, 7);
            log.e(TAG,"onResume: 驱动版无应答 无法提货");
        }
    }

    @Override
    public int getThrottleTime() {
        return 100;
    }


    public void restTime() {
        if (ConfigUtils.getConfig(DeliverActivity.this) != null &&
            ConfigUtils.getConfig(DeliverActivity.this).vmc_count_down_time_settings != null) {
            if (ConfigUtils.getConfig(DeliverActivity.this).vmc_count_down_time_settings.general_page_countdown !=
                0) {
                mBackPresenter.setTimeLeft(ConfigUtils.getConfig(DeliverActivity.this).vmc_count_down_time_settings.general_page_countdown);
            } else {
                mBackPresenter.setTimeLeft(BackViewModel.DEFAULT_TIMELEFT);
            }
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

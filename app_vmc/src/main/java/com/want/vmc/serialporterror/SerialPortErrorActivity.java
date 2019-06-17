package com.want.vmc.serialporterror;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import com.vmc.core.utils.ConfigUtils;
import com.want.vmc.R;
import vmc.vendor.VActivity;
import vmc.vendor.common.back.BackContract;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.common.back.BackPresenter;
import vmc.vendor.common.back.BackViewModel;

/**
 * <b>Create Date:</b> 2016/12/13<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b> <br>
 */
public class SerialPortErrorActivity extends VActivity {
    private final String FRAGMENT_TAG_SERIAL = "fragment_tag_serialporterror";
    private BackPresenter mBackPresenter;

    public static void start(Context context) {
        Intent intent = new Intent(context, SerialPortErrorActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_home_serialport_land_activity);
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_SERIAL);

        //图片展示
        if (null == fragment && null != findViewById(R.id.home_serial_port_error)) {
            fragment = SerialPortErrorFragment.newInstance();
            ft.add(R.id.home_serial_port_error, fragment, FRAGMENT_TAG_SERIAL);
        }
        // 返回
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_SERIAL);
        if (null == fragment && null != findViewById(R.id.vendor_back)) {
            fragment = BackFragment.newInstance();
            ft.add(R.id.vendor_back, fragment, FRAGMENT_TAG_SERIAL);
        }
        if (null != fragment) {
            mBackPresenter = new BackPresenter((BackContract.View) fragment) {

                @Override
                public void onBack() {
                    super.onBack();
                    SerialPortErrorActivity.this.finish();
                }

                @Override
                public void onTimerEnd() {
                    super.onTimerEnd();
                    SerialPortErrorActivity.this.finish();
                }
            };
        }
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConfigUtils.getConfig(SerialPortErrorActivity.this) != null &&
            ConfigUtils.getConfig(SerialPortErrorActivity.this).vmc_count_down_time_settings != null) {
            if (ConfigUtils.getConfig(SerialPortErrorActivity.this).vmc_count_down_time_settings.message_countdown_long != 0) {
                mBackPresenter.setTimeLeft(ConfigUtils.getConfig(SerialPortErrorActivity.this).vmc_count_down_time_settings.message_countdown_long);
            } else {
                mBackPresenter.setTimeLeft(BackViewModel.DEFAULT_TIMELEFT);
            }
        }
    }
}
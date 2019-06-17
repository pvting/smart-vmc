package com.want.vmc.core.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;

import com.want.base.sdk.framework.app.MToolbarActivity;
import com.want.base.sdk.model.analytic.IAnalytic;
import com.want.vmc.core.Constants;

/**
 * <b>Project:</b> project_template<br>
 * <b>Create Date:</b> 16/3/21<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 基本的{@link android.app.Activity}
 * <br>
 */
public class PActivity extends MToolbarActivity implements Constants {

    protected PowerManager mPowerManager = null;
    protected PowerManager.WakeLock mWakeLock = null;

    @Override
    protected IAnalytic onCreateAnalytic() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove umeng analytic
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, getClass().getCanonicalName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mWakeLock) {
            mWakeLock.acquire();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mWakeLock) {
            mWakeLock.release();
        }
    }
}

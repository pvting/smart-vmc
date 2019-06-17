package vmc.vendor.service.boot;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.vmc.core.utils.BLLOrderUtils;
import com.want.base.sdk.framework.app.MBroadcastReceiver;
import com.want.base.sdk.utils.SystemUtils;
import com.want.vmc.core.Constants;

/**
 * <b>Create Date:</b> 9/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 重启系统广播。
 * <br>
 */
public class RebootReceiver extends MBroadcastReceiver {

    private Handler mHandler = new Handler(Looper.getMainLooper());


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();
        if (Constants.Action.REBOOT.equals(action)) {
            reboot();

        }
    }

    public void reboot() {
        if (BLLOrderUtils.getCurrentOrder() == null) {//如果当前没有订单则立刻重启
            SystemUtils.reboot();
            return;
        }

        mHandler.postDelayed(new Runnable() {//如果当前有订单则延迟5S再判断一次
            @Override
            public void run() {
                reboot();
            }
        }, 5000);


    }

}

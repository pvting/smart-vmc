package vmc.vendor.service.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import vmc.core.log;
import vmc.vendor.utils.IntentHelper;


/**
 * <b>Create Date:</b> 9/8/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 开机启动广播
 * <br>
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();
        log.d(TAG, "onReceive: 收到启动广播, action: " + action);
        if (!TextUtils.isEmpty(action)) {
            if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
//                Intent starter = new Intent(context, MainActivity.class);
//                starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(starter);


                SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
                String username = sp.getString("name", "");
                String password = sp.getString("password", "");

                if (username.isEmpty()||password.isEmpty()) {
                    //杀死该应用进程
                    android.os.Process.killProcess(android.os.Process.myPid());
                    return;
                }




                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        IntentHelper.startMain(context);
                    }
                }, 30000);
            }
        }
    }
}

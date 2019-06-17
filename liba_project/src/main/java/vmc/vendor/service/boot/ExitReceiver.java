package vmc.vendor.service.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import vmc.vendor.VApplication;

/**
 * <b>Create Date:</b> 4/25/17<br>
 * <b>Author:</b> Wisn<br>
 * <b>Description:退出整个程序</b> <br>
 */

public class ExitReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //接受程序安装完成后发送的广播
        String action = intent.getAction();
        if (action != null && action.equals("InstallApp_Success_KillSelf")) {
            //需要安装的包名
            String packageName = intent.getStringExtra("package_name");
            //退出程序的延迟时间
            int exitApplicationDelayTime = intent.getIntExtra("exit_application_delay_time", 200);
            //TODO 退出程序
            if(packageName!=null&&packageName.equalsIgnoreCase(context.getPackageName())){
                VApplication.getInstance().exit();
            }
        }
    }
}

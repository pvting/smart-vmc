package vmc.vendor.model.daemon;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;

import com.want.base.sdk.utils.ProcessUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import vmc.core.log;
import vmc.vendor.utils.IntentHelper;

/**
 * <b>Create Date:</b> 9/12/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 守护进程。
 * <br>
 */
public class DaemonService extends Service {
    /** 重启主进程 */
    public static final String CMD_RESTART = "vmc.project.CMD_RESTART";
    private static final String TAG = "DaemonService";

    /**
     * 重启主进程
     *
     * @param context
     */
    public static void restartMainProcess(Context context) {
        Intent intent = new Intent(context, DaemonService.class);
        intent.setAction(CMD_RESTART);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action = intent.getAction();
        handleAction(action);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleAction(String action) {
        log.v(TAG, "handleAction: 收到命令: " + action);
        // 重启主进程
        if (CMD_RESTART.equals(action)) {
            restartMainProcess();
        }
    }

    private void restartMainProcess() {
        log.d(TAG, "restartMainProcess: 重启主进程");
        final String pName = getPackageName();
        log.v(TAG, "restartMainProcess: 主进程名称: " + pName);
        final int pId = ProcessUtils.getProcessId(this, pName);
        log.v(TAG, "restartMainProcess: 主进程PID: " + pId);
        if (-1 != pId) {
            final String time =
                    new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date(System.currentTimeMillis()));
            log.v(TAG, "restartMainProcess: 当前时间: " + time);
            Process.killProcess(pId);
            log.v(TAG, "restartMainProcess: 结束主进程");
            log.d(TAG, "restartMainProcess: 启动首页");
            IntentHelper.startMain(this);
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
        }
    }
}

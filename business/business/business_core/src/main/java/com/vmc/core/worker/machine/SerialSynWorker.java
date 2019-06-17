package com.vmc.core.worker.machine;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.vmc.core.OdooAction;
import com.vmc.core.worker.Worker;

import java.lang.ref.WeakReference;

import vmc.core.log;
import vmc.machine.core.VMCContoller;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/12/14<br>
 * <b>Author:</b> kevin_zhuang<br>
 * <b>Description:</b>串口连接检测 <br>
 */
public class SerialSynWorker extends Worker {

    private static final String TAG = SerialSynWorker.class.getSimpleName();

    private WeakReference<Context> mContextWeakReference;
    private static SerialSynWorker INSTANCE;

    private SerialSynWorker(Context context) {
        mContextWeakReference = new WeakReference<Context>(context);
    }


    public static SerialSynWorker getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (SerialSynWorker.currentThread()) {
                if (null == INSTANCE) {
                    INSTANCE = new SerialSynWorker(context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    protected void onWorking() {
        //查询串口异常任务
        safeWait(Time.MINUTE_1);

        if (VMCContoller.getInstance().isConnectError()) {
            log.e(TAG,"onWorking: 串口断开链接...");
            LocalBroadcastManager.getInstance(mContextWeakReference.get()).sendBroadcast(new Intent(OdooAction.BLL_SERIAL_ERROR_TO_UI));
            this.stopWork();
        }

    }

}

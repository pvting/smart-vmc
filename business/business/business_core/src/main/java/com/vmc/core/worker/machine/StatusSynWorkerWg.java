package com.vmc.core.worker.machine;

import android.content.Context;
import android.text.TextUtils;

import com.vmc.core.BLLController;
import com.vmc.core.Odoo;
import com.vmc.core.OdooHttpCallback;
import com.vmc.core.model.OdooMessage;
import com.vmc.core.model.machine.Machine;
import com.vmc.core.request.stock.StockSyncRequest;
import com.vmc.core.worker.Worker;
import com.vmc.core.utils.InitUtils;
import com.want.base.http.error.HttpError;
import com.want.location.ILocation;
import com.want.location.LocationManager;

import java.lang.ref.WeakReference;

import vmc.core.log;


/**
 * <b>Create Date:</b> 2017/3/13<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> 水神机器状态上报 <br>
 */
public class StatusSynWorkerWg extends Worker {
    private static final String TAG = "StatusSynWorker";

    private static StatusSynWorkerWg INSTANCE;
    private WeakReference<Context> mContextWeakReference;
    private Odoo mOdoo;
    private LocationManager mLocationManager;

    private StatusSynWorkerWg(Context context) {
        super();
        mContextWeakReference = new WeakReference<Context>(context);
    }

    public static StatusSynWorkerWg getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (StatusSynWorkerWg.class) {
                if (null == INSTANCE) {
                    INSTANCE = new StatusSynWorkerWg(context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        this.mOdoo = Odoo.getInstance(mContextWeakReference.get());
        mLocationManager = LocationManager.getInstance();
    }

    @Override
    protected void onWorking() {
        safeWait(2 * Time.MINUTE_1);
        Context context = mContextWeakReference.get();
        if (null == context) {
            return;
        }
        if (!InitUtils.isInit(context)) {
            log.i(TAG, "status sync: 机器尚未初始化, 略过库存上报");
            return;
        }
        final ILocation location = mLocationManager.getLastKnownLocation();
        final String status =  BLLController.getInstance().getVmcRunningStates();
        final Machine machine = new Machine();
        machine.status = status;
        // TODO: 2017/5/9 此时这个代码 如何理解
        log.i(TAG, "machine_id:" + InitUtils.getInitMachineId(context));

        String machineId = InitUtils.getInitMachineId(context);
        if (TextUtils.isEmpty(machineId)) {
            return;
        }
        int id = Integer.parseInt(machineId);

        log.i(TAG, "machine_id:" + id);
        log.i(TAG, "status:" + status);
        machine.id = id;
        if (null != location) {
            machine.location = location.getLongitude() + "," + location.getLatitude();
        }
        mLocationManager.requestLocationUpdate();

        StockSyncRequest request = new StockSyncRequest(machine,null);
        mOdoo.statusSync(request, new OdooHttpCallback<OdooMessage>(context) {
            @Override
            public void onSuccess(OdooMessage result) {
                // ignore
                log.v(TAG, "statusSync-->onSuccess, 状态上报成功");
            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);
                log.w(TAG, "statusSync-->onError, 状态上报失败");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                log.v(TAG, "statusSync-->onFinish, 状态上报结束");
            }
        });
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        log.w(TAG, "onFinish: 状态上报服务已结束");
    }
}

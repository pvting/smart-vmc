package com.vmc.core.worker.machine;

import android.content.Context;


import com.vmc.core.BLLController;
import com.vmc.core.Odoo;
import com.vmc.core.OdooHttpCallback;
import com.vmc.core.model.OdooMessage;
import com.vmc.core.model.machine.Machine;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.model.stock.Stock;
import com.vmc.core.request.stock.StockSyncRequest;
import com.vmc.core.utils.BLLProductUtils;
import com.vmc.core.worker.Worker;
import com.vmc.core.utils.InitUtils;
import com.want.base.http.error.HttpError;
import com.want.location.ILocation;
import com.want.location.LocationManager;

import java.lang.ref.WeakReference;
import java.util.List;

import vmc.core.log;


/**
 * <b>Create Date:</b> 07/11/2016<br>
 * <b>Author:</b> Wisn<br>
 * <b>Description:</b>
 * 机器状态上报
 * <br>
 */

public class StatusSynWorker extends Worker {
    private static final String TAG = "StatusSynWorker";

    private static StatusSynWorker INSTANCE;
    private WeakReference<Context> mContextWeakReference;
    private Odoo mOdoo;
    private LocationManager mLocationManager;

    private StatusSynWorker(Context context) {
        super();
        mContextWeakReference = new WeakReference<Context>(context);
    }

    public static StatusSynWorker getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (StatusSynWorker.class) {
                if (null == INSTANCE) {
                    INSTANCE = new StatusSynWorker(context);
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

        safeWait(Time.MINUTE_2);

        Context context = mContextWeakReference.get();
        if (null == context) {
            return;
        }

        if (!InitUtils.isInit(context)) {
            log.d(TAG, "onWorking: 机器尚未初始化, 略过库存上报");
            return;
        }

        final ILocation location = mLocationManager.getLastKnownLocation();

        final String status = BLLController.getInstance().getVmcRunningStates();

        final Machine machine = new Machine();

        final List<Stock> stocks = BLLProductUtils.getStocks();

        machine.status = status;

        if (null != location) {
            machine.location = location.getLongitude() + "," + location.getLatitude();
        }

        mLocationManager.requestLocationUpdate();

        StockSyncRequest request = new StockSyncRequest(machine,stocks);

        mOdoo.statusSync(request, new OdooHttpCallback<OdooMessage>(context) {
            @Override
            public void onSuccess(OdooMessage result) {
                log.v(TAG, "statusSync-->onSuccess, 状态上报成功");
            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);
                log.e(TAG, "statusSync-->onError, 状态上报失败");
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

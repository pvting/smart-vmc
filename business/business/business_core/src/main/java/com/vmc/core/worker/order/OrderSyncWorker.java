package com.vmc.core.worker.order;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;


import com.vmc.core.Odoo;
import com.vmc.core.OdooHttpCallback;
import com.vmc.core.model.OdooMessage;
import com.vmc.core.model.order.Order;
import com.vmc.core.request.order.OrderSyncRequest;
import com.vmc.core.utils.BLLOrderUtils;
import com.vmc.core.worker.Worker;
import com.vmc.core.utils.InitUtils;
import com.want.base.http.error.HttpError;

import java.lang.ref.WeakReference;
import java.util.List;

import vmc.core.log;


/**
 * <b>Create Date:</b> 04/11/2016<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 订单同步Worker
 * <br>
 */
public class OrderSyncWorker extends Worker {
    private static final String TAG = "OrderSyncWorker";

    private static OrderSyncWorker INSTANCE;
    private WeakReference<Context> mContextWeakReference;
    private Handler  mHandler;
    private Odoo mOdoo;

    private OrderSyncWorker(Context context) {
        super();
        mContextWeakReference = new WeakReference<Context>(context);
        mHandler=new Handler(Looper.getMainLooper());
    }

    public static OrderSyncWorker getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (OrderSyncWorker.class) {
                if (null == INSTANCE) {
                    INSTANCE = new OrderSyncWorker(context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 通知上报订单
     */
    public void notifySync() {
        log.v(TAG, "notifySync: 订单上报已被通知");
        safeNotify();
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        this.mOdoo = Odoo.getInstance(mContextWeakReference.get());
    }

    @Override
    protected void onWorking() {
        final Context context = mContextWeakReference.get();
        if (null == context) {
            return;
        }

        // 如果机器尚未初始化, 不执行
        if (!InitUtils.isInit(context)) {
            return;
        }

        log.v(TAG, "onWorking: 订单上报运行中...");
        List<Order> orders = BLLOrderUtils.getPendingOrders(context);
        if (0 == orders.size()) {
            log.v(TAG, "onWorking: 没有需要上报的订单, 进入等待状态");
            safeWait();
        } else {
            log.v(TAG, "onWorking: 有待上报的订单, 开始上报...");
            for (Order order : orders) {
                log.v(TAG, "onWorking: 上报订单: " + order.toJson());
                syncOrder(context, mOdoo, order);
                // wait sync thread
                log.v(TAG, "onWorking: 等待当前订单上报结束");
                safeWait();
            }
        }

    }
    private void syncOrder(final Context  context, final Odoo odoo, final Order order) {
        odoo.orderSync(new OrderSyncRequest(order),
                       new OdooHttpCallback<OdooMessage>(context) {
                           @Override
                           public void onSuccess(OdooMessage result) {
                               log.v(TAG, "syncOrder, onSuccess: 订单上报成功");
                               BLLOrderUtils.removeOrder(context, order);
                               log.v(TAG, "syncOrder, onSuccess: 继续上报下一个订单");
                               safeNotify();
                           }

                           @Override
                           public void onError(HttpError error) {
                               super.onError(error);
                               log.v(TAG, "syncOrder, onError: 上报失败, 稍后继续上报当前订单");
                               if (null != mHandler) {
                                   mHandler.postDelayed(new Runnable() {
                                       @Override
                                       public void run() {
                                           syncOrder(context, odoo, order);
                                       }
                                   },  Time.SECOND_30);
                               }
                           }
                       });
    }

    @Override
    protected void onFinish() {
        super.onFinish();
    }
}

package com.vmc.core.worker.config;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import com.vmc.core.Odoo;
import com.vmc.core.OdooAction;
import com.vmc.core.OdooHttpCallback;
import com.vmc.core.model.config.ConfigInit;
import com.vmc.core.model.order.Order;
import com.vmc.core.request.config.ConfigRequest;
import com.vmc.core.utils.BLLOrderUtils;
import com.vmc.core.utils.ConfigUtils;
import com.vmc.core.worker.Worker;
import com.want.base.http.error.HttpError;

import java.lang.ref.WeakReference;

import vmc.core.log;


/**
 * <b>Create Date:</b> 06/12/2016<br>
 * <b>Author:</b> huyunqiang<br>
 * <b>Description:</b>
 * 执行指令
 * <br>
 */
public class ConfigWorker extends Worker {
    private final String TAG = "ConfigWorker";
    private static ConfigWorker INSTANCE;
    private WeakReference<Context> mContextWeakReference;

    private ConfigWorker(Context context) {
        super();
        this.mContextWeakReference = new WeakReference<Context>(context);
    }

    public static ConfigWorker getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (ConfigWorker.class) {
                if (null == INSTANCE) {
                    INSTANCE = new ConfigWorker(context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        final Context context = mContextWeakReference.get();
        if (null == context) {
            log.v(TAG, "ConfigWorker: context is null ,application has been killed ");
            stopWork();
            return;
        }
    }

    @Override
    protected void onWorking() {
        // 10分钟从后台获取一次配置
        safeWait(Time.MINUTE_10);
        final Context context = mContextWeakReference.get();
        if (null == context) {
            stopWork();
            log.v(TAG, "ConfigWorker: context is null ,application has been killed ");
            return;
        }
        initConfig(context);
    }


    /**
     * 获取配置参数
     *
     * @param context
     */
    private void initConfig(final Context context) {
        Odoo.getInstance(context).initConfig(new ConfigRequest(), new OdooHttpCallback<ConfigInit>(context) {
            @Override
            public void onSuccess(final ConfigInit result) {
                log.d(TAG, "initConfig-->onSuccess: 获取配置参数成功,10分钟后再次获取");

                if (result == null) {
                    return;
                }
                if (result.is_upgrade) {//如果服务需要升级
                    isBroadcastUpgrade(context.getApplicationContext());
                } else {//如果不在升级中
                    if (ConfigUtils.getConfig(context.getApplicationContext()).is_upgrade) {//上一次是升级中
                        log.i(TAG, "isBroadcastUpgrade: 取消升级");
                        Intent intentUpgrade = new Intent(OdooAction.BLL_CANCEL_UPGRADE_TO_UI);//取消升级
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intentUpgrade);
                        if (mHandler != null) {
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler = null;
                        }
                    }
                }
                //保存配置参数
                ConfigUtils.setConfig(context.getApplicationContext(), result);
            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);
                log.d(TAG, "initConfig-->onError: 获取配置参数失败,10分钟后再次获取");
            }
        });
    }

    private Handler mHandler;

    void isBroadcastUpgrade(final Context context) {
        Order currentOrder = BLLOrderUtils.getCurrentOrder();
        if (currentOrder == null) {//判断当前内存是否存在订单
            log.i(TAG, "isBroadcastUpgrade: 需要提示升级");
            Intent intentUpgrade = new Intent(OdooAction.BLL_UPGRADE_TO_UI);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intentUpgrade);
            if (mHandler!=null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
            return;
        }

        if (null == mHandler) {
            mHandler = new Handler(Looper.getMainLooper());
        }

        mHandler.removeCallbacksAndMessages(null);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isBroadcastUpgrade(context);
            }
        }, 2000);
    }


}
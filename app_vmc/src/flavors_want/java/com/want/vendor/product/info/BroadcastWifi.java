package com.want.vendor.product.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

import com.vmc.core.BLLController;
import com.vmc.core.Odoo;
import com.vmc.core.OdooAction;
import com.vmc.core.OdooHttpCallback;
import com.vmc.core.model.config. ConfigInit;
import com.vmc.core.request.config.ConfigRequest;
import com.vmc.core.utils.ConfigUtils;
import com.want.base.http.error.HttpError;
import com.want.core.log.lg;

import vmc.core.log;

/**
 * <b>Create Date:</b> 2016/12/10<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b> <br>
 */
public class BroadcastWifi extends BroadcastReceiver {

    private static boolean tag;
    private static String TAG = "BroadcastWifi";

    public BroadcastWifi() {
        super();
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        boolean netState = BLLController.getInstance().isNetState(context);
        /**
         * 没有执行return,则说明当前无网络连接
         */
        if (!netState) {
            //无网络
            NetWorkObservable.getInstance().setData(false);
            log.d(TAG, "BroadcastWifi-->onReceive 网络状态: offline");
        } else {
            NetWorkObservable.getInstance().setData(true);
            if (!tag) {
                log.d(TAG, "BroadcastWifi-->onReceive 网络状态: online");
                initConfig(context, Odoo.getInstance(context));
                tag = true;
            }
        }
    }

    /**
     * 获取配置参数
     *
     * @param odoo
     */
    private void initConfig(final Context context, final Odoo odoo) {
        log.d(TAG, "initconfig: 获取配置参数...");
        odoo.initConfig(new ConfigRequest(), new OdooHttpCallback<ConfigInit>(context) {
            @Override
            public void onSuccess(ConfigInit result) {
                log.d(TAG, "onSuccess: 获取配置参数成功...");
                ConfigUtils.setConfig(context, result);
                //发送广播  Action 的名字
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(OdooAction.VMC_NOTICE_PHONE_UPDATE));
            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);
                log.e(TAG, "onError: 获取配置参数失败...");
            }
        });
    }


}


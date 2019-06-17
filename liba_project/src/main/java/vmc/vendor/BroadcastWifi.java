package vmc.vendor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

import com.vmc.core.Odoo;
import com.vmc.core.OdooAction;
import com.vmc.core.OdooHttpCallback;
import com.vmc.core.model.config.ConfigInit;
import com.vmc.core.request.config.ConfigRequest;
import com.vmc.core.utils.ConfigUtils;
import com.want.base.http.error.HttpError;

import vmc.core.log;


/**
 * <b>Create Date:</b> 2016/12/10<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b> <br>
 */
public class BroadcastWifi extends BroadcastReceiver {


    private static final String TAG = "BroadcastWifi";

    private static boolean tag;

    public BroadcastWifi() {
        super();
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        //获取网络Wifi的状态
        NetworkInfo.State
                wifiState =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        //获取移动数据网络状态
        NetworkInfo.State
                mobileState =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        //没有执行return,则说明当前无网络连接
        if (wifiState != NetworkInfo.State.CONNECTED && mobileState != NetworkInfo.State.CONNECTED) {
            //无网络
            NetWorkObservable.getInstance().setData(false);
            log.d(TAG, "NetSattus：offline");
        } else {
            NetWorkObservable.getInstance().setData(true);
            if (!tag) {
                log.d(TAG, "NetSattus：online");
                updateConfig(context);
                tag = true;
            }
        }
    }

    /**
     * 获取配置参数
     *
     * @param context
     */
    private void updateConfig(final Context context) {
        log.d(TAG, "initconfig: 获取配置参数...");
        Odoo.getInstance(context).initConfig(new ConfigRequest(), new OdooHttpCallback<ConfigInit>(context) {
            @Override
            public void onSuccess(ConfigInit result) {
                log.d(TAG, "onSuccess: 获取配置参数成功.");
                ConfigUtils.setConfig(context, result);
                LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(new Intent(OdooAction.VMC_NOTICE_PHONE_UPDATE));
            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);
                log.d(TAG, "onError: 获取配置参数失败...");
            }
        });
    }


}


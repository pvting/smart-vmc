package com.vmc.api;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.vmc.core.BLLController;
import com.vmc.core.ReplenishAction;
import com.vmc.core.model.product.SupplyProductList;
import com.vmc.core.utils.BLLProductUtils;
import com.vmc.core.utils.InitUtils;

import java.util.List;

import vmc.core.log;
import vmc.machine.core.VMCContoller;

/**
 * <b>Create Date:</b>2017/5/24 17:18<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class ReplenishReceiver extends BroadcastReceiver {

    private final String TAG = "ReplenishReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case ReplenishAction.STOCK_SYNC: //补货|换货
                String str = intent.getStringExtra("data");
                if (TextUtils.isEmpty(str)) {
                    log.e(TAG, "ReplenishReceiver-->onReceive: 补货失败,补货信息为空...");
                    return;
                }
                SupplyProductList list;
                try {
                    list = new Gson().fromJson(str, SupplyProductList.class);
                } catch (Exception e) {
                    log.e(TAG, "ReplenishReceiver-->onReceive: 补货失败,数据解析失败");
                    Intent intentResult = new Intent(ReplenishAction.STOCK_SYNC_ACTION);
                    intentResult.putExtra("msg", "error");
                    intentResult.putExtra("result", "数据错误,解析异常...");
                    intentResult.putExtra("code", "1");
                    context.sendBroadcast(intentResult);
                    return;
                }
                BLLController.getInstance().stockSyncToVMC(context, list);
                break;
            case ReplenishAction.TAKE_STOCK: //盘点
                String str2 = intent.getStringExtra("data");
                if (TextUtils.isEmpty(str2)) {
                    log.e(TAG, "ReplenishReceiver-->onReceive: 盘点失败,补货信息为空...");
                    return;
                }
                SupplyProductList list2;
                try {
                    list2 = new Gson().fromJson(str2, SupplyProductList.class);
                } catch (Exception e) {
                    log.e(TAG, "ReplenishReceiver-->onReceive: 盘点失败,数据解析失败");
                    Intent intentResult = new Intent(ReplenishAction.STOCK_SYNC_ACTION);
                    intentResult.putExtra("msg", "error");
                    intentResult.putExtra("result", "数据错误,解析异常...");
                    intentResult.putExtra("code", "3");
                    context.sendBroadcast(intentResult);
                    return;
                }
                BLLProductUtils.takeStock(context, list2);
                break;
            case ReplenishAction.SUPPLY_GET_STOCK: //补货app获取商品信息
                String productInfo = BLLProductUtils.getSVMProductInfo(context);
                log.i(TAG, "ReplenishReceiver-->onReceive: " + "补货app获取商品信息" + productInfo);
                Intent itn = new Intent(ReplenishAction.STOCK_SHARE);
                itn.putExtra("data", productInfo);
                context.sendBroadcast(itn);//发送库存信息
                break;
            case ReplenishAction.GET_FACTORY_CODE:
                String factory_code = null;
                if (VMCContoller.getInstance() != null) {
                    factory_code = VMCContoller.getInstance().getVendingMachineId();
                }
                if (TextUtils.isEmpty(factory_code)) {
                    factory_code = InitUtils.getFactoryCode(context);
                }
                log.i(TAG, "ReplenishReceiver-->onReceive: " + "补货app获取机器编号" + factory_code);
                Intent codeIntent = new Intent(ReplenishAction.FACTORY_CODE_SHARE);
                codeIntent.putExtra("factory_code", factory_code);
                context.sendBroadcast(codeIntent);//发送编码
                break;
            case ReplenishAction.SUPPLY_EXIT:
                //获取ActivityManager
                ActivityManager mAm = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
                //获得当前运行的task
                List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
                for (ActivityManager.RunningTaskInfo rti : taskList) {
                    //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
                    if (rti.topActivity.getPackageName().equals(context.getPackageName())) {
                        mAm.moveTaskToFront(rti.id, 0);
                        return;
                    }
                }
                //若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
                PackageManager mPackageManager = context.getPackageManager();
                Intent mainIntent = mPackageManager.getLaunchIntentForPackage(context.getPackageName());
                if (null == mainIntent) {
                    return;
                }
                context.getApplicationContext().startActivity(mainIntent);

                break;
        }
    }
}
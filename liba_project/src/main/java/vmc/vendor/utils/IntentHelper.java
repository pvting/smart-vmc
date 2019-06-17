package vmc.vendor.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.utils.InitUtils;

import java.util.List;
import java.util.Locale;

import vmc.vendor.Constants;
import vmc.vendor.web.WebActivity;

/**
 * <b>Create Date:</b> 2016/11/14<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class IntentHelper implements Constants {

    private IntentHelper() {
        //no instance
    }

    /**
     * 启动 启动页
     *
     * @param context
     */
    public static void startMain(Context context) {
        final Intent intent = new Intent(Action.MAIN);
        checkContext(context, intent);
        context.startActivity(intent);
    }

    /**
     * 启动首页
     *
     * @param context
     */
    public static void startHome(Context context) {
        final Intent intent = new Intent(Action.HOME);
        checkContext(context, intent);
        context.startActivity(intent);
    }



    /**
     * 启动首页
     *
     * @param context
     */
    public static void startUpgrade(Context context) {
        final Intent intent = new Intent(Action.SERVICE_UPGRADE);
        checkContext(context, intent);
        context.startActivity(intent);
    }







    /**
     * 启动提货码页面
     *
     * @param context
     */
    public static void startDeliver(Context context) {
        final Intent intent = new Intent(Action.DELIVER);
        checkContext(context, intent);
        context.startActivity(intent);
    }

    /**
     * 启动商品列表页面
     *
     * @param context
     */
    public static void startProductList(Context context) {
        final Intent intent = new Intent(Action.PRODUCTLIST);
        checkContext(context, intent);
        context.startActivity(intent);
    }

    /**
     * 启动商品详情页面
     *
     * @param context context
     */
    public static void startProductInfo(Context context, BLLStackProduct product) {
        final Intent intent = new Intent(Action.PRODUCT_INFO);
        intent.putExtra(Extras.DATA, product);
        checkContext(context, intent);
        context.startActivity(intent);
    }


    public static void startProductResult(Context context, String tip, String payType, int productId,boolean isRefund) {


        Intent starter = new Intent(Action.PRODUCT_RESULT);


        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(starter, 0);
        if (list.size() == 0) {
            // 说明系统中不存在这个activity
            return;
        }


        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putExtra("tip", tip);
        starter.putExtra("payType", payType);
        starter.putExtra("id", productId);
        starter.putExtra("isRefund",isRefund);
        context.startActivity(starter);
    }

    public static void startProductResult(Context context,String order, String tip, String payType, int productId,boolean isRefund) {


        Intent starter = new Intent(Action.PRODUCT_RESULT);


        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(starter, 0);
        if (list.size() == 0) {
            // 说明系统中不存在这个activity
            return;
        }


        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putExtra("order",order);
        starter.putExtra("tip", tip);
        starter.putExtra("payType", payType);
        starter.putExtra("id", productId);

        starter.putExtra("isRefund",isRefund);
        context.startActivity(starter);
    }



    /**
     * 启动内置浏览器
     *
     * @param context
     * @param url
     */
    public static void startWebView(Context context, String url) {
        WebActivity.start(context, getUrlWithMachineId(context, url), WebActivity.class);
    }

    private static String getUrlWithMachineId(Context context, String url) {
        final String machineId = InitUtils.getInitMachineId(context);
        return String.format(Locale.getDefault(), "%s?machine_id=%s", url, machineId);
    }

    private static void checkContext(Context context, Intent intent) {
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }


}

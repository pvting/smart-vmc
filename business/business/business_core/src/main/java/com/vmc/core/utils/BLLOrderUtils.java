package com.vmc.core.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.vmc.core.BLLController;
import com.vmc.core.OdooAction;
import com.vmc.core.model.order.Order;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import vmc.core.log;
import vmc.machine.core.VMCContoller;


/**
 * <b>Create Date:</b> 8/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class BLLOrderUtils {

    private static final String TAG = "BLLOrderUtils";

    private static final String ORDER_SP_NAME = "orders";

    private static final String ORDER_NAME_ORDER = "order";

    public static final int MODE_READABLE = Context.MODE_WORLD_READABLE;

    private static Random sOrderIdRandom = new Random();


    private  static  Order currentOrder ;

    private BLLOrderUtils() {
        //no instance
    }


    /**
     * 创建本地订单
     *
     * @param product_id
     *
     * @return
     */
    public static void createNativeOrder(int product_id) {


        currentOrder = new Order();

        currentOrder.id = createOrderId();

        currentOrder.create_time = getTimeStamp(new Date().getTime());

        BLLProduct bp = BLLProductUtils.getProductById(product_id);

        currentOrder.setAmount(bp.price);

        BLLStackProduct bllStackProduct = new BLLStackProduct();

        bllStackProduct.product_id = product_id;

        bllStackProduct.price = bp.price;

        bllStackProduct.name = bp.name;

        currentOrder.setProduct(bllStackProduct);

        log.i(TAG, "createNativeOrder: 创建订单:" + currentOrder.toJson());


    }


    /**
     * 更新订单价格
     *
     * @param price
     *
     * @return
     */
    public static void updateOrderPrice(int price) {
        if (currentOrder == null) {
            log.e(TAG, "updateOrderPrice: 订单不存在");
            return;
        }
        log.i(TAG, "updateOrderPrice: 更新订单价格为:" + price);
        currentOrder.setAmount(price);
    }


    /**
     * 更新订单出货状态
     *
     * @param shipping_status
     *
     * @return
     */
    public static void updateOrderShippingStatus(
                                                 boolean shipping_status,
                                                 int boxNo,
                                                 int stackNo, int outIndex, int error_code) {


        if (boxNo == -1 || stackNo == -1) {
            log.e(TAG, "updateOrderShippingStatus: 货柜或货道不存在");
            return;
        }

        BLLProduct bp =BLLController.getInstance().getSelectProduct();
        if (null==bp){
            bp = BLLProductUtils.getProductByRoadId(boxNo, stackNo);
        }


        if (currentOrder == null) {//按键选货

            currentOrder = new Order();

            if (null == bp) {
                log.e(TAG, "updateOrderShippingStatus: 商品不存在");
                currentOrder = null;
                return;
            }

            BLLStackProduct bsp = BLLProductUtils.sBLLProductsByRoadMap.get(boxNo + "*" + stackNo);
            if (bsp == null) {
                log.e(TAG, "updateOrderShippingStatus: " + boxNo + "*" + stackNo + "本地商品不存在");
                currentOrder = null;
                return;
            }

            currentOrder.id = createOrderId();

            currentOrder.setAmount(bp.price);

            currentOrder.setProduct(bsp);

            currentOrder.create_time = getTimeStamp(new Date().getTime());

            currentOrder.payment_method = Order.Payment.NONE.getPayment();

            currentOrder.status = Order.Status.PAID.getStatus();

            currentOrder.payment_status = Order.PayStatus.PAID.getStatus();

        }
        log.i(TAG, "updateOrderShippingStatus: 更新订单出货结果开始:" + currentOrder.toJson());
        if (outIndex == 0) {//如果是第一个出货 修正订单中的商品

            log.i(TAG, "updateOrderShippingStatus: 更改第一个出货状态: " + shipping_status);

            BLLStackProduct bsp = BLLProductUtils.sBLLProductsByRoadMap.get(boxNo + "*" + stackNo);
            currentOrder.shipping_status = shipping_status + "";
            currentOrder.error_code = error_code;
            if (bsp != null) {
                currentOrder.setProduct(bsp);
                if (bp != null) {
                    currentOrder.setAmount(bp.getPromotionPirce(currentOrder.payment_method));
                }
            }
        } else {//否者是赠品的出货状态
            log.i(TAG, "updateOrderShippingStatus: 更改第二个出货状态: " + shipping_status);
            currentOrder.promotion_error_code = error_code;
            currentOrder.promotion_shipping_status = shipping_status + "";
        }

        if (outIndex == 0) {
            currentOrder.payment_status = Order.PayStatus.PAID.getStatus();
            currentOrder.status = Order.Status.PAID.getStatus();
        }

        if (currentOrder.payment_method.equals(Order.Payment.RMB.getPayment()) ||
            currentOrder.payment_method.equals(Order.Payment.NONE.getPayment())) {
            if (outIndex == 0) {
                if (!shipping_status) {
                    log.d(TAG, "updateOrderShippingStatus: 现金支付第一个商品出货失败,更改为未支付");
                    currentOrder.payment_status = Order.PayStatus.UNPAY.getStatus();
                    currentOrder.status = Order.Status.CANCEL.getStatus();
                } else {
                    currentOrder.payment_status = Order.PayStatus.PAID.getStatus();
                    currentOrder.status = Order.Status.PAID.getStatus();
                }
            }
        }
        log.i(TAG, "updateOrderShippingStatus: 更新订单出货结果结束:" + currentOrder.toJson());
    }

    /**
     * 触发订单同步
     *
     * @param context
     */
    public static void requestOrderSync(Context context) {
        log.i(TAG, "requestOrderSync: 触发订单同步");
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(OdooAction.BLL_ORDERSYNC_TO_BLL));
    }


    /**
     * 更新促销商品货道
     *
     * @param promotion_stack_no
     * @param promotion_box_no
     */
    public static void updateOrderPromotion(int promotion_id,
                                            String promotion_box_no,
                                            String promotion_stack_no) {

        if (currentOrder == null) {
            log.e(TAG, "updateOrderPromotion: 订单不存在");
            return;
        }

        log.i(TAG, "updateOrderPromotion: 更新订单促销信开始:" + currentOrder.toJson());

        currentOrder.promotion_id = promotion_id;

        currentOrder.promotion_stack_no = promotion_stack_no;

        currentOrder.promotion_box_no = promotion_box_no;
        log.i(TAG, "updateOrderPromotion: 更新订单促销信息结束:" + currentOrder.toJson());
    }

    /**
     * 更新订单支付方式
     *
     * @param payment_method
     */
    public static void updateOrderPaymentMethod(Order.Payment payment_method) {

        if (currentOrder == null) {
            log.e(TAG, "updateOrderPaymentMethod: 订单不存在");
            return;
        }
        currentOrder.payment_method = payment_method.getPayment();
        log.i(TAG, "updateOrderPaymentMethod: 更新订单支付方式:" + payment_method);
    }

    /**
     * 更新订单支付状态
     *
     * @param payment_status
     */
    public static void updateOrderPayStatus(Order.PayStatus payment_status) {

        if (currentOrder == null) {
            log.e(TAG, "updateOrderPayStatus: 订单不存在");
            return;
        }
        currentOrder.payment_status = payment_status.getStatus();
        log.i(TAG, "更新订单支付状态:" + payment_status);
    }


    /**
     * 更新订单状态
     *
     * @param status
     */
    public static void updateOrderStatus(Order.Status status) {

        if (currentOrder == null) {
            log.e(TAG, "updateOrderPayStatus: 订单不存在");
            return;
        }
        currentOrder.status = status.getStatus();
    }


    /**
     * 获取当前订单
     *
     * @return
     */
    public static synchronized Order getCurrentOrder() {

        return currentOrder;
    }


    public static synchronized void saveOrder(Context context) {
        log.i(TAG, "updateOrderPayStatus: 保存订单开始");
        if (currentOrder == null) {
            return;
        }

        Intent intent = new Intent(OdooAction.BLL_ORDER_SHARE);
        intent.putExtra("data", currentOrder.toJson().toString());
        context.sendBroadcast(intent);

        // 暂时保存到xml文件
        final SharedPreferences sp = context.getSharedPreferences(ORDER_SP_NAME, MODE_READABLE);
        Set<String> set = sp.getStringSet(ORDER_NAME_ORDER, new HashSet<String>());
        set.add(currentOrder.toJson().toString());
        final SharedPreferences.Editor editor = sp.edit();
        editor.remove(ORDER_NAME_ORDER);
        editor.putStringSet(ORDER_NAME_ORDER, set);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        currentOrder = null;
        log.i(TAG, "updateOrderPayStatus: 保存订单结束");
    }

    /**
     * 保存与同步订单
     *
     * @param context
     */
    public static synchronized void saveAndSyncOrder(Context context) {
        saveOrder(context);

        requestOrderSync(context);
    }

    /**
     * 移除订单
     *
     * @param context
     * @param order
     */
    public static synchronized void removeOrder(Context context, Order order) {

        if (order == null) {
            log.i(TAG, "removeOrder: 订单为空");
            return;
        }


        log.i(TAG, "removeOrder: 移除已上报的订单" + order.toJson());
        final SharedPreferences sp = context.getSharedPreferences(ORDER_SP_NAME, MODE_READABLE);
        Set<String> set = sp.getStringSet(ORDER_NAME_ORDER, new HashSet<String>());
        if (set.size() == 0) {
            return;
        }

        final String orderId = order.id;
        String removed = null;
        for (String s : set) {
            if (s.contains(orderId)) {
                removed = s;
                break;
            }
        }

        if (!TextUtils.isEmpty(removed)) {
            set.remove(removed);
        }

        final SharedPreferences.Editor editor = sp.edit();
        // 如果set已经为空, 则移除
        if (0 == set.size()) {
            editor.remove(ORDER_NAME_ORDER);
        } else {
            editor.putStringSet(ORDER_NAME_ORDER, set);
        }
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    /**
     * 获取订单列表
     *
     * @param context
     *
     * @return
     */
    public static synchronized List<Order> getPendingOrders(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(ORDER_SP_NAME, MODE_READABLE);
        Set<String> set = sp.getStringSet(ORDER_NAME_ORDER, new HashSet<String>());
        List<Order> orders = new ArrayList<>();
        Order order;
        for (String s : set) {
            order = new Gson().fromJson(s, Order.class);
            orders.add(order);
        }
        return orders;
    }

    /**
     * 生成订单ID
     * <pre>
     *      订单ID规则:
     *      机器ID-产品ID-当前日期时间戳-5位随机数, 如:
     *      <b>93004343-productId-160822153324-12401</b>
     * </pre>
     * @return
     */
    private static String createOrderId() {
        return String.format(Locale.getDefault(),
                             "%s_%s_%s",
                             VMCContoller.getInstance().getVendingMachineId(),
                             getTimeStamp(),
                             sOrderIdRandom.nextInt(99999));
    }


    private static String getTimeStamp() {
        return new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault()).format(new Date());
    }

    public static String getTimeStamp(long timesInMillions) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        return format.format(new Date(timesInMillions));
    }


}

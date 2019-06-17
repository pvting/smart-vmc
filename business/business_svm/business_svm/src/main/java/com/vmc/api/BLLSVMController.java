package com.vmc.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.vmc.core.IBLLController;
import com.vmc.core.Odoo;
import com.vmc.core.OdooAction;
import com.vmc.core.OdooHttpCallback;
import com.vmc.core.ReplenishAction;
import com.vmc.core.model.config.PayMentWay;
import com.vmc.core.model.order.Order;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.model.product.FreeBie;
import com.vmc.core.model.product.OdooProductList;
import com.vmc.core.model.product.PickUpProduct;
import com.vmc.core.model.product.RestVerify;
import com.vmc.core.model.product.SupplyProductList;
import com.vmc.core.utils.BLLOrderUtils;
import com.vmc.core.utils.BLLPayMentController;
import com.vmc.core.utils.BLLProductUtils;
import com.vmc.core.utils.ConfigUtils;
import com.want.base.http.error.HttpError;

import java.util.ArrayList;
import java.util.List;

import vmc.core.log;
import vmc.machine.core.VMCContoller;
import vmc.machine.core.model.VMCStackProduct;


/**
 * <b>Create Date:</b>2017/3/29 09:11<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b>售货机公共实现放在这里处理<br>
 */
public abstract class BLLSVMController implements IBLLController {

    public final String TAG = "BLLSVMController";

    /**
     * 出货接收的广播
     */
    protected OutGoodsStatusReceiver mOutGoodsStatusReceiver;

    /**
     * 按键选货广播
     */
    protected SelectProductReceiver mSelectProductReceiver;


    /**
     * 现金投入广播
     */
    protected CashPayReceiver mCashPayReceiver;


    /**
     * 写入货道广播
     */
    protected SetProductReceiver mSetProductReceiver;


    /**
     * 机器初始化广播
     */
    protected MachineInitReceiver mMachineInitReceiver;

    /**
     * 机器开门广播
     */
    protected MachineDoorReceiver mMachineDoorReceiver;


    /**
     * 监听系统网络广播
     */
    protected NetworkChangeReceive mNetworkChangeReceive;


    /**
     * 料道状态集合
     */
    public ArrayList<Integer> statusStates = new ArrayList<>();


    /**
     * 初始化状态  0 还未初始化   1  初始化完成   2 初始化失败
     */
    private int initState;


    /**
     * 当前选中的商品
     */
    private BLLProduct mSelectProduct;


    /**
     * 当前选中的料道
     */
    protected BLLStackProduct mBLLStackProduct;


    /**
     * 禁止处理现金广播
     */
    private boolean mBanCashBroadcast;


    /**
     * 出货总数
     */
    protected int totalNum;

    /**
     * 当前出货下标
     */
    protected int outIndex;


    /**
     * 投入的金额
     */
    private int localCash;

    /**
     * 补货的商品列表
     */
    protected SupplyProductList spList;


    /**
     * 是否已选中商品
     */
    private boolean isSelected;


    /**
     * 获取监控状态
     *
     * @return json字符串
     */
    @Override
    public String getVmcRunningStates() {
        log.v(TAG, "getVmcRunningStates: 取vmc运行状态");
        return VMCContoller.getInstance().getVmcRunningStates();
    }


    /**
     * 设置商品
     *
     * @param products 商品列表
     */
    @Override
    public void setProducts(List<VMCStackProduct> products) {
        log.v(TAG, "setProducts: 开始写商品道货道");
        VMCContoller.getInstance().setProducts(products);

    }


    /**
     * 普通出货 （提货码以及赠品）
     *
     * @param bsp 料道商品
     */
    @Override
    public void outGoods(BLLStackProduct bsp) {
        log.i(TAG, "outGoods: 发出出货指令,货柜:" + bsp.box_no + ",货道:" + bsp.stack_no);
        //出货过程中 不通知上层现金变化
        mBanCashBroadcast = true;
        VMCContoller.getInstance().outGoods(bsp.getBoxNoInt(), bsp.getStackNoInt());
    }


    /**
     * 现金出货
     *
     * @param boxId  货柜号
     * @param roadId 货道号
     * @param price  价格 （分）
     */
    @Override
    public void outGoodsByCash(int boxId, int roadId, int price) {
        log.i(TAG, "outGoodsByCash: 发出出货指令,货柜:" + boxId + ",货道:" + roadId + ",价格" + price);

        //出货过程中 不通知上层现金变化
        mBanCashBroadcast = true;
        BLLStackProduct bsp = BLLProductUtils.getStackProduct(boxId, roadId);
        if (bsp != null)
            if (BLLOrderUtils.getCurrentOrder() != null) {
                Order order = BLLOrderUtils.getCurrentOrder();
                order.setProduct(bsp);
            }
        VMCContoller.getInstance().outGoodsByCash(boxId, roadId, price);

    }


    /**
     * 线上出货
     */
    @Override
    public void outGoodsOnLine() {
        if (mBLLStackProduct != null) {
            log.i(TAG,
                  "outGoodsOnLine: 发出出货指令,货柜:" +
                  mBLLStackProduct.getBoxNoInt() +
                  ",货道:" +
                  mBLLStackProduct.getStackNoInt());

            prepareOut(mBLLStackProduct.product_id);
            //出货过程中 不通知上层现金变化
            mBanCashBroadcast = true;
            if (BLLOrderUtils.getCurrentOrder() != null) {
                Order order = BLLOrderUtils.getCurrentOrder();
                order.setProduct(mBLLStackProduct);
            }
            VMCContoller.getInstance()
                        .outGoods(mBLLStackProduct.getBoxNoInt(), mBLLStackProduct.getStackNoInt());
        } else {
            log.e(TAG, "outGoodsOnLine: 没有选中任何商品");
        }

    }


    /**
     * 初始化注册广播等 差异化处理
     *
     * @param context 上下文
     */
    @Override
    public abstract void init(Context context);

    /**
     * 重置状态
     */
    @Override
    public void restOutGoods() {
        log.d(TAG, "restOutGoods: 重置出货状态");
        mSelectProduct = null;
        totalNum = 0;
        outIndex = 0;
    }


    /**
     * 选中商品
     * 创建订单
     *
     * @param context         上下文
     * @param bllStackProduct 料道商品
     */
    @Override
    public void selectProduct(Context context, BLLStackProduct bllStackProduct) {

        log.i(TAG,
              "selectProduct: 选中商品: " +
              bllStackProduct.name +
              ",货柜:" +
              bllStackProduct.box_no +
              ",货道:" +
              bllStackProduct.stack_no);

        //初始化当前出货个数

        totalNum = 0;

        outIndex = 0;

        //选中的料道
        mBLLStackProduct = bllStackProduct;

        Order order = BLLOrderUtils.getCurrentOrder();

        if (order != null) {//如果发现当前还存在订单，则同步订单道后台

            BLLOrderUtils.saveAndSyncOrder(context);
        }

        BLLProduct bp = BLLProductUtils.getProductById(bllStackProduct.product_id);

        if (null != bp) {
            try {
                mSelectProduct = (BLLProduct) bp.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                mSelectProduct = bp;
            }
        }

        isSelected = true;

        //创建本地订单
        BLLOrderUtils.createNativeOrder(bllStackProduct.product_id);

        //创建支付请求对象
        BLLPayMentController.getInstance().markOrderRequest(bllStackProduct.product_id);


    }

    /**
     * 提货码出货
     * 直接创建订单，出货
     *
     * @param bllStackProduct 料道商品
     * @param context         上下文
     */
    @Override
    public void selectVerifyProduct(BLLStackProduct bllStackProduct, Context context) {
        //提货码不需要出赠品
        outIndex = 0;

        totalNum = 1;


        if (BLLOrderUtils.getCurrentOrder() != null) {//如果发现当前还存在订单，则同步订单道后台

            BLLOrderUtils.saveAndSyncOrder(context);
        }

        BLLProduct bp = BLLProductUtils.getProductById(bllStackProduct.product_id);

        if (null != bp) {
            try {
                mSelectProduct = (BLLProduct) bp.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                mSelectProduct = bp;
            }
        }

        isSelected = true;

        //创建本地订单
        BLLOrderUtils.createNativeOrder(bllStackProduct.product_id);

        //支付状态为已支付
        BLLOrderUtils.getCurrentOrder().payment_status = Order.PayStatus.PAID.getStatus();

        //订单状态为已支付
        BLLOrderUtils.getCurrentOrder().status = Order.Status.PAID.getStatus();

        //支付方式为提货码
        BLLOrderUtils.getCurrentOrder().payment_method = Order.Payment.CODE.getPayment();

        //促销赠策为空(默认为0)
        BLLOrderUtils.getCurrentOrder().promotion_id = 0;

        log.i(TAG, "selectVerifyProduct: 提货码准备出货");

        //下发出货指令
        outGoods(bllStackProduct);
    }


    /**
     * 发送出货状态
     *
     * @param context         上下文
     * @param boxId           货柜号
     * @param roadId          料道号
     * @param shipping_status 出货状态
     */

    @Override
    public void sendOutGoodsStatus(Context context,
                                   int boxId,
                                   int roadId,
                                   boolean shipping_status) {

        Order order = BLLOrderUtils.getCurrentOrder();

        if (null == order) {
            log.e(TAG, "sendOutGoodsStatus:订单为空");
            return;
        }
        //通知出货状态
        Intent it = new Intent(BLL_OUTGOODS_TO_UI);
        it.putExtra("box_no", boxId);
        it.putExtra("stack_no", roadId);
        it.putExtra("totalNum", totalNum);
        it.putExtra("outIndex", outIndex);
        it.putExtra("outGoodsState", shipping_status);//出货状态
        it.putExtra("payMentType", order.payment_method);//支付方式
        it.putExtra("currentOrderId", order.id);

        boolean isRefund = isRefund(outIndex == 2, context);
        log.i(TAG, "sendOutGoodsStatus 是否退款: " + isRefund);
        it.putExtra("isRefund", isRefund);


        LocalBroadcastManager.getInstance(context).sendBroadcast(it);
    }

    /**
     * 是否退款
     *
     * @param context 上下文
     *
     * @return
     */
    private boolean isRefund(boolean isPromotion, Context context) {

        if (isPromotion) {//如果是赠品，不支持退款
            return false;
        }


        Order order = BLLOrderUtils.getCurrentOrder();

        if (order == null) {

            log.e(TAG, "isRefund: 订单为空");

            return false;
        }

        int errorCode = order.error_code;

        if (errorCode == 0) {
            return false;
        }

        String refund_amount_ceiling = ConfigUtils.getConfig(context).refund_amount_ceiling;


        if (TextUtils.isEmpty(refund_amount_ceiling)) {

            return false;
        }

        int ceiling = 0;

        try {
            ceiling = Integer.parseInt(refund_amount_ceiling);
        } catch (Exception e) {
            log.e(TAG, "isRefund: 数据转换异常");
        }

        if (ceiling == 0) {
            return false;
        }

        int paymentAcount = order.getAmount();

        if (paymentAcount > ceiling) {

            return false;
        }

        String paymentType = order.payment_method;


        return isRefundByPayment(paymentType, context) && isRefundByCode(errorCode, context);


    }


    /**
     * 根据支付方式 判断是否支持退款
     *
     * @param paymentType 支付方式
     * @param context
     *
     * @return
     */
    private boolean isRefundByPayment(String paymentType, Context context) {
        boolean isSupport = false;
        switch (paymentType) {
            case "WECHATPAY":
                if (ConfigUtils.getConfig(context).weixinpay_refund) {
                    isSupport = true;
                }
                break;
            case "ALIPAY":
                if (ConfigUtils.getConfig(context).alipay_refund) {
                    isSupport = true;
                }
                break;
        }
        return isSupport;
    }


    /**
     * 根据出货失败原因,是否支持退款
     *
     * @param context   上下文
     * @param errorCode 出货失败 错误码
     *
     * @return
     */
    private boolean isRefundByCode(int errorCode, Context context) {

        boolean isSupport = false;
        switch (errorCode) {
            case -2:
                if (ConfigUtils.getConfig(context).shipment_fail_two) {
                    isSupport = true;
                }
                break;
            case 1:
                if (ConfigUtils.getConfig(context).shipment_fail_three) {
                    isSupport = true;
                }
                break;
            case 2:
                if (ConfigUtils.getConfig(context).shipment_fail_one) {
                    isSupport = true;
                }
                break;
            default:
                isSupport = false;
                break;
        }
        return isSupport;
    }

    /**
     * 发送出货超时
     *
     * @param context        上下文
     * @param product_id     商品ID
     * @param currentOrderId 当前订单号
     * @param payType        支付方式
     * @param isPromotion    当前是否是赠品
     */
    public void sendOutGoodsTimeOut(Context context,
                                    int product_id, String currentOrderId,
                                    String payType, boolean isPromotion) {
        //通知出货状态
        Intent it = new Intent(BLL_OUTGOODS_TIMEOUT_TO_UI);
        it.putExtra("product_id", product_id);
        it.putExtra("isPromotion", isPromotion);
        it.putExtra("payMentType", payType);//支付方式
        it.putExtra("currentOrderId", currentOrderId);
        boolean refund = isRefund(isPromotion, context);
        it.putExtra("isRefund", refund);
        log.i(TAG, "sendOutGoodsStatus 是否退款: " + refund);
        LocalBroadcastManager.getInstance(context).sendBroadcast(it);
    }


    /**
     * 准备出货计算是否有赠品
     *
     * @param product_id 商品ID
     */
    @Override
    public void prepareOut(int product_id) {
        log.i(TAG, "prepareOut: 开始计算出货数量");

        BLLProduct productFrist = mSelectProduct;

        totalNum = 1;

        if (productFrist == null) {
            log.i(TAG, "prepareOut: 结束计算");
            return;
        }


        if (productFrist.mPromotionDetail == null) {
            log.i(TAG, "prepareOut: 结束计算");
            return;
        }


        if (productFrist.mPromotionDetail.freebie == null ||
            productFrist.mPromotionDetail.freebie.size() == 0) {
            log.i(TAG, "prepareOut: 结束计算");
            return;
        }
        if (!"one_more".endsWith(productFrist.mPromotionDetail.promotion_type)) {
            log.i(TAG, "prepareOut: 结束计算");
            return;
        }

        ArrayList<BLLProduct> freebie = new ArrayList<>();
        for (FreeBie freeBie : productFrist.mPromotionDetail.freebie) {
            BLLProduct bp = BLLProductUtils.getProductById(freeBie.id);
            if (bp.product_id == productFrist.product_id) {//如果是买A赠A
                if (outIndex < 1) {//屏幕选货
                    if (getSaleableStackProductByProductCount(bp) > 1) {
                        freebie.add(bp);
                    }
                } else {//按键选货 表示已经出了一个货物
                    if (getSaleableStackProductByProductCount(bp) > 0) {
                        freebie.add(bp);
                    }
                }
            } else {//如果是买A赠B
                if (getSaleableStackProductByProductCount(bp) > 0) {
                    freebie.add(bp);
                }
            }
        }

        if (freebie.size() == 0) {
            log.i(TAG, "prepareOut: 结束计算");
            return;
        }

        Order order = BLLOrderUtils.getCurrentOrder();
        if (null == order) {

            log.i(TAG, "prepareOut: 结束计算");
            return;
        }
        if (null == productFrist.mPromotionDetail.payment_option) {
            log.i(TAG, "prepareOut: 结束计算");
            return;
        }
        if (productFrist.mPromotionDetail.payment_option.contains(order.payment_method)) {
            totalNum += 1;
            log.i(TAG, "prepareOut: 需要出货数量 " + totalNum + " 个");
        }
        log.i(TAG, "prepareOut: 结束计算");
    }


    /**
     * 重置选中的料道
     */
    @Override
    public void cancelSelectProduct() {
        log.i(TAG, "cancelSelectProduct: 重置选中商品");

        mBLLStackProduct = null;

        mBanCashBroadcast = false;

        //重置支付请求
        BLLPayMentController.getInstance().resetPayRequest();

        isSelected = false;

    }

    /**
     * 请求人民币
     *
     * @param context 上下文
     */
    @Override
    public void requestRMB(Context context) {

        if (mBanCashBroadcast) {
            log.w(TAG, "requestRMB: 正在出货中...暂不处理金额变化");
            return;
        }


        if (mBLLStackProduct == null) {
            log.w(TAG, "requestRMB: 没有选中任何货道 ");
            return;
        }

        if (mSelectProduct == null) {
            log.e(TAG, "requestRMB: 没有选中任何商品");
            return;
        }


        lightButton();

        int productPrice = mSelectProduct.getPromotionPirce(Order.Payment.RMB.getPayment());

        if (BLLOrderUtils.getCurrentOrder() == null) {
            log.e(TAG, "requestRMB: 没有创建订单");
            return;
        }

        BLLOrderUtils.updateOrderPrice(productPrice);


        if (localCash >= productPrice) {


            if (!isCanOutgoods(localCash, productPrice)) {
                return;
            }


            log.i(TAG, "requestRMB: 当前金额 " + localCash + ">=商品金额" + productPrice);

            BLLOrderUtils.updateOrderPaymentMethod(Order.Payment.RMB);

            prepareOut(mBLLStackProduct.product_id);

            BLLOrderUtils.updateOrderPayStatus(Order.PayStatus.PAID);

            BLLOrderUtils.updateOrderStatus(Order.Status.PAID);


            //发送至UI支付成功
            Intent it = new Intent(BLL_PAY_STATUS_TO_UI);
            it.putExtra("PayStatus", true);
            it.putExtra("outIndex", outIndex + 1);
            it.putExtra("totalNum", totalNum);
            LocalBroadcastManager.getInstance(context).sendBroadcast(it);

            //现金出货
            outGoodsByCash(mBLLStackProduct.getBoxNoInt(), mBLLStackProduct.getStackNoInt(), productPrice);
        }


    }

    /**
     * 是否可以出货
     *
     * @param localCash    现金 （分）
     * @param productPrice 商品价格 （分）
     *
     * @return
     */
    public abstract boolean isCanOutgoods(int localCash, int productPrice);


    /**
     * 有灯的让灯亮起来（久保田，响应的灯亮了，才能现金出货）
     */
    public abstract void lightButton();


    /**
     * 生成机（水神）
     *
     * @return
     */
    @Override
    public boolean isWatergodGenerator() {
        // no use
        return false;
    }


    /**
     * 电解液 （水神）
     *
     * @return
     */
    @Override
    public boolean isWatergodElectrolyte() {
        // no use
        return false;
    }


    /**
     * 获取商品可销售的料道，差异化处理
     *
     * @param product 商品
     *
     * @return 料道商品
     */
    @Override
    public abstract BLLStackProduct getSaleableStackProductByProduct(BLLProduct product);


    /**
     * 获取商品可销售总数，差异化处理
     *
     * @param product 商品
     *
     * @return 数量
     */
    @Override
    public abstract int getSaleableStackProductByProductCount(BLLProduct product);


    /**
     * 获取初始化状态
     *
     * @return 0 未初始化  1 初始化成功  2 初始化失败
     */
    @Override
    public int getInitState() {

        return initState;
    }

    /**
     * 出货广播
     */
    public class OutGoodsStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //获取料道号
            int roadId = intent.getIntExtra("stack_no", -1);

            //获取货柜号
            int boxId = intent.getIntExtra("box_no", -1);

            //获取出货状态
            boolean shipping_status = intent.getBooleanExtra("outGoodsState", false);

            //获取出货原因  0 出货成功，没有原因   1 出货失败，弹簧不转  2 出货失败，光感未检测到
            int errorCode = intent.getIntExtra("error_code", 0);

            log.i(TAG,
                  "OutGoodsStatusReceiver-->onReceive: 货柜:" +
                  boxId +
                  ",货道:" +
                  roadId +
                  ",出货状态:" +
                  shipping_status +
                  ",errorCode" +
                  errorCode);

            //本地冲减库存
            writeDownProductStock(errorCode, boxId, roadId, context);

            if (totalNum == 0) {//如果出货数为0,那么则认定是机器主动出货。
                totalNum = 1;
                //如果当前有订单，表示之前上个商品 并且不和当前商品相同  先保存再上传
                Order order = BLLOrderUtils.getCurrentOrder();

                if (order != null && mBLLStackProduct != null &&
                    order.getProduct().product_id != mBLLStackProduct.product_id) {
                    BLLOrderUtils.saveAndSyncOrder(context);
                }
            }


            //更改出货状态 没有订单时创建支付方式为"其他"订单
            BLLOrderUtils.updateOrderShippingStatus(shipping_status, boxId, roadId, outIndex, errorCode);

            //后台冲减库存
            writeDownProductStockToOdoo(errorCode, outIndex, BLLOrderUtils.getCurrentOrder());

            //当前为第一个出货
            outIndex += 1;

            //通知出货状态
            sendOutGoodsStatus(context, boxId, roadId, shipping_status);

            //出货状态为失败 不需要处理下面事务 出货终止 订单结束
            if (!shipping_status) {
                BLLOrderUtils.saveAndSyncOrder(context);
                restOutGoods();
                //取消选中商品
                cancelSelectProduct();
                return;
            }


            //订单需要promotionID
            if (null == mSelectProduct) {
                BLLOrderUtils.saveAndSyncOrder(context);
                //取消选中商品
                cancelSelectProduct();
                restOutGoods();
                return;
            }

            if (totalNum > outIndex) {//如果总数大于当前出货个数  还有赠品要出
                //获取要出货的赠品
                final BLLStackProduct
                        promotionStackProduct =
                        BLLProductUtils.getPromotionStackProduct(mSelectProduct.product_id);

                if (promotionStackProduct == null) {//表示无赠品 订单结束
                    log.i(TAG, "OutGoodsStatusReceiver-->onReceive: 未发现赠品,出货结束");
                    BLLOrderUtils.saveAndSyncOrder(context);
                    //取消选中商品
                    restOutGoods();
                    cancelSelectProduct();
                    return;
                }

                //更新促销信息
                BLLOrderUtils.updateOrderPromotion(mSelectProduct.mPromotionDetail.promotion_id,
                                                   promotionStackProduct.box_no + "",
                                                   promotionStackProduct.origin_stack_no);

                log.i(TAG, "OutGoodsStatusReceiver-->onReceive: 发现赠品 3S后继续出货");
                //出货
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        outGoods(promotionStackProduct);
                    }
                }, 3000);
            } else {
                BLLOrderUtils.saveAndSyncOrder(context);
                //取消选中商品
                restOutGoods();
                cancelSelectProduct();
                log.i(TAG, "OutGoodsStatusReceiver-->onReceive: 出货结束");
            }
        }

    }

    /**
     * 每一个机型扣库存机制不同
     *
     * @param errorCode 出货失败
     * @param box_no
     * @param road_no
     * @param context
     */
    public abstract void writeDownProductStock(int errorCode, int box_no, int road_no, Context context);


    /**
     * 后台每一个机型扣库存机制不同
     *
     * @param errorCode 出货失败原因 0 出货成功/vmc无法区分失败原因; 1 弹簧不转; 2 光感没有检测到。
     * @param outIndex  当前出货下标
     * @param order     订单
     */
    public abstract void writeDownProductStockToOdoo(int errorCode, int outIndex, Order order);


    /**
     * 选择商品广播
     */
    public class SelectProductReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int roadId = intent.getIntExtra("selectGoods_roadId", -1);
            int boxId = intent.getIntExtra("selectGoods_boxId", -1);


            BLLStackProduct bsp = BLLProductUtils.getStackProduct(boxId, roadId);

            if (null == bsp) {
                log.e(TAG, "SelectProductReceiver-->onReceive: 没有此货道" + boxId + "," + roadId);
                return;
            }
            BLLProduct bp = BLLProductUtils.getProductById(bsp.product_id);
            if (null == bp) {
                log.e(TAG, "SelectProductReceiver-->onReceive: 没有此商品" + boxId + "," + roadId);
                return;
            }
            if (bsp.quantity <= 0 || !bsp.saleTag) {
                log.e(TAG, "SelectProductReceiver-->onReceive: 商品库存为0" + boxId + "," + roadId);
                return;
            }

            //如果已经选中该该商品  则不需要重复选中
            if (mBLLStackProduct != null &&
                mBLLStackProduct.getBoxNoInt() == boxId &&
                mBLLStackProduct.getStackNoInt() == roadId) {
                log.w(TAG, "SelectProductReceiver-->onReceive: 已经选中了该商品");
                return;
            }

            mBLLStackProduct = bsp;


            log.i(TAG, "SelectProductReceiver-->onReceive: 按键选货 货柜:" + boxId + ",货道:" + roadId);

            selectProduct(context, mBLLStackProduct);

            //通知UI选中商品
            Intent it = new Intent(BLL_GOODS_SELECTED_TO_UI);
            it.putExtra("product", mBLLStackProduct);
            LocalBroadcastManager.getInstance(context).sendBroadcast(it);
        }
    }

    /**
     * 投入现金广播
     */
    public class CashPayReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            localCash = intent.getIntExtra("localCash", 0);

            log.i(TAG, "CashPayReceiver-->onReceive: 当前投入现金: " + localCash);

            int payment_cash = ConfigUtils.getConfig(context).payment_way.payment_cash;

            if (payment_cash !=1) {
                log.i(TAG, "payment_cash: 服务端配置不支持纸硬币器投币");
                return;
            }

            if (!mBanCashBroadcast) {
                Intent intent1 = new Intent(com.vmc.core.OdooAction.BLL_RECIVERMONEY_TO_UI);
                intent1.putExtra("localCash", localCash);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
            }

            requestRMB(context);//请求人民币

        }
    }


    /**
     * 设置初始化状态
     *
     * @param initState 状态：0 还未初始化； 1 已完成；  2 已失败。
     */
    public void setInitState(int initState) {
        this.initState = initState;
    }

    /**
     * 售货机初始化广播
     */
    public class MachineInitReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean initState = intent.getBooleanExtra("initState", false);
            if (initState) {
                log.d(TAG, "MachineInitReceiver-->onReceive: 机器初始化成功");
                setInitState(1);
            } else {
                log.e(TAG, "MachineInitReceiver-->onReceive: 机器初始化失败");
                setInitState(2);
            }
            intent.setAction(BLL_INIT_STATE_TO_UI);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }


    /**
     * 写入商品广播
     */
    public class SetProductReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean writeState = intent.getBooleanExtra("setProductResult", false);

            if (writeState) {
                log.i(TAG, "SetProductReceiver-->onReceive: 写入商品成功.");
                if (spList != null) {//更新内存数据
                    BLLProductUtils.stockSync(context, spList);
                }
            } else {
                log.e(TAG, "SetProductReceiver-->onReceive: 写入商品失败. ");
            }
            spList = null;
            Intent intentResult = new Intent(ReplenishAction.STOCK_SYNC_RESULT);
            if (writeState) {
                intentResult.putExtra("msg", "success");
            } else {
                intentResult.putExtra("msg", "error");
            }
            intentResult.putExtra("result", "主控同步完成");
            intentResult.putExtra("code", "1");
            context.sendBroadcast(intentResult);//因为要同步给补货，用全局广播发送

            Intent intentUI = new Intent(BLL_SETPRODUCT_TO_UI);
            intentUI.putExtra("setProductResult", writeState);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intentUI);
        }
    }


    /**
     * 售货机开门广播
     */
    public class MachineDoorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean doorOpen = intent.getBooleanExtra("doorState", false);
            log.i(TAG, "MachineDoorReceiver-->onReceive: 门是否开着: " + doorOpen);
            if (isSelected) {//如果已经选货
                log.i(TAG, "MachineDoorReceiver-->onReceive: 已选中商品 不可弹出补货APP");
                if (doorOpen){//且门开
                    intent.setAction(BLL_DOOR_STATE_TO_UI_SELECTED_PRODUCT);
                }else {//门关闭
                    intent.setAction(OdooAction.UI_CANCEL_PROBLEM_TO_UI);
                    intent.putExtra("showType",3);
                }
            } else {//通知弹出补货
                intent.setAction(BLL_DOOR_STATE_TO_UI);
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    /**
     * 网络状态
     *
     * @param context 上下文
     *
     * @return
     */
    @Override
    public boolean isNetState(Context context) {
        return getNetState(context);
    }


    /**
     * 系统网络状态广播
     */
    public class NetworkChangeReceive extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            if (BLLPayMentController.getInstance().isLooperPayStatus()) {//如果正在轮询

                //判断是否支持纸硬币器
                boolean isSupportCash = false;
                PayMentWay payment_way = ConfigUtils.getConfig(context).payment_way;
                if (null != payment_way && payment_way.payment_cash == 1) {
                    isSupportCash = true;
                }
                if (!getNetState(context)) {//无网络的情况下
                    log.i(TAG,"NetworkChangeReceive-->onReceive: 网络断开");
                    Intent intentisSelecting = new Intent(BLL_NET_STATE_TO_UI);
                    intentisSelecting.putExtra("hasCoinBox",isSupportCash);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intentisSelecting);
                }else {//来网络的情况
                    log.i(TAG,"NetworkChangeReceive-->onReceive: 网络联通");
                    Intent intent1 = new Intent(OdooAction.UI_CANCEL_PROBLEM_TO_UI);
                    intent1.putExtra("showType",isSupportCash?1:2);
                    LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent1);
                }
            }
        }
    }

    /**
     * 获取网络状态
     *
     * @param context 上下文
     *
     * @return
     */
    public boolean getNetState(Context context) {
        ConnectivityManager
                connectivity =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            if (null != networkInfo && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新商品列表
     *
     * @param productList 商品集合
     * @param context     上下文
     */
    @Override
    public abstract void updateStackProduct(OdooProductList productList, Context context);


    /**
     * 同步商品可销售状态
     */
    @Override
    public abstract void syncStackSaleableState();


    /**
     * 取消选中商品（久保田机型，取消灯亮）
     */
    @Override
    public void cancelDeal() {
        VMCContoller.getInstance().cancelDeal();
    }


    /**
     * 是否卡禁止
     *
     * @return
     */
    @Override
    public abstract boolean isCardBan();


    /**
     * 取消订单
     *
     * @param context 上下文
     */
    @Override
    public void cancelOrder(Context context) {
        Order order = BLLOrderUtils.getCurrentOrder();
        VMCContoller.getInstance().cancelDeal();
        if (order != null) {
            log.i(TAG, "cancelOrder: 取消订单");
            if (order.payment_status.equals(Order.PayStatus.PAID.toString())) {//支付成功会调用出货
                if (TextUtils.isEmpty(order.shipping_status)) {//如果商品出货状态为空 表示商品出货超时了
                    //通知出货状态

                    log.e(TAG, "cancelOrder: 商品出货超时");
                    order.shipping_status = "timeout";
                    if (order.payment_method.equals("RMB")) {
                        order.status = Order.Status.CANCEL.getStatus();
                        order.payment_status = Order.PayStatus.UNPAY.getStatus();
                    }
                    order.sub_product_stock = "false";
                    int stempErrorCode = order.error_code;
                    order.error_code = -2;

                    sendOutGoodsTimeOut(context,
                                        order.getProduct().product_id, order.id,
                                        order.payment_method, false);
                    order.error_code = stempErrorCode;


                } else if (order.shipping_status.equals("true") &&
                           !TextUtils.isEmpty(order.promotion_box_no) &&
                           !TextUtils.isEmpty(order.promotion_stack_no) &&
                           TextUtils.isEmpty(order.promotion_shipping_status)) {//如果赠品没状态 表示赠品超时了
                    int stempErrorCode = order.promotion_error_code;
                    order.promotion_error_code = -2;

                    //通知出货状态
                    sendOutGoodsTimeOut(context,
                                        order.getProduct().product_id, order.id,
                                        order.payment_method, true);
                    order.promotion_error_code = stempErrorCode;


                    log.e(TAG, "cancelOrder: 赠品出货超时");
                    order.promotion_shipping_status = "timeout";
                    order.sub_gift_stock = "false";
                }

            }
            restOutGoods();
            BLLOrderUtils.saveAndSyncOrder(context);
        }
    }


    /**
     * 是否可以取消订单
     *
     * @return
     */
    @Override
    public boolean canCancelOrder() {
        Order order = BLLOrderUtils.getCurrentOrder();
        if (order != null) {
            if (order.payment_status.equals(Order.PayStatus.PAID.toString())) {
                log.i(TAG, "canCancelOrder: 不可取消订单");
                return false;
            }
        }
        log.i(TAG, "canCancelOrder: 可取消订单");
        return true;
    }

    /**
     * 获取当前现金
     *
     * @return 现金（分）
     */
    @Override
    public int getLocalMoney() {
        return localCash;
    }


    /**
     * 补货|换货
     *
     * @param context 上下文
     * @param list    补货app传递过来的数据
     */
    @Override
    public abstract void stockSyncToVMC(Context context, SupplyProductList list);


    /**
     * 验证提货码
     *
     * @param mCode    提货码
     * @param mContext 上下文
     */
    @Override
    public void verifyPickUpGoodsCode(final String mCode, final Context mContext) {

        final Intent intent = new Intent(OdooAction.BLL_VERIFY_RESULT_TO_UI);

        log.i(TAG, "verifyPickUpGoodsCode: 开始验证提货码" + mCode);

        Odoo.getInstance(mContext).goodsPicked(mCode, new OdooHttpCallback<PickUpProduct>(mContext) {
            @Override
            public void onSuccess(final PickUpProduct result) {
                if (result.product_id > 0) {
                    BLLProduct bp = BLLProductUtils.getProductById(result.product_id);

                    if (bp == null) {
                        log.i(TAG, "verifyPickUpGoodsCode: 没有此商品");
                        intent.putExtra("result", "error");
                        intent.putExtra("msg", "没有此商品");
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                        resetVerify(mCode, mContext);
                        return;
                    }

                    BLLStackProduct bsp = getSaleableStackProductByProduct(bp);
                    if (bsp == null) {
                        log.i(TAG, "verifyPickUpGoodsCode: 商品库存不足");
                        intent.putExtra("result", "error");
                        intent.putExtra("msg", "商品库存不足");
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                        resetVerify(mCode, mContext);
                        return;
                    }

                    intent.putExtra("result", "success");
                    intent.putExtra("msg", "开始出货");
                    intent.putExtra("product", bsp);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

                    selectVerifyProduct(bsp, mContext);

                    BLLOrderUtils.getCurrentOrder().pick_good_code = mCode;


                    return;
                }

                log.i(TAG, "verifyPickUpGoodsCode: 服务器错误");
                intent.putExtra("result", "error");
                intent.putExtra("msg", "服务器错误");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                resetVerify(mCode, mContext);
            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);
                intent.putExtra("result", "error");
                if (!TextUtils.isEmpty(error.getMessage())) {
                    log.w(TAG,
                          "verifyPickUpGoodsCode: 验证失败," +
                          error.getMessage());
                    intent.putExtra("msg", error.getMessage());
                }
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                log.i(TAG, "verifyPickUpGoodsCode: 验证提货码结束");
            }
        });
    }


    /**
     * 出后失败后 重置提货码
     *
     * @param code    提货码
     * @param context 上下文
     */
    private void resetVerify(final String code, final Context context) {

        Odoo.getInstance(context).resetVmcPickGoodCode(code, new OdooHttpCallback<RestVerify>(context) {
            @Override
            public void onSuccess(RestVerify result) {

                if (result != null && result.success) {
                    log.i(TAG, "resetVerify,onSuccess: 重置提货码成功");
                } else {
                    log.i(TAG, "resetVerify,onSuccess: 重置提货码失败，继续重置");
                    resetVerify(code, context);
                }
            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);
                log.i(TAG, "resetVerify,onError: 重置提货码失败，继续重置");
                resetVerify(code, context);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    @Override
    public BLLProduct getSelectProduct() {

        return mSelectProduct;
    }

    @Override
    public boolean isDoorOpen() {
        return VMCContoller.getInstance().isDoorOpen();
    }

    @Override
    public boolean isDriveError() {
        return VMCContoller.getInstance().isDriveError();
    }
}
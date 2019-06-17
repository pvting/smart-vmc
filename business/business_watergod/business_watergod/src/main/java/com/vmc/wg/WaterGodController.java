package com.vmc.wg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v4.content.LocalBroadcastManager;

import com.vmc.core.IBLLController;
import com.vmc.core.model.order.Order;
import com.vmc.core.model.product.BLLCategory;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.model.product.OdooProduct;
import com.vmc.core.model.product.OdooProductList;
import com.vmc.core.model.product.SupplyProductList;
import com.vmc.core.utils.BLLOrderUtils;
import com.vmc.core.utils.BLLPayMentController;
import com.vmc.core.utils.BLLProductUtils;

import java.util.ArrayList;
import java.util.List;

import vmc.core.log;
import vmc.machine.core.VMCAction;
import vmc.machine.core.VMCContoller;
import vmc.machine.core.model.VMCStackProduct;


/**
 * <b>Create Date:</b>2017/3/29 09:11<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class WaterGodController implements IBLLController {


    private static final String TAG = "WaterGodController";

    /**
     * 出货接收的广播
     */
    private OutGoodsStatusReceiver mOutGoodsStatusreceiver;

    /**
     * 开关门广播
     */
    private MachineDoorReceiver mMachineDoorReceiver;


    /**
     * 机器初始化广播
     */
    private MachineInitReceiver mMachineInitReceiver;


    /**
     * 水神传感器广播
     */
    private GetSensorStatus mGetSensorStatus;


    /**
     * 当前选中的商品
     */
    private BLLStackProduct mBLLStackProduct;

    /**
     * 出货总数
     */
    private int totalNum;

    /**
     * 当前出货下标
     */
    private int outIndex;

    /**
     * 第一个出货的商品
     */
    private BLLProduct mFristOutBLLProduct;

    /**
     * 初始化状态  0 还未初始化   1  初始化完成   2 初始化失败
     */
    public int initState;


    /**
     * 造水器状态
     */
    private boolean watergodGenerator = true;


    /**
     * 原液状态
     */
    private boolean watergodElectrolyte = true;


    /**
     * 网络状态
     */
    private NetworkChangeReceive mNetworkChangeReceive;


    @Override
    public String getVmcRunningStates() {
        return VMCContoller.getInstance().getVmcRunningStates();
    }

    @Override
    public void setProducts(List<VMCStackProduct> products) {
        //水神不需要设置货道  no use
    }

    @Override
    public void outGoods(BLLStackProduct bsp) {
        log.i(TAG, "出货:" + bsp.toString());
        VMCContoller.getInstance().outGoods(bsp.product_id, (int) (bsp.getWegiht() * 10));

    }

    @Override
    public void outGoodsByCash(int boxId, int roadId, int price) {
        //水神没有现金支付    no use
    }

    @Override
    public void outGoodsOnLine() {
        if (mBLLStackProduct != null) {
            log.i(TAG, "出货:" + mBLLStackProduct.toString());
            prepareOut(mBLLStackProduct.product_id);
            outGoods(mBLLStackProduct);
        }
    }

    @Override
    public void init(Context context) {

        //出水广播
        mOutGoodsStatusreceiver = new OutGoodsStatusReceiver();
        IntentFilter mIntentFilter = new IntentFilter(VMCAction.VMC_TO_BLL_OUTGOODS);
        context.registerReceiver(mOutGoodsStatusreceiver, mIntentFilter);

        //开关门广播
        mMachineDoorReceiver = new MachineDoorReceiver();
        IntentFilter mIntentFilter1 = new IntentFilter(VMCAction.VMC_TO_BLL_DOOR_STATE);
        context.registerReceiver(mMachineDoorReceiver, mIntentFilter1);

        //机器初始化广播
        mMachineInitReceiver = new MachineInitReceiver();
        IntentFilter mIntentFilter2 = new IntentFilter(VMCAction.VMC_TO_BLL_INIT_FINISH);
        context.registerReceiver(mMachineInitReceiver, mIntentFilter2);

        //获取机器传感器广播
        mGetSensorStatus = new GetSensorStatus();
        IntentFilter mIntentFilter3 = new IntentFilter(VMCAction.VMC_TO_BLL_SENSOR_STATE);
        context.registerReceiver(mGetSensorStatus, mIntentFilter3);

        //监听网络广播
        mNetworkChangeReceive = new NetworkChangeReceive();
        IntentFilter mIntentFilter4 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(mNetworkChangeReceive, mIntentFilter4);
    }

    @Override
    public void restOutGoods() {
        mFristOutBLLProduct = null;
        totalNum = 0;
        outIndex = 0;
    }

    @Override
    public void selectVerifyProduct(BLLStackProduct bllStackProduct, Context context) {
        outIndex = 0;
        totalNum = 1;

        Order order = BLLOrderUtils.getCurrentOrder();

        if (order != null) {
            BLLOrderUtils.saveAndSyncOrder(context);
        }

        BLLOrderUtils.createNativeOrder(bllStackProduct.product_id);
        BLLOrderUtils.getCurrentOrder().payment_status = Order.PayStatus.PAID.getStatus();
        BLLOrderUtils.getCurrentOrder().status = Order.Status.PAID.getStatus();
        BLLOrderUtils.getCurrentOrder().payment_method = Order.Payment.CODE.getPayment();
        BLLOrderUtils.getCurrentOrder().promotion_id = 0;
        log.i(TAG, "准备出货");

        outGoods(bllStackProduct);


    }

    @Override
    public void sendOutGoodsStatus(Context context,
                                   int boxId,
                                   int roadId,
                                   boolean shipping_status
                                      ) {



        //通知出货状态
        Intent it = new Intent(BLL_OUTGOODS_TO_UI);
        it.putExtra("box_no", boxId);
        it.putExtra("stack_no", roadId);
        it.putExtra("totalNum", totalNum);
        it.putExtra("outIndex", outIndex);
        it.putExtra("outGoodsState", shipping_status);//出货状态
        it.putExtra("payMentType",   BLLOrderUtils.getCurrentOrder()==null?"NONE":BLLOrderUtils.getCurrentOrder().payment_method);
        it.putExtra("payStatus", "PAID");//支付状态
        LocalBroadcastManager.getInstance(context).sendBroadcast(it);

    }

    @Override
    public void prepareOut(int product_id) {
        BLLProduct productFrist = BLLProductUtils.getProductById(product_id);

        totalNum = 1;

        if (productFrist.mPromotionDetail == null) {

            return;
        }


        if (productFrist.mPromotionDetail.freebie == null ||
            productFrist.mPromotionDetail.freebie.size() == 0) {

            return;
        }
        if (!"one_more".endsWith(productFrist.mPromotionDetail.promotion_type)) {

            return;
        }

        if (productFrist.mPromotionDetail.freebie.size() == 0) {

            return;
        }

        Order order = BLLOrderUtils.getCurrentOrder();
        if (null == order) {
            return;
        }


        if (null == productFrist.mPromotionDetail.payment_option) {
            return;
        }
        if (productFrist.mPromotionDetail.payment_option.contains(order.payment_method)) {
            totalNum += 1;
        }
        log.i(TAG, "change payment " + order.payment_method);
    }

    @Override
    public void selectProduct(Context context, BLLStackProduct bllStackProduct) {
        totalNum = 0;
        outIndex = 0;
        mBLLStackProduct = null;


        mBLLStackProduct = bllStackProduct;


        Order order = BLLOrderUtils.getCurrentOrder();

        if (order != null) {

            BLLOrderUtils.saveAndSyncOrder(context);
        }

        BLLOrderUtils.createNativeOrder(bllStackProduct.product_id);

        //创建支付请求对象
        BLLPayMentController.getInstance().markOrderRequest(bllStackProduct.product_id);


    }


    @Override
    public void cancelSelectProduct() {
        log.i(TAG, "重置选中商品");
        mBLLStackProduct = null;
        BLLPayMentController.getInstance().resetPayRequest();
    }

    @Override
    public void requestRMB(Context context) {
        //水神没有现金支付  no use

    }

    @Override
    public boolean isWatergodGenerator() {
        return watergodGenerator;
    }

    @Override
    public boolean isWatergodElectrolyte() {
        return watergodElectrolyte;
    }

    @Override
    public int getInitState() {
        return initState;
    }

    @Override
    public BLLStackProduct getSaleableStackProductByProduct(BLLProduct product) {
        if (product == null) {
            return null;
        }
        ArrayList<BLLStackProduct> mBLLStackProducts = product.mBLLStackProducts;
        if (mBLLStackProducts == null) {
            return null;
        }
        for (BLLStackProduct bsp : mBLLStackProducts) {
            return bsp;
        }

        return null;
    }

    @Override
    public int getSaleableStackProductByProductCount(BLLProduct product) {

        return 1;
    }


    /**
     * 出货广播  水神
     */
    public class OutGoodsStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int product_id = intent.getIntExtra("box_no", -1);

            BLLProduct outBp = BLLProductUtils.getProductById(product_id);
            /**
             * 获取出货状态
             */
            boolean shipping_status = intent.getBooleanExtra("outGoodsState", false);
            log.i(TAG,
                  "出货  box:" + outBp.fristBoxNo + ",road:" + outBp.fristStackNo + ",出货:" + shipping_status);
            /**
             *更改出货状态
             */
            BLLOrderUtils.updateOrderShippingStatus(
                                                    shipping_status,
                                                    outBp.fristBoxNo,
                                                    outBp.fristStackNo,
                                                    outIndex, 0);
            /**
             *拿到当前已经出货的商品
             */
            BLLProduct bp = BLLProductUtils.getProductByRoadId(outBp.fristBoxNo, outBp.fristStackNo);
            /**
             * 冲减库存
             */
            BLLProductUtils.writeDownProductStock(outBp.fristBoxNo, outBp.fristStackNo, context);
            /**
             *  取消选中商品
             */
            cancelSelectProduct();
            outIndex += 1;
            if (totalNum == 0) {//如果是机器主动出货  投足额，按键选货
                prepareOut(bp.product_id);
            }
            //通知出货状态
            sendOutGoodsStatus(context, outBp.fristBoxNo, outBp.fristStackNo, shipping_status);
            //出货状态为失败 不需要处理下面事务
            if (!shipping_status) {
                BLLOrderUtils.saveAndSyncOrder(context);
                restOutGoods();
                return;
            }
            Order order = BLLOrderUtils.getCurrentOrder();
            log.i(TAG, order.toString());
            /**
             *记录第一次出货的商品 需要记住promotionID
             */
            if (outIndex == 1) {
                mFristOutBLLProduct = bp;
            }
            if (totalNum > outIndex) {//如果总数大于当前出货个数  还有货要出
                //获取要出货的赠品
                BLLStackProduct
                        promotionStackProduct =
                        BLLProductUtils.getPromotionStackProduct(mFristOutBLLProduct.product_id);
                if (promotionStackProduct == null) {//表示无赠品
                    restOutGoods();
                    return;
                }

                /**
                 * 更新促销信息
                 */
                BLLOrderUtils.updateOrderPromotion(mFristOutBLLProduct.mPromotionDetail.promotion_id,
                                                   promotionStackProduct.box_no + "",
                                                   promotionStackProduct.origin_stack_no);
                /**
                 * 出货
                 */
                outGoods(promotionStackProduct);
            } else {
                restOutGoods();
                BLLOrderUtils.saveAndSyncOrder(context);
            }

        }

    }


    /**
     * 售货机开门广播
     */
    public class MachineDoorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.i(TAG, "door isOpen:" + intent.getBooleanExtra("doorState", false));
            intent.setAction(BLL_DOOR_STATE_TO_UI);
            context.sendBroadcast(intent);
        }
    }


    /**
     * 售货机初始化广播
     */
    private class MachineInitReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean initState = intent.getBooleanExtra("initState", false);
            log.i(TAG, "machine isInit:" + initState);
            if (initState) {
                WaterGodController.this.initState = 1;
            } else {
                WaterGodController.this.initState = 2;
            }
            intent.setAction(BLL_INIT_STATE_TO_UI);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    /**
     * 传感器广播    水神
     */
    private class GetSensorStatus extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            watergodElectrolyte = intent.getBooleanExtra("liquidState", true);
            watergodGenerator = intent.getBooleanExtra("outPut", true);
            log.i(TAG,
                  "machine sensor status:" +
                  "watergodElectrolyte:" +
                  watergodElectrolyte +
                  ",watergodGenerator:" +
                  watergodGenerator);
            Intent intentUI = new Intent(BLL_SENSOR_STATE_TO_UI);
            intentUI.putExtra("liquidState", watergodElectrolyte);
            intentUI.putExtra("outPut", watergodGenerator);
            context.sendBroadcast(intentUI);
        }
    }

    public class NetworkChangeReceive extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Intent intentUI = new Intent(BLL_SENSOR_STATE_TO_UI);
            context.sendBroadcast(intentUI);
        }
    }


    /**
     * 网络状态
     *
     * @return
     */
    @Override
    public boolean isNetState(Context context) {
        return getNetState(context);
    }

    @Override
    public void updateStackProduct(OdooProductList productList, Context context) {

        BLLProductUtils.sBLLProductMap.clear();
        BLLProductUtils.sBLLCategoryMap.clear();
        BLLProductUtils.sBLLProductsByRoadMap.clear();

        synchronized (BLLProductUtils.sBLLLock) {
            for (OdooProduct op : productList.records) {
                BLLStackProduct bsp = new BLLStackProduct();//创建商品
                bsp.product_id = op.id;
                bsp.name = op.name;
                bsp.box_no = Integer.parseInt(op.box_no);
                bsp.stack_no = Integer.parseInt(op.stack_no);
                bsp.price = op.price;
                bsp.origin_stack_no = op.stack_no;
                bsp.image_url = op.image_url;
                bsp.seq_no = op.seq_no;
                bsp.category_name = op.product_type;
                bsp.net_weight = op.net_weight;
                bsp.product_details_image_url = op.product_details_image_url;

                //初始化通过货柜和货道获取商品的Map
                BLLProductUtils.sBLLProductsByRoadMap.put(bsp.box_no + "*" + bsp.stack_no, bsp);
            }

            for (String key : BLLProductUtils.sBLLProductsByRoadMap.keySet()) {
                BLLStackProduct bsp = BLLProductUtils.sBLLProductsByRoadMap.get(key);
                BLLProduct bp = BLLProductUtils.sBLLProductMap.get(bsp.product_id);

                if (bp == null) {//如果是空的  表示加入到去重复的Map
                    bp = new BLLProduct();//创建一个商品对象

                    bp.fristBoxNo = bsp.box_no;
                    if (bp.fristStackNo == 0 || bp.fristStackNo >= bsp.stack_no) {
                        bp.fristStackNo = bsp.stack_no;
                    }
                    bp.product_id = bsp.product_id;
                    bp.price = bsp.price;
                    bp.image_url = bsp.image_url;
                    bp.product_details_image_url = bsp.product_details_image_url;
                    bp.name = bsp.name;
                    bp.net_weight = bsp.net_weight;
                    bp.category_name = bsp.category_name;

                    BLLProductUtils.sBLLProductMap.put(bp.product_id, bp);
                }

                bp.mBLLStackProducts.add(bsp);

                //分类
                BLLCategory bc = BLLProductUtils.sBLLCategoryMap.get(bsp.category_name);
                if (bc == null) {
                    bc = new BLLCategory();

                    bc.category_name = bsp.category_name;

                    BLLProductUtils.sBLLCategoryMap.put(bc.category_name, bc);
                }
                bc.mBLLProductHashMap.put(bp.product_id, bp);
            }
            BLLProductUtils.saveProductsToSP(context);
        }

    }

    @Override
    public void syncStackSaleableState() {
        // no use
    }

    @Override
    public void cancelDeal() {

    }

    @Override
    public boolean isCardBan() {
        return false;
    }

    @Override
    public void cancelOrder(Context context) {

    }

    @Override
    public int getLocalMoney() {
        return 0;
    }

    @Override
    public void stockSyncToVMC(Context context, SupplyProductList list) {

    }

    @Override
    public void verifyPickUpGoodsCode(String mInputString, Context mContext) {
        //no use
    }


    public boolean getNetState(Context context) {
        boolean flag = false;
        //得到网络连接信息
        ConnectivityManager
                manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;

    }

    @Override
    public boolean canCancelOrder() {
        return true;
    }

    @Override
    public BLLProduct getSelectProduct() {
        return null;
    }

    @Override
    public boolean isDoorOpen() {
        return false;
    }

    @Override
    public boolean isDriveError() {
        return false;
    }
}
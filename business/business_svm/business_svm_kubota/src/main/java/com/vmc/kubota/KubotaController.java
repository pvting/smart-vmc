package com.vmc.kubota;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.vmc.api.BLLSVMController;
import com.vmc.core.BLLController;
import com.vmc.core.ReplenishAction;
import com.vmc.core.model.order.Order;
import com.vmc.core.model.product.BLLCategory;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.model.product.OdooProduct;
import com.vmc.core.model.product.OdooProductList;
import com.vmc.core.model.product.SupplyProduct;
import com.vmc.core.model.product.SupplyProductList;
import com.vmc.core.utils.BLLOrderUtils;
import com.vmc.core.utils.BLLProductUtils;

import java.util.ArrayList;

import vmc.core.log;
import vmc.machine.core.VMCAction;
import vmc.machine.core.VMCContoller;


/**
 * <b>Create Date:</b>2017/3/29 09:11<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class KubotaController extends BLLSVMController {


    /**
     * 料道状态广播 久保田
     */
    private StackStatusReceiver mStackStatusReceiver;


    private CardBan mCardBan;

    private CardCan mCardCan;


    private DealFinish mDealFinish;


    private boolean isCardBan = false;

    @Override
    public void init(Context context) {
        log.v(TAG, "init: 久保田BLL初始化开始");
        //出货广播
        mOutGoodsStatusReceiver = new OutGoodsStatusReceiver();
        IntentFilter mIntentFilter = new IntentFilter(VMCAction.VMC_TO_BLL_OUTGOODS);
        context.registerReceiver(mOutGoodsStatusReceiver, mIntentFilter);

        //按键选货广播
        mSelectProductReceiver = new SelectProductReceiver();
        IntentFilter mIntentFilter1 = new IntentFilter(VMCAction.VMC_TO_BLL_GOODS_SELECTED);
        context.registerReceiver(mSelectProductReceiver, mIntentFilter1);

        //开关门广播
        mMachineDoorReceiver = new MachineDoorReceiver();
        IntentFilter mIntentFilter2 = new IntentFilter(VMCAction.VMC_TO_BLL_DOOR_STATE);
        context.registerReceiver(mMachineDoorReceiver, mIntentFilter2);

        //设置货道广播
        mSetProductReceiver = new SetProductReceiver();
        IntentFilter mIntentFilter3 = new IntentFilter(VMCAction.VMC_TO_BLL_SETPRODUCT);
        context.registerReceiver(mSetProductReceiver, mIntentFilter3);

        //机器初始化广播
        mMachineInitReceiver = new MachineInitReceiver();
        IntentFilter mIntentFilter4 = new IntentFilter(VMCAction.VMC_TO_BLL_INIT_FINISH);
        context.registerReceiver(mMachineInitReceiver, mIntentFilter4);

        //货道销售状态广播
        mStackStatusReceiver = new StackStatusReceiver();
        IntentFilter mIntentFilter5 = new IntentFilter(VMCAction.VMC_TO_BLL_SELLABLE_ROADS);
        context.registerReceiver(mStackStatusReceiver, mIntentFilter5);

        //监听网络状态广播
        mNetworkChangeReceive = new NetworkChangeReceive();
        IntentFilter mIntentFilter6 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(mNetworkChangeReceive, mIntentFilter6);


        //卡禁止广播
        mCardBan = new CardBan();
        IntentFilter mIntentFilter7 = new IntentFilter(VMCAction.VMC_TO_BLL_CARD_BAN);
        context.registerReceiver(mCardBan, mIntentFilter7);

        //卡允许广播
        mCardCan = new CardCan();
        IntentFilter mIntentFilter8 = new IntentFilter(VMCAction.VMC_TO_BLL_CARD_CAN);
        context.registerReceiver(mCardCan, mIntentFilter8);

//        // 订单结束广播
//        mDealFinish = new DealFinish();
//        IntentFilter mIntentFilter9 = new IntentFilter(VMCAction.VMC_TO_BLL_DEAL_FINISH);
//        context.registerReceiver(mDealFinish, mIntentFilter9);

        log.v(TAG, "init: 久保田BLL初始化结束");

    }

    @Override
    public boolean isCanOutgoods(int localCash, int productPrice) {
        return true;
    }

    @Override
    public void lightButton() {
        //有灯的则让灯亮起来
        VMCContoller.getInstance().selectProduct(mBLLStackProduct.box_no, mBLLStackProduct.stack_no);
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
            if (!bsp.saleTag) {//当机器实际不可销售时  略过此货道
                continue;
            }

            if (bsp.box_no == 11) {
                if (bsp.quantity > 1) {
                    return bsp;
                }
            } else {
                if (bsp.quantity > 0)
                    return bsp;
            }
        }

        return null;
    }

    @Override
    public int getSaleableStackProductByProductCount(BLLProduct product) {
        if (product == null) {
            return 0;
        }
        ArrayList<BLLStackProduct> mBLLStackProducts = product.mBLLStackProducts;
        if (mBLLStackProducts == null) {
            return 0;
        }
        int count = 0;
        for (BLLStackProduct bsp : mBLLStackProducts) {
            log.i(TAG,
                  "getSaleableStackProductByProductCount: 货柜:" +
                  bsp.box_no +
                  " 货道:" +
                  bsp.stack_no +
                  " 数量:" +
                  bsp.quantity +
                  " 销售状态:" +
                  bsp.saleTag);
            if (!bsp.saleTag) {//当机器实际不可销售时  略过此货道
                continue;
            }
            if (bsp.box_no == 11) {
                if (bsp.quantity > 1) {
                    count += (bsp.quantity - 1);
                }
            } else {
                if (bsp.quantity > 0)
                    count += (bsp.quantity);
            }
        }

        return count;
    }

    @Override
    public void writeDownProductStock(
            int errorCode,
            int box_no,
            int road_no,
            Context context) {
        BLLProductUtils.writeDownProductStock(box_no, road_no, context);

    }


    @Override
    public void writeDownProductStockToOdoo(int errorCode,
                                            int outIndex,
                                            Order order) {
        if (outIndex == 0) {//原商品
            if (errorCode == 0 || errorCode == 2) {
                order.sub_product_stock = "true";
            } else {
                order.sub_product_stock = "false";
            }
        } else {//赠品
            if (errorCode == 0 || errorCode == 2) {
                order.sub_gift_stock = "true";
            } else {
                order.sub_gift_stock = "false";
            }
        }

    }


    /**
     * 更新商品列表
     *
     * @param productList
     * @param context
     */
    @Override
    public void updateStackProduct(OdooProductList productList, Context context) {
        synchronized (BLLProductUtils.sBLLLock) {
            if (BLLProductUtils.sBLLProductsByRoadMap.size() == 0) {
                BLLProductUtils.initProductFromSP(context);
            }
            ArrayList<BLLProduct> changePriceProducts = new ArrayList<>();
            boolean haveChange = false;
            for (OdooProduct mOdooproduct : productList.records) {
                BLLProduct bp = BLLProductUtils.sBLLProductMap.get(mOdooproduct.id);//获取对应的product
                if (bp == null) {//如果获取不到，表示新增了一个
                    log.w(TAG, "updateStackProduct: 不能直接新增商品,跳过更新商品" + mOdooproduct.name);
                    return;
                }
                /*赋值*/
                bp.name = mOdooproduct.name;

                if (bp.price != mOdooproduct.price) {//如果当前价格不与服务器相同
                    log.i(TAG,
                          "updateStackProduct: 更改商品价格:" +
                          bp.name +
                          ";原价:" +
                          bp.price +
                          ";新价:" +
                          mOdooproduct.price);
                    bp.price = mOdooproduct.price;
                    haveChange = true;
                }

                bp.net_weight = mOdooproduct.net_weight;
                bp.image_url = mOdooproduct.image_url;
                bp.product_details_image_url = mOdooproduct.product_details_image_url;
                changePriceProducts.add(bp);
                /*当商品的分类更改了，做相应的移动处理*/

                if (null == bp.category_name) {
                    log.w(TAG, "updateStackProduct: 该商品没有分类,不需要更新分类" + bp.name);
                    return;
                }

                if (!bp.category_name.equals(mOdooproduct.product_type)) {//如果旧的不等于新的
                    BLLCategory category = BLLProductUtils.sBLLCategoryMap.get(bp.category_name);//获取旧的分类
                    if (category != null) {
                        category.mBLLProductHashMap.remove(bp.product_id);//移除旧的
                        if (category.mBLLProductHashMap.size() == 0) {
                            BLLProductUtils.sBLLCategoryMap.remove(bp.category_name);
                        }
                        category = BLLProductUtils.sBLLCategoryMap.get(mOdooproduct.product_type);//获取新的
                        bp.category_name = mOdooproduct.product_type;
                        if (category == null) {//新的不存在 则添加新的分类
                            category = new BLLCategory();
                            category.category_name = bp.category_name;
                            BLLProductUtils.sBLLCategoryMap.put(category.category_name, category);
                            log.i(TAG, "updateStackProduct: 添加新分类:" + bp.category_name);
                        }
                        category.mBLLProductHashMap.put(bp.product_id, bp);
                    }
                }
            }
            if (haveChange && changePriceProducts.size() > 0) {
                log.i(TAG, "updateStackProduct: 发现价格改动,重新写入机器");
                BLLController.getInstance()
                             .setProducts(BLLProductUtils.makeVmcProductList(changePriceProducts));//重新写入
            }
            BLLProductUtils.saveProductsToSP(context);
        }
    }

    /**
     * 同步货道状态
     */
    @Override
    public synchronized void syncStackSaleableState() {
        log.d(TAG, statusStates + "");
        if (statusStates != null) {
            for (int i = 0; i < statusStates.size(); i++) {
                BLLStackProduct bsp = BLLProductUtils.getStackProduct(11, i + 1);
                if (bsp == null) {
                    continue;
                }
                if (statusStates.get(i) == 0) {
                    bsp.saleTag = true;
                } else {
                    bsp.saleTag = false;
                }
            }
        }
    }

    @Override
    public boolean isCardBan() {

        return isCardBan;
    }


    /**
     * 料道状态  久保田
     */
    private class StackStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Integer> stackStatus = intent.getIntegerArrayListExtra("sellableroads");
            statusStates.clear();
            statusStates.addAll(stackStatus);
            syncStackSaleableState();
        }
    }

    /**
     * 卡禁止 久保田
     */
    private class CardBan extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.i(TAG, "CardBan-->onReceive: 卡禁止");
            isCardBan = true;
           if (null!=BLLOrderUtils.getCurrentOrder()){
               BLLOrderUtils.getCurrentOrder().payment_method=Order.Payment.RMB.getPayment();
           }
            LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(new Intent(BLL_CARD_BAN_TO_UI));
        }
    }

    /**
     * 卡允许  久保田
     */
    private class CardCan extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            isCardBan = false;
            log.i(TAG, "CardCan-->onReceive: 卡允许");
            LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(new Intent(BLL_CARD_CAN_TO_UI));


        }
    }


    /**
     * 交易结束 久保田
     */
    private class DealFinish extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (outIndex == totalNum) {//如果当前出最后一个交易结束
                context.sendBroadcast(new Intent(BLL_DEAL_FINISH_TO_UI));
            }
        }
    }

    @Override
    public void stockSyncToVMC(Context context, SupplyProductList list) {
        this.spList = list;
        if (list.data == null) {
            log.e(TAG, "stockSyncToVMC: 补货失败,补货列表为空...");
            return;
        }
        ArrayList<SupplyProduct> changeList = new ArrayList<>();
        for (SupplyProduct item : list.data) {
            if (item.getType() == 1) {
                log.i(TAG, "stockSyncToVMC: 发现换货...");
                changeList.add(item);
            }
        }
        if (changeList.size() > 0) {//
            Intent intent = new Intent(ReplenishAction.STOCK_SYNC_ACTION);
            intent.putExtra("msg", "success");
            intent.putExtra("result", "主控同步中");
            intent.putExtra("code", "1");
            context.sendBroadcast(intent);
            BLLController.getInstance().setProducts(BLLProductUtils.makeVmcProductListformSupply(list.data));
            return;
        }
        BLLProductUtils.stockSync(context, list);

        this.spList = null;//清空
    }
}
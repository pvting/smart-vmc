package com.vmc.yichu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.vmc.api.BLLSVMController;
import com.vmc.core.BLLController;
import com.vmc.core.OdooAction;
import com.vmc.core.ReplenishAction;
import com.vmc.core.model.order.Order;
import com.vmc.core.model.product.BLLCategory;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.model.product.OdooProduct;
import com.vmc.core.model.product.OdooProductList;
import com.vmc.core.model.product.SupplyProduct;
import com.vmc.core.model.product.SupplyProductList;
import com.vmc.core.utils.BLLProductUtils;

import java.util.ArrayList;
import java.util.Random;

import vmc.core.log;
import vmc.machine.core.VMCAction;
import vmc.machine.core.VMCContoller;


/**
 * <b>Create Date:</b>2017/3/29 09:11<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class YichuController extends BLLSVMController {

    @Override
    public void init(Context context) {
        log.d(TAG, "init: 易触BLL初始化开始");
        //接收出货广播
        mOutGoodsStatusReceiver = new OutGoodsStatusReceiver();
        IntentFilter mIntentFilter = new IntentFilter(VMCAction.VMC_TO_BLL_OUTGOODS);
        context.registerReceiver(mOutGoodsStatusReceiver, mIntentFilter);

        //键盘选货广播
        mSelectProductReceiver = new SelectProductReceiver();
        IntentFilter mIntentFilter1 = new IntentFilter(VMCAction.VMC_TO_BLL_GOODS_SELECTED);
        context.registerReceiver(mSelectProductReceiver, mIntentFilter1);

        //现金投入广播
        mCashPayReceiver = new CashPayReceiver();
        IntentFilter mIntentFilter2 = new IntentFilter(VMCAction.VMC_TO_BLL_RECEIVE_MONEY);
        context.registerReceiver(mCashPayReceiver, mIntentFilter2);

        //开关门广播
        mMachineDoorReceiver = new MachineDoorReceiver();
        IntentFilter mIntentFilter3 = new IntentFilter(VMCAction.VMC_TO_BLL_DOOR_STATE);
        context.registerReceiver(mMachineDoorReceiver, mIntentFilter3);

        //设置货道状态广播
        mSetProductReceiver = new SetProductReceiver();
        IntentFilter mIntentFilter4 = new IntentFilter(VMCAction.VMC_TO_BLL_SETPRODUCT);
        context.registerReceiver(mSetProductReceiver, mIntentFilter4);

        //初始化状态广播
        mMachineInitReceiver = new MachineInitReceiver();
        IntentFilter mIntentFilter5 = new IntentFilter(VMCAction.VMC_TO_BLL_INIT_FINISH);
        context.registerReceiver(mMachineInitReceiver, mIntentFilter5);

        //监听网络状态广播
        mNetworkChangeReceive = new NetworkChangeReceive();
        IntentFilter mIntentFilter6 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(mNetworkChangeReceive, mIntentFilter6);

        //串口异常广播
        SerialPortReceiver serialPortReceiver = new SerialPortReceiver();
        IntentFilter mIntentFilter7 = new IntentFilter("com.avm.serialport.NOTICE_AVM_DISCONNECT");
        context.registerReceiver(serialPortReceiver, mIntentFilter7);
//
        log.d(TAG, "init: 易触BLL初始化结束");
    }



    /**
     * 串口
     */
    public class SerialPortReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.e(TAG,"SerialPortReceiver-->onReceive:收到串口异常广播");
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(OdooAction.BLL_SERIAL_ERROR_TO_UI));
        }
    }




    @Override
    public boolean isCanOutgoods(int localCash, int productPrice) {
        return true;
    }

    @Override
    public void lightButton() {

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
        ArrayList<BLLStackProduct> arryDrink = new ArrayList<>();
        ArrayList<BLLStackProduct> bsps = new ArrayList<>();
        for (BLLStackProduct bsp : mBLLStackProducts) {//先出饮料柜
            if (VMCContoller.getInstance().getStockByRoad(bsp.getBoxNoInt(), bsp.getStackNoInt()) <= 0) {
                continue;
            }
            if (bsp.box_no == 11) {
                if (bsp.quantity > 1) {//预留一瓶
                    arryDrink.add(bsp);
                }
            }


            if (bsp.box_no == 9) {
                if (bsp.quantity > 0)
                    bsps.add(bsp);
            }

        }

        if (arryDrink.size() > 0) {
            Random rd = new Random();
            int drinkNumber = rd.nextInt(arryDrink.size());
            return arryDrink.get(drinkNumber);
        }
        if (bsps.size() == 0) {
            return null;
        }
        Random random = new Random();
        int randomProductNumbler = random.nextInt(bsps.size());//随机数
        return bsps.get(randomProductNumbler);


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
            int sdkcount = VMCContoller.getInstance().getStockByRoad(bsp.getBoxNoInt(), bsp.getStackNoInt());
            log.i(TAG, "getSaleableStackProductByProductCount: 货柜:" + bsp.box_no + " 货道:" + bsp.stack_no + " 库存:" + bsp.quantity + " 销售状态:" + (sdkcount > 0 ? "正常" : "禁卖")+" 商品名称:"+bsp.name);
            if (sdkcount <= 0) {
                continue;
            }

            if (bsp.box_no == 11) {//饮料柜预留一瓶
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
        if (errorCode == 0 || errorCode == 2) {//当出货成功和出货失败原因是光感检测不到的时候扣库存
            BLLProductUtils.writeDownProductStock(box_no, road_no, context);
        }
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

    @Override
    public void updateStackProduct(OdooProductList productList, Context context) {
        synchronized (BLLProductUtils.sBLLLock) {
            if (BLLProductUtils.sBLLProductsByRoadMap.size() == 0) {
                BLLProductUtils.initProductFromSP(context);
            }
            ArrayList<BLLProduct> changePriceProducts = new ArrayList<>();
            for (OdooProduct mOdooproduct : productList.records) {
                BLLProduct bp = BLLProductUtils.sBLLProductMap.get(mOdooproduct.id);//获取对应的product
                if (bp == null) {//如果获取不到，表示新增了一个
                    log.w(TAG, "updateStackProduct: 不能直接新增商品,跳过更新商品" + mOdooproduct.name);
                    return;
                }
                /*赋值*/
                bp.name = mOdooproduct.name;

                if (bp.price != mOdooproduct.price) {//如果当前价格不与服务器相同
                    log.i(TAG, "updateStackProduct: 更改商品价格:" + bp.name + ";原价:" + bp.price + ";新价:" + mOdooproduct.price);
                    bp.price = mOdooproduct.price;
                    changePriceProducts.add(bp);
                }
                bp.net_weight = mOdooproduct.net_weight;
                bp.image_url = mOdooproduct.image_url;
                bp.product_details_image_url = mOdooproduct.product_details_image_url;

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

            if (changePriceProducts.size() > 0) {
                log.i(TAG, "updateStackProduct: 发现价格改动,重新写入机器");
                BLLController.getInstance()
                             .setProducts(BLLProductUtils.makeVmcProductList(changePriceProducts));//重新写入
            }
            BLLProductUtils.saveProductsToSP(context);
        }


    }

    @Override
    public void syncStackSaleableState() {
        //no use
    }

    @Override
    public boolean isCardBan() {
        //no use
        return false;
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
            BLLController.getInstance().setProducts(BLLProductUtils.makeVmcProductListformSupply(changeList));
            return;
        }

        BLLProductUtils.stockSync(context, list);

        this.spList = null;//清空
    }





}
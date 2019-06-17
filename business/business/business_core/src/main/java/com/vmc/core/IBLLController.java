package com.vmc.core;


import android.content.Context;

import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.model.product.OdooProductList;
import com.vmc.core.model.product.SupplyProductList;

import java.util.List;

import vmc.machine.core.model.VMCStackProduct;

public interface IBLLController extends OdooAction {


    /**
     * 获取机器状态
     *
     * @return
     */
    String getVmcRunningStates();

    /**
     * 设置货道
     *
     * @param products
     */
    void setProducts(List<VMCStackProduct> products);


    /**
     * 出货 根据料道商品出货
     *
     * @param bsp
     */
    void outGoods(BLLStackProduct bsp);


    /**
     * 现金出货
     *
     * @param boxId
     * @param roadId
     * @param price
     */
    void outGoodsByCash(int boxId, int roadId, int price);


    /**
     * 在线出货
     */
    void outGoodsOnLine();

    /**
     * 注册接收VMC广播
     *
     * @param context
     */
    void init(Context context);

    /**
     * 重置出货
     */
    void restOutGoods();

    /**
     * 选中需要提货的商品
     *
     * @param bllStackProduct
     * @param context
     */
    void selectVerifyProduct(BLLStackProduct bllStackProduct, Context context);

    /**
     * 向UI发送出货状态
     *
     * @param context
     * @param boxId
     * @param roadId
     * @param shipping_status
     */
    void sendOutGoodsStatus(Context context, int boxId, int roadId, boolean shipping_status);

    /**
     * 准备出货前的一些初始化
     *
     * @param product_id
     */
    void prepareOut(int product_id);

    /**
     * 选中需要出货的商品
     *
     * @param context
     * @param bsp
     */
    void selectProduct(Context context, BLLStackProduct bsp);


    /**
     * 取消选中出货的商品
     */
    void cancelSelectProduct();

    /**
     * 人民币出货
     *
     * @param context
     */
    void requestRMB(Context context);




    /**
     * 水神
     *
     * @return
     */
    boolean isWatergodGenerator();


    /**
     * 水神
     *
     * @return
     */
    boolean isWatergodElectrolyte();


    /**
     * 初始化状态
     *
     * @return
     */
    int getInitState();

    /**
     * 通过商品获取可销售的料道
     *
     * @param product
     *
     * @return
     */
    BLLStackProduct getSaleableStackProductByProduct(BLLProduct product);


    /**
     * 通过商品可销售的库存
     *
     * @param product
     *
     * @return
     */
    int getSaleableStackProductByProductCount(BLLProduct product);


    /**
     * 判断网络状态
     *
     * @param context
     *
     * @return
     */
    boolean isNetState(Context context);


    /**
     * 更新商品列表
     *
     * @param productList
     * @param context
     */
    void updateStackProduct(OdooProductList productList, Context context);

    /**
     * 同步料到
     */
    void syncStackSaleableState();


    /**
     * 取消交易
     */
    void cancelDeal();

    /**
     * 是否是卡禁用
     *
     * @return
     */
    boolean isCardBan();

    /**
     * 取消订单
     *
     * @param context
     */
    void cancelOrder(Context context);



    /**
     * 判断能否取消订单
     *
     */
    boolean canCancelOrder();


    /**
     * 获取当前金额
     *
     * @return
     */
    int getLocalMoney();


    void stockSyncToVMC(Context context, SupplyProductList list);


    /**
     * 提货码验证
     * @param mInputString
     * @param mContext
     */
    void  verifyPickUpGoodsCode(String mInputString, Context mContext);


    /**
     * 获取选中的商品
     * @return
     */
    BLLProduct getSelectProduct();


    boolean  isDoorOpen();

    boolean isDriveError();



}

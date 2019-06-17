package com.vmc.core;

import android.content.Context;

import com.vmc.core.model.config.ConfigInit;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.model.product.OdooProductList;
import com.vmc.core.model.product.SupplyProductList;
import com.vmc.core.utils.ConfigUtils;

import java.util.List;

import vmc.machine.core.VMCContoller;
import vmc.machine.core.model.VMCStackProduct;

/**
 * <b>Create Date:</b>2017/3/28 16:18<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class BLLController implements IBLLController {

    private static volatile BLLController INSTANCE;
    private static final Object sLock = new Object();
    private IBLLController mController;

    private BLLController() {

    }

    /**
     * 单例模式  获取对象
     *
     * @return
     */
    public static BLLController getInstance() {
        if (null == INSTANCE) {
                synchronized (sLock) {
                if (null == INSTANCE) {
                    INSTANCE = new BLLController();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 设置控制器
     *
     * @param controller
     */
    public void setController(IBLLController controller) {
        this.mController = controller;
    }


    @Override
    public String getVmcRunningStates() {
        return mController.getVmcRunningStates();
    }

    @Override
    public void setProducts(List<VMCStackProduct> products) {
        mController.setProducts(products);
    }

    @Override
    public void outGoods(BLLStackProduct bsp) {
        mController.outGoods(bsp);

    }

    @Override
    public void outGoodsByCash(int boxId, int roadId, int price) {
        mController.outGoodsByCash(boxId, roadId, price);
    }

    @Override
    public void outGoodsOnLine() {
        mController.outGoodsOnLine();

    }

    @Override
    public void init(Context context) {
        mController.init(context);

    }

    @Override
    public void restOutGoods() {
        mController.restOutGoods();

    }

    @Override
    public void selectVerifyProduct(BLLStackProduct bllStackProduct, Context context) {
        mController.selectVerifyProduct(bllStackProduct, context);
    }

    @Override
    public void sendOutGoodsStatus(Context context,
                                   int boxId,
                                   int roadId,
                                   boolean shipping_status) {
        mController.sendOutGoodsStatus(context, boxId, roadId, shipping_status);
    }

    @Override
    public void prepareOut(int product_id) {

        mController.prepareOut(product_id);

    }

    @Override
    public void selectProduct(Context context, BLLStackProduct bllStackProduct) {
        mController.selectProduct(context, bllStackProduct);
    }

    @Override
    public void cancelSelectProduct() {
        mController.cancelSelectProduct();
    }

    @Override
    public void requestRMB(Context context) {
        mController.requestRMB(context);
    }




    @Override
    public boolean isWatergodGenerator() {
        return mController.isWatergodGenerator();
    }

    @Override
    public boolean isWatergodElectrolyte() {
        return mController.isWatergodElectrolyte();
    }

    @Override
    public int getInitState() {
        return mController.getInitState();
    }

    @Override
    public BLLStackProduct getSaleableStackProductByProduct(BLLProduct product) {
        return mController.getSaleableStackProductByProduct(product);
    }

    @Override
    public int getSaleableStackProductByProductCount(BLLProduct product) {
        return mController.getSaleableStackProductByProductCount(product);
    }

    @Override
    public boolean isNetState(Context context) {
        return mController.isNetState(context);
    }

    @Override
    public void updateStackProduct(OdooProductList productList, Context context) {
        mController.updateStackProduct(productList, context);
    }

    @Override
    public synchronized void syncStackSaleableState() {
        mController.syncStackSaleableState();
    }

    @Override
    public void cancelDeal() {
        mController.cancelDeal();
    }

    @Override
    public boolean isCardBan() {
        return mController.isCardBan();

    }

    @Override
    public void cancelOrder(Context context) {
        mController.cancelOrder(context);
    }

    @Override
    public boolean canCancelOrder() {

        return   mController.canCancelOrder();
    }

    @Override
    public int getLocalMoney() {

        return mController.getLocalMoney();
    }

    @Override
    public void stockSyncToVMC(Context context, SupplyProductList list) {
        mController.stockSyncToVMC(context, list);
    }

    @Override
    public void verifyPickUpGoodsCode(String mInputString, Context mContext) {
        mController.verifyPickUpGoodsCode(mInputString, mContext);
    }

    @Override
    public BLLProduct getSelectProduct() {
        return mController.getSelectProduct();
    }

    @Override
    public boolean isDoorOpen() {
        return mController.isDoorOpen();
    }

    @Override
    public boolean isDriveError() {
        return mController.isDriveError();
    }






}
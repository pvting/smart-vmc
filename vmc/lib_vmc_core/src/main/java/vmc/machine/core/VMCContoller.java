package vmc.machine.core;

import android.content.Context;

import java.util.List;

import vmc.machine.core.model.VMCStackProduct;

/**
 * <b>Create Date:</b> 8/20/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class VMCContoller implements IVMCController {

    private static VMCContoller INSTANCE;
    private static final Object sLock = new Object();

    private IVMCController mController;

    private VMCContoller() {
        // hide
    }

    public static VMCContoller getInstance() {
        if (null == INSTANCE) {
            synchronized (sLock) {
                if (null == INSTANCE) {
                    INSTANCE = new VMCContoller();
                }
            }
        }
        return INSTANCE;
    }

    private void checkInit() {
        if (null == mController) {
            throw new RuntimeException("You must call setController() method first");
        }

    }

    public void setController(IVMCController controller) {
        this.mController = controller;
    }

    @Override
    public void init(Context context) {
        checkInit();
        mController.init(context);
    }

    @Override
    public int outGoods(int boxId,int roadId) {
        return mController.outGoods(boxId,roadId);
    }

    @Override
    public int outGoodsByCash(int box,int road, int price) {
        return mController.outGoodsByCash(box,road, price);
    }

    @Override
    public String getVendingMachineId() {
        return mController.getVendingMachineId();
    }

    @Override
    public int setVendingMachineId(String machineId) {
       return mController.setVendingMachineId(machineId);
    }

    @Override
    public void setProduct(int boxId, int roadId, String productId, int count, int price) {
        mController.setProduct(boxId, roadId, productId, count, price);
    }

    @Override
    public void setProducts(List<VMCStackProduct> list) {
        mController.setProducts(list);
    }


    @Override
    public String getVmcRunningStates() {
        return mController.getVmcRunningStates();
    }

    @Override
    public int getStockByRoad(int box,int road) {
        return mController.getStockByRoad(box,road);
    }

    @Override
    public void cashInit() {
        mController.cashInit();
    }

    @Override
    public void cashFinish() {
        mController.cashFinish();
    }

    @Override
    public int getProcessIdByRealId(int realId) {
        return mController.getProcessIdByRealId(realId);
    }

    @Override
    public void selectProduct(int box,int roadId) {
        mController.selectProduct(box,roadId);
    }

    @Override
    public void cancelDeal() {
        mController.cancelDeal();
    }

    @Override
    public boolean isConnectError() {
        return mController.isConnectError();
    }

    @Override
    public void outGoodsOneStep(int roadId, OnOutGoodsOK onOutGoodsOK) {
        mController.outGoodsOneStep(roadId, onOutGoodsOK);
    }

    @Override
    public int setFlowController(int waterFlow) {
        return mController.setFlowController(waterFlow);
    }

    @Override
    public int setMaxLitreAndTime(int Litre, int waterTime) {
        return mController.setMaxLitreAndTime(Litre,waterTime);
    }

    @Override
    public int setPumpTime(int pumpTime) {
        return mController.setPumpTime(pumpTime);
    }

    @Override
    public String getBrand() {
        return mController.getBrand();
    }

    @Override
    public boolean isLackOf50Cent() {
        return mController.isLackOf50Cent();
    }

    @Override
    public boolean isLackOf100Cent() {
        return mController.isLackOf100Cent();
    }

    @Override
    public boolean isDoorOpen() {
        return mController.isDoorOpen();
    }

    @Override
    public boolean isDriveError() {
        return mController.isDriveError();
    }

//    @Override
//    public void forceRoad() {
//        mController.forceRoad();
//    }


}

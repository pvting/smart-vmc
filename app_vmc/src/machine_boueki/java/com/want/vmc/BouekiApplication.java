package com.want.vmc;

import android.content.Context;

import com.vmc.core.BLLController;
import com.vmc.core.utils.BLLPayMentController;
import com.vmc.core.utils.VmcBLLPayMentControllerImpl;
import com.vmc.core.worker.ads.AdsDownloadWorker;
import com.vmc.core.worker.ads.AdsUpdaterWorker;
import com.vmc.core.worker.config.ConfigWorker;
import com.vmc.core.worker.logger.LoggerSyncWorker;
import com.vmc.core.worker.machine.SerialSynWorker;
import com.vmc.core.worker.machine.StatusSynWorker;
import com.vmc.core.worker.order.OrderSyncWorker;
import com.vmc.core.worker.product.ProductUpdateWorker;
import com.vmc.core.worker.product.PromotionStatusWorker;
import com.vmc.core.worker.product.PromotionUpdateWorker;
import com.vmc.kubota.KubotaController;

import vmc.core.log;
import vmc.machine.core.VMCContoller;
import vmc.machine.impl.boueki.BouekiControllerImpl;
import vmc.vendor.VApplication;

/**
 * <b>Create Date:</b> 9/8/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 易触机型
 * <br>
 */
public class BouekiApplication extends VApplication {

    @Override
    protected void onInitMachine(Context context) {

        BLLPayMentController bllPayMentController = BLLPayMentController.getInstance();
        bllPayMentController.setController(new VmcBLLPayMentControllerImpl());


        log.d(TAG,"onInitMachine: 等待机器初始化...");
        BLLController bllController =   BLLController.getInstance();
        bllController.setController(new KubotaController());
        BLLController.getInstance().init(this);


        VMCContoller contoller = VMCContoller.getInstance();
        contoller.setController(new BouekiControllerImpl());
        contoller.init(context);
    }

    @Override
    public void startLooperWork() {
        // 启动订单上报
        log.d(TAG, "onInit: 启动订单上报服务");
        OrderSyncWorker.getInstance(this).startWork();

        // 启动库存上报
        log.d(TAG, "onInit: 启动状态上报服务");
        StatusSynWorker.getInstance(this).startWork();

        // 启动广告下载服务
        log.d(TAG, "onInit: 启动广告下载服务");
        AdsDownloadWorker.getInstance(this).startWork();

        // 启动广告更新
        log.d(TAG, "onInit: 启动广告更新服务");
        AdsUpdaterWorker.getInstance(this).startWork();

        // 启动商品更新服务
        log.d(TAG, "onInit: 启动产品更新服务");
        ProductUpdateWorker.getInstance(this).startWork();

        //启动日志上传服务
        log.d(TAG, "onInit: 启动日志上传服务");
        LoggerSyncWorker.getInstance(this).startWork();

        log.d(TAG, "onInit: 启动配置参数获取");
        ConfigWorker.getInstance(this).startWork();

        log.d(TAG, "onInit: 启动促销获取");
        PromotionUpdateWorker.getInstance(this).startWork();

        log.d(TAG, "onInit: 启动促销状态获取");
        PromotionStatusWorker.getInstance(this).startWork();

        log.d(TAG, "onInit:串口状态获取");
        SerialSynWorker.getInstance(this).startWork();
    }
}

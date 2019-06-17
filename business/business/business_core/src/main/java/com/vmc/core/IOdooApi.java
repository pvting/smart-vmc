package com.vmc.core;


import com.vmc.core.model.OdooMessage;
import com.vmc.core.model.ads.AdList;
import com.vmc.core.model.config.ConfigInit;
import com.vmc.core.model.init.MachineInit;
import com.vmc.core.model.instruct.InstructList;
import com.vmc.core.model.pay.PayStatusResult;
import com.vmc.core.model.pay.QRCodeResult;
import com.vmc.core.model.pickup.PickCreateList;
import com.vmc.core.model.product.DeliverProduct;
import com.vmc.core.model.product.OdooProductList;
import com.vmc.core.model.product.OdooPromotionList;
import com.vmc.core.model.product.OdooStockList;
import com.vmc.core.model.product.PickUpProduct;
import com.vmc.core.model.product.ProductList;
import com.vmc.core.model.product.RestVerify;
import com.vmc.core.model.replenishment.FinancialTake;
import com.vmc.core.model.replenishment.ReplenishmentList;
import com.vmc.core.model.replenishment.StackSyncList;
import com.vmc.core.model.user.Mission;
import com.vmc.core.request.ads.AdsListRequest;
import com.vmc.core.request.config.ConfigRequest;
import com.vmc.core.request.init.InitRequest;
import com.vmc.core.request.instruct.InstructRequest;
import com.vmc.core.request.instruct.InstructUpdateRequest;
import com.vmc.core.request.order.OrderSyncRequest;
import com.vmc.core.request.pay.CardPayStatus;
import com.vmc.core.request.pay.PayRequest;
import com.vmc.core.request.pay.PayStatusRequest;
import com.vmc.core.request.pickup.PickCompleteRequest;
import com.vmc.core.request.pickup.PickCreateRequest;
import com.vmc.core.request.product.ProductListRequest;
import com.vmc.core.request.refund.RefundCompleteRequest;
import com.vmc.core.request.refund.RefundRequest;
import com.vmc.core.request.replenishment.FinancialSupplyRequest;
import com.vmc.core.request.replenishment.FinancialTakeConfirmRequest;
import com.vmc.core.request.replenishment.FinancialTakeRequest;
import com.vmc.core.request.replenishment.RecordRequest;
import com.vmc.core.request.replenishment.StackSyncRequest;
import com.vmc.core.request.replenishment.VmcSyncRequest;
import com.vmc.core.request.stock.StockSyncRequest;

/**
 * <b>Project:</b> Odoo<br>
 * <b>Create Date:</b> 15/12/29<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public interface IOdooApi extends odoo.core.IOdooApi {

    /**
     * 机器初始化
     *
     * @param request
     * @param callback
     */
    void init(InitRequest request,  OdooHttpCallback<MachineInit> callback);


    /**
     * 获取广告列表
     *
     * @param request
     * @param callback
     */
    void adList(AdsListRequest request,  OdooHttpCallback<AdList> callback);


    /**
     * 订单上报
     * @param request
     * @param callback
     */
    void orderSync(OrderSyncRequest request,  OdooHttpCallback<OdooMessage> callback);

    /**
     * 状态上报
     * @param request
     * @param callback
     */
    void statusSync(StockSyncRequest request,  OdooHttpCallback<OdooMessage> callback);



    /**
     * 提货码接口
     * @param pickgoods_code 提货码
     * @param callback
     */
    void goodsPicked(String pickgoods_code, OdooHttpCallback<PickUpProduct> callback);


    /**
     * 重置提货码
     * @param pickgoods_code 提货码
     * @param callback
     */
    void resetVmcPickGoodCode(String pickgoods_code, OdooHttpCallback<RestVerify> callback);


    /**
     * 配置参数接口
     * @param request
     * @param callback
     */
    void initConfig(ConfigRequest request, OdooHttpCallback<ConfigInit> callback);


    /**
     * 获取指令集
     */
    void instructGather(InstructRequest request, OdooHttpCallback<InstructList> callback);

    /**
     * 通知服务器更改状态
     *
     * @param callback
     */
    void updateInstructStatus(InstructUpdateRequest request, OdooHttpCallback<OdooMessage> callback);



    /**
     * 请求支付二维码
     *
     * @param callback
     */
    void payRequest(PayRequest request, OdooHttpCallback<QRCodeResult> callback);


    /**
     * 请求支付结果
     *
     * @param callback
     */
    void payStatus(PayStatusRequest request, OdooHttpCallback<PayStatusResult> callback);


    /**
     * 商品基本信息列表
     *
     * @param callback
     */
    void stackProductList(OdooHttpCallback<OdooProductList> callback);


    /**
     * 获取库存
     *
     * @param callback
     */
    void productStockList(OdooHttpCallback<OdooStockList> callback);

    /**
     * 获取促销
     *
     * @param callback
     */
    void promotionList(OdooHttpCallback<OdooPromotionList> callback);



    /**
     * 水神卡支付
     *
     * @param callback
     */
    void requestCard(PayRequest request, OdooHttpCallback<CardPayStatus> callback);


}

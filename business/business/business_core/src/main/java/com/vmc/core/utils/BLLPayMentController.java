package com.vmc.core.utils;


import android.content.Context;

import com.vmc.core.request.pay.PayRequest;

/**
 * <b>Create Date:</b>2017/3/31 17:13<br>
 * <b>Author:</b>LiDuo<br>
 * <b>Description: 控制器接口实现</b> <br>
 */
public class BLLPayMentController implements IBLLPayMentController {

    private static BLLPayMentController instance;

    private IBLLPayMentController mController;

    private BLLPayMentController() {

    }

    public synchronized static BLLPayMentController getInstance() {
        if (null == instance) {

            instance = new BLLPayMentController();

        }
        return instance;
    }


    public BLLPayMentController setController(IBLLPayMentController controller) {
        this.mController = controller;
        return instance;

    }

    /**
     * 生成支付请求
     *
     * @param product_id
     * @return
     */
    @Override
    public void markOrderRequest(int product_id) {
        mController.markOrderRequest(product_id);
    }

    /**
     * 重置网络请求
     */
    @Override
    public void resetPayRequest() {
        mController.resetPayRequest();
    }


    /**
     * 更改支付方式(svm [cardNum为空] 和 水神[cardNum 不为空])
     *
     * @param payType
     */
    @Override
    public void updatePayType(Context context, PayRequest.Payment payType, String cardNum) {
        mController.updatePayType(context, payType, cardNum);
    }


    /**
     * 出货操作
     *
     * @param context
     */
    @Override
    public void outGoods(Context context) {
        mController.outGoods(context);
    }


    /**
     * 请求卡支付 [目前主要是水神]
     *
     * @param context
     */
    @Override
    public void requestCardPay(final Context context) {
        mController.requestCardPay(context);
    }


    /**
     * 请求支付状态
     */
    public void startRequestPayStatus(final Context context) {
        mController.startRequestPayStatus(context);
    }

    /**
     * 查询支付结果
     */
    @Override
    public void requestQuery(final Context context) {
        mController.requestQuery(context);
    }


    /**
     * 请求支付
     *
     * @param mContext
     */
    @Override
    public void requestPay(final Context mContext) {
        mController.requestPay(mContext);
    }

    /**
     * 发送创建二维码广播
     *
     * @param context
     * @param type    1 表示 成功  2 表示失败  3，表示网络错误
     * @param qrStr   二维码
     */
    @Override
    public void sendCreateImageBroadcast(Context context,
                                         PayRequest request,
                                         int type,
                                         String qrStr) {
        mController.sendCreateImageBroadcast(context,
                request,
                type,
                qrStr);
    }

    /**
     * 停止轮训
     *
     */
    @Override
    public void onStopTimer() {
        mController.onStopTimer();
    }

    @Override
    public boolean isLooperPayStatus() {
        return mController.isLooperPayStatus();
    }
}
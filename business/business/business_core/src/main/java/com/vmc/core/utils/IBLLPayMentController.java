package com.vmc.core.utils;


import android.content.Context;

import com.vmc.core.request.pay.PayRequest;

/**
 * <b>Create Date:</b>2017/3/31 17:13<br>
 * <b>Author:</b>LiDuo<br>
 * <b>Description: 控制器接口</b> <br>
 */
public interface IBLLPayMentController {

    String TAG = "IBLLPayMentController";

    /**
     * 生成支付请求
     *
     * @param product_id
     * @return
     */
    void markOrderRequest(int product_id);

    /**
     * 重置网络请求
     */
    void resetPayRequest();


    /**
     * 更改支付方式(svm [cardNum为空] 和 水神[cardNum 不为空])
     *
     * @param payType
     */
    void updatePayType(Context context, PayRequest.Payment payType, String cardNum);


    /**
     * 出货操作
     *
     * @param context
     */
    void outGoods(Context context);


    /**
     * 请求卡支付 [目前主要是水神]
     *
     * @param context
     */
    void requestCardPay(final Context context);


    /**
     * 请求支付状态
     */
    void startRequestPayStatus(final Context context);

    /**
     * 查询支付结果
     */
    void requestQuery(final Context context);


    /**
     * 请求支付
     *
     * @param mContext
     */
    void requestPay(final Context mContext);

    /**
     * 发送创建二维码广播
     *
     * @param context
     * @param type    1 表示 成功  2 表示失败  3，表示网络错误
     * @param qrStr   二维码
     */
    void sendCreateImageBroadcast(Context context,
                                  PayRequest request,
                                  int type,
                                  String qrStr);
    /**
     * 停止轮训
     */
    void onStopTimer();


   boolean isLooperPayStatus();

}
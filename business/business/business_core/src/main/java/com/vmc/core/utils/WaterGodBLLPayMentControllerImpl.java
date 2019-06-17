package com.vmc.core.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.vmc.core.BLLController;
import com.vmc.core.Odoo;
import com.vmc.core.OdooAction;
import com.vmc.core.OdooHttpCallback;
import com.vmc.core.model.order.Order;
import com.vmc.core.model.pay.PayStatusResult;
import com.vmc.core.model.pay.QRCodeResult;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.request.pay.CardPayStatus;
import com.vmc.core.request.pay.PayRequest;
import com.vmc.core.request.pay.PayStatusRequest;
import com.want.base.http.error.HttpError;

import vmc.core.log;


/**
 * <b>Create Date:</b>2017/3/31 17:13<br>
 * <b>Author:</b>LiDuo<br>
 * <b>Description: 水神控制实现类</b> <br>
 */
public class WaterGodBLLPayMentControllerImpl implements IBLLPayMentController {

    /** 付款请求类 */
    private PayRequest mCurrentRequest;
    private BLLProduct sbllProduct;
    /** 支付标记和状态变量 */
    private boolean mIsReqAlipay = false;
    private boolean mIsReqWechat = false;
    private boolean mIsReqWangBi = false;
    private boolean isRequestPayStatus;

    /**
     * 生成支付请求
     *
     * @param product_id
     *
     * @return
     */
    @Override
    public void markOrderRequest(int product_id) {
        log.i(TAG, "markOrderRequest: 生成订单请求开始");
        mCurrentRequest = new PayRequest();
        mCurrentRequest.order_id = BLLOrderUtils.getCurrentOrder().id;
        sbllProduct = BLLProductUtils.getProductById(product_id);
        mCurrentRequest.total_amount = sbllProduct.price;
        log.i(TAG, "markOrderRequest: 生成订单请求结束");
    }

    /**
     * 重置网络请求
     */
    @Override
    public void resetPayRequest() {
        log.i(TAG, "重置网络请求");
        mCurrentRequest = null;
        sbllProduct = null;
        Constants.mAliPayQr = "";
        Constants.mWeChatQr = "";
        Constants.mWangBiQr = "";
        mIsReqAlipay = false;
        mIsReqWechat = false;
        mIsReqWangBi = false;
    }


    /**
     * 更改支付方式(svm [cardNum为空] 和 水神[cardNum 不为空])
     *
     * @param payType
     */
    @Override
    public void updatePayType(Context context, PayRequest.Payment payType, String cardNum) {
        if (mCurrentRequest == null) {
            log.e(TAG, "请求参数为空");
            return;
        }
        log.i(TAG, "updatePayType:" + payType.getPayment());
        mCurrentRequest.payment_type = payType.getPayment();

        if (sbllProduct != null && mCurrentRequest != null) {

            mCurrentRequest.total_amount = sbllProduct.getPromotionPirce(payType.toString()) / 100.00D;

            BLLOrderUtils.updateOrderPrice(sbllProduct.getPromotionPirce(payType.toString()));

            BLLOrderUtils.updateOrderPromotion(sbllProduct.getPromotionId(payType.toString()), "-1", "-1");
        }


        if (payType == PayRequest.Payment.ALIPAY) {

            //支付宝 设置本地订单的支付方式
            BLLOrderUtils.updateOrderPaymentMethod(Order.Payment.ALIPAY);


            if (mCurrentRequest.total_amount == 0) {
                outGoods(context);
                return;
            }


            if (TextUtils.isEmpty(Constants.mAliPayQr)) {
                log.i(TAG, "onPaymentChanged: 支付宝二维码为空");
                if (!mIsReqAlipay) {
                    //未请求中
                    log.i(TAG, "onPaymentChanged: 支付宝未启动支付请求");
                    requestPay(context);
                } else {
                    log.i(TAG, "onPaymentChanged: 支付宝正在支付请求");
                }

            } else {
                log.i(TAG, "onPaymentChanged: 支付宝二维码获取成功");
                sendCreateImageBroadcast(context, mCurrentRequest, 1, Constants.mAliPayQr);
            }
        } else if (payType == PayRequest.Payment.WECHATPAY) {

            //微信支付
            BLLOrderUtils.updateOrderPaymentMethod(Order.Payment.WECHATPAY);

            if (mCurrentRequest.total_amount == 0) {
                outGoods(context);
                return;
            }


            if (TextUtils.isEmpty(Constants.mWeChatQr)) {
                log.i(TAG, "onPaymentChanged: 微信二维码为空");
                if (!mIsReqWechat) {
                    requestPay(context);
                } else {
                    log.i(TAG, "onPaymentChanged: 微信正在支付请求");
                }
            } else {
                sendCreateImageBroadcast(context, mCurrentRequest, 1, Constants.mWeChatQr);
                log.i(TAG, "onPaymentChanged: 微信二维码获取成功");
            }
        } else if (payType == PayRequest.Payment.CARD_WATERGOD) {
            //支付宝 设置本地订单的支付方式
            BLLOrderUtils.updateOrderPaymentMethod(Order.Payment.CARD);
            if (mCurrentRequest.total_amount == 0) {
                outGoods(context);
                return;
            }


            mCurrentRequest.card_number = cardNum;
            requestCardPay(context);

        } else if (payType == PayRequest.Payment.WANGBI) {


            //旺币
            BLLOrderUtils.updateOrderPaymentMethod(Order.Payment.WANGBI);


            if (mCurrentRequest.total_amount == 0) {
                outGoods(context);
                return;
            }


            if (TextUtils.isEmpty(Constants.mWangBiQr)) {
                log.i(TAG, "onPaymentChanged: 旺币二维码为空");
                if (!mIsReqWangBi) {
                    //未请求旺币支付
                    log.i(TAG, "onPaymentChanged: 旺币未启动支付请求");
                    requestPay(context);
                } else {
                    log.i(TAG, "onPaymentChanged: 旺币正在支付请求");
                }

            } else {
                log.i(TAG, "onPaymentChanged: 旺币二维码获取成功");
                sendCreateImageBroadcast(context, mCurrentRequest, 1, Constants.mWangBiQr);
            }
        }
    }


    /**
     * 出货操作
     *
     * @param context
     */
    @Override
    public void outGoods(Context context) {
        BLLOrderUtils.updateOrderPayStatus(Order.PayStatus.PAID);
        BLLOrderUtils.updateOrderStatus(Order.Status.PAID);
        //发送支付成功广播
        Intent it = new Intent(OdooAction.BLL_PAY_STATUS_TO_UI);
        it.putExtra("PayStatus", true);
        LocalBroadcastManager.getInstance(context).sendBroadcast(it);

        //准备出货
        BLLController.getInstance().outGoodsOnLine();

        //停止轮询
        onStopTimer();
    }


    /**
     * 请求卡支付 [目前主要是水神]
     *
     * @param context
     */
    @Override
    public void requestCardPay(final Context context) {
        final PayRequest request = mCurrentRequest;

        if (request == null) {
            log.e(TAG, "请求参数为空");
            return;
        }

        Odoo.getInstance(context).requestCard(request, new OdooHttpCallback<CardPayStatus>(context) {
            @Override
            public void onSuccess(CardPayStatus result) {

                BLLOrderUtils.updateOrderPayStatus(Order.PayStatus.PAID);
                BLLOrderUtils.updateOrderStatus(Order.Status.PAID);
                BLLOrderUtils.updateOrderPaymentMethod(Order.Payment.CARD);
                log.i(TAG, "requestCard:onSuccess: 支付成功");

                BLLController.getInstance().outGoodsOnLine();
                //发送支付成功广播
                Intent it = new Intent(BLL_PAY_STATUS_TO_UI);
                it.putExtra("PayStatus", true);//支付状态
                it.putExtra("message", result.msg);//支付成功
                it.putExtra("remainMoney", result.remain_money);//余额
                log.e(TAG, "remainMoney：" + result.remain_money);
                LocalBroadcastManager.getInstance(context).sendBroadcast(it);


            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);

                log.e(TAG, "requestCard:onSuccess: 失败。" + error.toString());

                Intent it = new Intent(BLL_PAY_STATUS_TO_UI);
                it.putExtra("PayStatus", false);
                it.putExtra("message", error.getMessage());//返回消息
                it.putExtra("remainMoney", 0);//余额
                LocalBroadcastManager.getInstance(context).sendBroadcast(it);


            }
        });

    }


    /**
     * 请求支付状态
     */
    @Override
    public void startRequestPayStatus(final Context context) {

        if (isRequestPayStatus) {//如果开启了轮询，则不需要开启
            return;
        }
        if (context == null || ((Activity) context).isFinishing()) {
            onStopTimer();
            return;
        }
        isRequestPayStatus = true;
        requestQuery(context);
    }

    public void requestPayStatusLooper(final Context context) {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestQuery(context);
            }
        }, 1500);


    }


    /**
     * 查询支付结果
     */
    @Override
    public void requestQuery(final Context context) {

        if (context == null || ((Activity) context).isFinishing()) {
            log.e(TAG, "UI页面关闭");
            onStopTimer();
            return;
        }

        if (BLLOrderUtils.getCurrentOrder() == null) {
            log.e(TAG, "没有创建订单");
            onStopTimer();
            return;

        }
        log.i(TAG, "requestQuery: 开始请求支付状态");
        PayStatusRequest queryRequest = new PayStatusRequest();
        queryRequest.order_id = BLLOrderUtils.getCurrentOrder().id;

        Odoo.getInstance(context).payStatus(queryRequest, new OdooHttpCallback<PayStatusResult>(context) {
            @Override
            public void onSuccess(PayStatusResult result) {
                log.i(TAG, "requestQuery--> onSuccess: " + result);
                if (result.order_status == 1) {
                    BLLOrderUtils.updateOrderPayStatus(Order.PayStatus.PAID);
                    BLLOrderUtils.updateOrderStatus(Order.Status.PAID);
                    if (null != result && null != result.payment_type) {
                        if (result.payment_type.equals("alipay")) {
                            BLLOrderUtils.updateOrderPaymentMethod(Order.Payment.ALIPAY);
                        } else if (result.payment_type.equals("weixinpay")) {
                            BLLOrderUtils.updateOrderPaymentMethod(Order.Payment.WECHATPAY);
                        } else if (result.payment_type.equals("wangbipay")) {
                            BLLOrderUtils.updateOrderPaymentMethod(Order.Payment.WANGBI);
                        }
                    }

                    log.i(TAG, "requestQuery--> onSuccess: 支付成功。");

                    //发送支付成功广播
                    outGoods(context);
                } else {
                    requestPayStatusLooper(context);
                }
            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);
                requestPayStatusLooper(context);
                log.e(TAG, "requestQuery--> onError: 查询支付失败。");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                log.i(TAG, "requestQuery: 结束请求支付状态");
            }
        });
    }


    /**
     * 请求支付
     *
     * @param mContext
     */
    @Override
    public void requestPay(final Context mContext) {
        final PayRequest request = mCurrentRequest;

        if (request == null) {
            return;
        }

        log.i(TAG, "requestPay" + request.payment_type + ": begin");
        if (request.payment_type.equals(PayRequest.Payment.ALIPAY.getPayment())) {
            mIsReqAlipay = true;
        } else if (request.payment_type.equals(PayRequest.Payment.WECHATPAY.getPayment())) {
            mIsReqWechat = true;
        } else if (request.payment_type.equals(PayRequest.Payment.WANGBI.getPayment())) {
            mIsReqWangBi = true;
        }
        Odoo.getInstance(mContext).payRequest(request, new OdooHttpCallback<QRCodeResult>(mContext) {
            @Override
            public void onSuccess(QRCodeResult result) {
                if (null == request) {
                    log.e(TAG, "请求已经取消");
                    return;
                }
                if (null != result) {
                    if (!TextUtils.isEmpty(result.result)) {
                        if (result.result.equals("SUCCESS")) {
                            log.i(TAG, "requestPay--> onSuccess: 请求成功二维码:" + result.code_url);
                            if (request.payment_type.equals(PayRequest.Payment.ALIPAY.getPayment())) {
                                Constants.mAliPayQr = result.code_url;
                                sendCreateImageBroadcast(mContext, request, 1, Constants.mAliPayQr);
                            } else if (request.payment_type.equals(PayRequest.Payment.WECHATPAY.getPayment())) {
                                Constants.mWeChatQr = result.code_url;
                                sendCreateImageBroadcast(mContext, request, 1, Constants.mWeChatQr);
                            } else if (request.payment_type.equals(PayRequest.Payment.WANGBI.getPayment())) {
                                Constants.mWangBiQr = result.code_url;
                                sendCreateImageBroadcast(mContext, request, 1, Constants.mWangBiQr);
                            }
                        } else {
                            //请求失败
                            if (!TextUtils.isEmpty(result.error)) {
                                log.e(TAG, "requestPay--> onSuccess:" + result.error);
                            }

                            BLLOrderUtils.updateOrderPayStatus(Order.PayStatus.UNPAY);
                            BLLOrderUtils.updateOrderStatus(Order.Status.CANCEL);
                            sendCreateImageBroadcast(mContext, request, 2, "");

                        }
                    }
                } else {
                    log.e(TAG, "requestPay: 二维码获取异常");
                }
            }

            @Override
            public void onError(HttpError error) {
                super.onError(error);
                sendCreateImageBroadcast(mContext, request, 3, "");
                log.e(TAG, "requestPay:" + error.getMessage());

            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (request.payment_type.equals(PayRequest.Payment.ALIPAY.getPayment())) {
                    mIsReqAlipay = false;
                } else if (request.payment_type.equals(PayRequest.Payment.WECHATPAY.getPayment())) {
                    mIsReqWechat = false;
                } else if (request.payment_type.equals(PayRequest.Payment.WANGBI.getPayment())) {
                    mIsReqWangBi = false;
                }
            }
        });
        log.i(TAG, "requestPay: 二维码请求结束");
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

        if (mCurrentRequest == null) {
            log.e(TAG, "请求已经取消");
            return;
        }
        Intent intent = new Intent(OdooAction.BLL_CREATE_IMAGE_TO_UI);
        intent.putExtra("result", type);
        intent.putExtra("qrStr", qrStr);
        if (mCurrentRequest.payment_type.equals(PayRequest.Payment.ALIPAY.getPayment())) {
            intent.putExtra("paymentType", 1);
        } else if (mCurrentRequest.payment_type.equals(PayRequest.Payment.WECHATPAY.getPayment())) {
            intent.putExtra("paymentType", 2);
        } else if (mCurrentRequest.payment_type.equals(PayRequest.Payment.CARD_WATERGOD.getPayment())) {
            intent.putExtra("paymentType", 3);
        } else if (mCurrentRequest.payment_type.equals(PayRequest.Payment.WANGBI.getPayment())) {
            intent.putExtra("paymentType", 4);
        } else {
            intent.putExtra("paymentType", 0);
        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    /**
     * 停止轮询
     */
    @Override
    public void onStopTimer() {
        isRequestPayStatus = false;
    }

    @Override
    public boolean isLooperPayStatus() {
        return isRequestPayStatus;
    }

}
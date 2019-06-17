package com.want.vendor.uitls;
/*
    ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ 
       Author   :  lixiaodaoaaa
       Date     :  2017/9/18
       Time     :  11:12
    ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
 */

import android.content.Context;

import com.vmc.core.model.order.Order;
import com.vmc.core.utils.ConfigUtils;

public class PayStatusUtils {


    /**
     * 根据 payType 判断是不是线上支付
     *
     * @return True 是线上支付  False 不是线上支付
     */
    private static boolean isOnlinePay(String payType) {
        if (null == payType) {
            return false;
        }

        if (payType.equals(Order.Payment.ALIPAY) || payType.equals(Order.Payment.WECHATPAY.getPayment())) {
            return true;
        }
        return false;
    }


    /**
     * 是否显示自动退款 文案
     *
     * @return
     */
    public static boolean isShowAutoRefundTips(Context context, String payType) {
        //如果不是在线支付 直接不用显示
        if (!isOnlinePay(payType)) {
            return false;
        }

        boolean alipay_refund = ConfigUtils.getConfig(context).alipay_refund;
        boolean weixinpay_refund = ConfigUtils.getConfig(context).weixinpay_refund;

        if (isWeixinPay(payType)) {
            return weixinpay_refund;
        }
        if (isApliPay(payType)) {
            return alipay_refund;
        }

        return false;
    }


    private static boolean isWeixinPay(String payType) {
        return payType.equals(Order.Payment.WECHATPAY.getPayment());
    }

    private static boolean isApliPay(String payType) {
        return payType.equals(Order.Payment.ALIPAY.getPayment());
    }
}

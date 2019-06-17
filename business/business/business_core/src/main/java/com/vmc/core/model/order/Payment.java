package com.vmc.core.model.order;

/**
 * Created by huyunqiang on 2016/11/29.
 */

public enum Payment {
    /** 未指定*/
    NONE("NONE"),
    /** 支付宝*/
    ALIPAY("ALIPAY"),
    /** 微信*/
    WECHATPAY("WECHATPAY"),
    /** 人民币*/
    RMB("RMB"),
    /** 旺币*/
    WANGBI("WANGBI"),

    /** 提货码 */
    CODE("CODE");

    String payment;

    Payment(){}
    Payment(String payment) {
        this.payment = payment;
    }

    public String getPayment() {
        return this.payment;
    }

    public static Payment paymentOf(String payment) {
        if (ALIPAY.payment.equals(payment)) {
            return ALIPAY;
        } else if (WECHATPAY.payment.equals(payment)) {
            return WECHATPAY;
        } else if (RMB.payment.equals(payment)) {
            return RMB;
        } else if (WANGBI.payment.equals(payment)) {
            return WANGBI;
        }else if (CODE.payment.equals(payment)) {
            return CODE;
        }

        return NONE;
    }
}

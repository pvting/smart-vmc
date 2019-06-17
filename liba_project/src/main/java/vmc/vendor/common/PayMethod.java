package vmc.vendor.common;

/**
 * <b>Create Date:</b> 07/11/2016<br>
 * <b>Author:</b> Wisn<br>
 * <b>Description:</b> <br>
 */
public enum PayMethod {
    //没有支付方式
    NOPAYMENT,

    //微信支付
    WECHAT,

    //支付宝
    ALIPAY,

    //旺币支付
    WANGBI,

    //现金支付
    CASHPAYMENT,

    //支付成功
    PAYSUCCESS,

    //支付失败
    PAYFAILED,

    //出货成功
    OUTGOODSSUCCESS,

    //出货失败
    OUTGOODSFAILED,

    //获取二维码时 网络错误
    NETWORKERROR;
}

package com.want.vendor.product.paysuccess.help;

/**
 * *************************************
 * Created by lixiaodaoaaa on 2017/8/4. |
 * *************出货状态页**************  |
 * ************************************ |
 */


// 0:出货成功  1:出货失败 2:赠品出货失败  3: 提货码出货失败  4:出货超时  5:赠品出货超时  6:提货码出货超时
public interface OutStatus {

    //"出货成功";
    String OUT_SUCCESS = "0";

    //"出货失败\n请按下方退币杆退币!" or "出货失败";
    String OUT_FAILER = "1";

    //"赠品出货失败";
    String OUT_EXTRA_FAILER = "2";

    //"出货失败\n提货码仍有效，请稍后重试！";
    String OUT_CODE_FAILER = "3";

    //"出货超时";
    String OUT_TIME_OUT = "4";

    //"出货超时";
    String OU_EXTRA_TIME_OUT = "5";

    // "提货码出货超时\n提货码仍有效，请稍后重试！";
    String OUT_CODE_TIME_OUT = "6";

}

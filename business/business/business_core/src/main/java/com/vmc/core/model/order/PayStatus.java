package com.vmc.core.model.order;

/**
 * Created by huyunqiang on 2016/11/29.
 */

public enum PayStatus {
    /** 已支付*/
    PAID("PAID"),
    /** 未支付*/
    UNPAY("UNPAY");

    private String status;

    PayStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public static PayStatus statusOf(String status) {
        if (PAID.status.equals(status)) {
            return PAID;
        } else if (UNPAY.status.equals(status)) {
            return UNPAY;
        }
        return UNPAY;
    }
}


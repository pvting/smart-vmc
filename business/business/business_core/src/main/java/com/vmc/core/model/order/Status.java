package com.vmc.core.model.order;

/**
 * Created by huyunqiang on 2016/11/29.
 */

public enum Status {
    /** 已创建*/
    CREATED("CREATED"),
    /** 用户取消*/
    CANCEL("CANCEL"),
    /** 已支付*/
    PAID("PAID"),
    /** 已完成*/
    FINISHED("FINISHED");

    private String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public static Status statusOf(String status) {
        if (CREATED.status.equals(status)) {
            return CREATED;
        } else if (CANCEL.status.equals(status)) {
            return CANCEL;
        } else if (PAID.status.equals(status)) {
            return PAID;
        } else if (FINISHED.status.equals(status)) {
            return FINISHED;
        }
        return CREATED;
    }
}

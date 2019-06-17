package com.vmc.core.request.refund;

import com.vmc.core.request.BaseRequest;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/20<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 */

public class RefundRequest extends BaseRequest {
    public int uid;
    public int status;// 0为提货历史；1为还货；2为还货历史
    public RefundRequest() {}

    public RefundRequest(int uid, int status) {
        this.uid = uid;
        this.status = status;
    }
}

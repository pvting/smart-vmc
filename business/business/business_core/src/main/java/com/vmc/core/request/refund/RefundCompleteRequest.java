package com.vmc.core.request.refund;

import com.vmc.core.model.pickup.PickCreate;
import com.vmc.core.request.BaseRequest;

import java.util.List;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/21<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 */

public class RefundCompleteRequest extends BaseRequest {
    public List<PickCreate> mRefundCompleteList;

    public RefundCompleteRequest(List<PickCreate> refundCompleteList) {
        mRefundCompleteList = refundCompleteList;
    }
}

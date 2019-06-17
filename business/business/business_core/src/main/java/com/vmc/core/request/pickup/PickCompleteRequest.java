package com.vmc.core.request.pickup;

import com.vmc.core.model.pickup.PickCreate;
import com.vmc.core.request.BaseRequest;

import java.util.List;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/19<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 * 提货完成请求
 */

public class PickCompleteRequest extends BaseRequest {

    public List<PickCreate> mProductsList;
    public List<PickCreate> mSupplysList;
    public int uid;

    public PickCompleteRequest() {}

    public PickCompleteRequest(List<PickCreate> mProductsList, List<PickCreate> mSupplysList, int uid) {
        this.mProductsList = mProductsList;
        this.mSupplysList = mSupplysList;
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "PickCompleteRequest{}";
    }
}

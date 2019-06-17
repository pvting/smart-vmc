package com.vmc.core.request.pickup;

import com.vmc.core.model.pickup.PickCreate;
import com.vmc.core.request.BaseRequest;

import java.util.List;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/19<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 * 请求提货单Request
 */

public class PickCreateRequest extends BaseRequest {
    public List<PickCreate> mPickCreateList;

    public PickCreateRequest() {
    }

    public PickCreateRequest(List<PickCreate> pickCreateList) {
        mPickCreateList = pickCreateList;
    }

    @Override
    public String toString() {
        return "PickCreateRequest{" +
               "mProductsList=" + mPickCreateList +
               '}';
    }
}

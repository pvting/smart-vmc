package com.vmc.core.request.replenishment;

import com.vmc.core.request.BaseRequest;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/13<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 * 售货机同步完成
 */

public class VmcSyncRequest extends BaseRequest {

    public int sync_id;

    public VmcSyncRequest(int sync_id) {
        this.sync_id = sync_id;
    }

    @Override
    public String toString() {
        return "VmcSyncRequest{" +
                "sync_id=" + sync_id +
                '}';
    }
}

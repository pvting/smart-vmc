package com.vmc.core.request.replenishment;

import com.vmc.core.request.BaseRequest;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/20<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 */

public class FinancialTakeConfirmRequest extends BaseRequest {

    public int machine_id;
    public String machine_coin;
    public int uid;

    public FinancialTakeConfirmRequest() {}

    public FinancialTakeConfirmRequest(int machine_id, String machine_coin, int uid) {
        this.machine_id = machine_id;
        this.machine_coin = machine_coin;
        this.uid = uid;
    }
}

package com.vmc.core.request.replenishment;

import com.vmc.core.request.BaseRequest;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/20<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 * 补硬币请求
 */

public class FinancialSupplyRequest extends BaseRequest {

    public int machine_id;
    public int uid;
    public String machine_coin;
    public String portable_coin;

    public FinancialSupplyRequest() {}

    public FinancialSupplyRequest(int machine_id, int uid, String machine_coin, String portable_coin) {
        this.machine_id = machine_id;
        this.uid = uid;
        this.machine_coin = machine_coin;
        this.portable_coin = portable_coin;
    }
}

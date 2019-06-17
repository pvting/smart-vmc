package com.vmc.core.request.replenishment;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/20<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 * 请求提款金额
 */

public class FinancialTakeRequest {

    public int machine_id;

    public FinancialTakeRequest(int machine_id) {
        this.machine_id = machine_id;
    }

    public FinancialTakeRequest() {}
}

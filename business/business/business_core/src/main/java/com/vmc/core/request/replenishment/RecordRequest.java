package com.vmc.core.request.replenishment;

import com.vmc.core.model.machine.Machine;
import com.vmc.core.model.replenishment.Records;
import com.vmc.core.request.BaseRequest;

import java.util.List;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/10<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 */

public class RecordRequest extends BaseRequest {

    public List<Records> recordsList;
    public Machine machine;
    public int supply_id;
    public int uid = -1;
    public int supply_status;//补货单状态，0: 已确认；1: 已提货；2: 补货历史

    public RecordRequest() {
    }

    public RecordRequest(int uid) {
        this.uid = uid;
    }

    public RecordRequest(int uid, int supply_status) {
        this.uid = uid;
        this.supply_status = supply_status;
    }

    public RecordRequest(List<Records> recordsList, Machine machine, int supply_id) {
        this.recordsList = recordsList;
        this.machine = machine;
        this.supply_id = supply_id;
    }

    public RecordRequest(List<Records> recordsList, Machine machine, int uid, int supply_id) {
        this.recordsList = recordsList;
        this.machine = machine;
        this.supply_id = supply_id;
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "RecordRequest{}";
    }
}

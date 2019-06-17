package com.vmc.core.request.stock;

import com.vmc.core.model.machine.Machine;
import com.vmc.core.model.stock.Stock;
import com.vmc.core.request.BaseRequest;

import java.util.List;

/**
 * <b>Create Date:</b> 8/29/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class StockSyncRequest extends BaseRequest {

    public List<Stock> stocks;
    public Machine machine;

    public StockSyncRequest(Machine machine,List<Stock> stocks) {
        this.stocks = stocks;
        this.machine = machine;
    }

    @Override
    public String toString() {
        return "StockSyncRequest{" +
               " machine=" + machine +
               '}';
    }
}

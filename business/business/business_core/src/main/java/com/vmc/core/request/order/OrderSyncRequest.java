package com.vmc.core.request.order;

import com.vmc.core.model.order.Order;
import com.vmc.core.request.BaseRequest;

/**
 * <b>Create Date:</b> 8/26/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class OrderSyncRequest extends BaseRequest {
    public Order order;

    public OrderSyncRequest(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "OrderSyncRequest{" +
               "order=" + order +
               '}';
    }
}

package com.vmc.core.request.init;

import com.vmc.core.request.BaseRequest;

/**
 * <b>Create Date:</b> 8/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class InitRequest extends BaseRequest {

    /** 机器唯一编码 */
    public String factory_code;

    @Override
    public String toString() {
        return "InitRequest{" +
               "factory_code='" + factory_code + '\'' +
               '}';
    }
}

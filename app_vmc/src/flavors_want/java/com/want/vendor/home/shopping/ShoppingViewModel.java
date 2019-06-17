package com.want.vendor.home.shopping;

import android.content.Context;
import android.view.View;

import com.vmc.core.BLLController;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.base.sdk.utils.ActivityUtils;
import com.want.vendor.product.list.ProductListActivity;
import com.want.vendor.tips.serialporterror.SerialPortErrorActivity;

import vmc.machine.core.VMCContoller;
import vmc.vendor.utils.SerialPortUtils;

/**
 * ViewModel Stub.
 */
public class ShoppingViewModel extends AbsViewModel {

    public ShoppingViewModel() {

    }

    public ShoppingViewModel(Context context) {
        super(context);
    }


    /**
     * 接受串口异常发送的广播，并进行响应的处理。
     * 取出保存的共享参数进行比较
     */
    public void onClick(View view) {
        if (BLLController.getInstance().getInitState() != 1){
            return;
        }
        final Context context = ActivityUtils.getActivity(view);
        if (SerialPortUtils.isError(context) || VMCContoller.getInstance().isConnectError()) {
            SerialPortErrorActivity.start(context);
        } else {
            ProductListActivity.start(context);
        }
    }

}

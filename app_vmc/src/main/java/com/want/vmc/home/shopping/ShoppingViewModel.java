package com.want.vmc.home.shopping;

import android.content.Context;
import android.view.View;

import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.base.sdk.utils.ActivityUtils;
import com.want.vmc.serialporterror.SerialPortErrorActivity;

import vmc.vendor.utils.IntentHelper;
import vmc.vendor.utils.SerialPortUtils;

/**
 * <b>Create Date:</b> 10/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
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
        final Context context = ActivityUtils.getActivity(view);
        if (SerialPortUtils.isError(context)) {
            SerialPortErrorActivity.start(context);
        } else {
            IntentHelper.startProductList(context);
        }
    }
}

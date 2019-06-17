package com.want.vendor.home.surprise;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.vmc.core.BLLController;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.base.sdk.utils.ActivityUtils;
import com.want.vendor.deliver.DeliverActivity;

import vmc.machine.core.VMCContoller;
import vmc.vendor.utils.SerialPortUtils;

/**
 * ViewModel Stub.
 */
public class SurpriseViewModel extends AbsViewModel {

    public SurpriseViewModel() {

    }

    public SurpriseViewModel(Context context) {
        super(context);
    }

    public void onClick(View v) {

        if (BLLController.getInstance().getInitState() != 1){
            return;
        }


        final Activity activity = ActivityUtils.getActivity(v);
        if (SerialPortUtils.isError(activity) || VMCContoller.getInstance().isConnectError()) {
            com.want.vendor.tips.surprisingserialport.SurprisingErrorPortActivity.start(v.getContext());
        } else {
            // 跳转到提货码
            DeliverActivity.start(activity);
        }
    }
}

package com.want.vendor.tips.surprisingserialport;

import com.vmc.core.utils.ConfigUtils;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.vmc.core.Constants;

import android.content.Context;
import android.databinding.Bindable;
import android.os.Handler;
import android.text.TextUtils;

import vmc.core.log;

/**
 * ViewModel Stub.
 */
public class SurprisingErrorPortViewModel extends AbsViewModel {

    private Context mContext;
    private static final String TAG = "SurprisingErrorPortViewModel";

    public SurprisingErrorPortViewModel(){}

    public SurprisingErrorPortViewModel(Context context) {
        super(context);
        mContext=context;

    }

    /**
     * 获取客服电话
     *
     * @return
     */
    @Bindable
    public String getConfigNum() {
        if (!TextUtils.isEmpty(ConfigUtils.getConfig(mContext).customer_phone)) {
            log.v(TAG, "客服电话:" + ConfigUtils.getConfig(mContext).customer_phone);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    notifyPropertyChanged(BR.configNum);
                    notifyChange();
                }
            }, 1 * Constants.Time.MINUTE_1);
            return  ConfigUtils.getConfig(mContext).customer_phone;
        } else {
            log.v(TAG, "服务电话获取失败:" + ConfigUtils.getConfig(mContext).customer_phone);
            return "";
        }
    }

}

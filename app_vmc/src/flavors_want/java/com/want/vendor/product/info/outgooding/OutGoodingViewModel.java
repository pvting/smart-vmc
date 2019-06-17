package com.want.vendor.product.info.outgooding;

import android.content.Context;

import com.vmc.core.model.product.BLLProduct;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;


/**
 * <b>Create Date:</b> 2016/11/22<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public class OutGoodingViewModel extends AbsViewModel {
    private static final String TAG = "OutGoodingViewModel";
    private BLLProduct mProduct;
    private Context mContext;

    public OutGoodingViewModel(Context context, OutGoodingContract.View view) {
        super(context);
        this.mContext = context;
    }

    public void init() {

    }


    /**
     * 跟随页面销毁操作
     */
    public void onDestory() {
    }
}
package com.want.vendor.deliver.success;

import android.app.Activity;
import android.content.Context;
import android.databinding.Bindable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.vmc.core.model.product.DeliverProduct;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.base.sdk.utils.ActivityUtils;
import com.want.vmc.R;
import com.want.vendor.deliver.DeliverActivity;
import com.want.vendor.tips.GuideProblemCodeActivity;

/**
 * ViewModel Stub.
 */
public class SuccessViewModel extends AbsViewModel implements View.OnClickListener{
    private static final int DEFAULT_TIMELEFT = 60;
    private SuccessContract.Presenter mPresenter;
    private boolean isCanIntent = true;

    private DeliverProduct mProduct;

    public SuccessViewModel(Context context) {
        super(context);
    }

    public SuccessViewModel(DeliverProduct product, SuccessContract.Presenter presenter) {
        this.mProduct = product;
        this.mPresenter = presenter;
    }

    @Bindable
    public String getProductName() {
        return null != mProduct ? mProduct.name : "";
    }

    @Bindable
    // FIXME: 2016/11/22 重新命名
    public String getNet(){
        if (null==mProduct){
            return "";

        }
        return mProduct.net_weight;
    }


    @Bindable
    public String getUnit(){
        if (null==mProduct){
            return "";

        }
        return mProduct.unit;

    }
    @Bindable
    public String getQuantity(){
        if (null==mProduct){
            return "0";

        }
        return  String.valueOf(mProduct.product_quantity);
    }

    @Bindable
    public String getImageUrl(){
        if (null==mProduct){
            return "";

        }
        return  mProduct.image_url;
    }

    @Override
    public void onClick(View v) {

        final int id = v.getId();
        //重新跳转到输入 提货码界面
        if (R.id.vendor_order_success_continue== id) {

            final Activity activity = ActivityUtils.getActivity(v);
            DeliverActivity.start(activity);
            activity.finish();
        } else if (R.id.vendor_order_success_heliping == id) {
            if (isCanIntent){
                final Activity activity = ActivityUtils.getActivity(v);
                GuideProblemCodeActivity.start(activity);
            }
            isCanIntent = false;
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            isCanIntent = true;
                        }
                    }, 500);
        }
    }

}

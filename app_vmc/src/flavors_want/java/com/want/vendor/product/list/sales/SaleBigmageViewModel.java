package com.want.vendor.product.list.sales;

import android.content.Context;
import android.databinding.Bindable;

import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.core.log.lg;
import com.want.vmc.R;

import vmc.core.log;


/**
 * ViewModel Stub.
 */
public class SaleBigmageViewModel extends AbsViewModel {
    private static final String TAG = "SaleBigmageViewModel";
    public String mImageURL;

    public SaleBigmageViewModel(String imageURL) {
        this.mImageURL = imageURL;

    }

    public SaleBigmageViewModel(Context context) {
        super(context);
    }

    // TODO

    @Bindable
    public String getImageUrl() {



        return mImageURL;
    }

    @Bindable
    public int getImageLoading(){
        return R.drawable.vendor_home_bigimage_loading;
//        return R.drawable.vendor_home_bigads_popup_frame;
    }

    @Bindable
    public int getImageError(){
        return R.drawable.vendor_home_bigimage_loading;
//        return R.drawable.vendor_home_bigads_popup_frame;
    }
}

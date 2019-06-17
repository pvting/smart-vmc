package com.want.vendor.home.bigimgads;

import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.core.log.lg;
import com.want.vmc.R;

import android.content.Context;
import android.databinding.Bindable;

import vmc.core.log;


/**
 * ViewModel Stub.
 */
public class BigImageViewModel extends AbsViewModel {
    private static final String TAG = "BigImageViewModel";
    public String mImageURL;

    public BigImageViewModel(String imageURL) {
        this.mImageURL = imageURL;

    }

    public BigImageViewModel(Context context) {
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
    }

    @Bindable
    public int getImageError(){
        return R.drawable.vendor_home_bigimage_loading;
    }
}

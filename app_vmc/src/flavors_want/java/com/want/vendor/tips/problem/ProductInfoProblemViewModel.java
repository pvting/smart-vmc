package com.want.vendor.tips.problem;

import android.content.Context;
import android.databinding.Bindable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vmc.core.utils.ConfigUtils;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.vmc.R;

import vmc.core.log;


/**
 * ViewModel Stub.
 */
public class ProductInfoProblemViewModel extends AbsViewModel {

    private static final String TAG = "ProductInfoProblemViewModel";
    private Context mContext;
    private String hint;

    ProductInfoProblemViewModel(Context context, ImageView imageView, int type) {
        this.mContext = context;
        String imageUrl = ConfigUtils.getConfig(mContext).img_url;
        Glide.with(context).load(imageUrl).error(R.drawable.vendor_guide_problem_error).centerCrop().diskCacheStrategy(
                DiskCacheStrategy.ALL).crossFade().into(imageView);

        switch (type){
            case 1:
                hint = context.getString(R.string.vendor_guide_product_info_has_coin_problem);

                break;
            case 2:
                hint = context.getString(R.string.vendor_guide_product_info_no_coin_problem);

                break;
            case 3:
                hint = context.getString(R.string.vendor_guide_product_info_no_coin_problem);

                break;

            case 4:
                hint = context.getString(R.string.vendor_guide_deliver_info_problem);
                break;

            default:
                hint = context.getString(R.string.vendor_guide_product_info_no_coin_problem);
                break;
        }



    }

@Bindable
    public String getHint() {
        return hint;
    }


    /**
     * 获取客服电话
     *
     * @return
     */
    @Bindable
    public String getCustomerTelphone() {
        if (!TextUtils.isEmpty(ConfigUtils.getConfig(mContext).customer_phone)) {
            log.d(TAG, "getCustomerTelphone: 客服电话:" + ConfigUtils.getConfig(mContext).customer_phone);
            return ConfigUtils.getConfig(mContext).customer_phone;
        } else {
            log.d(TAG, "getCustomerTelphone: 客服电话获取失败");
            return "";
        }
    }

    /**
     * 是否显示"或拨打"
     *
     * @return
     */
    @Bindable
    public int getTelphoneVisible() {
        if (!TextUtils.isEmpty(ConfigUtils.getConfig(mContext).customer_phone)) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

}

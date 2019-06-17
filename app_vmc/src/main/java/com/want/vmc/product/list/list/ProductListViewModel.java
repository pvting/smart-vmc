package com.want.vmc.product.list.list;

import android.app.Activity;
import android.content.Context;
import android.databinding.Bindable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vmc.core.BLLController;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.utils.BLLProductUtils;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.base.sdk.utils.ActivityUtils;
import com.want.vmc.R;
import com.want.vmc.serialporterror.SerialPortErrorActivity;

import vmc.vendor.utils.IntentHelper;
import vmc.vendor.utils.SerialPortUtils;

/**
 * <b>Create Date:</b> 10/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class ProductListViewModel extends AbsViewModel {

    protected BLLProduct mProduct;
    private Context context;
    private Toast mToast;
    /** 自定义textView */
    private TextView tvToastText;

    public ProductListViewModel(Context context, BLLProduct product) {
        this.mProduct = product;
        this.context = context;
    }

    public ProductListViewModel(Context context) {
        super(context);
    }

    @Bindable
    public BLLProduct getProduct() {
        return mProduct;
    }

    /**
     * 获取商品名称
     *
     * @return
     */
    @Bindable
    public String getProductName() {
        if (null != mProduct) {
            return mProduct.name;
        }
        return "";
    }

    @Bindable
    public String getImageUrl() {
        if (null != mProduct) {
            return mProduct.image_url;
        }
        return "";
    }

    @Bindable
    public int getStock() {
        if (null == mProduct) {
            return View.GONE;
        }

        final boolean hasStock = hasStock();

        return hasStock ? View.GONE : View.VISIBLE;
    }

    protected boolean hasStock() {
        return null != mProduct &&
               0 < BLLController.getInstance().getSaleableStackProductByProductCount(mProduct);
    }

    @Bindable
    public String getPrice() {
        String priceStr;
        int price = mProduct.price;
        if (price <= 0) {
            priceStr = "0.00";
        } else {
            int yuan = price / 100;
            int change = price % 100;
            String changeStr = "00" + change;
            changeStr = changeStr.substring(changeStr.length() - 2, changeStr.length());
            priceStr = yuan + "." + changeStr;
        }
        return priceStr;
    }

    @Bindable
    public boolean isClickable() {
        return hasStock();
    }


    public void onClickDetails(View view) {
        final Activity activity = ActivityUtils.getActivity(view);
        final BLLProduct product = getProduct();

        if (SerialPortUtils.isError(activity)) {
            SerialPortErrorActivity.start(activity);
        } else {
            BLLStackProduct bsp = BLLController.getInstance().getSaleableStackProductByProduct(product);

            if (bsp == null) {
                toastInfo(context, "商品已售空");
                ActivityUtils.getActivity(view).finish();
                return;
            }

            //生成本地订单
            BLLController.getInstance().selectProduct(context, bsp);

            IntentHelper.startProductInfo(activity, bsp);
        }
    }

    @Bindable
    public int getSalesPromotionShow() {
        if (mProduct.mPromotionDetail != null) {

            if (mProduct.mPromotionDetail.promotion_type.equals("one_more")) {
                BLLStackProduct promotionStackProduct = BLLProductUtils.getPromotionStackProduct(mProduct.product_id);
                if (null==promotionStackProduct) {//如果没有赠品
                    return View.GONE;
                }
            }
            return View.VISIBLE;
        }
        return View.GONE;


    }


    @Bindable
    public Drawable getPromotionDrawable() {
        if (mProduct.mPromotionDetail != null &&
            mProduct.mPromotionDetail.promotion_id > 0 &&
            mProduct.mPromotionDetail.promotion_type != null) {
            if (mProduct.mPromotionDetail.promotion_type.equals("discount")) {//折扣
                return context.getResources()
                              .getDrawable(R.drawable.vendor_product_sales_promotionicon_discount);

            } else if (mProduct.mPromotionDetail.promotion_type.equals("one_more")) {//买赠
                return context.getResources()
                              .getDrawable(R.drawable.vendor_product_sales_promotionicon_add);
            } else {//立减
                return context.getResources()
                              .getDrawable(R.drawable.vendor_product_sales_promotionicon);
            }
        } else {
            return context.getResources()
                          .getDrawable(R.drawable.vendor_product_sales_promotionicon);
        }
    }


    /**
     * 自定义Toast
     */

    public void toastInfo(Context context, String data) {
        if (mToast == null) {
            mToast = Toast.makeText(context, data, Toast.LENGTH_SHORT);
            View view = View.inflate(context, R.layout.vendor_deliver_custom_toast_layout, null);
            mToast.setView(view);
            tvToastText = (TextView) view.findViewById(R.id.tvToastText);
            tvToastText.setText(data);
        } else {
            tvToastText.setText(data);
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

}

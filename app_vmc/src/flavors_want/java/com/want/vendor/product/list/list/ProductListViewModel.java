package com.want.vendor.product.list.list;

import android.content.Context;
import android.databinding.Bindable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.vmc.core.BLLController;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;
import com.want.base.sdk.utils.ActivityUtils;
import com.want.vendor.product.info.ProductInfoActivity;
import com.want.vendor.tips.serialporterror.SerialPortErrorActivity;

import java.text.DecimalFormat;

import vmc.vendor.utils.SerialPortUtils;

/**
 * <b>Create Date:</b> 10/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 竖版商品列表。
 * <br>
 */
public class ProductListViewModel extends com.want.vmc.product.list.list.ProductListViewModel {

    private boolean canClick = true;

    public ProductListViewModel(Context context, BLLProduct product) {
        super(context, product);
    }

    @Bindable
    @Override
    public BLLProduct getProduct() {
        return super.getProduct();
    }

    @Bindable
    @Override
    public String getImageUrl() {
        return super.getImageUrl();
    }

    @Bindable
    public String getProductName() {
        return super.getProductName();
    }

    @Bindable
    @Override
    public int getStock() {
        return super.getStock();
    }

    @Bindable
    public String getNetWeight() {
        final BLLProduct product = getProduct();
        if (null != product) {
            if (!TextUtils.isEmpty(product.net_weight)) {
                return "净含量：" + product.net_weight;
            }
        }

        return "";
    }

    @Bindable
    @Override
    public int getSalesPromotionShow() {
        return super.getSalesPromotionShow();
    }


    @Bindable
    @Override
    public String getPrice() {
        int price = mProduct.price;
        if (null != mProduct.mPromotionDetail &&
                null != mProduct.mPromotionDetail.promotion_type) {
            //如果是促销
            if (mProduct.mPromotionDetail.promotion_type.equals("discount") ||
                    mProduct.mPromotionDetail.promotion_type.equals("unchange_count")) {
                if (mProduct.mPromotionDetail.promotion_price != 0) {
                    price = mProduct.mPromotionDetail.promotion_price;
                }
            }
        }
        String priceStr;
        if (price <= 0) {
            priceStr = "0.00";
        } else {
            double yuan = price / 100D;
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            priceStr = decimalFormat.format(yuan);
        }
        return "￥ " + priceStr;
    }



    @Bindable
    @Override
    public boolean isClickable() {
        return canClick && super.isClickable();
    }

    @Override
    public void onClickDetails(View view) {

        if (!canClick){
            return;
        }

        final Context context = ActivityUtils.getActivity(view);
        final BLLProduct product = getProduct();
        if (SerialPortUtils.isError(context)) {
            SerialPortErrorActivity.start(context);
        } else {
            canClick = false;
            BLLStackProduct bsp = BLLController.getInstance().getSaleableStackProductByProduct(product);

            if (bsp == null) {
                toastInfo(context.getApplicationContext(), "商品已售空");
                ActivityUtils.getActivity(view).finish();
                return;
            }

            //生成本地订单
            BLLController.getInstance().selectProduct(context, bsp);

            ProductInfoActivity.start(context, bsp);
        }

        // 防止连续快速点击商品列表造成的问题
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                canClick = true;
            }
        }, 300);
    }


    /**
     * @return
     */
    @Bindable
    public String getOlderPriceYuan() {
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
        return "￥ " + priceStr;
    }


    /**
     * 是否显示原价
     *
     * @return
     */
    @Bindable
    public int getShowOlderPrice() {
        //TODO;;
        if (mProduct.mPromotionDetail != null) {//如果是促销
            if (mProduct.mPromotionDetail.promotion_type.equals("discount") ||
                    mProduct.mPromotionDetail.promotion_type.equals("unchange_count")) {
                return View.VISIBLE;
            }
        }
        return View.GONE;
    }


    @Bindable
    @Override
    public Drawable getPromotionDrawable() {
        return super.getPromotionDrawable();
    }
}

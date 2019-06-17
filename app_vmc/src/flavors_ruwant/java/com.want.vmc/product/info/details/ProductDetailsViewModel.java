package com.want.vmc.product.info.details;

import android.content.Context;
import android.databinding.Bindable;
import android.graphics.drawable.Drawable;

import android.view.View;


import com.vmc.core.BLLController;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.FreeBie;
import com.vmc.core.utils.BLLProductUtils;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;

import java.text.DecimalFormat;


import com.want.vmc.R;

/**
 * ViewModel Stub.
 */
public class ProductDetailsViewModel extends AbsViewModel {

    private static final String TAG = ProductDetailsViewModel.class.getSimpleName();
    private BLLProduct mProduct;
    private boolean support = true;
    private Context context;
    private int mPrice;

    public ProductDetailsViewModel(Context context, BLLProduct product) {
        this.mProduct = product;
        this.context = context;
        mPrice = mProduct.price;
    }

    public ProductDetailsViewModel(Context context) {
        super(context);
    }

    @Bindable
    public String getName() {
        return mProduct.name;
    }

    @Bindable
    public String getPrice() {
        return String.valueOf(mProduct.price);
    }

    @Bindable
    public String getImageUrl() {
        return mProduct.image_url;
    }

    /**
     * 买赠，折扣，促销
     * “one_more”、 “discount” 和“unchange_count”
     *
     * @return
     */
    @Bindable
    public String getCompaign() {

        if (!support) {//如果都完成
            return "";
        }

        if (null != mProduct.mPromotionDetail && null != mProduct.mPromotionDetail.promotion_type) {
            if (mProduct.mPromotionDetail!=null) {
                return "活动:" + mProduct.mPromotionDetail.name + "\n" + "此商品参与游戏活动，赢取更多奖品";
            }
            return "";
        }
        return "";
    }

    /**
     * 判断是否有活动
     *
     * @return
     */
    @Bindable
    public int getPromotionVisible() {
        if (!support) {
            return View.GONE;
        }
        if (null != mProduct.mPromotionDetail && null != mProduct.mPromotionDetail.promotion_type) {
            if (mProduct.mPromotionDetail!=null) {
                return View.VISIBLE;
            }

            return View.GONE;
        }
        return View.GONE;
    }

    /**
     * 判断是否有活动
     *
     * @return
     */
    @Bindable
    public int getPromotionInVisible() {
        if (null != mProduct.mPromotionDetail && null != mProduct.mPromotionDetail.promotion_type) {
            if (mProduct.mPromotionDetail!=null) {
                return View.VISIBLE;
            }

            return View.INVISIBLE;
        }
        return View.INVISIBLE;
    }

    /**
     * 判断是否以赠完
     *
     * @return
     */
    @Bindable
    public int getPromotionAdd() {
        if (null != mProduct &&
            null != mProduct.mPromotionDetail &&
            null != mProduct.mPromotionDetail.promotion_type &&
            mProduct.mPromotionDetail.promotion_type.equals("one_more") &&
            support) {
            if (mProduct.mPromotionDetail != null) {
                if (mProduct.mPromotionDetail.freebie.size() > 0) {
                    boolean haveFree = false;
                    for (FreeBie item : mProduct.mPromotionDetail.freebie) {
                        BLLProduct bp = BLLProductUtils.getProductById(item.id);
                        if (bp == null) {
                            continue;
                        }
                        if (bp.product_id==mProduct.product_id){
                            if (BLLController.getInstance().getSaleableStackProductByProductCount(bp) > 1) {
                                haveFree = true;
                                break;
                            }
                        }else{
                            if (BLLController.getInstance().getSaleableStackProductByProductCount(bp) > 0) {
                                haveFree = true;
                                break;
                            }
                        }
                    }
                    if (haveFree) {
                        return View.GONE;
                    } else {
                        return View.VISIBLE;
                    }
                } else {
                    return View.GONE;
                }
            }
            return View.GONE;
        }
        return View.GONE;
    }


    @Bindable
    public Drawable getProType() {
        if (mProduct.mPromotionDetail != null &&
            mProduct.mPromotionDetail.promotion_id > 0 &&
            mProduct.mPromotionDetail.promotion_type != null) {

            if (mProduct.mPromotionDetail.promotion_type.equals("discount")) {//折扣
                return context.getResources().getDrawable(R.drawable.vendor_product_discount_icon);

            } else if (mProduct.mPromotionDetail.promotion_type.equals("one_more")) {//买赠
                return context.getResources().getDrawable(R.drawable.vendor_product_add_icon);

            } else {//立减
                return context.getResources().getDrawable(R.drawable.vendor_product_minus_icon);
            }
        } else {
            return context.getResources().getDrawable(R.drawable.vendor_product_minus_icon);
        }
    }


    /**
     * 获取支付的价格
     *
     * @return
     */
    @Bindable
    public String getPriceYuan() {  int price = mProduct.price;
        if (null != mProduct.mPromotionDetail && null != mProduct.mPromotionDetail.promotion_type && support) {
            if (mProduct.mPromotionDetail!=null) {//如果是促销
                if (mProduct.mPromotionDetail.promotion_type.equals("discount") || mProduct.mPromotionDetail.promotion_type.equals("unchange_count")) {
                    if (mProduct.mPromotionDetail.promotion_price != 0) {
                        price = mProduct.mPromotionDetail.promotion_price;
                    }
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


    /**
     * 获取原价
     *
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
        return "原价￥ " + priceStr;
    }

    /**
     * 是否显示原价
     *
     * @return
     */
    @Bindable
    public int getShowOlderPrice() {
        if (mProduct.mPromotionDetail!=null) {//如果是促销
            if (mProduct.mPromotionDetail.promotion_type.equals("discount") || mProduct.mPromotionDetail.promotion_type.equals("unchange_count")) {
                return View.VISIBLE;
            }
        }
        return View.GONE;
    }


    /**
     * 设置促销的价格
     * @param price 促销的价格
     */
    public void setPromotionPrice(int price) {
        mPrice = price;
    }


    /**
     * 是否支持的支付方式
     *
     * @param support
     */
    public void isSupport(boolean support) {
        this.support = support;
        notifyChange();
    }

}

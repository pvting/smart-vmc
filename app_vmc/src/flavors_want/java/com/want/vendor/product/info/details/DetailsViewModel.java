package com.want.vendor.product.info.details;

import android.content.Context;
import android.databinding.Bindable;
import android.view.View;

import com.vmc.core.BLLController;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.FreeBie;
import com.vmc.core.utils.BLLProductUtils;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;

import java.text.DecimalFormat;

import vmc.core.log;


/**
 * <b>Create Date:</b> 2016/11/14<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public class DetailsViewModel extends AbsViewModel{
    
    private static final String TAG = "DetailsViewModel";
    private BLLProduct mProduct;
    private boolean support = true;
    
    public DetailsViewModel( BLLProduct product ){
        this.mProduct = product;
    }
    

    
    @Bindable
    public String getName( ){
        return mProduct.name;
    }
    
    @Bindable
    public String getNetWeight( ){
        return mProduct.net_weight;
    }
    
    @Bindable
    public String getImageUrl( ){
        return mProduct.image_url;
    }
    
    
    /**
     * 买赠，折扣，促销
     * “one_more”、 “discount” 和“unchange_count”
     *
     * @return
     */
    @Bindable
    public String getCompaign( ){
        
        if (!support) {//如果都完成
            return "";
        }
        
        if (null != mProduct.mPromotionDetail && null != mProduct.mPromotionDetail.promotion_type) {
            return "活动:" + mProduct.mPromotionDetail.name + "\n" + "此商品参与游戏活动，赢取更多奖品";
        }
        return "";
    }
    
    
    /**
     * 判断是否有活动
     *
     * @return
     */
    @Bindable
    public int getPromotionVisible( ){
        if (!support) {
            return View.GONE;
        }
        if (null != mProduct.mPromotionDetail && null != mProduct.mPromotionDetail.promotion_type) {
            log.d(TAG, "有促销活动");
            if (mProduct.mPromotionDetail != null) {
                return View.VISIBLE;
            }
            
            return View.GONE;
        }
        return View.GONE;
    }
    
    /**
     * 获取支付的价格
     *
     * @return
     */
    @Bindable
    public String getPriceYuan( ){
        int price = mProduct.price;
        if (null != mProduct.mPromotionDetail && null != mProduct.mPromotionDetail.promotion_type && support) {
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
    
    /**
     * 获取原价
     *
     * @return
     */
    @Bindable
    public String getOlderPriceYuan( ){
        String priceStr;
        int price = mProduct.price;
        if (price <= 0) {
            priceStr = "0.00";
        } else {
            int yuan = price / 100;
            int change = price % 100;
            String changeStr = "00" + change;
            changeStr = changeStr.substring(changeStr.length( ) - 2, changeStr.length( ));
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
    public int getShowOlderPrice( ){
        if (mProduct.mPromotionDetail != null && support) {//如果是促销
            if (mProduct.mPromotionDetail.promotion_type.equals("discount") ||
                    mProduct.mPromotionDetail.promotion_type.equals("unchange_count")) {
                return View.VISIBLE;
            }
        }
        return View.GONE;
    }
    
    
    /**
     * 是否支持的支付方式
     *
     * @param support
     */
    public void isSupport( boolean support ){
        this.support = support;
        notifyChange( );
    }
    
    
    /**
     * 判断是否以赠完
     *
     * @return
     */
    @Bindable
    public int getPromotionAdd( ){
        if (null != mProduct &&
                null != mProduct.mPromotionDetail &&
                null != mProduct.mPromotionDetail.promotion_type &&
                mProduct.mPromotionDetail.promotion_type.equals("one_more") &&
                support) {
            if (mProduct.mPromotionDetail != null) {
                if (mProduct.mPromotionDetail.freebie.size( ) > 0) {
                    boolean haveFree = false;
                    for (FreeBie item : mProduct.mPromotionDetail.freebie) {
                        BLLProduct bp = BLLProductUtils.getProductById(item.id);
                        if (bp == null) {
                            continue;
                        }
                        if (bp.product_id == mProduct.product_id) {
                            if (BLLController.getInstance( ).getSaleableStackProductByProductCount(bp) > 1) {
                                haveFree = true;
                                break;
                            }
                        } else {
                            if (BLLController.getInstance( ).getSaleableStackProductByProductCount(bp) > 0) {
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
}

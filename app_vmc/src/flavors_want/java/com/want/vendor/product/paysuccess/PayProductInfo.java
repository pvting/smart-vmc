package com.want.vendor.product.paysuccess;

import android.content.Context;

/**
 * 支付成功
 */
public class PayProductInfo {
    private Context context;
    private String tip;
    private String payType;
    private int productId;
    private String order;


    public PayProductInfo(Context context) {
        this.context = context;
    }

    public PayProductInfo(Context context, String tip, String payType, int productId) {
        this(context);
        this.tip = tip;
        this.payType = payType;
        this.productId = productId;
    }

    public PayProductInfo(Context context, String tip, String payType, int productId, String order) {
        this(context, tip, payType, productId);
        this.order = order;
    }


    public void setContext(Context context) {
        this.context = context;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }


    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }


    public Context getContext() {
        return context;
    }

    public String getTip() {
        return tip;
    }

    public String getPayType() {
        return payType;
    }

    public int getProductId() {
        return productId;
    }
}

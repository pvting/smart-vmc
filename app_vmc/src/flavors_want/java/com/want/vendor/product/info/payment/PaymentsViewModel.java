package com.want.vendor.product.info.payment;

import android.content.Context;
import android.databinding.Bindable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.utils.BLLProductUtils;
import com.vmc.core.utils.ConfigUtils;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.vmc.R;

/**
 * <b>Create Date:</b> 2016/11/18<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public class PaymentsViewModel extends AbsViewModel {

    private static final int DELAY_CLICK_TIME = 600;
    private Context mContext;
    private PaymentsContract.View mView;
    private boolean isClick = true;
    private Runnable mRunnable;
    private Handler mHandler;
    protected BLLProduct mProduct;

    public PaymentsViewModel() {
    }

    public PaymentsViewModel(Context context) {
        super(context);
    }


    public PaymentsViewModel(Context context, PaymentsContract.View view) {
        this.mContext = context;
        this.mView = view;
        mHandler = new Handler(Looper.getMainLooper());
        mRunnable = new Runnable() {
            @Override
            public void run() {
                isClick = true;
            }
        };
    }


    public PaymentsViewModel(Context context, BLLProduct mProduct, PaymentsContract.View view) {
        this.mContext = context;
        this.mProduct = mProduct;
        this.mView = view;
        mHandler = new Handler(Looper.getMainLooper());
        mRunnable = new Runnable() {
            @Override
            public void run() {
                isClick = true;
            }
        };
    }


    /**
     * 选择支付方式
     *
     * @param view 选择点击支付方式
     */
    public void onChangePayment(View view) {
        if (isClick) {
            isClick = false;
            mHandler.postDelayed(mRunnable, DELAY_CLICK_TIME);
            final int id = view.getId();
            if (id == R.id.vendor_payment_alipay) {
                mView.setPaymentMethod(1);
            } else if (id == R.id.vendor_payment_wechat) {
                mView.setPaymentMethod(2);
            } else if (id == R.id.vendor_payment_cash) {
                mView.setPaymentMethod(3);
            } else if (id == R.id.vendor_payment_wangbi) {
                mView.setPaymentMethod(4);
            }
        }
    }


    /**
     * 是否支持支付宝支付
     *
     * @return
     */
    public boolean isSupportAlipay() {
        int payment_alipay = ConfigUtils.getConfig(mContext).payment_way.payment_alipay;
        if (payment_alipay == 1) {
            return true;
        }
        return false;
    }

    /**
     * 显示隐藏支付宝支付按钮
     *
     * @param context
     *
     * @return
     */
    public Drawable getAliPay(Context context) {
        int backgroundResid = 0;
        if (isClickableAlipay()) {
            backgroundResid = R.drawable.button_select;
        } else {
            backgroundResid = R.drawable.vendor_payment_alipay_non;
        }
        return context.getResources().getDrawable(backgroundResid);
    }

    /**
     * 是否支持微信支付
     *
     * @return
     */
    public boolean isSupportWechatpay() {
        int payment_weixin = ConfigUtils.getConfig(mContext).payment_way.payment_weixin;
        if (payment_weixin == 1) {
            return true;
        }
        return false;
    }

    /**
     * 显示隐藏微信支付按钮
     *
     * @param context
     *
     * @return
     */
    public Drawable getWechatPay(Context context) {
        int backgroundResid = 0;
        if (isClickableWechatpay()) {
            backgroundResid = R.drawable.button_select_weixin;
        } else {
            backgroundResid = R.drawable.vendor_payment_wechat_non;
        }
        return context.getResources().getDrawable(backgroundResid);
    }

    /**
     * 是否支持旺币支付
     *
     * @return
     */
    public boolean isSupportWangbipay() {
        int payment_wangbi = ConfigUtils.getConfig(mContext).payment_way.payment_wangbi;
        if (payment_wangbi == 1) {
            return true;
        }
        return false;
    }

    /**
     * 显示隐藏旺币支付按钮
     *
     * @param context
     *
     * @return
     */
    public Drawable getWangbiPay(Context context) {
        int backgroundResid = 0;
        if (isClickableWangbipay()) {
            backgroundResid = R.drawable.button_select_wantbi;
        } else {
            backgroundResid = R.drawable.vendor_payment_wangbi_non;
        }
        return context.getResources().getDrawable(backgroundResid);
    }

    /**
     * 是否支付现金支付
     *
     * @return
     */
    public boolean isSupportCashpay() {
        int payment_cash = ConfigUtils.getConfig(mContext).payment_way.payment_cash;
        return payment_cash == 1;
    }

    /**
     * 显示隐藏现金支付按钮
     *
     * @param context
     *
     * @return
     */
    public Drawable getCashPay(Context context) {
        int backgroundResid = 0;
        if (isSupportCashpay()) {
            backgroundResid = R.drawable.button_select_rmb;
        } else {
            backgroundResid = R.drawable.vendor_payment_cash_non;
        }
        return context.getResources().getDrawable(backgroundResid);
    }

    @Bindable
    public boolean isClickableAlipay() {
        if (getNetState(mContext)) {
            if (isSupportAlipay()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Bindable
    public boolean isClickableWechatpay() {
        if (getNetState(mContext)) {
            if (isSupportWechatpay()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Bindable
    public boolean isClickableWangbipay() {
        if (getNetState(mContext)) {
            if (isSupportWangbipay()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Bindable
    public boolean isClickableCashpay() {
        if (isSupportCashpay()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断当前是否联网
     *
     * @param context
     *
     * @return
     */
    private boolean getNetState(Context context) {
        ConnectivityManager
                connectivity =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            if (null != networkInfo && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    //支付宝
    @Bindable
    public Drawable getPromotionDrawableByAlipay() {

        String payment = mProduct.getPromotionTypeByPayment("ALIPAY");

        if (payment == null) {
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon);
        }

        if (payment.equals("discount")) {//折扣
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon_discount);

        } else if (payment.equals("one_more")) {//买赠
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon_add);
        } else {//立减
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon);
        }

    }


    @Bindable
    public int getPromotionByAlipayShow() {

        String payment = mProduct.getPromotionTypeByPayment("ALIPAY");

        if (payment == null) {
            return View.GONE;
        }


        if (mProduct.mPromotionDetail.promotion_type.equals("one_more")) {
            BLLStackProduct promotionStackProduct = BLLProductUtils.getPromotionStackProduct(mProduct.product_id);
            if (null==promotionStackProduct) {//如果没有赠品
                return View.GONE;
            }
        }







        return View.VISIBLE;

    }

    //微信
    @Bindable
    public Drawable getPromotionDrawableByWeixin() {

        String payment = mProduct.getPromotionTypeByPayment("WECHATPAY");

        if (payment == null) {
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon);
        }

        if (payment.equals("discount")) {//折扣
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon_discount);

        } else if (payment.equals("one_more")) {//买赠
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon_add);
        } else {//立减
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon);
        }

    }


    @Bindable
    public int getPromotionByWeixinShow() {

        String payment = mProduct.getPromotionTypeByPayment("WECHATPAY");

        if (payment == null) {
            return View.GONE;
        }

        if (mProduct.mPromotionDetail.promotion_type.equals("one_more")) {
            BLLStackProduct promotionStackProduct = BLLProductUtils.getPromotionStackProduct(mProduct.product_id);
            if (null==promotionStackProduct) {//如果没有赠品
                return View.GONE;
            }
        }



        return View.VISIBLE;

    }

    //现金
    @Bindable
    public Drawable getPromotionDrawableByCash() {

        String payment = mProduct.getPromotionTypeByPayment("RMB");

        if (payment == null) {
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon);
        }

        if (payment.equals("discount")) {//折扣
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon_discount);

        } else if (payment.equals("one_more")) {//买赠
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon_add);
        } else {//立减
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon);
        }

    }


    @Bindable
    public int getPromotionByCashShow() {

        String payment = mProduct.getPromotionTypeByPayment("RMB");

        if (payment == null) {
            return View.GONE;
        }

        if (mProduct.mPromotionDetail.promotion_type.equals("one_more")) {
            BLLStackProduct promotionStackProduct = BLLProductUtils.getPromotionStackProduct(mProduct.product_id);
            if (null==promotionStackProduct) {//如果没有赠品
                return View.GONE;
            }
        }

        return View.VISIBLE;

    }

    //旺币
    @Bindable
    public Drawable getPromotionDrawableByWantbi() {

        String payment = mProduct.getPromotionTypeByPayment("WANGBI");

        if (payment == null) {
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon);
        }

        if (payment.equals("discount")) {//折扣
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon_discount);

        } else if (payment.equals("one_more")) {//买赠
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon_add);
        } else {//立减
            return mContext.getResources()
                           .getDrawable(R.drawable.vendor_product_sales_promotionicon);
        }

    }


    @Bindable
    public int getPromotionByWantbiShow() {

        String payment = mProduct.getPromotionTypeByPayment("WANGBI");

        if (payment == null) {
            return View.GONE;
        }

        if (mProduct.mPromotionDetail.promotion_type.equals("one_more")) {
            BLLStackProduct promotionStackProduct = BLLProductUtils.getPromotionStackProduct(mProduct.product_id);
            if (null==promotionStackProduct) {//如果没有赠品
                return View.GONE;
            }
        }

        return View.VISIBLE;

    }


}

package com.want.vmc.product.info.payment;

import android.content.Context;
import android.databinding.Bindable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.vmc.core.utils.ConfigUtils;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;

import com.want.vmc.R;

/**
 * <b>Create Date:</b> 11/01/16<br>
 * <b>Author:</b> peiweiwei<br>
 * <b>Description:</b> ViewModel stub. <br>
 */
public class PaymentViewModel extends AbsViewModel {

    private Context mContext;
    public PaymentContract.View mView;

    public PaymentViewModel() {
    }

    public PaymentViewModel(Context context) {
        super(context);
    }

    public PaymentViewModel(Context context,PaymentContract.View view){
        this.mContext = context;
        mView = view;
    }

    /**
     * 改变支付方式
     * @param view 支付选择按钮
     */
    public void onChangePayment(View view){
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
            backgroundResid = R.drawable.vendor_payment_alipay_normal;
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
            backgroundResid = R.drawable.vendor_payment_wechat_normal;
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
            backgroundResid = R.drawable.vendor_payment_wangbi_normal;
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
        if (payment_cash == 1) {
            return true;
        }
        return false;
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
            backgroundResid = R.drawable.vendor_payment_cash_normal;
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

}

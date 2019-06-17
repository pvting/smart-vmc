package com.want.vmc.product.info.payment;

import android.content.Context;
import android.databinding.Bindable;
import android.view.View;

import com.want.base.sdk.framework.app.mvp.AbsViewModel;

import com.want.vmc.R;

/**
 * <b>Create Date:</b> 11/02/16<br>
 * <b>Author:</b> peiweiwei<br>
 * <b>Description:</b> ViewModel stub. <br>
 */
public class ReChoosePaymentViewModel extends AbsViewModel {
    private final int ALIPAYMENT = 1;
    private final int WECHATPAYMENT = 2;
    private final int CASHPAYMENT = 3;
    private final int WANGBIPAYMENT = 4;
    public int mImgId;
    public int mPaymentId;
    public int mPaytipId;
    public ReChoosePaymentContract.View mView;

    public ReChoosePaymentViewModel() {

    }

    public ReChoosePaymentViewModel(ReChoosePaymentContract.View view, int method) {
        this.mView = view;
        setMethod(method);
    }

    private void setMethod(int method) {
        if (method == ALIPAYMENT) {
            mImgId = R.drawable.icon_alipay_normal;
            mPaymentId = R.string.pay_alipay;
            mPaytipId = R.string.pay_alipay_tip;
        } else if (method == WECHATPAYMENT) {
            mImgId = R.drawable.icon_wechat_normal;
            mPaymentId = R.string.pay_wechat;
            mPaytipId = R.string.pay_wechat_tip;
        } else if (method == CASHPAYMENT) {
            mImgId = R.drawable.icon_rmb_normal;
            mPaymentId = R.string.pay_rmb;
            mPaytipId = R.string.pay_rmb_tip;
        } else if (method == WANGBIPAYMENT) {
            mImgId = R.drawable.icon_wangbi_normal;
            mPaymentId = R.string.pay_wangbi;
            mPaytipId = R.string.pay_wangbi_tip;
        }
    }

    public void setPaymentMethod(int method) {
        setMethod(method);
        notifyChange();
    }

    @Bindable
    public int getPaymentIcon() {
        return mImgId;
    }

    public String getPaymentDescription(Context context) {
        return context.getString(R.string.product_payment_description,
                                 context.getString(mPaymentId),
                                 context.getString(mPaytipId));
    }

    public void rechoosePayment(View view){
        mView.reSetPaymentMethod();
    }
}

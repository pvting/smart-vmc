package com.want.vendor.product.info.payment;

import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.vmc.R;

import android.content.Context;
import android.databinding.Bindable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

/**
 * <b>Create Date:</b> 2016/11/21<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public class ReSelectPaymentsViewModel extends AbsViewModel {

    private static final int DELAY_CLICK_TIME = 600;
    private final int ALIPAYMENT = 1;
    private final int WECHATPAYMENT = 2;
    private final int CASHPAYMENT = 3;
    private final int WANGBIPAYMENT = 4;
    public int mImgId;
    public int mPaymentId;
    public int mPaytipId;
    private ReSelectPaymentsContract.View mView;
    private  boolean show = true ;
    private boolean isClick = true;
    private Handler mHandler;
    private Runnable mRunnable;

    public ReSelectPaymentsViewModel() {

    }

    public ReSelectPaymentsViewModel(ReSelectPaymentsContract.View view, int method) {
        this.mView = view;
        setMethod(method);
        mHandler = new Handler(Looper.getMainLooper());
        mRunnable = new Runnable() {
            @Override
            public void run() {
                isClick = true;
            }
        };
    }

    private void setMethod(int method) {
        if (method == ALIPAYMENT) {
            mImgId = com.want.vmc.R.drawable.icon_alipay_normal;
            mPaymentId = com.want.vmc.R.string.pay_alipay;
            mPaytipId = com.want.vmc.R.string.pay_alipay_tip;
        } else if (method == WECHATPAYMENT) {
            mImgId = com.want.vmc.R.drawable.icon_wechat_normal;
            mPaymentId = com.want.vmc.R.string.pay_wechat;
            mPaytipId = com.want.vmc.R.string.pay_wechat_tip;
        } else if (method == CASHPAYMENT) {
            mImgId = com.want.vmc.R.drawable.icon_rmb_normal;
            mPaymentId = com.want.vmc.R.string.pay_rmb;
            mPaytipId = com.want.vmc.R.string.pay_rmb_tip;
        } else if (method == WANGBIPAYMENT) {
            mImgId = com.want.vmc.R.drawable.icon_wangbi_normal;
            mPaymentId = com.want.vmc.R.string.pay_wangbi;
            mPaytipId = com.want.vmc.R.string.pay_wangbi_tip;
        }
    }

    public void setPaymentMethod(int method) {
        setMethod(method);
        notifyChange();
    }

    @Bindable
    public int getShowResetBtn(){
        if (!show){

            return View.INVISIBLE;

        }
        return View.VISIBLE;
    }



    public void setShowRest(boolean show){
        this.show = show;
        notifyChange();

    }

    @Bindable
    public int getPaymentIcon() {
        return mImgId;
    }

    public String getPaymentDescription(Context context) {
        return context.getString(com.want.vmc.R.string.product_payment_description,
                                 context.getString(mPaymentId),
                                 context.getString(mPaytipId));
    }

    public void rechoosePayment(View view) {
        if (isClick) {
            isClick = false;
            mHandler.postDelayed(mRunnable, DELAY_CLICK_TIME);
            mView.reSetPaymentsMethod();
        }
    }

    public void rechoosePayment() {
        if (isClick) {
            isClick = false;
            mHandler.postDelayed(mRunnable, DELAY_CLICK_TIME);
            mView.reSetPaymentsMethod();
        }
    }


}

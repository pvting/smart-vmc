package com.want.vendor.product.info.payment;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * <b>Create Date:</b> 2016/11/18<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public class PaymentsPresenter extends AbsPresenter implements PaymentsContract.Presenter {

    public PaymentsPresenter(PaymentsContract.View view) {
        super(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PaymentsContract.View getView() {
        return super.getView();
    }

    // TODO

}
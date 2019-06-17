package com.want.vendor.product.info.payment;

import com.want.base.sdk.framework.app.mvp.IPresenter;
import com.want.base.sdk.framework.app.mvp.IView;

/**
 * <b>Create Date:</b> 2016/11/18<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public interface PaymentsContract {

    interface Presenter extends IPresenter {
        // TODO
    }

    interface View extends IView {
        void setPaymentMethod(int method);
    }

}

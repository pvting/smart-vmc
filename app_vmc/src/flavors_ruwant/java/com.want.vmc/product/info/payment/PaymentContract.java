package com.want.vmc.product.info.payment;

import com.want.base.sdk.framework.app.mvp.IPresenter;
import com.want.base.sdk.framework.app.mvp.IView;

/**
 * <b>Create Date:</b> 11/01/16<br>
 * <b>Author:</b> peiweiwei<br>
 * <b>Description:</b> PaymentContract <br>
 */
public interface PaymentContract {

    interface Presenter extends IPresenter {
        // TODO
    }

    interface View extends IView {
        void setPaymentMethod(int method);
    }

}

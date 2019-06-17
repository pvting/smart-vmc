package com.want.vendor.product.info.payment;

import com.want.base.sdk.framework.app.mvp.IPresenter;
import com.want.base.sdk.framework.app.mvp.IView;

/**
 * <b>Create Date:</b> 2016/11/21<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public interface ReSelectPaymentsContract {

    interface Presenter extends IPresenter {
        // TODO
    }

    interface View extends IView {
        void reSetPaymentsMethod();
    }

}

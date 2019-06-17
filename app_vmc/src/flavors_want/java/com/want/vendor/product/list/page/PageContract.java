package com.want.vendor.product.list.page;

import com.want.base.sdk.framework.app.mvp.IPresenter;
import com.want.base.sdk.framework.app.mvp.IView;

/**
 * Contract of Page.
 */
public interface PageContract {

    interface Presenter extends IPresenter {
        // TODO
        void onTimeOut();

        void onBack();

        void onNextPage();

        void onPreviousPage();



    }

    interface View extends IView {

        void onNextPage();

        void onPreviousPage();
        // TODO
        void onTimeOut();

        void onBack();

        void onTimeRest();

        void onTimeStop();

    }

}

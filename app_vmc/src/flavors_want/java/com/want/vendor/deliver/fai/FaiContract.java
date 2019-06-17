package com.want.vendor.deliver.fai;

import com.want.base.sdk.framework.app.mvp.IPresenter;
import com.want.base.sdk.framework.app.mvp.IView;

/**
 * Contract of Fai.
 */
public interface FaiContract {

    interface Presenter extends IPresenter {

    }

    interface View extends IView {

        String getStringNo();

        String getExtra();


    }

}

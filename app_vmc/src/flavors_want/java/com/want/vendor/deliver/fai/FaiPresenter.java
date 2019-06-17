package com.want.vendor.deliver.fai;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * Presenter stub.
 */
public class FaiPresenter extends AbsPresenter implements FaiContract.Presenter {

    public FaiPresenter(FaiContract.View view) {
        super(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected FaiContract.View getView() {
        return super.getView();
    }

}
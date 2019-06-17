package com.want.vendor.deliver.success;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * Presenter stub.
 */
public class SuccessPresenter extends AbsPresenter implements SuccessContract.Presenter {

    public SuccessPresenter(SuccessContract.View view) {
        super(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected SuccessContract.View getView() {
        return super.getView();
    }

}
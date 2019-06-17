package com.want.vendor.deliver.fillout;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * Presenter stub.
 */
public class FillOutPresenter extends AbsPresenter implements FillOutContract.Presenter {

    public FillOutPresenter(FillOutContract.View view) {
        super(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected FillOutContract.View getView() {
        return super.getView();
    }

}
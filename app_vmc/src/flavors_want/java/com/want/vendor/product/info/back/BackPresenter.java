package com.want.vendor.product.info.back;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * Presenter stub.
 */
public class BackPresenter extends AbsPresenter implements BackContract.Presenter {

    public BackPresenter(BackContract.View view) {
        super(view);
    }

    @Override
    protected BackContract.View getView() {
        return super.getView();
    }

    @Override
    public void onBack() {
        getView().onBack();
    }

    @Override
    public void onTimerEnd() {
        getView().onTimerEnd();
    }

    @Override
    public void setTimeLeft(int timeLeft) {
        getView().setTimeLeft(timeLeft);
    }

}
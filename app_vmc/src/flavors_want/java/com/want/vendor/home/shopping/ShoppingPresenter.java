package com.want.vendor.home.shopping;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * Presenter stub.
 */
public class ShoppingPresenter extends AbsPresenter implements ShoppingContract.Presenter {

    public ShoppingPresenter(ShoppingContract.View view) {
        super(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ShoppingContract.View getView() {
        return super.getView();
    }

    // TODO

}
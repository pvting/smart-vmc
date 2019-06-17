package com.want.vendor.product.paysuccess.game;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * Presenter stub.
 */
public class GamePresenter extends AbsPresenter implements GameContract.Presenter {

    public GamePresenter(GameContract.View view) {
        super(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected GameContract.View getView() {
        return super.getView();
    }

    // TODO

}
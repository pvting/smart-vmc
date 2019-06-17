package com.want.vmc.home.shopping;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * <b>Create Date:</b> 10/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
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
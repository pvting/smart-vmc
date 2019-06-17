package com.want.vendor.product.info.details;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * <b>Create Date:</b> 2016/11/14<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public class DetailsPresenter extends AbsPresenter implements DetailsContract.Presenter {

    public DetailsPresenter(DetailsContract.View view) {
        super(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected DetailsContract.View getView() {
        return super.getView();
    }

    // TODO

}
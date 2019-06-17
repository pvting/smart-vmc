package com.want.vendor.home.info;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * <b>Create Date:</b> 2016/11/14<br>
 * <b>Author:</b> ZhongWenjie<br>
 * <b>Description:</b> <br>
 */
public class InfoPresenter extends AbsPresenter implements InfoContract.Presenter {

    public InfoPresenter(InfoContract.View view) {
        super(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected InfoContract.View getView() {
        return super.getView();
    }

    // TODO

}
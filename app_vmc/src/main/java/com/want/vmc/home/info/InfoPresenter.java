package com.want.vmc.home.info;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * <b>Create Date:</b> 10/21/16<br>
 * <b>Author:</b> Gordon<br>
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

}
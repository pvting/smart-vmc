package com.want.vmc.home.advert;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * <b>Create Date:</b> 10/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class AdvertPresenter extends AbsPresenter implements AdvertContract.Presenter {

    public AdvertPresenter(AdvertContract.View view) {
        super(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AdvertContract.View getView() {
        return super.getView();
    }
    // TODO

}
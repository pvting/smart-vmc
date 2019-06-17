package com.want.vendor.common.help;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * Presenter stub.
 */
public class HelpPresenter extends AbsPresenter implements HelpContract.Presenter {

    public HelpPresenter(HelpContract.View view) {
        super(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected HelpContract.View getView() {
        return super.getView();
    }




}
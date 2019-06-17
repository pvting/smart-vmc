package com.want.vmc.serialporterror;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * Presenter stub.
 */
public class SerialPortErrorPresenter extends AbsPresenter implements
                                                                    SerialPortErrorContract.Presenter {

    public SerialPortErrorPresenter(SerialPortErrorContract.View view) {
        super(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected SerialPortErrorContract.View getView() {
        return super.getView();
    }

    // TODO

}
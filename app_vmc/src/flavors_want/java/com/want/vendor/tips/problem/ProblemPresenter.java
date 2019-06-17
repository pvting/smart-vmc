package com.want.vendor.tips.problem;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * Presenter stub.
 */
public class ProblemPresenter extends AbsPresenter implements ProblemContract.Presenter {

    public ProblemPresenter(ProblemContract.View view) {
        super(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ProblemContract.View getView() {
        return super.getView();
    }

    // TODO

}
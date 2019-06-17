package com.want.vendor.product.list.page;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * Presenter stub.
 */
public class PagePresenter extends AbsPresenter implements PageContract.Presenter {
public int currentIndex;


    public PagePresenter(PageContract.View view) {
        super(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PageContract.View getView() {
        return super.getView();
    }

    @Override
    public void onTimeOut() {
        getView().onTimeOut();

    }

    @Override
    public void onBack() {
        getView().onBack();

    }

    @Override
    public void onNextPage() {
       // TODO: 2016/11/22


    }

    @Override
    public void onPreviousPage() {
        // TODO: 2016/11/22

    }



}
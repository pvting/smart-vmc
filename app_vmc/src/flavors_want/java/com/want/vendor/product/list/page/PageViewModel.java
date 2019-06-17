package com.want.vendor.product.list.page;

import android.content.Context;
import android.databinding.Bindable;
import android.view.View;

import com.want.base.sdk.framework.app.mvp.AbsViewModel;

/**
 * ViewModel Stub.
 */
public class PageViewModel extends AbsViewModel {
    private PageContract.View fragView;

    public boolean  showNext = true;
    public boolean  showPreviousPage =  false;

    public PageViewModel() {

    }

    public PageViewModel(Context context) {
        super(context);
    }

    public PageViewModel(PageContract.View view) {
        this.fragView = view;
    }

    // TODO
    public void onNextPage(View view) {
        fragView.onNextPage();
    }

    // TODO
    public void onPreviousPage(View view) {
        fragView.onPreviousPage();
    }

    @Bindable
    public int getShowNextBtn() {
        if (showNext){
        return View.VISIBLE;}
        return View.GONE;
    }

    @Bindable
    public int getShowPreviousBtn() {

        if (showPreviousPage){
            return View.VISIBLE;}
        return View.GONE;

    }


}

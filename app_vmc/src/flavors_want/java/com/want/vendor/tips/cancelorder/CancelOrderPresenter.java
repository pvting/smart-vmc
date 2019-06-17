package com.want.vendor.tips.cancelorder;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * Presenter stub.
 */
public class CancelOrderPresenter extends AbsPresenter implements CancelOrderContract.Presenter{
    
    public CancelOrderPresenter( CancelOrderContract.View view ){
        super(view);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected CancelOrderContract.View getView( ){
        return super.getView( );
    }
    
    @Override
    public void onBack( ){
    
    }
}
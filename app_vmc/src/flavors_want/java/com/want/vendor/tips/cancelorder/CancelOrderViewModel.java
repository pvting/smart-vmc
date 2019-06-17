package com.want.vendor.tips.cancelorder;

import android.content.Context;
import android.view.View;

import com.want.base.sdk.framework.app.mvp.AbsViewModel;

import java.util.Timer;

import static vmc.vendor.common.back.BackViewModel.DEFAULT_TIMELEFT;

/**
 * ViewModel Stub.
 */
public class CancelOrderViewModel extends AbsViewModel{
    
    private Timer mTimeLeftTimer;
    public int mSetTimeLeft = DEFAULT_TIMELEFT;
    private int mTimeLeft = mSetTimeLeft;
    private CancelOrderContract.Presenter mPresenter;
    private boolean isPause = true;
    private boolean isDestroy = false;
    
    public CancelOrderViewModel( CancelOrderContract.Presenter presenter ){
        this.mPresenter = presenter;
    }
    
    public CancelOrderViewModel( Context context ){
        super(context);
    }
    
  
    
    void pause( ){
        this.isPause = true;
        notifyChange( );
    }
    
    void resume( ){
        this.isPause = false;
        notifyChange( );
    }
    
    void destroy( ){
        this.isDestroy = true;
    }
    
    /**
     * 按下返回
     *
     * @param v
     */
    public void onBackClicked( View v ){
        if (null != mPresenter) {
            mPresenter.onBack( );
        }
    }
}

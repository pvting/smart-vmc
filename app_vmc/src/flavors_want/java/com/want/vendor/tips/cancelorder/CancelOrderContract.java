package com.want.vendor.tips.cancelorder;

import com.want.base.sdk.framework.app.mvp.IPresenter;
import com.want.base.sdk.framework.app.mvp.IView;

/**
 * <b>Create Date:</b> 2017/1/7<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public interface CancelOrderContract{
    
    interface Presenter extends IPresenter{
        /**
         * 用户主动按下返回
         */
        void onBack( );
        
    }
    
    interface View extends IView{
        /**
         * 用户主动按下返回
         */
        void onBack( );
        
    }
    
}

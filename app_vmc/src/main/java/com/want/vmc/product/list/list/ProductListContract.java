package com.want.vmc.product.list.list;

import android.content.Context;


import com.vmc.core.model.product.BLLProduct;
import com.want.base.sdk.framework.app.mvp.IPresenter;
import com.want.base.sdk.framework.app.mvp.IView;

import java.util.List;

/**
 * <b>Create Date:</b> 10/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public interface ProductListContract {

    interface Presenter extends IPresenter {
        /**
         * 请求商品列表
         *
         * @param context
         */
        void reqProducts(Context context);
    }

    interface View extends IView {
        /**
         * 商品列表被更新
         *
         * @param result   {@link vmc.vendor.Constants.Result#OK} or {@link vmc.vendor.Constants.Result#FAIL}
         * @param e        exception
         */
        void onProducts(int result, List<BLLProduct> products, Throwable e);
    }

}

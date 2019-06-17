package com.want.vendor.product.list.category;

import com.want.base.sdk.framework.app.mvp.IPresenter;
import com.want.base.sdk.framework.app.mvp.IView;

/**
 * <b>Create Date:</b> 11/01/16<br>
 * <b>Author:</b> Peiweiwei<br>
 * <b>Description:</b> ProductCategoryContract <br>
 */
public interface ProductCategoryContract {

    interface Presenter extends IPresenter {
        void onCategoryClick(String pos);
    }

    interface View extends IView {

        void onCateClickChange(String pos);
    }

}

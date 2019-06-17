package com.want.vendor.product.list.list;

import android.content.Context;

/**
 * <b>Create Date:</b> 10/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public interface ProductListContract {

    interface Presenter extends com.want.vmc.product.list.list.ProductListContract.Presenter {

        void reqProducts(Context contxt,String catetoryIndex,int PageIndex);

        void onCanNext(boolean arrow);
        void onCanPrevious(boolean arrow);
    }

    interface View extends com.want.vmc.product.list.list.ProductListContract.View {
    }

}

package com.want.vmc.product.list.list;

import android.content.Context;


import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.utils.BLLProductUtils;
import com.want.base.sdk.framework.app.mvp.AbsPresenter;

import java.util.ArrayList;

import vmc.vendor.Constants;

/**
 * <b>Create Date:</b> 10/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class ProductListPresenter extends AbsPresenter implements ProductListContract.Presenter, Constants {
     private String categoryName = "全部";
    public ProductListPresenter(ProductListContract.View view) {
        super(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ProductListContract.View getView() {
        return super.getView();
    }

    @Override
    public void reqProducts(Context context) {
//        final ProductList productList = ProductUtils.getProductList(context);
        ArrayList<BLLProduct> productList = BLLProductUtils.getProductListByPageIndex(categoryName,0,9999);


        if (null == productList || productList.size()==0) {
            getView().onProducts(Result.FAIL, null, new Throwable("ProductList is null. Cache invalid"));
        } else {
            getView().onProducts(Result.OK, productList, null);
        }
    }
}
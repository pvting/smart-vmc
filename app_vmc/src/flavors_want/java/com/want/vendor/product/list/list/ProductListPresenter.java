package com.want.vendor.product.list.list;

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

        final ArrayList<BLLProduct> productList = BLLProductUtils.getProductListByPageIndex("全部", 0);

        if (null == productList) {
            getView().onProducts(Result.FAIL, null, new Throwable("ProductList is null. Cache invalid"));
        } else {
            getView().onProducts(Result.OK, productList, null);
        }
    }

    @Override
    public void reqProducts(Context context, String catetoryIndex, int PageIndex) {
        if(PageIndex==0){
            onCanPrevious(false);

        }else{
            onCanPrevious(true);

        }
        final ArrayList<BLLProduct> productList =BLLProductUtils.getProductListByPageIndex(catetoryIndex,PageIndex);
//        Log.d("TAG",productList.toString());
        //等于9时, 可能有下一页,也可能没有
        if (BLLProductUtils.haveNextPage(catetoryIndex,PageIndex, 9)) {
            onCanNext(true);
        } else {
            onCanNext(false);//小于9 不能下一页
        }
        if (null == productList) {
            getView().onProducts(Result.FAIL, null, new Throwable("ProductList is null. Cache invalid"));
        } else {
            getView().onProducts(Result.OK, productList, null);
        }
    }

    @Override
    public void onCanNext(boolean arrow) {

    }

    @Override
    public void onCanPrevious(boolean arrow) {

    }

}
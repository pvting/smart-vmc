package com.want.vendor.product.list.category;

import com.want.base.sdk.framework.app.mvp.AbsPresenter;

/**
 * <b>Create Date:</b> 11/1/16<br>
 * <b>Author:</b> peiweiwei<br>
 * <b>Description:</b>Presenter stub. <br>
 */
public class ProductCategoryPresenter extends AbsPresenter implements ProductCategoryContract.Presenter {

    public ProductCategoryPresenter(ProductCategoryContract.View view) {
        super(view);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ProductCategoryContract.View getView() {
        return super.getView();
    }

    @Override
    public void onCategoryClick(String pos) {
        (getView()).onCateClickChange(pos);

    }

    // TODO

}
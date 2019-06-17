package com.want.vendor.product.list.list;

import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmc.core.model.product.BLLProduct;
import com.want.vendor.product.list.OnCategroyAndPageChangeListener;
import com.want.vmc.databinding.VendorProductitemLayoutBinding;

import vmc.vendor.Constants;

/**
 * <b>Create Date:</b> 10/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class ProductListFragment extends com.want.vmc.product.list.list.ProductListFragment implements
        ProductListContract.View,
        Constants,
        OnCategroyAndPageChangeListener {
    /**
     * 分页便宜量
     */
    private int mPageIndex;
    private String mCategroyName = "全部";

    public static ProductListFragment newInstance() {
        return newInstance(GridLayoutManager.HORIZONTAL);
    }

    public static ProductListFragment newInstance(int orientation) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle bundle = getBundle(orientation);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ProductListContract.Presenter getPresenter() {
        return (ProductListContract.Presenter) super.getPresenter();
    }

    @Override
    protected void onSetupRecyclerView(RecyclerView recyclerView) {
        super.onSetupRecyclerView(recyclerView);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void onReqProducts() {
        getPresenter().reqProducts(this.getContext(), mCategroyName, mPageIndex);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected View onCreateItemView(LayoutInflater inflater, int viewType) {
        return VendorProductitemLayoutBinding.inflate(inflater, null, true).getRoot();
    }

    @Override
    protected void onUpdateItemView(final View view, int pos, BLLProduct data, final int viewType) {
        final VendorProductitemLayoutBinding binding = DataBindingUtil.getBinding(view);
        final ProductListViewModel viewModel = new ProductListViewModel(this.getActivity(), data);
        binding.setModel(viewModel);
        binding.productListOrderpriceText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//        binding.productDetailsOrderpriceText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    public void onCategroyAndPageChange(String categoryName, int pageIndex) {
        getPresenter().reqProducts(this.getContext(), categoryName, pageIndex);
        this.mPageIndex = pageIndex;
        this.mCategroyName = categoryName;
    }
}
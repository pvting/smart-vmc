package com.want.vendor.product.list.category;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmc.core.utils.BLLProductUtils;
import com.want.vmc.databinding.VendorProductCategoryLayoutBinding;
import com.want.vendor.product.list.OnCategroyAndPageChangeListener;
import com.want.vendor.ui.view.CategoryView;

import vmc.vendor.VFragment;

/**
 * <b>Create Date:</b>2016/11/20,下午12:04<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class ProductCategoryFragment extends VFragment implements ProductCategoryContract.View,CategoryView.OnCategoryListener {

    public static ProductCategoryFragment newInstance() {
        ProductCategoryFragment fragment = new ProductCategoryFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorProductCategoryLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final VendorProductCategoryLayoutBinding binding = DataBindingUtil.getBinding(view);
        binding.setModel(new ProductCategoryViewModel(getActivity()));
        binding.productListCategroy.setSrc(BLLProductUtils.getCategoryList());
        binding.productListCategroy.setOnCategoryListener(this);
    }
    @Override
    public void onCategoryClick(String pos) {
        ((ProductCategoryPresenter)getPresenter()).onCategoryClick(pos);

    }
    @Override
    public void onCateClickChange(String pos) {
        ((OnCategroyAndPageChangeListener)getActivity()).onCategroyAndPageChange(pos,0);
    }
}
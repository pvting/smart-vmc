package com.want.vmc.product.list.category;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.vmc.databinding.VendorProductlistCategoryLayoutBinding;
import vmc.vendor.VFragment;

/**
 * <b>Create Date:</b> 11/1/16<br>
 * <b>Author:</b> peiweiwei<br>
 * <b>Description:</b>View stub. <br>
 */
public class ProductCategoryFragment extends VFragment implements ProductCategoryContract.View {

    public static ProductCategoryFragment newInstance() {
        ProductCategoryFragment fragment = new ProductCategoryFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorProductlistCategoryLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final VendorProductlistCategoryLayoutBinding binding = DataBindingUtil.getBinding(view);
        binding.setModel(new ProductCategoryViewModel(getActivity()));
    }

}
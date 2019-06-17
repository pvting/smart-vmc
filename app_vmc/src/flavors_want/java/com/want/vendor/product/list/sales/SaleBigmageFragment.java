package com.want.vendor.product.list.sales;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.databinding.VendorHomeSalebigmageLayoutBinding;

/**
 * View stub.
 */
public class SaleBigmageFragment extends MFragment implements SaleBigmageContract.View {

    public static SaleBigmageFragment newInstance() {
        SaleBigmageFragment fragment = new SaleBigmageFragment();
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected SaleBigmageContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    // TODO
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorHomeSalebigmageLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VendorHomeSalebigmageLayoutBinding binding = DataBindingUtil.getBinding(view);
        SaleBigmageViewModel model = new SaleBigmageViewModel(getArguments().getString("url"));
        binding.setModel(model);
    }

}
package com.want.vendor.home.bigimgads;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.databinding.VendorHomeBigimageLayoutBinding;

/**
 * View stub.
 */
public class BigImageFragment extends MFragment implements BigImageContract.View {


    public static BigImageFragment newInstance() {
        BigImageFragment fragment = new BigImageFragment();
        return fragment;
    }


    @Override
    @SuppressWarnings("unchecked")
    protected BigImageContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return VendorHomeBigimageLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VendorHomeBigimageLayoutBinding binding = DataBindingUtil.getBinding(view);
        BigImageViewModel model = new BigImageViewModel(getArguments().getString("url"));
        binding.setModel(model);

    }


}
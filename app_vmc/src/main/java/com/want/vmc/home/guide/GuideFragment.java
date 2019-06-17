package com.want.vmc.home.guide;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.vmc.databinding.VendorHomeGuideLayoutBinding;

import vmc.vendor.VFragment;

/**
 * <b>Create Date:</b> 10/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class GuideFragment extends VFragment implements GuideContract.View {

    public static GuideFragment newInstance() {
        GuideFragment fragment = new GuideFragment();
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected GuideContract.Presenter getPresenter() {
        return super.getPresenter();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorHomeGuideLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final VendorHomeGuideLayoutBinding binding = DataBindingUtil.getBinding(view);
        binding.setModel(new GuideViewModel(getActivity()));
    }
}
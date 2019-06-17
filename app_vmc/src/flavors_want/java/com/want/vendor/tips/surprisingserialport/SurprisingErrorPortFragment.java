package com.want.vendor.tips.surprisingserialport;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.databinding.VendorHomeSurspringserialporterrorLayoutBinding;



/**
 * View stub.
 */
public class SurprisingErrorPortFragment extends MFragment implements SurprisingErrorPortContract.View {

    private  SurprisingErrorPortViewModel mSurprisingErrorPortViewModel;
    public static SurprisingErrorPortFragment newInstance() {
        SurprisingErrorPortFragment fragment = new SurprisingErrorPortFragment();
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected SurprisingErrorPortContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
    return VendorHomeSurspringserialporterrorLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VendorHomeSurspringserialporterrorLayoutBinding
                binding = DataBindingUtil.getBinding(view);
         mSurprisingErrorPortViewModel = new SurprisingErrorPortViewModel(getActivity());
        binding.setModel(mSurprisingErrorPortViewModel);
    }
}
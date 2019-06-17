package com.want.vendor.tips.serialporterror;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.databinding.VendorHomeSerialporterrorLayoutBinding;

/**
 * View stub.
 */
public class SerialPortErrorFragment extends MFragment implements SerialPortErrorContract.View {


    public static SerialPortErrorFragment newInstance() {
        SerialPortErrorFragment fragment = new SerialPortErrorFragment();
        return fragment;
    }
    @Override
    @SuppressWarnings("unchecked")
    protected SerialPortErrorContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorHomeSerialporterrorLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VendorHomeSerialporterrorLayoutBinding
                binding = DataBindingUtil.getBinding(view);
        SerialPortErrorViewModel model = new SerialPortErrorViewModel(getActivity());
        binding.setModel(model);
    }

}
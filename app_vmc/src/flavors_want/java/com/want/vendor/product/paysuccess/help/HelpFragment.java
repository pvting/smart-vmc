package com.want.vendor.product.paysuccess.help;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.vmc.R;
import com.want.vmc.databinding.VendorPaymentSuccessHelpBinding;

import vmc.vendor.VFragment;


/**
 * View stub.
 */
public class HelpFragment extends VFragment implements HelpContract.View {


    private HelpViewModel mHelpViewModel;

    public static HelpFragment newInstance() {
        HelpFragment fragment = new HelpFragment();
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected HelpContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorPaymentSuccessHelpBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final VendorPaymentSuccessHelpBinding binding = DataBindingUtil.getBinding(view);
        String tip = getArguments().getString("tip");
        String payType = getArguments().getString("payType");
        String order = getArguments().getString("order");
        boolean isRefund = getArguments().getBoolean("isRefund");


        mHelpViewModel = new HelpViewModel(getActivity(), tip, payType, order, isRefund);
        binding.setModel(mHelpViewModel);
        if (mHelpViewModel.getPaymentResult()) {
            binding.vendorPaymentStatusTipsImageview.setImageResource(R.drawable.vendor_payment_successpay);
        } else {
            binding.vendorPaymentStatusTipsImageview.setImageResource(R.drawable.vendor_deliver_order_failure_image);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
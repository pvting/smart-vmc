package com.want.vmc.product.info.payment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.vmc.databinding.VendorPaymentChooseLayoutBinding;
import vmc.vendor.VFragment;

/**
 * <b>Create Date:</b> 11/01/16<br>
 * <b>Author:</b> peiweiwei<br>
 * <b>Description:</b> View stub. <br>
 */
public class   PaymentFragment extends VFragment implements PaymentContract.View {

    public interface OnPaymentCallback {
        void onPaymentChanged(int method);
    }

    private OnPaymentCallback mOnPaymentCallback;

    public static PaymentFragment newInstance() {
        PaymentFragment fragment = new PaymentFragment();
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PaymentContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPaymentCallback) {
            mOnPaymentCallback = (OnPaymentCallback) context;
        } else {
            throw new IllegalArgumentException("Activity must be implement OnPaymentCallback");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorPaymentChooseLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final VendorPaymentChooseLayoutBinding binding = DataBindingUtil.getBinding(view);
        binding.setModel(new PaymentViewModel(getActivity(),PaymentFragment.this));
    }

    @Override
    public void setPaymentMethod(int method) {
        mOnPaymentCallback.onPaymentChanged(method);
    }
}
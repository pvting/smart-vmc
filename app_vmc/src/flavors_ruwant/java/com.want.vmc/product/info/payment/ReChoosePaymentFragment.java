package com.want.vmc.product.info.payment;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.base.sdk.framework.app.MFragment;

import com.want.vmc.databinding.VendorPaymentRechooseLayoutBinding;

/**
 * <b>Create Date:</b> 11/02/16<br>
 * <b>Author:</b> peiweiwei<br>
 * <b>Description:</b> View stub. <br>
 */
public class ReChoosePaymentFragment extends MFragment implements ReChoosePaymentContract.View {

    OnResetPaymentCallback mOnResetPaymentCallback;
    private ReChoosePaymentViewModel mReChoosePaymentViewModel;

    public static ReChoosePaymentFragment newInstance() {
        ReChoosePaymentFragment fragment = new ReChoosePaymentFragment();
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ReChoosePaymentContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof ReChoosePaymentFragment.OnResetPaymentCallback) {
            mOnResetPaymentCallback = (ReChoosePaymentFragment.OnResetPaymentCallback) context;
        } else {
            throw new IllegalArgumentException("Activity must be implement OnPaymentCallback");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorPaymentRechooseLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final int method = getArguments().getInt(Extras.DATA);
        final VendorPaymentRechooseLayoutBinding binding = DataBindingUtil.getBinding(view);
        mReChoosePaymentViewModel = new ReChoosePaymentViewModel(ReChoosePaymentFragment.this, method);
        binding.setModel(mReChoosePaymentViewModel);
    }

    public void setPaymentMethod(int method) {
        mReChoosePaymentViewModel.setPaymentMethod(method);
    }

    @Override
    public void reSetPaymentMethod() {
        mOnResetPaymentCallback.onPaymentReset();
    }

    public interface OnResetPaymentCallback {
        /**
         * 重置支付方式
         */
        void onPaymentReset();
    }
}
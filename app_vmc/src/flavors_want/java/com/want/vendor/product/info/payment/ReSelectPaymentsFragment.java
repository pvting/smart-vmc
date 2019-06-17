package com.want.vendor.product.info.payment;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.vmc.databinding.VendorPaymentReselectLayoutBinding;

import vmc.vendor.VFragment;

/**
 * <b>Create Date:</b> 2016/11/21<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public class ReSelectPaymentsFragment extends VFragment implements ReSelectPaymentsContract.View {

    OnResetPaymentCallback mOnResetPaymentCallback;
    private ReSelectPaymentsViewModel mReSelectPaymentsViewModel;
    VendorPaymentReselectLayoutBinding binding;

    public static ReSelectPaymentsFragment newInstance() {
        ReSelectPaymentsFragment fragment = new ReSelectPaymentsFragment();
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ReSelectPaymentsContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof ReSelectPaymentsFragment.OnResetPaymentCallback) {
            mOnResetPaymentCallback = (ReSelectPaymentsFragment.OnResetPaymentCallback) context;
        } else {
            throw new IllegalArgumentException("Activity must be implement OnPaymentCallback");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorPaymentReselectLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int method = getArguments().getInt(Extras.DATA);
        binding = DataBindingUtil.getBinding(view);
        mReSelectPaymentsViewModel = new ReSelectPaymentsViewModel(ReSelectPaymentsFragment.this, method);
        binding.setModel(mReSelectPaymentsViewModel);
    }


    public void setCanClick(boolean canClick) {
        if (mReSelectPaymentsViewModel != null) {
            mReSelectPaymentsViewModel.setShowRest(canClick);
        }
    }

    public void rechoosePayment() {
        if (mReSelectPaymentsViewModel != null) {
            mReSelectPaymentsViewModel.rechoosePayment();
        }
    }





    public void setPaymentMethod(int method) {
        mReSelectPaymentsViewModel.setPaymentMethod(method);
    }

    @Override
    public void reSetPaymentsMethod() {
        mOnResetPaymentCallback.onPaymentReset();
    }






    public interface OnResetPaymentCallback {
        /**
         * 重置支付方式
         */
        void onPaymentReset();
    }

}
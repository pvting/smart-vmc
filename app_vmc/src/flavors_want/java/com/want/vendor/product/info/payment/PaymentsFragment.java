package com.want.vendor.product.info.payment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmc.core.OdooAction;
import com.vmc.core.model.product.BLLProduct;
import com.want.vmc.databinding.VendorPaymentSelectLayoutBinding;

import vmc.vendor.VFragment;

/**
 * <b>Create Date:</b> 2016/11/18<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public class PaymentsFragment extends VFragment implements PaymentsContract.View {

    private PaymentsViewModel mPaymentsViewModel;

    public interface OnPaymentCallback {
        void onPaymentChanged(int method);
    }

    private BroadcastReceiver mVmcReceiveMoneyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (TextUtils.equals(action, OdooAction.VMC_RECEIVE_MONEY)) {
                setPaymentMethod(3);
            }
        }
    };

    private OnPaymentCallback mOnPaymentCallback;

    public static PaymentsFragment newInstance() {
        PaymentsFragment fragment = new PaymentsFragment();
        return fragment;
    }

    public static PaymentsFragment newInstance(BLLProduct mProduct) {
        PaymentsFragment fragment = new PaymentsFragment();
        Bundle bundle =getBundle(mProduct);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PaymentsContract.Presenter getPresenter() {
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
        return VendorPaymentSelectLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final VendorPaymentSelectLayoutBinding binding = DataBindingUtil.getBinding(view);

        BLLProduct mProduct = getArguments().getParcelable(Extras.DATA);
        mPaymentsViewModel = new PaymentsViewModel(getActivity(), mProduct, this);
        binding.setModel(mPaymentsViewModel);
    }

    public void notifyView() {
        mPaymentsViewModel.notifyChange();
    }

    @Override
    public void setPaymentMethod(int method) {

        mOnPaymentCallback.onPaymentChanged(method);
    }


    @Override
    public void onPause() {
        super.onPause();
    }
}
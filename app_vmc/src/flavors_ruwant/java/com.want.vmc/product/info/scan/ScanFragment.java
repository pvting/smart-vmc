package com.want.vmc.product.info.scan;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmc.core.model.product.BLLProduct;
import com.want.vmc.product.info.payment.PaymentFragment;

import vmc.vendor.VFragment;

/**
 * <b>Create Date:</b> 11/02/16<br>
 * <b>Author:</b> Wisn<br>
 * <b>Description:</b> <br>
 */
public class ScanFragment extends VFragment implements ScanContract.View ,PaymentFragment.OnPaymentCallback{



    private ScanViewModel mScanViewModel;

    public static ScanFragment newInstance(BLLProduct product) {
        ScanFragment fragment = new ScanFragment();
        Bundle bundle = getBundle(product);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ScanContract.Presenter getPresenter() {
        return super.getPresenter();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return com.want.vmc.databinding.VendorProductInfoScanLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final BLLProduct product = getArguments().getParcelable(Extras.DATA);
        final com.want.vmc.databinding.VendorProductInfoScanLayoutBinding
                binding = DataBindingUtil.getBinding(view);
        mScanViewModel = new ScanViewModel(ScanFragment.this.getActivity(),product);
        binding.setModel(mScanViewModel);
        mScanViewModel.init();

    }

    @Override
    public void onPaymentChanged(int method) {
        if (mScanViewModel!=null){
            mScanViewModel.changePayment(method);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mScanViewModel.onDestory();
    }


}
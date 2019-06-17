package com.want.vendor.product.info.scanner;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmc.core.model.product.BLLProduct;
import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.databinding.VendorPaymentScannerLayoutBinding;
import com.want.vendor.product.info.payment.PaymentsFragment;

import vmc.machine.core.VMCContoller;

/**
 * <b>Create Date:</b> 2016/11/22<br>
 * <b>Author:</b> huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class ScannerFragment extends MFragment implements ScannerContract.View, PaymentsFragment.OnPaymentCallback {


    private ScannerViewModel mScannerViewModel;
    private BLLProduct product;
    private boolean isPay;

    public static ScannerFragment newInstance(BLLProduct product) {
        ScannerFragment fragment = new ScannerFragment();
        Bundle bundle = getBundle(product);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ScannerFragment newInstance(BLLProduct product,boolean isPay) {
        ScannerFragment fragment = new ScannerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Extras.DATA,product);
        bundle.putBoolean("isPay",isPay);
        fragment.setArguments(bundle);
        return fragment;
    }



    @Override
    @SuppressWarnings("unchecked")
    protected ScannerContract.Presenter getPresenter() {
        return super.getPresenter();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorPaymentScannerLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        product = getArguments().getParcelable(Extras.DATA);
        isPay = getArguments().getBoolean("isPay");
        final VendorPaymentScannerLayoutBinding binding = DataBindingUtil.getBinding(view);
        mScannerViewModel = new ScannerViewModel(getActivity(), this, product,isPay);
        binding.setModel(mScannerViewModel);
        mScannerViewModel.init();



    }

    @Override
    public void onPaymentChanged(int method) {
        if (mScannerViewModel == null) {
            return;
        }
        mScannerViewModel.changePayment(method);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScannerViewModel.onDestory();
    }

    @Override
    public void finish() {
        if (null != getActivity() && !getActivity().isFinishing()) {
            getActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        VMCContoller.getInstance().cashFinish();
        super.onDestroyView();
    }

}
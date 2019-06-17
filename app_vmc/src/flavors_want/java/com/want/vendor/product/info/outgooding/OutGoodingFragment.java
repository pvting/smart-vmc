package com.want.vendor.product.info.outgooding;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.vmc.core.model.product.BLLProduct;
import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.R;
import com.want.vmc.databinding.VendorPaymentOutgoodingLayoutBinding;

import vmc.machine.core.VMCContoller;

/**
 * <b>Create Date:</b> 2016/11/22<br>
 * <b>Author:</b> huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class OutGoodingFragment extends MFragment implements OutGoodingContract.View {


    private OutGoodingViewModel outGoodingViewModel;
    private BLLProduct product;
    private boolean isPay;

    public static OutGoodingFragment newInstance(BLLProduct product) {
        OutGoodingFragment fragment = new OutGoodingFragment();
        Bundle bundle = getBundle(product);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static OutGoodingFragment newInstance(BLLProduct product, boolean isPay) {
        OutGoodingFragment fragment = new OutGoodingFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Extras.DATA, product);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    @SuppressWarnings("unchecked")
    protected OutGoodingContract.Presenter getPresenter() {
        return super.getPresenter();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorPaymentOutgoodingLayoutBinding.inflate(inflater, container, false).getRoot();
    }
    DraweeController draweeController;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        product = getArguments().getParcelable(Extras.DATA);
        isPay = getArguments().getBoolean("isPay");
        final VendorPaymentOutgoodingLayoutBinding binding = DataBindingUtil.getBinding(view);
        outGoodingViewModel = new OutGoodingViewModel(getActivity(), this);
        binding.setModel(outGoodingViewModel);

         draweeController = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res://com.want.vendor/" + R.raw.vendor_payinfo_outgooding))
                .setAutoPlayAnimations(true)
                .build();
        binding.outgoodingIv.setController(draweeController);
        outGoodingViewModel.init();
    }


    @Override
    public void onDestroy() {
        if(null!=draweeController&&null!=draweeController.getAnimatable()){
            if (draweeController.getAnimatable().isRunning()){
                draweeController.getAnimatable().stop();
            }
        }
        outGoodingViewModel.onDestory();
        super.onDestroy();
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
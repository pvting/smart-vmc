package com.want.vendor.deliver.success;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmc.core.model.product.DeliverProduct;
import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.databinding.VendorDeliverSuccessLayoutBinding;

/**
 * View stub.
 */
public class SuccessFragment extends MFragment implements SuccessContract.View {

    private SuccessViewModel mSuccessViewModel;
    private DeliverProduct mProduct;


    public static SuccessFragment newInstance(DeliverProduct product) {
        SuccessFragment fragment = new SuccessFragment();
        final Bundle bundle = getBundle(product);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected SuccessContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorDeliverSuccessLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Bundle extras = null != savedInstanceState ? savedInstanceState : getArguments();
        mProduct = extras.getParcelable(Extras.DATA);

        final VendorDeliverSuccessLayoutBinding binding = DataBindingUtil.getBinding(view);
        mSuccessViewModel = new SuccessViewModel(mProduct, getPresenter());
        binding.setModel(mSuccessViewModel);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Extras.DATA, mProduct);
        super.onSaveInstanceState(outState);
    }

}
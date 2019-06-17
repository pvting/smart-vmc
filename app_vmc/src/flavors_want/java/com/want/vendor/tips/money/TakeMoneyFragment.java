package com.want.vendor.tips.money;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.databinding.VendorShoppingTakemoneyLayoutBinding;

/**
 * View stub.
 */
public class TakeMoneyFragment extends MFragment implements TakeMoneyContract.View {

   private TakeMoneyViewModel mTakeMoneyViewModel;
    @Override
    @SuppressWarnings("unchecked")
    protected TakeMoneyContract.Presenter getPresenter() {
        return super.getPresenter();
    }
    public static TakeMoneyFragment newInstance() {
        TakeMoneyFragment fragment = new TakeMoneyFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorShoppingTakemoneyLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final VendorShoppingTakemoneyLayoutBinding binding = DataBindingUtil.getBinding(view);
        mTakeMoneyViewModel = new TakeMoneyViewModel(getActivity());
        binding.setModel(mTakeMoneyViewModel);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
package com.want.vendor.product.paysuccess.game;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.databinding.VendorPaymentSuccessGameBinding;

/**
 * View stub.
 */
public class GameFragment extends MFragment implements GameContract.View {

    private GameViewModel model;

    public static GameFragment newInstance() {
        GameFragment fragment = new GameFragment();
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected GameContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorPaymentSuccessGameBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String tip = getArguments().getString("tip");
        boolean isPay = getArguments().getBoolean("pay");
        String payType = getArguments().getString("payType");

        int productId = getArguments().getInt("id");
        final VendorPaymentSuccessGameBinding binding = DataBindingUtil.getBinding(view);
        model = new GameViewModel(this.getActivity(), payType, productId);
        binding.setModel(model);
    }
}
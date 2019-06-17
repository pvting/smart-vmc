package com.want.vendor.home.guide.shopping;
;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.want.vmc.databinding.VendorHomeShoppingGuideLayoutBinding;

import vmc.vendor.VFragment;


/**
 * <b>Create Date:</b> 11/22/16<br>
 * <b>Author:</b> huyunqiang<br>
 * <b>Description:引导购买页面</b> <br>
 */


public class ShoppingGuideFragment extends VFragment implements ShoppingGuideContract.View {

    private VendorHomeShoppingGuideLayoutBinding mShoppingGuideLayoutBinding;
    private ShoppingGuideViewModel mShoppingGuideViewModel;

    @Override
    @SuppressWarnings("unchecked")
    protected ShoppingGuideContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    public static ShoppingGuideFragment newInstance() {
        ShoppingGuideFragment fragment = new ShoppingGuideFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorHomeShoppingGuideLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mShoppingGuideLayoutBinding = DataBindingUtil.getBinding(view);
        mShoppingGuideViewModel = new ShoppingGuideViewModel(getActivity());
        mShoppingGuideLayoutBinding.setModel(mShoppingGuideViewModel);
    }

}
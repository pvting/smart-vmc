package com.want.vendor.home.shopping;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.want.vmc.R;
import com.want.vmc.databinding.VendorHomeShopLayoutBinding;

import vmc.vendor.VFragment;

/**
 * View stub.
 */
public class ShoppingFragment extends VFragment implements ShoppingContract.View {

    private ShoppingViewModel mShoppingViewModel;
    public static ShoppingFragment newInstance() {
        ShoppingFragment fragment = new ShoppingFragment();
        return fragment;
    }
    @Override
    @SuppressWarnings("unchecked")
    protected ShoppingContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    // TODO


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorHomeShopLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    DraweeController draweeController1;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       final VendorHomeShopLayoutBinding binding = DataBindingUtil.getBinding(view);
        binding.setModel(new ShoppingViewModel(getActivity()));

        Uri uri1 = Uri.parse("res://com.want.vendor/" + R.raw.vendor_home_shopping_icon_gif);
         draweeController1 = Fresco.newDraweeControllerBuilder()
                                                   .setUri(uri1)
                                                   .setAutoPlayAnimations(true)
                                                   .build();
        binding.shopImg.setController(draweeController1);


    }

    @Override
    public void onResume() {
        super.onResume();
        if (null!=draweeController1&&null!=draweeController1.getAnimatable()&&!draweeController1.getAnimatable().isRunning()){
            draweeController1.getAnimatable().start();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (null!=draweeController1&&null!=draweeController1.getAnimatable()&&draweeController1.getAnimatable().isRunning()){
            draweeController1.getAnimatable().stop();

        }
    }
}
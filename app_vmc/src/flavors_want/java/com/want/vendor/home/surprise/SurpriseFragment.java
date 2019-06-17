package com.want.vendor.home.surprise;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.R;
import com.want.vmc.databinding.VendorHomeSurpriseLayoutBinding;

/**
 * View stub.
 */
public class SurpriseFragment extends MFragment implements SurpriseContract.View {

    private SurpriseViewModel mSurpriseViewModel;

    public static SurpriseFragment newInstance() {
        SurpriseFragment fragment = new SurpriseFragment();
        return fragment;
    }
    @Override
    @SuppressWarnings("unchecked")
    protected SurpriseContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    // TODO


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorHomeSurpriseLayoutBinding.inflate(inflater,container,false).getRoot();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final VendorHomeSurpriseLayoutBinding binding = DataBindingUtil.getBinding(view);
        mSurpriseViewModel = new SurpriseViewModel(getActivity());
        binding.setModel(mSurpriseViewModel);

//        Uri uri1 = Uri.parse("res://com.want.vendor/" + R.raw.vendor_home_surprise_icon_gif);
////        SimpleDraweeView     mSimpleDraweeView1 = (SimpleDraweeView) view.findViewById(R.id.fresco_image);
//        DraweeController draweeController1 = Fresco.newDraweeControllerBuilder()
//                                                   .setUri(uri1)
//                                                   .setAutoPlayAnimations(true)
//                                                   .build();
//        binding.surpriseImg.setController(draweeController1);

    }
}
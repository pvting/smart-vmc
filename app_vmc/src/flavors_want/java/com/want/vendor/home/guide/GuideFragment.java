package com.want.vendor.home.guide;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.R;
import com.want.vmc.databinding.VendorHomeGuideLayoutItemBinding;

/**
 * View stub.
 */
public class GuideFragment extends MFragment implements GuideContract.View {
    private GuideViewModel mGuideViewModel;

    @Override
    @SuppressWarnings("unchecked")
    protected GuideContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    public static GuideFragment newInstance() {
        GuideFragment fragment = new GuideFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorHomeGuideLayoutItemBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final VendorHomeGuideLayoutItemBinding binding = DataBindingUtil.getBinding(view);
        mGuideViewModel = new GuideViewModel(getActivity());
        binding.setModel(mGuideViewModel);
//
//              Uri uri1 = Uri.parse("res://com.want.vendor/" + R.raw.vendor_home_guid_icon_gif);
////        SimpleDraweeView     mSimpleDraweeView1 = (SimpleDraweeView) view.findViewById(R.id.fresco_image);
//        DraweeController draweeController1 = Fresco.newDraweeControllerBuilder()
//                                                   .setUri(uri1)
//                                                   .setAutoPlayAnimations(true)
//                                                   .build();
//        binding.frescoImage.setController(draweeController1);
//
    }
}
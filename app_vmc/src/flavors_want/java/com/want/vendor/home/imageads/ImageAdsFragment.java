package com.want.vendor.home.imageads;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.databinding.VendorHomeImageadsLayoutBinding;


/**
 * <b>Create Date:</b> 2016/11/14<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class ImageAdsFragment extends MFragment implements ImageAdsContract.View {

    private ImageAdsViewModel mImageAdsViewModel;
    private VendorHomeImageadsLayoutBinding binding;

    public static ImageAdsFragment newInstance() {
        ImageAdsFragment fragment = new ImageAdsFragment();
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ImageAdsContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorHomeImageadsLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.getBinding(view);
        mImageAdsViewModel = new ImageAdsViewModel(binding.homeAdvertImage,getActivity());
        binding.setModle(mImageAdsViewModel);
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageAdsViewModel.onResume();
    }

    @Override
    public void onPause() {
        mImageAdsViewModel.onDestroy();
        super.onPause();
    }
}
package com.want.vmc.home.advert;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.vmc.databinding.VendorHomeAdvertLayoutBinding;

import vmc.vendor.VFragment;

/**
 * <b>Create Date:</b> 10/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 广告展示页面
 * <br>
 */
public class AdvertFragment extends VFragment implements AdvertContract.View {


    private VendorHomeAdvertLayoutBinding mAdvertLayoutBinding;
    private AdvertViewModel mAdvertViewModel;

    public static AdvertFragment newInstance() {
        AdvertFragment fragment = new AdvertFragment();
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AdvertContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorHomeAdvertLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdvertLayoutBinding = DataBindingUtil.getBinding(view);
        // TODO: 10/22/16 处理异常恢复
        mAdvertViewModel = new AdvertViewModel(mAdvertLayoutBinding.advertVideo, getActivity());
        mAdvertLayoutBinding.setModel(mAdvertViewModel);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdvertViewModel.onResume();
    }

    @Override
    public void onPause() {
        mAdvertViewModel.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mAdvertViewModel.onDestroy();
        super.onDestroy();
    }
}
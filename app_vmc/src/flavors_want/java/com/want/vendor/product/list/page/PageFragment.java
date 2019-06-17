package com.want.vendor.product.list.page;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmc.core.utils.ConfigUtils;
import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.databinding.VendorPageControlLayoutBinding;

import vmc.project.ui.view.CountDownView;


/**
 * View stub.
 */
public class PageFragment extends MFragment implements PageContract.View, CountDownView.OnCountdownEndListener, View.OnClickListener {

    private VendorPageControlLayoutBinding binding;
    PageViewModel model;

    public static PageFragment newInstance() {
        PageFragment fragment = new PageFragment();
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PageContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    // TODO

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorPageControlLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.getBinding(view);
        model = new PageViewModel(this);
        binding.setModel(model);
        binding.pageControlBackBtn.setOnCountdownEndListener(this);
        if (ConfigUtils.getConfig(getContext()) != null &&
            ConfigUtils.getConfig(getContext()).vmc_count_down_time_settings != null) {
            if (ConfigUtils.getConfig(getContext()).vmc_count_down_time_settings.general_page_countdown !=
                0) {
                binding.pageControlBackBtn.setCountDownTime(ConfigUtils.getConfig(getContext()).vmc_count_down_time_settings.general_page_countdown);
            }
        }
        binding.pageControlBackBtn.setOnClickListener(this);
    }


    @Override
    public void onNextPage() {
        getPresenter().onNextPage();


    }

    @Override
    public void onPreviousPage() {
        getPresenter().onPreviousPage();

    }

    @Override
    public void onTimeOut() {
        getActivity().finish();

    }

    @Override
    public void onBack() {
        getActivity().finish();
    }

    @Override
    public void onTimeRest() {
        binding.pageControlBackBtn.resetCount();

    }

    @Override
    public void onTimeStop() {
        binding.pageControlBackBtn.stopCount();

    }


    @Override
    public void onEnd(CountDownView cv) {
        getPresenter().onTimeOut();

    }

    @Override
    public void onClick(View v) {
        getPresenter().onBack();
    }

    public void isShowNext(boolean show) {

        model.showNext = show;
        model.notifyChange();


    }
    public void isShowonPrevious(boolean show) {

        model.showPreviousPage = show;
        model.notifyChange();


    }


}
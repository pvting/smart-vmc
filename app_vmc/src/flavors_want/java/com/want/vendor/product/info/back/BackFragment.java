package com.want.vendor.product.info.back;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.vmc.databinding.VendorProductInfoBackBinding;

import vmc.vendor.Constants;
import vmc.vendor.VFragment;


/**
 * View stub.
 */
public class BackFragment extends VFragment implements BackContract.View {

    private BackViewModel mBackViewModel;
    private VendorProductInfoBackBinding binding;

    public static BackFragment newInstance() {
        BackFragment fragment = new BackFragment();
        return fragment;
    }


    public BackFragment() {

    }

    @Override
    @SuppressWarnings("unchecked")
    protected BackContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return VendorProductInfoBackBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.getBinding(view);
        mBackViewModel = new BackViewModel(getPresenter());
        binding.setModel(mBackViewModel);
    }

    @Override
    public void onPause() {
        if (null != weakBackBroadcast) {
            getActivity().unregisterReceiver(weakBackBroadcast);
            weakBackBroadcast = null;
        }
        if (null != mBackViewModel) {
            mBackViewModel.pause();
        }
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (null != mBackViewModel) {
            mBackViewModel.resume();
        }
        IntentFilter filter = new IntentFilter(Constants.Action.CLICK_PAYMENTH_METHOD);
        getActivity().registerReceiver(weakBackBroadcast, filter);
    }

    @Override
    public void onDestroy() {
        if (null != mBackViewModel) {
            mBackViewModel.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void resetTimer() {
        if (null != mBackViewModel) {
            mBackViewModel.reset();
        }
    }

    @Override
    public void onBack() {
    }

    @Override
    public void onTimerEnd() {
        if (null != weakBackBroadcast) {
            getActivity().unregisterReceiver(weakBackBroadcast);
            weakBackBroadcast = null;
        }
    }

    @Override
    public void setTimeLeft(int timeLeft) {
        mBackViewModel.setTimeLeft(timeLeft);
    }

    @Override
    public void pauseTime() {
        if (null != mBackViewModel) {
            mBackViewModel.pause();
        }

    }

    @Override
    public void resumeTime() {
        if (null != mBackViewModel) {
            mBackViewModel.resume();
        }
    }


    private BroadcastReceiver weakBackBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (!TextUtils.equals(action, Constants.Action.CLICK_PAYMENTH_METHOD)) {
                return;
            }
            boolean reset = intent.getExtras().getBoolean("reset");
            if (reset) {
                binding.layoutBackNormal.setVisibility(View.VISIBLE);
                binding.layoutWeakBack.setVisibility(View.GONE);
            } else {
                binding.layoutBackNormal.setVisibility(View.GONE);
                binding.layoutWeakBack.setVisibility(View.VISIBLE);
            }
        }
    };

}
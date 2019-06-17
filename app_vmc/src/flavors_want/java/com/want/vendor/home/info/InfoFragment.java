package com.want.vendor.home.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.want.vmc.databinding.VendorHomeWeatherLayoutBinding;

import vmc.vendor.VFragment;


/**
 * View stub.
 */

/**
 * <b>Create Date:</b> 2016/11/14<br>
 * <b>Author:</b> ZhongWenjie<br>
 * <b>Description:</b> <br>
 */
public class InfoFragment extends VFragment implements InfoContract.View {

    private InfoViewModel mInfoViewModel;

    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected InfoContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorHomeWeatherLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        getActivity().registerReceiver(receiverTime, filter);


        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(Intent.ACTION_DATE_CHANGED);
        getActivity().registerReceiver(receiverDate, filter2);

        final VendorHomeWeatherLayoutBinding binding = DataBindingUtil.getBinding(view);
        mInfoViewModel = new InfoViewModel(getActivity());
        binding.setModel(mInfoViewModel);
    }

    @Override
    public void onResume() {
        super.onResume();
        mInfoViewModel.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public InfoViewModel getViewModel() {
        if (mInfoViewModel != null) {
            return mInfoViewModel;
        }
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiverTime);
        getActivity().unregisterReceiver(receiverDate);
    }


    private final BroadcastReceiver receiverTime = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {

                if (mInfoViewModel != null) {
                    mInfoViewModel.notifyPropertyChanged(BR.hello);
                    mInfoViewModel.notifyPropertyChanged(BR.time);
                }
            }
        }
    };
    private final BroadcastReceiver receiverDate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_DATE_CHANGED)) {
                if (mInfoViewModel != null) {
                    mInfoViewModel.notifyPropertyChanged(BR.date);
                }
            }

        }

    };

}
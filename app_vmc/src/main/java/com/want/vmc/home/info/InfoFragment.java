package com.want.vmc.home.info;

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
import com.want.vmc.databinding.VendorHomeInfoLayoutBinding;

import vmc.vendor.VFragment;


/**
 * <b>Create Date:</b> 10/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class InfoFragment extends VFragment implements InfoContract.View {

    private VendorHomeInfoLayoutBinding mInfoLayoutBinding;
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
        return VendorHomeInfoLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        IntentFilter filterTime = new IntentFilter();
        filterTime.addAction(Intent.ACTION_TIME_TICK);
        getActivity().registerReceiver(receiverTime, filterTime);


        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        getActivity().registerReceiver(receiverDate, filter);


        mInfoLayoutBinding = DataBindingUtil.getBinding(view);
        mInfoViewModel = new InfoViewModel(getActivity());
        mInfoLayoutBinding.setModel(mInfoViewModel);
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

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiverDate);

        getActivity().unregisterReceiver(receiverTime);
        super.onDestroy();
    }


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

    private final BroadcastReceiver receiverTime = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {

                if (mInfoViewModel != null) {
                    mInfoViewModel.notifyPropertyChanged(BR.time);
                }
            }
        }
    };


}
package com.want.vendor.common.upgrade;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.databinding.VendorUpgradeFragmentBinding;

/**
 * View stub.
 */
public class UpgradeFragment extends MFragment {

    public static final String TAG = "UpgradeFragment";

    public static UpgradeFragment newInstance() {
        Bundle args = new Bundle();
        UpgradeFragment fragment = new UpgradeFragment();
        fragment.setArguments(args);
        return new UpgradeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return VendorUpgradeFragmentBinding.inflate(inflater,container,false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
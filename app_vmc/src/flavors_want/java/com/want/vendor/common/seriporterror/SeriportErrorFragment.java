package com.want.vendor.common.seriporterror;

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
public class SeriportErrorFragment extends MFragment {

    public static final String TAG = "UpgradeFragment";

    public static SeriportErrorFragment newInstance() {
        Bundle args = new Bundle();
        SeriportErrorFragment fragment = new SeriportErrorFragment();
        fragment.setArguments(args);
        return new SeriportErrorFragment();
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
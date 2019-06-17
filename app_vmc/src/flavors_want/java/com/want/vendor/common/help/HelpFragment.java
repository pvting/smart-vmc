package com.want.vendor.common.help;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.want.vmc.databinding.VendorCommonHelpLayoutBinding;
import vmc.vendor.VFragment;

/**
 * View stub.
 */
public class HelpFragment extends VFragment implements HelpContract.View {

    private HelpViewModel mHelpViewModel;

    public static HelpFragment newInstance() {
        HelpFragment fragment = new HelpFragment();
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected HelpContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorCommonHelpLayoutBinding.inflate(inflater,container,false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final VendorCommonHelpLayoutBinding binding = DataBindingUtil.getBinding(view);
        mHelpViewModel = new HelpViewModel(getPresenter());
        binding.setModel(mHelpViewModel);
    }

}
package com.want.vendor.home.guide.problem;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.databinding.VendorGuideHintLayoutBinding;

/**
 * View stub.
 */
public class ProblemHelpFragment extends MFragment implements ProblemHelpContract.View {
   private ProblemHelpViewModel mProblemHelpViewModel;
    @Override
    @SuppressWarnings("unchecked")
    protected ProblemHelpContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    public static ProblemHelpFragment newInstance() {
        ProblemHelpFragment fragment = new ProblemHelpFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorGuideHintLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final VendorGuideHintLayoutBinding binding = DataBindingUtil.getBinding(view);
        mProblemHelpViewModel = new ProblemHelpViewModel(getActivity());
        binding.setModel(mProblemHelpViewModel);
    }
}
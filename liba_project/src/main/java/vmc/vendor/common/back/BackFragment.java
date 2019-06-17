package vmc.vendor.common.back;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import vmc.project.databinding.VendorCommonBackLayoutBinding;
import vmc.vendor.VFragment;

/**
 * View stub.
 */
public class BackFragment extends VFragment implements BackContract.View {

    private BackViewModel mBackViewModel;

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
        return VendorCommonBackLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final VendorCommonBackLayoutBinding binding = DataBindingUtil.getBinding(view);
        mBackViewModel = new BackViewModel(getPresenter());
        binding.setModel(mBackViewModel);
    }

    @Override
    public void onPause() {
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
    }

    @Override
    public void onDestroy() {
        if (null != mBackViewModel) {
            mBackViewModel.destroy();
        }
        super.onDestroy();
    }

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


}
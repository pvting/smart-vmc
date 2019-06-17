package com.want.vendor.deliver.fai;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.base.sdk.framework.app.MFragment;
import com.want.vmc.databinding.VendorDeliverFailLayoutBinding;

/**
 * View stub.
 */
public class FaiFragment extends MFragment implements FaiContract.View {
    private FaiViewModel mFaiViewModel;

    //单例模式
    public static  FaiFragment newInstance(){
        FaiFragment faiFragment = new FaiFragment();
        return faiFragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected FaiContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorDeliverFailLayoutBinding.inflate(inflater,container,false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final VendorDeliverFailLayoutBinding binding = DataBindingUtil.getBinding(view);
        mFaiViewModel = new FaiViewModel(getPresenter(), this);
        binding.setModel(mFaiViewModel);
    }

    @Override
    public String getStringNo() {
        String input_no = getArguments().getString("str_no");
        if (TextUtils.isEmpty(input_no)) {
            return "";
        }
        return input_no;

    }

    @Override
    public String getExtra() {
        String extra = getArguments().getString("extra");
        if (TextUtils.isEmpty(extra)) {
            return "";
        }
        return extra;
    }


}
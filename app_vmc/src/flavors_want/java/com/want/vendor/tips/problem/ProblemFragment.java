package com.want.vendor.tips.problem;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.want.vmc.databinding.VendorGuideProblemCodeLayoutBinding;
import com.want.vmc.databinding.VendorOrderGuideProblemCodeLayoutBinding;
import com.want.vmc.databinding.VendorProductInfoProblemLayoutBinding;

import vmc.vendor.VFragment;

/**
 * View stub.
 */
public class ProblemFragment extends VFragment implements ProblemContract.View {


    public static ProblemFragment newInstance() {
        ProblemFragment fragment = new ProblemFragment();
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ProblemContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //bundle 不为空
        Bundle bundle = getArguments();

        if (bundle == null) {
            return VendorGuideProblemCodeLayoutBinding.inflate(inflater, container, false).getRoot();
        }

        int showType = bundle.getInt("showType", 0);
        switch (showType){
            case 0://普通的
                return VendorGuideProblemCodeLayoutBinding.inflate(inflater, container, false).getRoot();
            case 1://带详细说明的
                return VendorProductInfoProblemLayoutBinding.inflate(inflater, container, false).getRoot();
            case 2://带详细说明的
                return VendorProductInfoProblemLayoutBinding.inflate(inflater, container, false).getRoot();
            case 3://带详细说明的
                return VendorProductInfoProblemLayoutBinding.inflate(inflater, container, false).getRoot();
            case 4://带订单的
                return VendorOrderGuideProblemCodeLayoutBinding.inflate(inflater, container, false).getRoot();
            case 5://带详细说明的
                return VendorProductInfoProblemLayoutBinding.inflate(inflater, container, false).getRoot();
            case 6:// 提货码：带详细说明的
                return VendorProductInfoProblemLayoutBinding.inflate(inflater, container, false).getRoot();
            case 7://提货码：带详细说明的
                return VendorProductInfoProblemLayoutBinding.inflate(inflater, container, false).getRoot();
            default://普通的
                return VendorGuideProblemCodeLayoutBinding.inflate(inflater, container, false).getRoot();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //bundle 不为空
        Bundle bundle = getArguments();

        if (bundle == null) {//普通的
            VendorGuideProblemCodeLayoutBinding binding = DataBindingUtil.getBinding(view);
            ProblemViewModel viewModel = new ProblemViewModel(getActivity(), binding.vendorGuideProblemImgCode);
            binding.setModel(viewModel);
            return;
        }

        int showType = bundle.getInt("showType", 0);
        switch (showType){
            case 0://普通的
                VendorGuideProblemCodeLayoutBinding binding = DataBindingUtil.getBinding(view);
                ProblemViewModel viewModel = new ProblemViewModel(getActivity(), binding.vendorGuideProblemImgCode);
                binding.setModel(viewModel);
            return;

            case 1://带详细说明的  有投币器
                VendorProductInfoProblemLayoutBinding binding1 = DataBindingUtil.getBinding(view);
                ProductInfoProblemViewModel viewModel1 = new ProductInfoProblemViewModel(getActivity(), binding1.vendorGuideProblemImgCode,1);
                binding1.setModel(viewModel1);
            return;

            case 2://带详细说明的  无投币器
                VendorProductInfoProblemLayoutBinding binding2 = DataBindingUtil.getBinding(view);
                ProductInfoProblemViewModel viewModel2 = new ProductInfoProblemViewModel(getActivity(), binding2.vendorGuideProblemImgCode,2);
                binding2.setModel(viewModel2);
                return;
            case 3://带详细说明的  门开
                VendorProductInfoProblemLayoutBinding binding3 = DataBindingUtil.getBinding(view);
                ProductInfoProblemViewModel viewModel3 = new ProductInfoProblemViewModel(getActivity(), binding3.vendorGuideProblemImgCode,3);
                binding3.setModel(viewModel3);
                return;
            case 4://带订单号的
                String order = bundle.getString("order");
                VendorOrderGuideProblemCodeLayoutBinding binding4 = DataBindingUtil.getBinding(view);
                OrderProblemViewModel viewModel4 = new OrderProblemViewModel(getActivity(), order, binding4.vendorGuideProblemImgCode);
                binding4.setModel(viewModel4);
                return;
            case 5://带详细说明的  驱动版异常
                VendorProductInfoProblemLayoutBinding binding5 = DataBindingUtil.getBinding(view);
                ProductInfoProblemViewModel viewModel5 = new ProductInfoProblemViewModel(getActivity(), binding5.vendorGuideProblemImgCode,3);
                binding5.setModel(viewModel5);
                return;

            case 6://提货码  带详细说明的  门开
                VendorProductInfoProblemLayoutBinding binding6= DataBindingUtil.getBinding(view);
                ProductInfoProblemViewModel viewModel6 = new ProductInfoProblemViewModel(getActivity(), binding6.vendorGuideProblemImgCode,4);
                binding6.setModel(viewModel6);
                return;

            case 7://提货码 带详细说明的  驱动版异常
                VendorProductInfoProblemLayoutBinding binding7 = DataBindingUtil.getBinding(view);
                ProductInfoProblemViewModel viewModel7 = new ProductInfoProblemViewModel(getActivity(), binding7.vendorGuideProblemImgCode,4);
                binding7.setModel(viewModel7);
                return;

            default://普通的
                VendorGuideProblemCodeLayoutBinding bindingdefault = DataBindingUtil.getBinding(view);
                ProblemViewModel viewModeldefault = new ProblemViewModel(getActivity(), bindingdefault.vendorGuideProblemImgCode);
                bindingdefault.setModel(viewModeldefault);
                return;
        }







    }
}
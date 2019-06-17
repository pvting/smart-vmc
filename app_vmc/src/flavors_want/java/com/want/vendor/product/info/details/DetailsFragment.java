package com.want.vendor.product.info.details;

import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmc.core.model.product.BLLProduct;
import com.want.vmc.databinding.VendorProductDetailsWantLayoutBinding;
import com.want.vendor.product.info.payment.PaymentsFragment;

import vmc.vendor.VFragment;

/**
 * <b>Create Date:</b> 2016/11/14<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public class DetailsFragment extends VFragment implements DetailsContract.View, PaymentsFragment.OnPaymentCallback {
    public DetailsViewModel model;
    BLLProduct product;


    public static DetailsFragment newInstance(BLLProduct product) {
        Bundle args = new Bundle();
        args.putParcelable(Extras.DATA, product);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected DetailsContract.Presenter getPresenter() {
        return super.getPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorProductDetailsWantLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        product = getArguments().getParcelable(Extras.DATA);
        final VendorProductDetailsWantLayoutBinding binding = DataBindingUtil.getBinding(view);
        model = new DetailsViewModel(product);
        binding.setModel(model);
        binding.productDetailsOrderpriceText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    public void onPaymentChanged(int method) {
        String payMentway = "";
        if (model == null) {
            return;
        }
        if (method == -1) {
            model.isSupport(true);
            return;
        }
        switch (method) {
            case 1:
                payMentway = "ALIPAY";
                break;
            case 2:
                payMentway = "WECHATPAY";
                break;
            case 3:
                payMentway = "RMB";
                break;
            case 4:
                payMentway = "WANGBI";
                break;
        }
        if (product != null
                && product.mPromotionDetail != null
                && product.mPromotionDetail.promotion_id > 0
                && product.mPromotionDetail.payment_option != null
                ) {//表示是促销
            if (!product.mPromotionDetail.payment_option.contains(payMentway)) {//不支持类型
                model.isSupport(false);
            } else {//支持类型
                model.isSupport(true);
            }
        } else {//不是促销都支持
            model.isSupport(true);
        }
    }
}
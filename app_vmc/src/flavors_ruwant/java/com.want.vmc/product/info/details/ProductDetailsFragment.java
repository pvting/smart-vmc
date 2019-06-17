package com.want.vmc.product.info.details;

import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.vmc.core.model.product.BLLProduct;
import com.want.vmc.R;
import com.want.vmc.databinding.VendorProductDetailsLayoutBinding;
import vmc.vendor.VFragment;
import com.want.vmc.product.info.payment.PaymentFragment;

/**
 * View stub.
 */
public class ProductDetailsFragment extends VFragment implements ProductDetailsContract.View,
                                                                 PaymentFragment.OnPaymentCallback {
    BLLProduct product;
    ProductDetailsViewModel model;

    public static ProductDetailsFragment newInstance(BLLProduct product) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle bundle = getBundle(product);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ProductDetailsContract.Presenter getPresenter() {
        return super.getPresenter();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return VendorProductDetailsLayoutBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        product = getArguments().getParcelable(Extras.DATA);
        final VendorProductDetailsLayoutBinding binding = DataBindingUtil.getBinding(view);
        model = new ProductDetailsViewModel(getActivity(), product);
        if (view.findViewById(R.id.vendor_old_price) != null) {
            ((TextView) view.findViewById(R.id.vendor_old_price)).getPaint()
                                                                 .setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        binding.setModel(model);

    }
    public void setPrice(int price){
        model.setPromotionPrice(price);
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
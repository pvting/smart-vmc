package com.want.vendor.product.paysuccess.help;

import android.content.Context;
import android.databinding.Bindable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;

import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.vendor.tips.GuideProblemCodeActivity;
import com.want.vendor.uitls.SpannableStringBuilder;

/**
 * ViewModel Stub.
 */
public class HelpViewModel extends AbsViewModel {
    private Context mContext;
    private String outProudctStatus;
    private String payType = "";
    private String order = "";
    private boolean isRefund = false;


    public HelpViewModel(Context context, String tip, String payType, String order) {
        super(context);
        this.mContext = context;
        this.outProudctStatus = tip;
        this.payType = payType;
        this.order = order;
    }

    public HelpViewModel(Context context, String tip, String payType, String order, boolean isRefund) {
        this(context, tip, payType, order);
        this.isRefund = isRefund;
    }


    public HelpViewModel(Context context, String tip, String payType) {
        super(context);
        this.mContext = context;
        this.outProudctStatus = tip;
        this.payType = payType;
    }


    public boolean getPaymentResult() {
        if (outProudctStatus.equals("0")) {
            return true;
        }
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Bindable
    public CharSequence getOutPayStatus() {
        switch (outProudctStatus) {
            case OutStatus.OUT_SUCCESS:
                return "出货成功";
            case OutStatus.OUT_FAILER:
                if (payType != null && payType.contains("RMB")) {
                    return "出货失败\n请按下方退币杆退币！";
                } else {
                    if (isRefund) {
                        return getAutoRefundWordSpannerStr();
                    } else {
                        return "出货失败";
                    }
                }
            case OutStatus.OUT_EXTRA_FAILER:
                return "赠品出货失败";
            case OutStatus.OUT_CODE_FAILER:
                return "出货失败\n提货码仍有效，请稍后重试!";
            case OutStatus.OUT_TIME_OUT:
                if (isRefund) {
                    return getAutoRefundWordSpannerStr();
                } else {
                    return "出货失败";
                }
            case OutStatus.OU_EXTRA_TIME_OUT:
                return "赠品出货失败";
            case OutStatus.OUT_CODE_TIME_OUT:
                return "提货码出货超时\n提货码仍有效，请稍后重试！";
        }
        return "";
    }


    public void skipToHelp(View view) {
        if (TextUtils.isEmpty(order)) {
            GuideProblemCodeActivity.start(view.getContext());
        } else {
            GuideProblemCodeActivity.start(view.getContext(), order);
        }
    }

    @Bindable
    public int getShowHelp() {
        return View.VISIBLE;
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    private CharSequence getAutoRefundWordSpannerStr() {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        SpannableStringBuilder.TextSpan textSpan = new SpannableStringBuilder.TextSpan();
        textSpan.setAbsoluteSize(28, true);
        spannableStringBuilder.append("出货失败")
                .append("\n")
                .append("正在处理这笔订单,若长时间未收到退款,请联系客服", textSpan);
        return spannableStringBuilder.getSpannableString();
    }
}

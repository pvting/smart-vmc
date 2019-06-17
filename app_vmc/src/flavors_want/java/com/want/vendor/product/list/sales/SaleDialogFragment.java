package com.want.vendor.product.list.sales;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.want.base.sdk.framework.app.dialog.BaseDialogFragment;
import com.want.vmc.R;

import vmc.vendor.common.back.BackContract;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.common.back.BackPresenter;
import vmc.vendor.utils.IntentHelper;

/**
 * Created by zhongwenjie on 2016/12/8.
 */

public class SaleDialogFragment extends BaseDialogFragment {

    private static final String FRAGMENT_SALE_TAG = "fragment_sale";
    private static final String FRAGMENT_BACK_TAG = "fragment_back";

    public static SaleDialogFragment newInstance() {
        SaleDialogFragment fragment = new SaleDialogFragment();
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sale_dialog, null);

        return view;
    }

    protected void onConfigDialog(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height=WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        onConfigDialog(dialog);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = null;

        Fragment fragment = fm.findFragmentByTag(FRAGMENT_SALE_TAG);
        if (null == fragment) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }
            fragment = SaleBigmageFragment.newInstance();
             Bundle mBundle = this.getArguments();
            fragment.setArguments(mBundle);
            ft.add(R.id.vendor_sale_dialog,fragment,FRAGMENT_SALE_TAG);
        }

        // 返回按钮
        fragment = fm.findFragmentByTag(FRAGMENT_BACK_TAG);
        if (null == fragment ) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }
            fragment = BackFragment.newInstance();
            ft.add(R.id.vendor_back,fragment,FRAGMENT_BACK_TAG);
        }
        if (null != fragment) {
            new BackPresenter((BackContract.View) fragment){

                @Override
                public void onBack() {
                    super.onBack();
                    dismiss();
                }

                @Override
                public void onTimerEnd() {
                    super.onTimerEnd();
                    IntentHelper.startHome(getActivity());
                }
            };
        }

        if (null != ft) {
            ft.commit();
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}

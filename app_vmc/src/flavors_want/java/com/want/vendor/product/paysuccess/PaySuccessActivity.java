package com.want.vendor.product.paysuccess;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.vmc.core.utils.ConfigUtils;
import com.want.base.sdk.framework.eventbus.MEventBus;
import com.want.vendor.deliver.fai.ClearEditTextEventBus;
import com.want.vendor.product.paysuccess.game.GameFragment;
import com.want.vendor.product.paysuccess.help.HelpContract;
import com.want.vendor.product.paysuccess.help.HelpFragment;
import com.want.vendor.product.paysuccess.help.HelpPresenter;
import com.want.vmc.R;

import vmc.vendor.VActivity;
import vmc.vendor.common.back.BackContract;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.common.back.BackPresenter;
import vmc.vendor.common.back.BackViewModel;
import vmc.vendor.utils.IntentHelper;

public class PaySuccessActivity extends VActivity {

    public static final String PAY_SUCCESS_HELP = "help";
    public static final String PAY_SUCCESS_CAMPAIGN = "campaign";
    private BackPresenter mBackPresenter;

    public static void start(PayProductInfo payProductInfo) {
        Intent starter = new Intent(payProductInfo.getContext(), PaySuccessActivity.class);
        starter.putExtra("tip", payProductInfo.getTip());
        starter.putExtra("payType", payProductInfo.getPayType());
        starter.putExtra("id", payProductInfo.getProductId());
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        payProductInfo.getContext().startActivity(starter);
    }

    public static void start(Context context, String tip, String payType, int productId) {
        Intent starter = new Intent(context, PaySuccessActivity.class);
        starter.putExtra("tip", tip);
        starter.putExtra("payType", payType);
        starter.putExtra("id", productId);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    public static void start(Context context, String tip, String payType, int productId, String order) {
        Intent starter = new Intent(context, PaySuccessActivity.class);
        starter.putExtra("tip", tip);
        starter.putExtra("payType", payType);
        starter.putExtra("id", productId);
        starter.putExtra("order", order);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    public static void start(Context context, String tip, String payType, int productId, String order, boolean isRefund) {
        Intent starter = new Intent(context, PaySuccessActivity.class);
        starter.putExtra("tip", tip);
        starter.putExtra("payType", payType);
        starter.putExtra("id", productId);
        starter.putExtra("order", order);
        starter.putExtra("isRefund", isRefund);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_payment_successpay_layout);

        final Bundle extras;
        if (null != savedInstanceState) {
            extras = savedInstanceState;
        } else {
            extras = getIntent().getExtras();
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // 问题求助
        Fragment fragment = fm.findFragmentByTag(PAY_SUCCESS_HELP);
        if (null == fragment && null != findViewById(R.id.payment_success_help)) {
            fragment = HelpFragment.newInstance();
            ft = fm.beginTransaction();
            fragment.setArguments(extras);
            ft.add(R.id.payment_success_help, fragment, PAY_SUCCESS_HELP);
        }
        if (null != fragment) {
            new HelpPresenter((HelpContract.View) fragment);
        }
        // 活动（游戏）
        fragment = fm.findFragmentByTag(PAY_SUCCESS_CAMPAIGN);
        if (null == fragment && null != findViewById(R.id.payment_success_campaign)) {
            fragment = GameFragment.newInstance();
            fragment.setArguments(extras);
            ft.add(R.id.payment_success_campaign, fragment, PAY_SUCCESS_CAMPAIGN);
        }
        // 返回
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_BACK);
        if (null == fragment && null != findViewById(R.id.payment_success_back)) {
            fragment = BackFragment.newInstance();
            ft.add(R.id.payment_success_back, fragment, FRAGMENT_TAG_BACK);

        }
        if (null != fragment) {
            mBackPresenter = new BackPresenter((BackContract.View) fragment) {
                @Override
                public void onBack() {
                    super.onBack();
                    MEventBus.getDefault().post(new ClearEditTextEventBus());
                    IntentHelper.startProductList(PaySuccessActivity.this);
                    PaySuccessActivity.this.finish();
                }

                @Override
                public void onTimerEnd() {
                    super.onTimerEnd();
                    IntentHelper.startHome(PaySuccessActivity.this);
                    PaySuccessActivity.this.finish();
                }
            };
        }
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ConfigUtils.getConfig(PaySuccessActivity.this) != null && ConfigUtils.getConfig(PaySuccessActivity.this).vmc_count_down_time_settings != null) {
            if (ConfigUtils.getConfig(PaySuccessActivity.this).vmc_count_down_time_settings.general_page_countdown !=
                    0) {
                mBackPresenter.setTimeLeft(ConfigUtils.getConfig(PaySuccessActivity.this).vmc_count_down_time_settings.general_page_countdown);
            } else {
                mBackPresenter.setTimeLeft(BackViewModel.DEFAULT_TIMELEFT);
            }
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                // 返回
                Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_BACK);
                if (!PaySuccessActivity.this.isFinishing() && null != fragment && fragment.isHidden()) {
                    ft.show(fragment);
                    findViewById(R.id.vendor_outproduct_loading).setVisibility(View.INVISIBLE);
                    ft.commitAllowingStateLoss();
                }
            }
        }, 10000);


    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}

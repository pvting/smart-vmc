package com.want.vmc.product.info;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.vmc.core.BLLController;
import com.vmc.core.OdooAction;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.utils.BLLProductUtils;
import com.vmc.core.utils.ConfigUtils;
import com.want.vmc.R;
import com.want.vmc.product.info.details.ProductDetailsContract;
import com.want.vmc.product.info.details.ProductDetailsFragment;
import com.want.vmc.product.info.details.ProductDetailsPresenter;
import com.want.vmc.product.info.payment.PaymentContract;
import com.want.vmc.product.info.payment.PaymentFragment;
import com.want.vmc.product.info.payment.PaymentPresenter;
import com.want.vmc.product.info.payment.ReChoosePaymentFragment;
import com.want.vmc.product.info.scan.ScanContract;
import com.want.vmc.product.info.scan.ScanFragment;
import com.want.vmc.product.info.scan.ScanPresenter;

import vmc.core.log;
import vmc.vendor.VActivity;
import vmc.vendor.common.back.BackContract;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.common.back.BackPresenter;
import vmc.vendor.common.back.BackViewModel;
import vmc.vendor.utils.IntentHelper;

/**
 * <b>Create Date:</b> 26/10/2016<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class ProductInfoActivity extends VActivity implements PaymentFragment.OnPaymentCallback,
                                                              ReChoosePaymentFragment.OnResetPaymentCallback {
    private static final String FRAGMENT_TAG_DETAILS = "details";
    private static final String FRAGMENT_TAG_PAYMENT_PICKER = "payment_picker";
    private static final String FRAGMENT_TAG_PAYMENT_PICKED = "payment_picked";
    private static final String FRAGMENT_TAG_SCANNER = "scanner";
    private BLLStackProduct mBLLStackProduct;
    private BLLProduct product;

    private final int CASHPAYMENT = 3;
    private android.app.AlertDialog mAlertDialog;
    private BackPresenter mBackPresenter;
    private final String TAG = "ProductInfoActivity";
    public boolean isCanBack = true;
    private boolean isCashing;
    public boolean mOutGooding = true;
    private View mViewBack;
    private View mViewPay;

    /**
     * 接收BL层广播 现金支付，进行支付方式的切换
     */
    private BroadcastReceiver mVmcMoneyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.d(TAG, "onReceive:mVmcMoneyReceiver --begin");
            final String action = intent.getAction();
            if (!mOutGooding) {
                return;
            }
            if (TextUtils.equals(action, OdooAction.BLL_RECIVERMONEY_TO_UI)) {
                onPaymentChanged(CASHPAYMENT);
                //每接收到一个投币的广播，时间重置90'
                mBackPresenter.setTimeLeft(90);
            }
            log.d(TAG, "onReceive:mVmcMoneyReceiver --end");
        }

    };

    //结束交易广播出现
    private BroadcastReceiver mVmcCancelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.d(TAG, "交易结束广播:begin");
            final String action = intent.getAction();
            if (TextUtils.equals(action, OdooAction.BLL_DEAL_FINISH_TO_UI)) {
                ProductInfoActivity.this.finish();
            }
            log.d(TAG, "交易结束广播:end");
        }
    };

    //卡禁止广播
    private BroadcastReceiver cardBanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.d(TAG, "onReceive:begin");
            final String action = intent.getAction();
            if (TextUtils.equals(action, OdooAction.BLL_CARD_BAN_TO_UI)) {
                log.d(TAG, "卡禁止");
                onPaymentChanged(CASHPAYMENT);
                isCashing = true;
            }
            log.d(TAG, "onReceive:end");
        }
    };


    //卡允许广播
    private BroadcastReceiver cardCanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.d(TAG, "onReceive:begin");
            final String action = intent.getAction();
            if (TextUtils.equals(action, OdooAction.BLL_CARD_CAN_TO_UI)) {
                isCashing = false;
            }
            log.d(TAG, "onReceive:end");
        }
    };
    /**
     * 接收BL层准备出货的广播 此时 隐藏掉当前页面的按钮
     */
    private BroadcastReceiver mVmcPrepareReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.d(TAG, "onReceive:mVmcPrepareReceiver --begin");
            final String action = intent.getAction();
            mOutGooding = false;
            if (TextUtils.equals(action, OdooAction.BLL_PRE_OUTGOODS_TO_UI)) {
                //出货过程中 隐藏掉 详情页面的按钮
                mViewPay.setVisibility(View.INVISIBLE);
                mViewBack.setVisibility(View.INVISIBLE);
            }
            log.d(TAG, "onReceive:mVmcPrepareReceiver --end");
        }
    };
    /**
     * 接收BL层支付成功的广播
     */
    private BroadcastReceiver mVmcPaySucessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.d(TAG, "onReceive:mVmcPrepareReceiver --begin");
            final String action = intent.getAction();
            if (TextUtils.equals(action, OdooAction.BLL_PAY_STATUS_TO_UI)) {
                //出货过程中 隐藏掉 详情页面的按钮
                mViewPay.setVisibility(View.INVISIBLE);
                mViewBack.setVisibility(View.INVISIBLE);
                mBackPresenter.setTimeLeft(90);
            }
            log.d(TAG, "onReceive:mVmcPrepareReceiver --end");
        }
    };
    /**
     * 接收BL层广播 出货结果界面
     */
    private BroadcastReceiver mVmcOutGoodsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.d(TAG, "onReceive:mVmcOutGoodsReceiver --begin");
            final String action = intent.getAction();
            if (TextUtils.equals(action, OdooAction.BLL_OUTGOODS_TO_UI)) {
                int totalNum = intent.getIntExtra("totalNum", 0);
                int outIndex = intent.getIntExtra("outIndex", 0);
                boolean outGoodsState = intent.getBooleanExtra("outGoodsState", true);
                //没有赠品的情况
                if (outIndex == 1 && totalNum == 1) {
                    log.i(TAG, "只有一个商品出货，当前第一个");
                    mOutGooding = true;
                    mViewPay.setVisibility(View.VISIBLE);
                    mViewBack.setVisibility(View.VISIBLE);
                    ProductInfoActivity.this.finish();
                }
                //有赠品，出第一个货失败的情况
                if (totalNum == 2 && outIndex == 1) {
                    log.i(TAG, "有2个商品出货,当前第一个");
                    if (outGoodsState) {
                        log.e(TAG, "first good outed ");
                    }
                    mOutGooding = true;
                    mViewPay.setVisibility(View.VISIBLE);
                    mViewBack.setVisibility(View.VISIBLE);
                }

                //有赠品的情况，出赠品失败的情况
                if (totalNum == 2 && outIndex == 2) {
                    log.i(TAG, "有2个商品出货,当前第2个");
                    mOutGooding = true;
                    mViewPay.setVisibility(View.VISIBLE);
                    mViewBack.setVisibility(View.VISIBLE);
                    ProductInfoActivity.this.finish();
                }
            }
            log.d(TAG, "onReceive:mVmcOutGoodsReceiver --end");
        }
    };

    public static void start(Context context, BLLProduct product) {
        Intent starter = new Intent(context, ProductInfoActivity.class);
        starter.putExtra(Extras.DATA, product);
        if (!(context instanceof Activity)) {
            starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestToolbar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_product_info_activity);

        mBLLStackProduct = getIntent().getParcelableExtra(Extras.DATA);

        product = BLLProductUtils.getProductById(mBLLStackProduct.product_id);

        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = null;

        // 商品信息mProduct
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_DETAILS);
        if (null == fragment && null != findViewById(R.id.product_info_details)) {
            ft = fm.beginTransaction();
            fragment = ProductDetailsFragment.newInstance(product);
            ft.add(R.id.product_info_details, fragment, FRAGMENT_TAG_DETAILS);
        }
        new ProductDetailsPresenter((ProductDetailsContract.View) fragment);

        // 支付方式选择
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_PAYMENT_PICKER);
        mViewPay = findViewById(R.id.product_info_payment);
        if (null == fragment && null != findViewById(R.id.product_info_payment)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }
            fragment = PaymentFragment.newInstance();
            ft.add(R.id.product_info_payment, fragment, FRAGMENT_TAG_PAYMENT_PICKER);
        }
        if (null != fragment) {
            new PaymentPresenter((PaymentContract.View) fragment);
        }

        // 返回
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_BACK);
        mViewBack = findViewById(R.id.vendor_back);
        if (null == fragment && null != findViewById(R.id.vendor_back)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }
            fragment = BackFragment.newInstance();
            ft.add(R.id.vendor_back, fragment, FRAGMENT_TAG_BACK);
        }
        if (null != fragment) {
            //取消交易
            mBackPresenter = new BackPresenter((BackContract.View) fragment) {
                @Override
                public void onBack() {
                    super.onBack();
                    if (!isCanBack) {
                        return;
                    }
                    //取消交易
                    BLLController.getInstance().cancelDeal();
                    ProductInfoActivity.this.finish();
                }

                @Override
                public void onTimerEnd() {
                    super.onTimerEnd();
                    //取消交易
                    BLLController.getInstance().cancelDeal();
                    IntentHelper.startHome(ProductInfoActivity.this);
                    ProductInfoActivity.this.finish();
                }
            };
        }
        // 扫描区
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_SCANNER);
        if (null == fragment && null != findViewById(R.id.product_info_scanner)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }
            fragment = ScanFragment.newInstance(product);
            ft.add(R.id.product_info_scanner, fragment, FRAGMENT_TAG_SCANNER);
        }
        if (null != fragment) {
            new ScanPresenter((ScanContract.View) fragment);
        }
        if (null != ft) {
            ft.commit();
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        if (BLLController.getInstance().isCardBan()) {
            isCashing = true;
            onPaymentChanged(CASHPAYMENT);
        }
    }

    private void initBoardCast(Context context) {
        log.d(TAG, "initBoardCast:begin");
        //交易结束广播接收
        IntentFilter filter1 = new IntentFilter(OdooAction.BLL_DEAL_FINISH_TO_UI);
        context.registerReceiver(mVmcCancelReceiver, filter1);

        IntentFilter filter2 = new IntentFilter(OdooAction.BLL_CARD_BAN_TO_UI);
        context.registerReceiver(cardBanReceiver, filter2);

        IntentFilter filter3 = new IntentFilter(OdooAction.BLL_CARD_CAN_TO_UI);
        context.registerReceiver(cardCanReceiver, filter3);

        IntentFilter filter4 = new IntentFilter(OdooAction.BLL_OUTGOODS_TO_UI);
        context.registerReceiver(mVmcOutGoodsReceiver, filter4);

        IntentFilter filter5 = new IntentFilter(OdooAction.BLL_PRE_OUTGOODS_TO_UI);
        context.registerReceiver(mVmcPrepareReceiver, filter5);

        IntentFilter filter6 = new IntentFilter(OdooAction.BLL_PAY_STATUS_TO_UI);
        context.registerReceiver(mVmcPaySucessReceiver, filter6);

        IntentFilter filter7 = new IntentFilter(OdooAction.BLL_RECIVERMONEY_TO_UI);
        context.registerReceiver(mVmcMoneyReceiver, filter7);

        log.d(TAG, "initBoardCast:end");
    }

    @Override
    public void onPaymentChanged(int method) {
        // 1. 支付方式变换的时候被调用
        // 2. 替换选择支付方式页面为"重选支付方式"页面
        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_PAYMENT_PICKER);
        if (null != fragment) {
            ft.hide(fragment);
        }
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_DETAILS);
        if (fragment != null) {
            ((ProductDetailsFragment) fragment).onPaymentChanged(method);
        }
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_PAYMENT_PICKED);
        if (null != fragment) {
            ft.show(fragment);
            final ReChoosePaymentFragment f = (ReChoosePaymentFragment) fragment;
            f.setPaymentMethod(method);
        } else {
            fragment = ReChoosePaymentFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putInt(Extras.DATA, method);
            fragment.setArguments(bundle);
            ft.add(R.id.product_info_payment, fragment, FRAGMENT_TAG_PAYMENT_PICKED);
        }
        ft.commit();

        //切换支付区域
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_SCANNER);
        if (null != fragment) {
            if (fragment instanceof PaymentFragment.OnPaymentCallback) {
                ((PaymentFragment.OnPaymentCallback) fragment).onPaymentChanged(method);
            }
        }
    }


    @Override
    public void onPaymentReset() {
        if (isCashing) {
            showTipDialog();
            return;
        }

        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_PAYMENT_PICKED);
        if (null != fragment) {
            ft.hide(fragment);
        }

        fragment = fm.findFragmentByTag(FRAGMENT_TAG_PAYMENT_PICKER);
        if (null == fragment) {
            fragment = PaymentFragment.newInstance();
            ft.add(R.id.product_info_payment, fragment, FRAGMENT_TAG_PAYMENT_PICKER);
        } else {
            ft.show(fragment);
        }

        ft.commit();

        //切换支付区域
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_SCANNER);
        if (null != fragment) {
            if (fragment instanceof PaymentFragment.OnPaymentCallback) {
                ((PaymentFragment.OnPaymentCallback) fragment).onPaymentChanged(-1);
            }
        }
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_DETAILS);
        if (fragment != null) {
            ((ProductDetailsFragment) fragment).onPaymentChanged(-1);
        }
    }

    /**
     * 当已投入现金切换支付方式，提示用户先退币
     */
    private void showTipDialog() {
        mAlertDialog = new android.app.AlertDialog.Builder(ProductInfoActivity.this).create();
        mAlertDialog.show();
        Window window = mAlertDialog.getWindow();
        window.setContentView(R.layout.vendor_tip_dialog);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        Button button = (Button) window.findViewById(R.id.commit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });


    }


    @Override
    protected void onDestroy() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBoardCast(ProductInfoActivity.this);
        if (ConfigUtils.getConfig(ProductInfoActivity.this).vmc_count_down_time_settings.purchase_page_countdown != 0) {
            mBackPresenter.setTimeLeft(ConfigUtils.getConfig(ProductInfoActivity.this).vmc_count_down_time_settings.purchase_page_countdown);
        } else {
            mBackPresenter.setTimeLeft(BackViewModel.DEFAULT_TIMELEFT);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        BLLController.getInstance().cancelSelectProduct();
        BLLController.getInstance().cancelOrder(this);
        unregisterReceiver(mVmcCancelReceiver);
        unregisterReceiver(mVmcOutGoodsReceiver);
        unregisterReceiver(cardBanReceiver);
        unregisterReceiver(cardCanReceiver);
        unregisterReceiver(mVmcPrepareReceiver);
        unregisterReceiver(mVmcPaySucessReceiver);
        unregisterReceiver(mVmcMoneyReceiver);
    }
}

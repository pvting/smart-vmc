package com.want.vendor.product.info;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.vmc.core.BLLController;
import com.vmc.core.OdooAction;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.utils.BLLOrderUtils;
import com.vmc.core.utils.BLLPayMentController;
import com.vmc.core.utils.BLLProductUtils;
import com.vmc.core.utils.ConfigUtils;
import com.want.vendor.product.info.back.BackContract;
import com.want.vendor.product.info.back.BackFragment;
import com.want.vendor.product.info.back.BackPresenter;
import com.want.vendor.product.info.back.BackViewModel;
import com.want.vendor.product.info.details.DetailsContract;
import com.want.vendor.product.info.details.DetailsFragment;
import com.want.vendor.product.info.details.DetailsPresenter;
import com.want.vendor.product.info.outgooding.OutGoodingContract;
import com.want.vendor.product.info.outgooding.OutGoodingFragment;
import com.want.vendor.product.info.outgooding.OutGoodingPresenter;
import com.want.vendor.product.info.payment.PaymentsContract;
import com.want.vendor.product.info.payment.PaymentsFragment;
import com.want.vendor.product.info.payment.PaymentsPresenter;
import com.want.vendor.product.info.payment.ReSelectPaymentsFragment;
import com.want.vendor.product.info.scanner.ScannerContract;
import com.want.vendor.product.info.scanner.ScannerFragment;
import com.want.vendor.product.info.scanner.ScannerPresenter;
import com.want.vendor.product.paysuccess.PaySuccessActivity;
import com.want.vendor.tips.GuideProblemCodeActivity;
import com.want.vendor.tips.TipsDialogFragment;
import com.want.vmc.R;

import java.util.Observable;

import vmc.core.log;
import vmc.vendor.Constants;
import vmc.vendor.VActivity;
import vmc.vendor.utils.IntentHelper;

import static com.want.vendor.ui.view.UtilToast.toastInfo;

/**
 * <b>Create Date:</b> 2016/11/14<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b>
 * 产品详情页面
 * <br>
 */
public class ProductInfoActivity extends VActivity implements PaymentsFragment.OnPaymentCallback,
                                                              ReSelectPaymentsFragment.OnResetPaymentCallback,
                                                              TipsDialogFragment.OnDissmissLisener {

    private static final String FRAGMENT_DETAILS_TAG = "f_details";
    private static final String FRAGMENT_PAYMENTS_TAG_UNSELECTED = "f_payments_unselected";
    private static final String FRAGMENT_PAYMENTS_TAG_SELECTED = "f_payments_selected";
    private static final String FRAGMENT_SCANNER_TAG = "f_scanner";
    //正在出货Fragment
    private static final String FRAGMENT_OUTGOODING_TAG = "f_out_godding";

    private BLLProduct mProduct;
    public boolean mIsCashPayment;
    private Fragment mFragment;
    private final int CASHPAYMENT = 3;
    private final String TAG = "ProductInfoActivity";

    private com.want.vendor.product.info.back.BackPresenter mBackPresenter;
    private NetWorkObserver observer;
    private boolean isCanBack = true;
    private View mViewBack;
    private View mViewPayScanner;
    private View mViewPayInfo;
    private View mViewOutgooding;


    private TextView tvOutGoods;

    public boolean isUnOutGooding = true;

    private boolean isCashing = false;


    /**
     * 接收BL层广播 现金支付，进行支付方式的切换
     */
    private BroadcastReceiver mVmcMoneyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.i(TAG, "onReceive:mVmcMoneyReceiver --begin");
            if(ProductInfoActivity.this.isFinishing()){
                log.e(TAG,"onReceive:mVmcMoneyReceiver --isFinishing");
                return;
            }

            final String action = intent.getAction();

            if (!isUnOutGooding) {//如果正在出货，投币页面不反应
                return;
            }

            if (TextUtils.equals(action, OdooAction.BLL_RECIVERMONEY_TO_UI)) {
                onPaymentChanged(CASHPAYMENT);
                //每接收到一个投币的广播，时间重置
                if (ConfigUtils.getConfig(ProductInfoActivity.this) != null &&
                    ConfigUtils.getConfig(ProductInfoActivity.this).vmc_count_down_time_settings != null) {
                    log.i(TAG, "onReceive:mVmcMoneyReceiver: 时间重置");
                    if (ConfigUtils.getConfig(ProductInfoActivity.this).vmc_count_down_time_settings.purchase_page_countdown !=
                        0) {
                        mBackPresenter.setTimeLeft(ConfigUtils.getConfig(ProductInfoActivity.this).vmc_count_down_time_settings.purchase_page_countdown);
                    } else {
                        mBackPresenter.setTimeLeft(BackViewModel.DEFAULT_TIMELEFT);
                    }
                }
            }
            log.i(TAG, "onReceive:mVmcMoneyReceiver --end");
        }

    };


    /**
     * 接收BLL层支付成功的广播
     */
    private BroadcastReceiver mVmcPaySucessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            log.i(TAG, "onReceive:mVmcPrepareReceiver --begin");
            if(ProductInfoActivity.this.isFinishing()){
                log.e(TAG,"onReceive:mVmcPaySucessReceiver --isFinishing");
                return;
            }
            final String action = intent.getAction();
            if (TextUtils.equals(action, OdooAction.BLL_PAY_STATUS_TO_UI)) {
                isUnOutGooding = false;

                setOutGoodAnimation();

                if (ConfigUtils.getConfig(ProductInfoActivity.this) != null &&
                    ConfigUtils.getConfig(ProductInfoActivity.this).vmc_count_down_time_settings != null) {
                    log.i(TAG, "onReceive:mVmcPaySucessReceiver:时间重置");
                    if (ConfigUtils.getConfig(ProductInfoActivity.this).vmc_count_down_time_settings.purchase_page_countdown !=
                        0) {
                        mBackPresenter.setTimeLeft(ConfigUtils.getConfig(ProductInfoActivity.this).vmc_count_down_time_settings.purchase_page_countdown);
                    } else {
                        mBackPresenter.setTimeLeft(BackViewModel.DEFAULT_TIMELEFT);
                    }
                }
            }
            log.i(TAG, "onReceive:mVmcPrepareReceiver --end");
        }
    };


    /**
     * 接受BLL层网络发生变化
     */
    private BroadcastReceiver mVmcNetStateReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(ProductInfoActivity.this.isFinishing()){
                log.e(TAG,"onReceive:mVmcNetStateReceive --isFinishing");
                return;
            }
            if (TextUtils.equals(action, OdooAction.BLL_NET_STATE_TO_UI)) {
                boolean hasCoinBox = intent.getBooleanExtra("hasCoinBox", false);
                if (hasCoinBox) {
                    //有投币器
                    GuideProblemCodeActivity.start(context, 1);
                } else {
                    //无投币器
                    GuideProblemCodeActivity.start(context, 2);
                }

            }
        }
    };


    /**
     * 接受BLL层开门广播
     */
    private BroadcastReceiver mVmcDoorStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            log.i(TAG, "onReceive:mVmcOutGoodsReceiver --begin");
            if(ProductInfoActivity.this.isFinishing()){
                log.e(TAG,"onReceive:mVmcDoorStateReceiver --isFinishing");
                return;
            }
            final String action = intent.getAction();
            if (TextUtils.equals(action, OdooAction.BLL_DOOR_STATE_TO_UI_SELECTED_PRODUCT)) {
                GuideProblemCodeActivity.start(context, 3);
            }
        }
    };


    /**
     * 接收BL层广播 出货结果界面
     */
    private BroadcastReceiver mVmcOutGoodsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.i(TAG, "onReceive:mVmcOutGoodsReceiver --begin");
            if(ProductInfoActivity.this.isFinishing()){
                log.e(TAG,"onReceive:mVmcOutGoodsReceiver --isFinishing");
                return;
            }
            final String action = intent.getAction();
            if (TextUtils.equals(action, OdooAction.BLL_OUTGOODS_TO_UI)) {

                int totalNum = intent.getIntExtra("totalNum", 0);
                int outIndex = intent.getIntExtra("outIndex", 0);
                String order = intent.getStringExtra("currentOrderId");
                String payType = intent.getStringExtra("payMentType");
                boolean isRefund = intent.getBooleanExtra("isRefund", false);

                // 0:出货成功  1:出货失败 2:赠品出货失败  3: 提货码出货失败  4:出货超时  5:赠品出货超时  6:提货码出货超时

                boolean outGoodsState = intent.getBooleanExtra("outGoodsState", true);
                //没有赠品的情况
                if (outIndex == 1 && totalNum == 1) {
                    log.i(TAG, "只有一个商品出货，当前第一个");
                    if (outGoodsState) {
                        PaySuccessActivity.start(context, "0", payType, mProduct.product_id, order, isRefund);
                    } else {
                        if (!TextUtils.isEmpty(payType) && payType.equals("CODE")) {
                            PaySuccessActivity.start(context,
                                                     "3",
                                                     payType,
                                                     mProduct.product_id,
                                                     order,
                                                     isRefund);
                        } else {
                            PaySuccessActivity.start(context,
                                                     "1",
                                                     payType,
                                                     mProduct.product_id,
                                                     order,
                                                     isRefund);
                        }
                    }
                    isUnOutGooding = true;
                    ProductInfoActivity.this.finish();
                }
                //有赠品，出第一个货失败的情况
                if (totalNum == 2 && outIndex == 1) {
                    log.i(TAG, "有2个商品出货,当前第一个");
                    isUnOutGooding = true;
                    if (outGoodsState) {
                        log.i(TAG, "first good outed ");
                    } else {
                        PaySuccessActivity.start(context, "1", payType, mProduct.product_id, order, isRefund);
                        ProductInfoActivity.this.finish();
                    }

                }
                //有赠品的情况，出赠品失败的情况
                if (totalNum == 2 && outIndex == 2) {
                    log.i(TAG, "有2个商品出货,当前第2个");
                    if (outGoodsState) {
                        PaySuccessActivity.start(context, "0", payType, mProduct.product_id, order, isRefund);
                    } else {
                        PaySuccessActivity.start(context, "2", payType, mProduct.product_id, order, isRefund);
                    }
                    isUnOutGooding = true;
                    ProductInfoActivity.this.finish();
                }

            }
            log.i(TAG, "onReceive:mVmcOutGoodsReceiver --end");
        }
    };


    //卡禁止广播 久保田
    private BroadcastReceiver cardBanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.i(TAG, "onReceive:begin");
            if(ProductInfoActivity.this.isFinishing()){
                log.e(TAG,"onReceive:cardBanReceiver --isFinishing");
                return;
            }
            final String action = intent.getAction();
            if (TextUtils.equals(action, OdooAction.BLL_CARD_BAN_TO_UI)) {
                log.i(TAG, "卡禁止");
                onPaymentChanged(CASHPAYMENT);
                isCashing = true;
            }
            log.i(TAG, "onReceive:end");
        }
    };


    //卡允许广播 久保田
    private BroadcastReceiver cardCanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.i(TAG, "onReceive:begin");
            if(ProductInfoActivity.this.isFinishing()){
                log.e(TAG,"onReceive:cardCanReceiver --isFinishing");
                return;
            }
            final String action = intent.getAction();
            if (TextUtils.equals(action, OdooAction.BLL_CARD_CAN_TO_UI)) {
                isCashing = false;
            }
            log.i(TAG, "onReceive:end");
        }
    };

    /**
     * 自商品列表进入
     *
     * @param context
     * @param product
     */
    public static void start(Context context, BLLStackProduct product) {
        Intent starter = new Intent(context, ProductInfoActivity.class);
        starter.putExtra(Extras.DATA, product);
        if (!(context instanceof Activity)) {
            starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(starter);
    }

    /**
     * 自提货码进入
     *
     * @param context
     * @param product
     * @param isPay
     */
    public static void start(Context context, BLLStackProduct product, boolean isPay) {
        Intent starter = new Intent(context, ProductInfoActivity.class);
        starter.putExtra(Extras.DATA, product);
        starter.putExtra("isPay", isPay);
        if (!(context instanceof Activity)) {
            starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.i(TAG, "start ProductInfoActivity");

        setContentView(R.layout.vendor_product_info_activity_layout);

        initBoardCast(ProductInfoActivity.this);

        mViewBack = findViewById(R.id.vendor_product_info_back);
        mViewPayScanner = findViewById(R.id.product_info_scanner);
        mViewPayInfo = findViewById(R.id.product_info_payment);
        mViewOutgooding = findViewById(R.id.vendor_product_info_outgooding);
        tvOutGoods = (TextView) findViewById(R.id.vendor_outproduct_loading);

        BLLStackProduct BLLStackProduct = getIntent().getParcelableExtra(Extras.DATA);

        BLLProduct selectProduct = BLLController.getInstance().getSelectProduct();

        if (null == selectProduct) {

            mProduct = BLLProductUtils.getProductById(BLLStackProduct.product_id);
        } else {
            mProduct = selectProduct;
        }

        observer = new NetWorkObserver() {
            @Override
            public void update(Observable observable, Object data) {
                if (!(boolean) data) {
                    log.i(TAG, "NetWorkObserver-->update:断网了");
                    NetWorkDeal();
                }
            }
        };

        NetWorkObservable.getInstance().addObserver(observer);

        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = null;

        // 商品信息
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_DETAILS_TAG);
        if (null == fragment && null != findViewById(R.id.product_info_details)) {
            ft = fm.beginTransaction();
            fragment = DetailsFragment.newInstance(mProduct);
            ft.add(R.id.product_info_details, fragment, FRAGMENT_DETAILS_TAG);
        }

        if (null != fragment) {
            new DetailsPresenter((DetailsContract.View) fragment);
        }

        // 支付方式选择
        fragment = fm.findFragmentByTag(FRAGMENT_PAYMENTS_TAG_UNSELECTED);
        if (null == fragment) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }
            fragment = PaymentsFragment.newInstance(mProduct);
            ft.add(R.id.product_info_payment, fragment, FRAGMENT_PAYMENTS_TAG_UNSELECTED);

        }

        if (null != fragment) {
            new PaymentsPresenter((PaymentsContract.View) fragment);
        }

        // 返回
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_BACK);
        if (null == fragment && null != mViewBack) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }
            fragment = BackFragment.newInstance();
            ft.add(R.id.vendor_product_info_back, fragment, FRAGMENT_TAG_BACK);
        }

        if (null != fragment) {
            mFragment = fragment;
            mBackPresenter = new BackPresenter((BackContract.View) mFragment) {
                @Override
                public void onBack() {
                    super.onBack();
                    log.i(TAG, "click back button");
                    if (!BLLController.getInstance().canCancelOrder()) {//如果不能取消订单,不做任何反应
                        log.i(TAG, "back:order have to pay  not allow back");
                        return;
                    }

                    if (!isCanBack) {//久保田
                        log.i(TAG, "back: kubota not allow back");
                        return;
                    }


                    if (BLLPayMentController.getInstance().isLooperPayStatus()) {
                        log.i(TAG, "back: online payment,show dialog");
                        TipsDialogFragment dialogFragment = new TipsDialogFragment();
                        FragmentManager fm = getSupportFragmentManager();
                        dialogFragment.show(fm);
                        mFragment.onPause();//停止倒计时
                        dialogFragment.setOnDissmissLisener(ProductInfoActivity.this);
                        return;
                    }

                    if (mIsCashPayment) {  // 如果是现金支付 点击返回，重置支付方式
                        log.i(TAG, "back: cash payment,reset payment");
                        onPaymentReset();
                        return;

                    }

                    //如果没有选择支付方式
                    log.i(TAG, "back: unselected payment and not pay,cancel the order");
                    BLLController.getInstance().cancelSelectProduct();
                    BLLController.getInstance().cancelOrder(ProductInfoActivity.this);
                    // 返回商品列表
                    ProductInfoActivity.this.finish();

                }


                @Override
                public void onTimerEnd() {
                    super.onTimerEnd();
                    // 取消交易,返回商品列表
                    log.i(TAG, "onTimerEnd: productinfo the timeout");

                    //超时取消订单
                    BLLController.getInstance().cancelSelectProduct();
                    BLLController.getInstance().cancelOrder(ProductInfoActivity.this);
                    IntentHelper.startHome(ProductInfoActivity.this);//返回首页
                    ProductInfoActivity.this.finish();
                }
            };
        }


        //是否加载正在出货动画；
        fragment = fm.findFragmentByTag(FRAGMENT_OUTGOODING_TAG);
        if (null == fragment && null != findViewById(com.want.vmc.R.id.vendor_product_info_outgooding)) {
            if (null == ft) ft = fm.beginTransaction();
            if (getIntent().getBooleanExtra("isPay", false)) {//从提货码进入时
                setOutGoodAnimation();
            }
            fragment = OutGoodingFragment.newInstance(mProduct, true);
            ft.add(com.want.vmc.R.id.vendor_product_info_outgooding, fragment, FRAGMENT_OUTGOODING_TAG);
        }
        if (null != fragment) {
            new OutGoodingPresenter((OutGoodingContract.View) fragment);
        }


        // 扫码区
        fragment = fm.findFragmentByTag(FRAGMENT_SCANNER_TAG);
        if (null == fragment && null != findViewById(com.want.vmc.R.id.product_info_scanner)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }

            fragment = ScannerFragment.newInstance(mProduct);
            ft.add(com.want.vmc.R.id.product_info_scanner, fragment, FRAGMENT_SCANNER_TAG);
        }

        if (null != fragment) {
            new ScannerPresenter((ScannerContract.View) fragment);
        }

        if (null != ft) {
            ft.commit();
        }
    }


    /**
     * 展示出货动画
     */
    private void setOutGoodAnimation() {
        //出货过程中 隐藏掉 详情页面的按钮
        mViewBack.setVisibility(View.INVISIBLE);
        mViewPayScanner.setVisibility(View.INVISIBLE);
        mViewPayInfo.setVisibility(View.INVISIBLE);
        mViewOutgooding.setVisibility(View.VISIBLE);
        //提示出货中。。。
        tvOutGoods.setVisibility(View.VISIBLE);
    }


    /**
     * 先判断购买商品是不是促销，如果是就执行
     * 支付方式有四种 1111 字符串来表示
     * 为1 表示 支持
     * 为0 表示 不支持
     * 机器的
     */
    private void NetWorkDeal() {
        //网络变化时，在线支付的支付方式刷新 变灰
        final FragmentManager fm = getSupportFragmentManager();
        PaymentsFragment fragment = (PaymentsFragment) fm.findFragmentByTag(FRAGMENT_PAYMENTS_TAG_UNSELECTED);
        if (fragment != null) {
            fragment.notifyView();
        }

        if (!isUnOutGooding) {//如果正在出货，不需要提示，管他有没有断网
            return;
        }

        int payment_cash = ConfigUtils.getConfig(this).payment_way.payment_cash;
        log.i(TAG, "payment_cash" + payment_cash);
        if (payment_cash == 1) {
            //此机器支持现金支付  断网时，若是促销产品 现金支付 不享受优惠活动
            toastInfo(ProductInfoActivity.this, "哎呀，连不上网了，请付现金吧～");
            onPaymentChanged(CASHPAYMENT);
        } else {
            //若不支持 现金
            toastInfo(ProductInfoActivity.this, "哎呀，连不上网了，请等会再试试～");
        }
    }

    /**
     * 初始化广播
     *
     * @param context
     */
    private void initBoardCast(Context context) {
        log.i(TAG, "initBoardCast:begin");

        IntentFilter filter1 = new IntentFilter(OdooAction.BLL_OUTGOODS_TO_UI);
        LocalBroadcastManager.getInstance(context).registerReceiver(mVmcOutGoodsReceiver, filter1);

        IntentFilter filter2 = new IntentFilter(OdooAction.BLL_RECIVERMONEY_TO_UI);
        LocalBroadcastManager.getInstance(context).registerReceiver(mVmcMoneyReceiver, filter2);

        IntentFilter filter3 = new IntentFilter(OdooAction.BLL_NET_STATE_TO_UI);
        LocalBroadcastManager.getInstance(context).registerReceiver(mVmcNetStateReceive, filter3);

        IntentFilter filter4 = new IntentFilter(OdooAction.BLL_PAY_STATUS_TO_UI);
        LocalBroadcastManager.getInstance(context).registerReceiver(mVmcPaySucessReceiver, filter4);

        IntentFilter filter5 = new IntentFilter(OdooAction.BLL_CARD_BAN_TO_UI);
        LocalBroadcastManager.getInstance(context).registerReceiver(cardBanReceiver, filter5);

        IntentFilter filter6 = new IntentFilter(OdooAction.BLL_CARD_CAN_TO_UI);
        LocalBroadcastManager.getInstance(context).registerReceiver(cardCanReceiver, filter6);

        IntentFilter filter7 = new IntentFilter(OdooAction.BLL_DOOR_STATE_TO_UI_SELECTED_PRODUCT);
        LocalBroadcastManager.getInstance(context).registerReceiver(mVmcDoorStateReceiver, filter7);


        log.i(TAG, "initBoardCast:end");
    }


    //当点击任意一种支付方式：
    @Override
    public void onPaymentChanged(int method) {

        mIsCashPayment = false;
        if (method > 0) {
            if (method == 3) {
                mIsCashPayment = true;
            }
        }

        if (!mIsCashPayment) {
            Intent intent = new Intent(Constants.Action.CLICK_PAYMENTH_METHOD);
            intent.putExtra("reset", false);
            sendBroadcast(intent);
        }

        // 1. 支付方式变换的时候被调用
        // 2. 替换选择支付方式页面为"重选支付方式"页面
        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_PAYMENTS_TAG_UNSELECTED);
        if (null != fragment) {
            ft.hide(fragment);
        }

        fragment = fm.findFragmentByTag(FRAGMENT_DETAILS_TAG);
        if (null != fragment) {
            ((DetailsFragment) fragment).onPaymentChanged(method);
        }
        fragment = fm.findFragmentByTag(FRAGMENT_PAYMENTS_TAG_SELECTED);
        if (null != fragment) {
            ft.show(fragment);
            ReSelectPaymentsFragment selectPaymentsFragment = (ReSelectPaymentsFragment) fragment;
            selectPaymentsFragment.setPaymentMethod(method);
        } else {
            fragment = ReSelectPaymentsFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putInt(Extras.DATA, method);
            fragment.setArguments(bundle);
            ft.add(R.id.product_info_payment, fragment, FRAGMENT_PAYMENTS_TAG_SELECTED);
        }
        ft.commitAllowingStateLoss();

        //切换支付区域
        fragment = fm.findFragmentByTag(FRAGMENT_SCANNER_TAG);
        if (null != fragment) {
            if (fragment instanceof PaymentsFragment.OnPaymentCallback) {
                ((PaymentsFragment.OnPaymentCallback) fragment).onPaymentChanged(method);
            }
        }

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (MotionEvent.ACTION_UP == action || MotionEvent.ACTION_CANCEL == action) {
            final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_BACK);
            if (fragment instanceof BackFragment && isUnOutGooding) {//并没有出货中
                final BackFragment f = (BackFragment) fragment;
                f.resetTimer();
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onPaymentReset() {
        mIsCashPayment = false;
        Intent intent = new Intent(Constants.Action.CLICK_PAYMENTH_METHOD);
        intent.putExtra("reset", true);
        sendBroadcast(intent);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_PAYMENTS_TAG_SELECTED);
        if (null != fragment) {
            ft.hide(fragment);
        }

        fragment = fm.findFragmentByTag(FRAGMENT_DETAILS_TAG);
        if (null != fragment) {//重置时返回促销样式
            ((DetailsFragment) fragment).onPaymentChanged(-1);
        }

        fragment = fm.findFragmentByTag(FRAGMENT_PAYMENTS_TAG_UNSELECTED);
        if (null == fragment) {
            fragment = PaymentsFragment.newInstance(mProduct);
            ft.add(R.id.product_info_payment, fragment, FRAGMENT_PAYMENTS_TAG_UNSELECTED);
        } else {
            ft.show(fragment);
        }

        ft.commit();

        // 切换支付区域
        fragment = fm.findFragmentByTag(FRAGMENT_SCANNER_TAG);
        if (null != fragment) {
            if (fragment instanceof PaymentsFragment.OnPaymentCallback) {
                ((PaymentsFragment.OnPaymentCallback) fragment).onPaymentChanged(-1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ConfigUtils.getConfig(ProductInfoActivity.this) != null &&
            ConfigUtils.getConfig(ProductInfoActivity.this).vmc_count_down_time_settings != null) {
            if (ConfigUtils.getConfig(ProductInfoActivity.this).vmc_count_down_time_settings.purchase_page_countdown !=
                0) {
                mBackPresenter.setTimeLeft(ConfigUtils.getConfig(ProductInfoActivity.this).vmc_count_down_time_settings.purchase_page_countdown);
            } else {
                mBackPresenter.setTimeLeft(BackViewModel.DEFAULT_TIMELEFT);
            }
        }


        if (BLLController.getInstance().isDoorOpen()) {//门异常
            log.e(TAG, "onResume: 门已开 无法购买");
            GuideProblemCodeActivity.start(this, 3);
            return;
        }


        if (BLLController.getInstance().isDriveError()) {//驱动异常
            GuideProblemCodeActivity.start(this, 5);
            log.e(TAG, "onResume: 门已开 无法购买");
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        log.i(TAG, "pause ProductInfoActivity");
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mVmcOutGoodsReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mVmcNetStateReceive);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mVmcMoneyReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mVmcPaySucessReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mVmcDoorStateReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(cardBanReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(cardCanReceiver);
        if (null != BLLOrderUtils.getCurrentOrder()) {
            log.e(TAG, "onDestroy ProductInfoActivity :非正常途径取消订单");
            BLLController.getInstance().cancelSelectProduct();
            BLLController.getInstance().cancelOrder(ProductInfoActivity.this);
        }
        log.i(TAG, "onDestroy ProductInfoActivity");
        if (observer != null) {
            NetWorkObservable.getInstance().deleteObserver(observer);
        }
        super.onDestroy();
    }


    @Override
    public void onDissmiss() {
        mFragment.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (BLLController.getInstance().isCardBan()) {
            isCashing = true;
            onPaymentChanged(CASHPAYMENT);
        }
    }
}

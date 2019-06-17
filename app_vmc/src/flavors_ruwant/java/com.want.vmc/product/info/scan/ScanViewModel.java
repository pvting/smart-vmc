package com.want.vmc.product.info.scan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.Bindable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.vmc.core.BLLController;
import com.vmc.core.OdooAction;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.request.pay.PayRequest;
import com.vmc.core.utils.BLLPayMentController;
import com.vmc.core.utils.ConfigUtils;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.vmc.R;

import net.glxn.qrgen.android.QRCode;

import vmc.core.log;
import vmc.vendor.VActivity;
import vmc.vendor.common.PayMethod;

/**
 * <b>Create Date:</b> 11/02/16<br>
 * <b>Author:</b> Wisn<br>
 * <b>Description:</b> <br>
 */
public class ScanViewModel extends AbsViewModel {
    private static final String TAG = "ScanViewModel";
    private BLLProduct mProduct;

    /**
     * 页面提示变量
     */
    private Bitmap mMQRCode;
    private int mPayIconIsVisible = View.GONE;
    private int mProgressIsVisible = View.GONE;
    private String mPayState;
    private String mPayMessage;
    private String mPayMent;
    private String mPayMentScan;
    private int mPayMentScanIcon;


    /**
     * 订单和支付成员变量
     */
    private Context mContext;

    private PayMethod mPayType;


    /**
     * 接收BL层广播 接收到 支付状态
     */
    private BroadcastReceiver mVmcPayStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.d(TAG, "onReceive:begin");
            final String action = intent.getAction();
            if (TextUtils.equals(action, OdooAction.BLL_PAY_STATUS_TO_UI)) {
                if (intent.getBooleanExtra("PayStatus", false)) {
                    onPaySuccess();
                }

            } else if (TextUtils.equals(action, OdooAction.BLL_CREATE_IMAGE_TO_UI)) {
                int result = intent.getIntExtra("result", 0);
                String qrStr = intent.getStringExtra("qrStr");
                int paymentType = intent.getIntExtra("paymentType", 0);
                switch (result) {
                    case 1:
                        createQrImage(qrStr, paymentType);
                        break;
                    case 2:
                        onPayReqError();
                        break;
                    case 3:
                        onNetWorkError();
                        break;
                }
            } else if (TextUtils.equals(action, OdooAction.BLL_PRE_OUTGOODS_TO_UI)) {
                int totalNum = intent.getIntExtra("totalNum", 0);
                int outIndex = intent.getIntExtra("outIndex", 0);
                Toast.makeText(mContext, outIndex + "/" + totalNum, Toast.LENGTH_SHORT).show();
            }
            log.d(TAG, "onReceive:end");
        }
    };

    public ScanViewModel(Context context, BLLProduct product) {
        super(context);
        this.mContext = context;
        this.mProduct = product;
    }

    public void init() {
        if (mProduct == null) {
            return;
        }
        initBoardCast();
        BLLStackProduct bsp = BLLController.getInstance().getSaleableStackProductByProduct(mProduct);

        if (bsp == null) {
            Toast.makeText(mContext, "此货不可售卖！抱歉", Toast.LENGTH_SHORT).show();
            ((VActivity) mContext).finish();
        }


        switchPayWay(PayMethod.NOPAYMENT);
    }


    /**
     * 支付成功
     * 规则:
     * 1.除现金支付,其他需主动发起出货(现金支付应设备满足金额会主动出货,故不主动发起出货)
     * 2.订单状态为用户已支付,支付状态为已支付
     */
    private void onPaySuccess() {
        switchPayWay(PayMethod.PAYSUCCESS);
    }

    /**
     * 支付请求失败异常处理
     */
    private void onPayReqError() {
        log.d(TAG, "onPayReqError: begin");

        onPaymentChanged(PayMethod.PAYFAILED);


        log.d(TAG, "onPayReqError: end");
    }

    /**
     * 网络异常
     */
    private void onNetWorkError() {
        log.d(TAG, "onNetWorkError: begin");
        onPaymentChanged(PayMethod.NETWORKERROR);
        log.d(TAG, "onNetWorkError: end");
    }


    /**
     * 生成二维码 生成后开启支付查询
     *
     * @param qrCode
     */
    private void createQrImage(String qrCode, int type) {
        // 避免由于网络慢，而造成的页面重叠情况
        PayMethod method = getPayMethod(type);

        if (mPayType != method) {
            return;
        }
        if (!TextUtils.isEmpty(qrCode)) {
            mMQRCode = QRCode.from(qrCode).bitmap();
        }
        mProgressIsVisible = View.GONE;
        notifyChange();

        log.d(TAG, "createQrImage: end");

        BLLPayMentController.getInstance().startRequestPayStatus(mContext);
    }


    /**
     * 初始化广播
     */
    private void initBoardCast() {
        IntentFilter mIntentFilter = new IntentFilter(OdooAction.BLL_PAY_STATUS_TO_UI);
        mIntentFilter.addAction(OdooAction.BLL_CREATE_IMAGE_TO_UI);
        mIntentFilter.addAction(OdooAction.BLL_PRE_OUTGOODS_TO_UI);
        mContext.registerReceiver(mVmcPayStatusReceiver, mIntentFilter);
    }


    /**
     * 提示支付方式icon是否显示
     *
     * @return
     */
    @Bindable
    public int getPayIconIsVisible() {
        return mPayIconIsVisible;
    }

    /**
     * 生成二维码等待进度是否显示
     *
     * @return
     */
    @Bindable
    public int getProgressIsVisible() {
        return mProgressIsVisible;
    }

    /**
     * 二维码图片
     *
     * @return
     */
    @Bindable
    public Bitmap getQRCodeBitmap() {
        return mMQRCode;
    }


    /**
     * 支付方式的icon
     *
     * @return
     */
    @Bindable
    public int getPayMentScanIcon() {
        return mPayMentScanIcon;
    }

    /**
     * 支付方式提示
     *
     * @return
     */
    @Bindable
    public String getPayMent() {
        return mPayMent;
    }

    /**
     * 支付方式文本提示
     *
     * @return
     */
    @Bindable
    public String getPayMentScan() {
        return mPayMentScan;

    }

    /**
     * 支付状态
     *
     * @return
     */
    @Bindable
    public String getPayState() {
        return mPayState;
    }

    /**
     * 支付操作提示的消息
     *
     * @return
     */
    @Bindable
    public String getPayMessage() {
        return mPayMessage;
    }

    /**
     * 修改支付方式后会回调这个这个方法
     *
     * @param method
     */
    public void changePayment(int method) {
        mPayType = getPayMethod(method);
        onPaymentChanged(mPayType);
    }

    /**
     * 支付方式修改
     *
     * @param payType
     */
    public void onPaymentChanged(PayMethod payType) {
        if (mContext == null) {
            return;
        }

        //更新支付页面
        switchPayWay(payType);

        if (PayMethod.ALIPAY == payType) {
//            BLLPayMentUtils.updatePayType(mContext, PayRequest.Payment.ALIPAY);
            BLLPayMentController.getInstance().updatePayType(mContext, PayRequest.Payment.ALIPAY, "");

        } else if (PayMethod.WECHAT == payType) {
//            BLLPayMentUtils.updatePayType(mContext, PayRequest.Payment.WECHATPAY);
            BLLPayMentController.getInstance().updatePayType(mContext, PayRequest.Payment.WECHATPAY, "");

        } else if (PayMethod.WANGBI == payType) {
//            BLLPayMentUtils.updatePayType(mContext, PayRequest.Payment.WANGBI);
            BLLPayMentController.getInstance().updatePayType(mContext, PayRequest.Payment.WANGBI, "");

        } else if (PayMethod.CASHPAYMENT == payType) {
//            BLLPayMentUtils.updatePayType(mContext, PayRequest.Payment.RMB);
            BLLPayMentController.getInstance().updatePayType(mContext, PayRequest.Payment.RMB, "");
            log.i(TAG, "change payment RMB");
        }


    }

    private PayMethod getPayMethod(int method) {
        PayMethod payMethod = PayMethod.NOPAYMENT;
        switch (method) {
            case 0:
                payMethod = payMethod.NOPAYMENT;
                break;
            case 1:
                payMethod = payMethod.ALIPAY;
                break;
            case 2:
                payMethod = payMethod.WECHAT;
                break;
            case 3:
                payMethod = payMethod.CASHPAYMENT;
                break;
            case 4:
                payMethod = payMethod.WANGBI;
                break;
        }
        return payMethod;
    }

    /**
     * 切换支付方式
     *
     * @param PayMethod
     */
    public void switchPayWay(PayMethod PayMethod) {
        mPayState = getString(R.string.scan_pay_and_waitproduct);
        mPayMessage = getString(R.string.scan_pay_and_waitproduct);
        mPayIconIsVisible = View.VISIBLE;
        mProgressIsVisible = View.VISIBLE;
        mMQRCode =
                BitmapFactory.decodeResource(mContext.getResources(), R.drawable.vendor_product_background);
        if (PayMethod.NOPAYMENT == PayMethod) {
            //未选择支付方式
            mPayIconIsVisible = View.GONE;
            mProgressIsVisible = View.GONE;
            mPayMent = getString(R.string.scan_no_payway);
            mPayMentScan = getString(R.string.scan_choose_payway);
            mMQRCode =
                    BitmapFactory.decodeResource(mContext.getResources(),
                                                 R.drawable.vendor_product_info_scan_none_payment);
            mPayMentScanIcon = R.drawable.vendor_product_info_scan_none_payment;

        } else if (PayMethod.ALIPAY == PayMethod) {
            //支付宝
            mPayMent = getString(R.string.scan_payway_alipay);
            mPayMentScan = getString(R.string.scan_choose_payway_alipay);
            mPayMentScanIcon = R.drawable.icon_alipay_normal;

        } else if (PayMethod.WECHAT == PayMethod) {
            //微信支付
            mPayMent = getString(R.string.scan_payway_wechat);
            mPayMentScan = getString(R.string.scan_choose_payway_wechat);
            mPayMentScanIcon = R.drawable.icon_wechat_normal;

        } else if (PayMethod.CASHPAYMENT == PayMethod) {
            //现金
            mPayMent = getString(R.string.scan_payway_cash);
            mPayMentScan = getString(R.string.scan_choose_payway_cash);
            mProgressIsVisible = View.GONE;
            mMQRCode = BitmapFactory.decodeResource(mContext.getResources(),
                                                    R.drawable.vendor_product_info_scan_cash);
            mPayMentScanIcon = R.drawable.icon_rmb_normal;

        } else if (PayMethod.WANGBI == PayMethod) {
            //旺币
            mPayMent = getString(R.string.scan_payway_wangbi);
            mPayMentScan = getString(R.string.scan_choose_payway_wangbi);
            mPayMentScanIcon = R.drawable.icon_wangbi_normal;

        } else if (PayMethod.PAYFAILED == PayMethod) {
            //支付失败
            mProgressIsVisible = View.GONE;
            mPayState = getString(R.string.scan_pay_faile);
            mPayMessage = getString(R.string.scan_operate_again_msg);
            mPayMent = getString(R.string.scan_pay_faile);
            mPayMentScan = getString(R.string.scan_operate_again_msg);
            mMQRCode = BitmapFactory.decodeResource(mContext.getResources(),
                                                    R.drawable.vendor_product_info_scan_pay_faild);

        } else if (PayMethod.PAYSUCCESS == PayMethod) {
            //支付成功
            mProgressIsVisible = View.GONE;
            mPayState = getString(R.string.scan_pay_success);
            mPayMessage = getString(R.string.scan_operate_again_msg);
            mPayMent = getString(R.string.scan_pay_success);
            mPayMentScan = getString(R.string.scan_operate_again_msg);
            mMQRCode = BitmapFactory.decodeResource(mContext.getResources(),
                                                    R.drawable.vendor_product_info_scan_pay_success);

        } else if (PayMethod.OUTGOODSSUCCESS == PayMethod) {
            //出货成功
            mProgressIsVisible = View.GONE;
            mPayState = getString(R.string.scan_outgood_success);
            mPayMessage = getString(R.string.scan_outgood_success_message);
            mPayMent = getString(R.string.scan_outgood_success);
            mPayMentScan = getString(R.string.scan_outgood_success_message);
            mMQRCode = BitmapFactory.decodeResource(mContext.getResources(),
                                                    R.drawable.vendor_product_info_scan_outgood_success);

        } else if (PayMethod.OUTGOODSFAILED == PayMethod) {
            //出货失败
            mProgressIsVisible = View.GONE;
            mPayState = getString(R.string.scan_outgood_faild);
            mPayMessage = getString(R.string.scan_outgood_faild_message);
            mPayMent = getString(R.string.scan_outgood_faild);
            mPayMentScan = getString(R.string.scan_outgood_faild_message);
            mMQRCode = BitmapFactory.decodeResource(mContext.getResources(),
                                                    R.drawable.vendor_product_info_scan_outgood_success);

        } else if (PayMethod.NETWORKERROR == PayMethod) {
            //网络异常
            mProgressIsVisible = View.GONE;
            mPayState = getString(R.string.scan_network_error);
            mPayMessage = getString(R.string.scan_network_error_msg);
            mPayMent = getString(R.string.scan_network_error);
            mPayMentScan = getString(R.string.scan_network_error_msg);
            mMQRCode = BitmapFactory.decodeResource(mContext.getResources(),
                                                    R.drawable.vendor_product_info_scan_pay_faild);

        }
        //更新绑定的数据
        notifyChange();
    }

    /**
     * 跟随页面销毁操作
     */
    public void onDestory() {
        if (mMQRCode != null) {
            mMQRCode.recycle();
            mMQRCode = null;
            System.gc();
        }
        mContext.unregisterReceiver(mVmcPayStatusReceiver);


    }


    /**
     * 获取客服电话
     *
     * @return
     */
    @Bindable
    public String getCustomerTelphone() {
        if (!TextUtils.isEmpty(ConfigUtils.getConfig(mContext).customer_phone)) {
            log.v(TAG, "客服电话:" + ConfigUtils.getConfig(mContext).customer_phone);
            return "如需帮助，请拨打：" + ConfigUtils.getConfig(mContext).customer_phone;
        } else {
            log.v(TAG, "客服电话获取失败" + ConfigUtils.getConfig(mContext).customer_phone);
            return "";
        }
    }

}









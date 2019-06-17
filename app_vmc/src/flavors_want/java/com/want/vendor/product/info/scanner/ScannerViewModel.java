package com.want.vendor.product.info.scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.Bindable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.vmc.api.BLLSVMController;
import com.vmc.core.BLLController;
import com.vmc.core.OdooAction;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.request.pay.PayRequest;
import com.vmc.core.utils.BLLPayMentController;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.vendor.product.list.sales.SaleDialogFragment;
import com.want.vmc.BR;
import com.want.vmc.R;

import net.glxn.qrgen.android.QRCode;

import vmc.core.log;
import vmc.vendor.VActivity;
import vmc.vendor.common.PayMethod;


/**
 * <b>Create Date:</b> 2016/11/22<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public class ScannerViewModel extends AbsViewModel {
    private static final String TAG = "ScanViewModel";
    private BLLProduct mProduct;
    /**
     * 页面提示变量
     */
    private Bitmap mMQRCode;
    private int mPayIconIsVisible = View.GONE;
    private int mProgressIsVisible = View.GONE;
    private int mMoneyIconIsVisible = View.VISIBLE;
    private int mElementIsVisible = View.VISIBLE;
    private int mLineIsVisible = View.VISIBLE;
    private String mPayState;
    private String mPayMessage;
    private String mPayMent;
    private String mPayMentScan;
    private int mPayMentScanIcon;
    private int mCodepaymenticon;
    private int mPayMentVisible = View.GONE;
    private int mTextCodeIsVisible = View.GONE;
    private boolean isPay;


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
            log.d(TAG, "mVmcPayStatusReceiver:begin");
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
            }
            log.d(TAG, "mVmcPayStatusReceiver:end");
        }
    };


    public ScannerViewModel(Context context, ScannerContract.View view, BLLProduct product, boolean isPay) {
        super(context);
        this.mContext = context;
        this.mProduct = product;
        this.isPay = isPay;
    }

    public void init() {
        if (mProduct == null) {
            return;
        }

        initBroadCast();

        //促销弹窗
        promotionWindow();


        BLLStackProduct bsp = BLLController.getInstance().getSaleableStackProductByProduct(mProduct);

        if (bsp == null) {
            Toast.makeText(mContext, "此货不可售卖！抱歉", Toast.LENGTH_SHORT).show();
            ((VActivity) mContext).finish();
        }



        if (isPay) {
            switchPayWay(PayMethod.PAYSUCCESS);
        } else {
            switchPayWay(PayMethod.NOPAYMENT);
        }

    }


    private void initBroadCast() {
        IntentFilter mIntentFilter = new IntentFilter(OdooAction.BLL_PAY_STATUS_TO_UI);
        mIntentFilter.addAction(OdooAction.BLL_CREATE_IMAGE_TO_UI);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mVmcPayStatusReceiver, mIntentFilter);

    }


    /**
     * 促销弹窗
     */
    private void promotionWindow() {
        if (mProduct.mPromotionDetail != null) {//促销弹窗
            SaleDialogFragment fragment = new SaleDialogFragment();
            Bundle mBundle = new Bundle();
            if (!TextUtils.isEmpty(mProduct.mPromotionDetail.promotional_image_links)) {
                mBundle.putString("url", mProduct.mPromotionDetail.promotional_image_links);
                fragment.setArguments(mBundle);
                fragment.show(((VActivity) mContext).getSupportFragmentManager());
            }
        }
    }


    /**
     * 支付成功
     * 规则:
     * 1.除现金支付,其他需主动发起出货(现金支付应设备满足金额会主动出货,故不主动发起出货)
     * 2.订单状态为用户已支付,支付状态为已支付
     */
    public void onPaySuccess() {

        switchPayWay(PayMethod.PAYSUCCESS);

//        mIsSaveOrder = false;
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
            mMQRCode = changeBitmap(QRCode.from(qrCode).bitmap());
        }
        mProgressIsVisible = View.GONE;
        mTextCodeIsVisible = View.GONE;
        mPayMentVisible = View.VISIBLE;
        notifyChange();
        log.d(TAG, "生成二维码成功");

        BLLPayMentController.getInstance().startRequestPayStatus(mContext);

    }

    /**
     * @param bitmapSrc 裁剪二维码白色底框
     *
     * @return
     */

    private Bitmap changeBitmap(Bitmap bitmapSrc) {
        Bitmap reBitmap = bitmapSrc;
        int a, r, g, b;
        int color;
        int height = bitmapSrc.getHeight();
        int width = bitmapSrc.getWidth();

        for (int i = 0; i < width; i++) {
            color = bitmapSrc.getPixel(i, i);
            a = Color.alpha(color);
            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
            if (a == 255 && r == 0 && g == 0 && b == 0) {
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                paint.setColor(Color.BLACK);

                Rect rectSrc = new Rect();
                rectSrc.left = i - 1;
                rectSrc.right = bitmapSrc.getWidth() - i + 1;
                rectSrc.top = i - 1;
                rectSrc.bottom = bitmapSrc.getHeight() - i + 1;

                canvas.drawBitmap(bitmapSrc, rectSrc, rectSrc, paint);
                reBitmap = bitmap;
                break;
            }
        }
        return reBitmap;

    }


    /**
     * 商品成分图片
     *
     * @return
     */
    @Bindable
    public String getElementImg() {
        log.d(TAG, "url:" + mProduct.product_details_image_url);
        return mProduct.product_details_image_url;
    }

    /**
     * 商品成分图片是否展示
     *
     * @return
     */
    @Bindable
    public int getElementIsVisible() {
        return mElementIsVisible;
    }

    /**
     * 选择支付图标是否展示
     *
     * @return
     */
    @Bindable
    public int getMoneyIconIsVisible() {
        return mMoneyIconIsVisible;
    }

    /**
     * 分割线是否显示
     */
    @Bindable
    public int getLineIsVisible() {
        return mLineIsVisible;
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
     * 二维码正在生成中是否显示
     */
    @Bindable
    public int getTextCodeIsVisible() {
        return mTextCodeIsVisible;
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
     * 二维码图片中间icon
     */
    @Bindable
    public int getCodepaymenticon() {

        return mCodepaymenticon;
    }

    /**
     * 是否显示中间图标
     */
    @Bindable
    public int getPayMentVisible() {
        return mPayMentVisible;
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
            log.d(TAG, "change payment ALIPAY");

        } else if (PayMethod.WECHAT == payType) {
//            BLLPayMentUtils.updatePayType(mContext, PayRequest.Payment.WECHATPAY);
            BLLPayMentController.getInstance().updatePayType(mContext, PayRequest.Payment.WECHATPAY, "");
            log.d(TAG, "change payment WECHATPAY");

        } else if (PayMethod.WANGBI == payType) {
//            BLLPayMentUtils.updatePayType(mContext, PayRequest.Payment.WANGBI);
            BLLPayMentController.getInstance().updatePayType(mContext, PayRequest.Payment.WANGBI, "");
            log.d(TAG, "change payment WANGBI");

        } else if (PayMethod.CASHPAYMENT == payType) {
//            BLLPayMentUtils.updatePayType(mContext, PayRequest.Payment.RMB);
            BLLPayMentController.getInstance().updatePayType(mContext, PayRequest.Payment.RMB, "");
            log.d(TAG, "change payment RMB");
        }


    }

    private PayMethod getPayMethod(int method) {

        PayMethod payMethod = PayMethod.NOPAYMENT;

        switch (method) {
            case 0:
                payMethod = PayMethod.NOPAYMENT;
                break;
            case 1:
                payMethod = PayMethod.ALIPAY;
                break;
            case 2:
                payMethod = PayMethod.WECHAT;
                break;
            case 3:
                payMethod = PayMethod.CASHPAYMENT;
                break;
            case 4:
                payMethod = PayMethod.WANGBI;
                break;
        }
        return payMethod;
    }

    /**
     * 切换支付方式
     *
     * @param payMethod
     */
    public void switchPayWay(PayMethod payMethod) {
        mPayState = getString(R.string.scan_pay_and_waitproduct);
        mPayMessage = getString(R.string.scan_pay_and_waitproduct);
        mPayIconIsVisible = View.VISIBLE;
        mProgressIsVisible = View.VISIBLE;
        mTextCodeIsVisible = View.GONE;
        mPayMentVisible = View.GONE;
        mElementIsVisible = View.GONE;
        mMoneyIconIsVisible = View.GONE;
        mLineIsVisible = View.GONE;
//        mMQRCode =
//                BitmapFactory.decodeResource(mContext.getResources(), R.drawable.vendor_product_background);
        mMQRCode =
                drawableToBitmap(mContext.getResources().getDrawable(R.drawable.payment_scanner_wait_qr_bg));
        if (PayMethod.NOPAYMENT == payMethod) {
            //未选择支付方式
            mPayIconIsVisible = View.GONE;
            mProgressIsVisible = View.GONE;
            mTextCodeIsVisible = View.GONE;
            mElementIsVisible = View.VISIBLE;
            mMoneyIconIsVisible = View.VISIBLE;
            mLineIsVisible = View.VISIBLE;
            mPayMentVisible = View.GONE;
            mPayMent = getString(R.string.scan_no_payway);
            mPayMentScan = getString(R.string.scan_choose_payway);
//            mMQRCode = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.vendor_product_info_scan_none_payment);
            mMQRCode = null;
            mPayMentScanIcon = R.drawable.vendor_product_info_scan_none_payment;

        } else if (PayMethod.ALIPAY == payMethod) {
            //支付宝
            mPayMent = getString(R.string.scan_payway_alipay);
            mPayMentScan = getString(R.string.scan_choose_payway_alipay);
            mPayMentScanIcon = R.drawable.icon_alipay_normal;
            mCodepaymenticon = R.drawable.alipay_payment_icon1;
            mPayMentVisible = View.GONE;
            mTextCodeIsVisible = View.VISIBLE;
            mProgressIsVisible = View.GONE;

        } else if (PayMethod.WECHAT == payMethod) {
            //微信支付
            mPayMent = getString(R.string.scan_payway_wechat);
            mPayMentScan = getString(R.string.scan_choose_payway_wechat);
            mPayMentScanIcon = R.drawable.icon_wechat_normal;
            mCodepaymenticon = R.drawable.weixin_payment_icon1;
            mPayMentVisible = View.GONE;
            mTextCodeIsVisible = View.VISIBLE;
            mProgressIsVisible = View.GONE;

        } else if (PayMethod.CASHPAYMENT == payMethod) {
            //现金
            mPayMent = getString(R.string.scan_payway_cash);
            mPayMentScan = getString(R.string.scan_choose_payway_cash);
            mProgressIsVisible = View.GONE;
            mMQRCode = BitmapFactory.decodeResource(mContext.getResources(),
                                                    R.drawable.vendor_product_info_scan_cash);
            mPayMentScanIcon = R.drawable.icon_rmb_normal;

        } else if (PayMethod.WANGBI == payMethod) {
            //旺币
            mPayMent = getString(R.string.scan_payway_wangbi);
            mPayMentScan = getString(R.string.scan_choose_payway_wangbi);
            mPayMentScanIcon = R.drawable.icon_wangbi_normal;
            mCodepaymenticon = R.drawable.wangbi_payment_icon1;
            mPayMentVisible = View.GONE;
            mTextCodeIsVisible = View.VISIBLE;
            mProgressIsVisible = View.GONE;

        } else if (PayMethod.PAYFAILED == payMethod) {
            //支付失败
            mProgressIsVisible = View.GONE;
            mPayState = getString(R.string.scan_pay_faile);
            mPayMessage = getString(R.string.scan_operate_again_msg);
            mPayMent = getString(R.string.scan_pay_faile);
            mPayMentScan = getString(R.string.scan_operate_again_msg);
            mMQRCode = BitmapFactory.decodeResource(mContext.getResources(),
                                                    R.drawable.vendor_product_info_scan_pay_faild);

        } else if (PayMethod.PAYSUCCESS == payMethod) {
            //支付成功
            mProgressIsVisible = View.GONE;
            mPayState = getString(R.string.scan_pay_success);
            mPayMessage = getString(R.string.scan_operate_again_msg);
            mPayMent = getString(R.string.scan_pay_success);
            mPayMentScan = getString(R.string.scan_operate_again_msg);
            //TODO;
            mMQRCode = BitmapFactory.decodeResource(mContext.getResources(),
                                                    R.drawable.vendor_product_info_scan_pay_success);

        } else if (PayMethod.OUTGOODSSUCCESS == payMethod) {
            //出货成功
            mProgressIsVisible = View.GONE;
            mPayState = getString(R.string.scan_outgood_success);
            mPayMessage = getString(R.string.scan_outgood_success_message);
            mPayMent = getString(R.string.scan_outgood_success);
            mPayMentScan = getString(R.string.scan_outgood_success_message);
            mMQRCode = BitmapFactory.decodeResource(mContext.getResources(),
                                                    R.drawable.vendor_product_info_scan_outgood_success);

        } else if (PayMethod.OUTGOODSFAILED == payMethod) {
            //出货失败
            mProgressIsVisible = View.GONE;
            mPayState = getString(R.string.scan_outgood_faild);
            mPayMessage = getString(R.string.scan_outgood_faild_message);
            mPayMent = getString(R.string.scan_outgood_faild);
            mPayMentScan = getString(R.string.scan_outgood_faild_message);
            mMQRCode = BitmapFactory.decodeResource(mContext.getResources(),
                                                    R.drawable.vendor_product_info_scan_outgood_success);

        } else if (PayMethod.NETWORKERROR == payMethod) {
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

        //回收bitmap
        if (mMQRCode != null) {
            mMQRCode.recycle();
            mMQRCode = null;
            System.gc();
        }
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mVmcPayStatusReceiver);

    }

    private Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap
                bitmap =
                Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                                    drawable.getIntrinsicHeight(),
                                    Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
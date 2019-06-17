package com.want.vendor.deliver.fillout;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.Bindable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vmc.core.BLLController;
import com.vmc.core.OdooAction;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.model.product.DeliverProduct;
import com.want.base.sdk.framework.app.MFragment;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.vendor.common.ToastUtil;
import com.want.vendor.deliver.DeliverActivity;
import com.want.vendor.deliver.FaiActivity;
import com.want.vendor.product.info.ProductInfoActivity;
import com.want.vmc.BR;
import com.want.vmc.R;

import vmc.core.log;

import static android.content.ContentValues.TAG;


/**
 * ViewModel Stub.
 */
public class FillOutViewModel extends AbsViewModel implements View.OnClickListener {

    public static final int DEFAULT_TAG_RESULT = 0;
    public String mInputString = "";
    private Activity mContext;
    private Toast mToast;
    private int mProgressIsVisible = View.GONE;
    private Boolean flag = false;
    /**
     * 自定义textView
     */
    private TextView tvToastText;
    public boolean isClickable = true;

    private DeliverProduct mDeliverProduct;
    private Fragment fragment;
    private int editTextPostion = 0;

    private EditText mEditText;


    public int getEditTextPostion() {
        return editTextPostion;
    }

    public void setEditTextPostion(int editTextPostion) {
        this.editTextPostion = editTextPostion;
    }

    public void clearInputString() {
        mInputString = "";
    }

    public FillOutViewModel(Context context) {
        super(context);
    }

    public FillOutViewModel(Activity context, MFragment fragment) {
        this.mContext = context;
        if (fragment instanceof FillOutFragment) {
            FillOutFragment fillOutFragment = (FillOutFragment) fragment;
            fillOutFragment.setPositionChangeListener(new PositionChangeListener() {
                @Override
                public void onPositionChanged(int position) {
                    setEditTextPostion(position);
                }
            });
        }
        LocalBroadcastManager.getInstance(mContext).registerReceiver(outPickUpGoodsResultReceiver,
                                                                       new IntentFilter(OdooAction.BLL_VERIFY_RESULT_TO_UI));
    }


//    public FillOutViewModel(Activity context) {
//        this.mContext = context;
//        mContext.registerReceiver(outPickUpGoodsResultReceiver,
//                                  new IntentFilter(OdooAction.BLL_VERIFY_RESULT_TO_UI));
//    }


    @Bindable
    public String getInput() {
        return mEditText.getText().toString();
    }

    public void setInput(String input, EditText editText) {
        if (isClickable) {
            mInputString = input;
            mEditText = editText;
            notifyChange();
        } else {
            return;
        }
    }

    @Bindable
    public int getProgressIsVisible() {
        return mProgressIsVisible;
    }


    /**
     * 自定义Toast
     */

    private void toastInfo(Context context, String data) {
        if (mToast == null) {
            mToast = Toast.makeText(context, data, Toast.LENGTH_SHORT);
            View view = View.inflate(context, R.layout.vendor_deliver_custom_toast_layout, null);
            mToast.setView(view);
            tvToastText = (TextView) view.findViewById(R.id.tvToastText);
            tvToastText.setText(data);
        } else {
            tvToastText.setText(data);
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    private String tempStartPointStr = "";
    private String temEndPointStr = "";

    private void resetTempStr() {
        tempStartPointStr = "";
        temEndPointStr = "";
    }

    public void deleteSelect(EditText editText) {
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        Editable editable = editText.getText();
        if (start != end) {
            if (start > end) {
                int temp = start;
                start = end;
                end = temp;
            }
            editable.delete(start, end);
        }
    }

    @Override
    public void onClick(final View v) {
        if (!isClickable) {
            return;
        }
        int start;
        Editable editable = mEditText.getText();
        final int id = v.getId();
        switch (id) {
            case R.id.vendor_input_x:
                editable.delete(0, editable.toString().length());
//                mInputString = "";
                break;
            case R.id.vendor_deliver_config_btn:
                if (configGoodsOut(v)) {
                    return;
                }
                break;
            case R.id.vendor_input_0:
//                setInputStringByStringId("vendor_input_0");
                deleteSelect(mEditText);
                start = mEditText.getSelectionStart();
                editable.insert(start, "0");
                break;
            case R.id.vendor_input_1:
//                setInputStringByStringId("vendor_input_1");
                deleteSelect(mEditText);
                start = mEditText.getSelectionStart();
                editable.insert(start, "1");
                break;

            case R.id.vendor_input_2:
//                setInputStringByStringId("vendor_input_2");
                deleteSelect(mEditText);
                start = mEditText.getSelectionStart();
                editable.insert(start, "2");
                break;
            case R.id.vendor_input_3:
//                setInputStringByStringId("vendor_input_3");
                deleteSelect(mEditText);
                start = mEditText.getSelectionStart();
                editable.insert(start, "3");
                break;
            case R.id.vendor_input_4:
//                setInputStringByStringId("vendor_input_4");
                deleteSelect(mEditText);
                start = mEditText.getSelectionStart();
                editable.insert(start, "4");
                break;

            case R.id.vendor_input_5:
//                setInputStringByStringId("vendor_input_5");
                deleteSelect(mEditText);
                start = mEditText.getSelectionStart();
                editable.insert(start, "5");
                break;

            case R.id.vendor_input_6:
//                setInputStringByStringId("vendor_input_6");
                deleteSelect(mEditText);
                start = mEditText.getSelectionStart();
                editable.insert(start, "6");
                break;
            case R.id.vendor_input_7:
//                setInputStringByStringId("vendor_input_7");
                deleteSelect(mEditText);
                start = mEditText.getSelectionStart();
                editable.insert(start, "7");
                break;
            case R.id.vendor_input_8:
//                setInputStringByStringId("vendor_input_8");
                deleteSelect(mEditText);
                start = mEditText.getSelectionStart();
                editable.insert(start, "8");
                break;
            case R.id.vendor_input_9:
//                setInputStringByStringId("vendor_input_9");
                deleteSelect(mEditText);
                start = mEditText.getSelectionStart();
                editable.insert(start, "9");
                break;
            case R.id.vendor_input_del:
//                if (mInputString.length() > 0 && editTextPostion > 0) {
//                    tempStartPointStr = mInputString.substring(0, editTextPostion);
//                    temEndPointStr = mInputString.substring(editTextPostion, mInputString.length());
//                    mInputString = tempStartPointStr.substring(0, tempStartPointStr.length() - 1) + temEndPointStr;
//                    resetTempStr();
//                }
                start = mEditText.getSelectionStart();
                int end = mEditText.getSelectionEnd();
                editable = mEditText.getText();
                if (start != end) {
                    if (start > end) {
                        int temp = start;
                        start = end;
                        end = temp;
                    }
                    editable.delete(start, end);
                } else if (start > 0)
                    editable.delete(start - 1, start);


//                if (mInputString.length() > 0) {
//                    mInputString = mInputString.substring(0, mInputString.length() - 1);
//                }
                break;
        }
        //判断提货码输入的个数
        if (mEditText.getText().length() > 8) {
            start = mEditText.getSelectionStart();
            editable.delete(start - 1, start);
            ToastUtil.toast(mContext, "数字长度有误，请重新输入", true);
            return;
        }
        notifyChange();
    }

//    public void setInputStringByStringId(String strIdName) {
//        String acitoName = strIdName.substring(strIdName.length() - 1, strIdName.length());
//        int inputNumber = Integer.parseInt(acitoName);
//        if (editTextPostion == mInputString.length()) {
//            mInputString += inputNumber;
//        } else {
//            tempStartPointStr = mInputString.substring(0, editTextPostion);
//            temEndPointStr = mInputString.substring(editTextPostion, mInputString.length());
//            tempStartPointStr += inputNumber;
//            mInputString = tempStartPointStr + temEndPointStr;
//            resetTempStr();
//        }
//    }

    public boolean configGoodsOut(final View v) {
        /** 网络异常，增加提示页面 */
        boolean netState = BLLController.getInstance().isNetState(mContext);
        if (!netState) {
            //无网络
            mProgressIsVisible = View.GONE;
            notifyPropertyChanged(BR.progressIsVisible);
            toastInfo(mContext, "网络异常，无法提货");
            return true;
        }

        /**如果flag 变为true 此按钮就不能点击。*/
        if (flag) {
            return true;
        }

        /**
         * 1.先判断 textview中的值，是否符合规范和判空处理。
         * 2.符合规范之后 获取用户输入的值 作为参数 进行网络请求
         *   根据返回的结果判断 成功或者失败  mInputString
         * */
        mProgressIsVisible = View.VISIBLE;
        if (TextUtils.isEmpty(mEditText.getText())) {
            mProgressIsVisible = View.GONE;
            notifyPropertyChanged(BR.progressIsVisible);
            toastInfo(mContext, "提货码不可为空");
            return true;
        }
        verifyPickUpGoodsCode();

        return false;
    }


    private void verifyPickUpGoodsCode() {
        /**
         * 调用提货码接口
         * */
        ((DeliverActivity) mContext).isCanBack = false;

        isClickable = false;

        BLLController.getInstance().verifyPickUpGoodsCode(mEditText.getText().toString(), mContext);

    }


    private BroadcastReceiver outPickUpGoodsResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mProgressIsVisible = View.GONE;
            notifyPropertyChanged(BR.progressIsVisible);
            ((DeliverActivity) mContext).isCanBack = true;
            isClickable = true;


            log.d(TAG, "outPickUpGoodsResultReceiver-->onReceive:begin");
            String result = intent.getStringExtra("result");
            String msg = intent.getStringExtra("msg");

            if (!TextUtils.isEmpty(result) && result.equals("success")) {
                BLLStackProduct bsp = intent.getParcelableExtra("product");
                ProductInfoActivity.start(context, bsp, true);
            } else {
                String commentStr = "提货码验证失败,请稍后重试";
                FaiActivity.start(mContext, mEditText.getText().toString(), TextUtils.isEmpty(msg) ? commentStr : msg);
                log.i(TAG, TextUtils.isEmpty(msg) ? "提货码验证失败" : msg);
            }
            log.d(TAG, "outPickUpGoodsResultReceiver-->onReceive:end");
        }
    };

    public void onDestroy() {
        ToastUtil.hiddeToast();
        mDeliverProduct = null;
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(outPickUpGoodsResultReceiver);
    }

    public interface PositionChangeListener {
        void onPositionChanged(int position);
    }

}

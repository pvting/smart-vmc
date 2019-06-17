package com.want.vendor.common;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.want.vmc.R;


/**
 * @author miaoxiongfei@foxmail.com
 * @date 2016-05-11 13:44
 */
public class ToastUtil {

    private static Toast mToast;


    public static void toast(Context context, CharSequence character, boolean isCenter) {
        if (mToast == null) {
            mToast = getCustomToast(context);
        }
        setUpToast(context, character, isCenter);
        mToast.show();
    }

    public static void hiddeToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }


    private static Toast getCustomToast(Context context) {
        mToast = new Toast(context);
        mToast.setView(LayoutInflater.from(context).inflate(R.layout.vendor_common_toast, null));
        return mToast;
    }


    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static void setUpToast(Context context, CharSequence character, boolean isCenter) {
        if (isCenter) {
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, dpToPx(240, context));
        }
        View toastView = mToast.getView();
        ((TextView) toastView.findViewById(R.id.tvToast)).setText(character);
    }
}

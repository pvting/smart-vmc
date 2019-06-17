package com.want.vmc.uitls;

import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.want.vmc.R;

/**
 * <b>Create Date:</b> 2016/11/25<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b> <br>
 */
public class UtilToast {
    private static TextView tvToastText;

    private static Toast mToast;
    public static void toastInfo(Context context,String data){
        if(mToast==null){
            mToast = Toast.makeText(context, data, Toast.LENGTH_SHORT);
        }else{
            mToast.setText(data);
        }
        mToast.show();
    }
    public static void toast(Context context, String data) {
        if (mToast == null) {
            mToast = Toast.makeText(context, data, 3000);
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



}

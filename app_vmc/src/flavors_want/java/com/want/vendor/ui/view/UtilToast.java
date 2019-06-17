package com.want.vendor.ui.view;

import android.content.Context;
import android.widget.Toast;

/**
 * <b>Create Date:</b> 2016/11/25<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b> <br>
 */
public class UtilToast {

    private static Toast mToast;
    public static void toastInfo(Context context,String data){
        if(mToast==null){
            mToast = Toast.makeText(context, data, Toast.LENGTH_SHORT);
        }else{
            mToast.setText(data);
        }
        mToast.show();
    }
}

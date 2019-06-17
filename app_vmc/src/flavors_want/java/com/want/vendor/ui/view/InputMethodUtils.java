package com.want.vendor.ui.view;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by zhongwenjie on 2017/7/6.
 */

public class InputMethodUtils {

    public static  void hideSoftKeyboard(View view) {
        InputMethodManager
                imm =
                (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

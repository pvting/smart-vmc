package com.want.vendor.ui.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * <b>Create Date:</b> 2016/11/25<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b> <br>
 */
public class FillOutTextView extends TextView implements TextWatcher{

    private Context mContext;

    public FillOutTextView(Context context) {
        super(context);
        mContext = context;
        addTextChangedListener(this);

    }
    public FillOutTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext = context;
        addTextChangedListener(this);
    }

    public FillOutTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        addTextChangedListener(this);
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {

        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if(text.length()>16){
            UtilToast.toastInfo(mContext,"无效提货码");
            this.setText(text.subSequence(0,text.length() - 1));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}

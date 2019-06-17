package com.want.vendor.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vmc.core.model.product.BLLCategory;
import com.want.vmc.R;


import java.util.ArrayList;

/**
 * <b>Create Date:</b>2016/11/20 17:29<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class CategoryView extends LinearLayout implements View.OnClickListener {
    private ArrayList<BLLCategory> srcList;
    private Context context;
    private OnCategoryListener listener;
    private ArrayList<TextView> viewlist =new ArrayList<>();

    public CategoryView(Context context) {
        super(context);
        initView(context);

    }

    public CategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CategoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setSrc(ArrayList<BLLCategory> srcList) {
        removeAllViews();//移除所有
        this.srcList = srcList;
        int count = srcList.size();
        viewlist.clear();

        for (int i = 0; i < count; i++) {
            TextView textview = new TextView(context);
            textview.setTextSize(24);

            textview.setText(srcList.get(i).category_name);
            if(i==0){
                textview.setBackgroundResource(R.drawable.vendor_categroy_bottom_change_bg);
            }
            textview.setTextColor(Color.WHITE);
            textview.getPaint().setFakeBoldText(false);
            textview.setGravity(Gravity.CENTER);
            textview.setOnClickListener(this);
            LinearLayout.LayoutParams lp = new LayoutParams(0,LayoutParams.MATCH_PARENT,1.0f);
            lp.gravity= Gravity.CENTER;
            addView(textview,i,lp);
            viewlist.add(textview);
        }


    }


    public void initView(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        int clickIndex = 0;
        int childCount = viewlist.size();
        for (int i = 0; i < childCount; i++) {
            viewlist.get(i).setBackgroundResource(0);
            viewlist.get(i).getPaint().setFakeBoldText(false);
            if (v == viewlist.get(i)) {
                clickIndex = i;
            }

        }

        v.setBackgroundResource(R.drawable.vendor_categroy_bottom_change_bg);
        ((TextView) v).getPaint().setFakeBoldText(true);

        listener.onCategoryClick(srcList.get(clickIndex).category_name);
    }

    public void setOnCategoryListener(OnCategoryListener listener) {

        this.listener = listener;

    }

   public interface OnCategoryListener {
        void onCategoryClick(String pos);
    }
}
package com.want.vendor.deliver.fai;

import android.app.Activity;
import android.content.Context;
import android.databinding.Bindable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.base.sdk.framework.eventbus.MEventBus;
import com.want.base.sdk.utils.ActivityUtils;
import com.want.vmc.R;
import com.want.vendor.deliver.DeliverActivity;
import com.want.vendor.tips.GuideProblemCodeActivity;

/**
 * ViewModel Stub.
 */
public class FaiViewModel extends AbsViewModel implements View.OnClickListener {
    private static final int DEFAULT_TIMELEFT = 60;
    private int mSetTimeLeft = DEFAULT_TIMELEFT;
    private final String TAG = "FaiActivity";
    private FaiContract.Presenter mPresenter;
    FaiContract.View view;
    private boolean isCanIntent = true;

    public FaiViewModel(Context context) {
        super(context);
    }

    public FaiViewModel(FaiContract.Presenter presenter, FaiContract.View view) {
        this.mPresenter = presenter;
        this.view = view;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (R.id.vendor_fai_input_btn == id) {
            //跳转到输入提货码界面
            final Activity activity = ActivityUtils.getActivity(v);
            MEventBus.getDefault().post(new ClearEditTextEventBus());
            DeliverActivity.start(activity);
            activity.finish();

        } else if (R.id.vendor_order_failure_heliping == id) {
            if (isCanIntent) {
                Log.i(TAG,isCanIntent+"");
                final Activity activity = ActivityUtils.getActivity(v);
                GuideProblemCodeActivity.start(activity);
            }
            isCanIntent = false;
            /** 延时 500毫秒 */
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            isCanIntent = true;
                        }
                    }, 500);
        }
    }

    @Bindable
    public String getInputString() {
        if (TextUtils.isEmpty(view.getStringNo())) {
            return "";
        }
        return view.getStringNo();


    }
    @Bindable
    public String getExtraString() {
        if (TextUtils.isEmpty(view.getExtra())) {
            return "";
        }
        return view.getExtra();


    }
}


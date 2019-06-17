package com.want.vendor.home.guide;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.base.sdk.utils.ActivityUtils;

/**
 * ViewModel Stub.
 */
public class GuideViewModel extends AbsViewModel {

    public GuideViewModel() {

    }

    public GuideViewModel(Context context) {
        super(context);
    }

    public void onClick(View v) {
        //  从首页的购物流程跳转到购物引导界面
        final Activity activity = ActivityUtils.getActivity(v);
        ShoppingGuideActivity.start(activity);
    }
}

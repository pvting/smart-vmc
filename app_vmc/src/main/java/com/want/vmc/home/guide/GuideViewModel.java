package com.want.vmc.home.guide;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.base.sdk.utils.ActivityUtils;
import com.want.vmc.guide.ShoppingGuideActivity;

/**
 * <b>Create Date:</b> 10/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class GuideViewModel extends AbsViewModel {

    public GuideViewModel() {

    }

    public GuideViewModel(Context context) {
        super(context);
    }


    public void onClick(View v) {
        // TODO: 10/21/16 跳转到购物引导
        final Activity activity = ActivityUtils.getActivity(v);
        ShoppingGuideActivity.start(activity);
    }
}

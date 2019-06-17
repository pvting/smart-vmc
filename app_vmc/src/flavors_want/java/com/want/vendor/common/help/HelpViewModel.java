package com.want.vendor.common.help;

import android.app.Activity;
import android.view.View;

import com.want.base.sdk.framework.app.mvp.AbsViewModel;
import com.want.base.sdk.utils.ActivityUtils;
import com.want.vendor.tips.GuideProblemCodeActivity;

/**
 * ViewModel Stub.
 */
public class HelpViewModel extends AbsViewModel {

    private HelpContract.Presenter mPresenter;
    private boolean flag = true;

    public HelpViewModel(HelpContract.Presenter presenter) {
        this.mPresenter = presenter;
    }


    /**
     * 按下跳转到问题详细；
     * @param v
     */
    public void onClick(View v) {
        final Activity activity = ActivityUtils.getActivity(v);
        GuideProblemCodeActivity.start(activity);
    }
}

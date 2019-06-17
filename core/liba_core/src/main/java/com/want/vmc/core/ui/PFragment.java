package com.want.vmc.core.ui;

import android.app.Activity;

import com.want.base.sdk.framework.app.MFragment;
import com.want.base.sdk.model.analytic.IAnalytic;

/**
 * <b>Project:</b> project_template<br>
 * <b>Create Date:</b> 16/3/21<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 基本的{@link android.support.v4.app.Fragment}
 * <br>
 */
public class PFragment extends MFragment {

    @Override
    protected IAnalytic onCreateAnalytic() {
        return null;
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
    }
}

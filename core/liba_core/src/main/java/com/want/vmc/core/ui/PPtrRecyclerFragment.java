package com.want.vmc.core.ui;

import android.app.Activity;

import com.want.base.sdk.framework.app.fragment.MPtrRecyclerFragment;
import com.want.base.sdk.model.analytic.IAnalytic;
import com.want.vmc.core.Constants;

/**
 * <b>Project:</b> project_template<br>
 * <b>Create Date:</b> 16/3/21<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 支持下拉刷新, 上拉加载更多的{@link android.support.v4.app.Fragment}
 * <br>
 */
public abstract class PPtrRecyclerFragment<T> extends MPtrRecyclerFragment<T> implements Constants {

    @Override
    protected IAnalytic onCreateAnalytic() {
        return null;
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
    }
}

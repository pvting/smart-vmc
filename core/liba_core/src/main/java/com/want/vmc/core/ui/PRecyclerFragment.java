package com.want.vmc.core.ui;

import android.app.Activity;

import com.want.base.sdk.framework.app.fragment.MRecyclerFragment;
import com.want.base.sdk.model.analytic.IAnalytic;

/**
 * <b>Project:</b> project_template<br>
 * <b>Create Date:</b> 16/3/21<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 包含{@link android.widget.ListView}的{@link android.support.v4.app.Fragment}.
 * <br>
 */
public abstract class PRecyclerFragment<T> extends MRecyclerFragment<T> {

    @Override
    protected IAnalytic onCreateAnalytic() {
        return null;
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
    }
}

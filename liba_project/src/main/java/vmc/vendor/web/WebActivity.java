package vmc.vendor.web;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.want.base.sdk.framework.app.MWebActivity;
import com.want.base.sdk.model.analytic.IAnalytic;

import vmc.project.R;
import vmc.project.ui.view.CountDownView;

/**
 * <b>Create Date:</b> 9/2/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class WebActivity extends MWebActivity {

    private CountDownView mCountDownView;
    @Override
    protected View onCreateView() {
        return getLayoutInflater().inflate(R.layout.web_activity, null, true);
    }

    @Override
    protected IAnalytic onCreateAnalytic() {
        return null;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCountDownView = (CountDownView) findViewById(R.id.web_countdownview);
        mCountDownView.setOnCountdownEndListener(new CountDownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountDownView cv) {
                WebActivity.this.finish();
            }
        });

        mCountDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.this.finish();
            }
        });
    }

    @Override
    public void setupToolbar(AppCompatActivity appCompatActivity, Toolbar toolbar) {
        super.setupToolbar(appCompatActivity, toolbar);
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (MotionEvent.ACTION_UP == action || MotionEvent.ACTION_CANCEL == action) {
            mCountDownView.resetCount();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            // do nothing
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

package com.want.vendor.product.info.back;

import android.content.Context;
import android.databinding.Bindable;
import android.view.View;

import com.want.base.sdk.framework.app.mvp.AbsViewModel;

import java.util.Timer;
import java.util.TimerTask;

/**
 * ViewModel Stub.
 */
public class BackViewModel extends AbsViewModel {

    public static final int DEFAULT_TIMELEFT = 60;

    private Timer mTimeLeftTimer;
    public int mSetTimeLeft = DEFAULT_TIMELEFT;
    private int mTimeLeft = mSetTimeLeft;
    private BackContract.Presenter mPresenter;
    private boolean isPause = true;
    private boolean isDestroy = false;

    public BackViewModel(BackContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    public BackViewModel(Context context) {
        super(context);
    }

    @Bindable
    public int getTimeLeft() {

        resetTimer();

        // 计时器已销毁
        if (isDestroy) {
            return mTimeLeft;
        }

        // 暂停状态不更新计时器
        if (isPause) {
            return mTimeLeft;
        }

        // 倒计时结束
        if (0 == mTimeLeft) {
            if (null != mPresenter) {
                mPresenter.onTimerEnd();
            }
            return 0;
        }

        mTimeLeftTimer = new Timer();
        mTimeLeftTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mTimeLeft -= 1;
                notifyChange();
                this.cancel();
            }
        }, 1000);

        return mTimeLeft;
    }

    /**
     * 设置倒计时总长
     *
     * @param timeLeft
     */
    public void setTimeLeft(int timeLeft) {
        mTimeLeft = timeLeft;
        mSetTimeLeft = timeLeft;
        resetTimer();
        notifyChange();
    }

    private void resetTimer() {
        if (null != mTimeLeftTimer) {
            mTimeLeftTimer.cancel();
            mTimeLeftTimer = null;
        }
    }

    void reset() {
        this.mTimeLeft = mSetTimeLeft;
        resetTimer();
        notifyChange();
    }

    void pause() {
        this.isPause = true;
        notifyChange();
    }

    void resume() {
        this.isPause = false;
        notifyChange();
    }

    void destroy() {
        this.isDestroy = true;
        resetTimer();
    }

    /**
     * 按下返回
     *
     * @param v
     */
    public void onBackClicked(View v) {
        if (null != mPresenter) {
            mPresenter.onBack();
        }
    }
}

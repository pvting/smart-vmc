package vmc.vendor.common.back;

import com.want.base.sdk.framework.app.mvp.IPresenter;
import com.want.base.sdk.framework.app.mvp.IView;

/**
 * Contract of Back.
 */
public interface BackContract {

    interface Presenter extends IPresenter {
        /**
         * 用户主动按下返回
         */
        void onBack();

        /**
         * 自动计时器结束计时
         */
        void onTimerEnd();

        /**
         * 动态设置倒计时
         *
         * @param timeLeft
         */
        void setTimeLeft(int timeLeft);
    }

    interface View extends IView {

        /**
         * 用户主动按下返回
         */
        void onBack();

        /**
         * 自动计时器结束计时
         */
        void onTimerEnd();

        /**
         * 动态设置倒计时
         *
         * @param timeLeft
         */
        void setTimeLeft(int timeLeft);

        void pauseTime();

        void  resumeTime();
    }

}

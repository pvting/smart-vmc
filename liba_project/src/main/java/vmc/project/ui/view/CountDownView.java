package vmc.project.ui.view;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * <b>Create Date:</b> 8/22/16<br>
 * <b>Author:</b> likun<br>
 * <b>Description:</b>
 * 自定义计时器按钮
 * <br>
 */
public class CountDownView extends Button implements View.OnClickListener {

    private static String TAG = CountDownView.class.getSimpleName();
    private CountDownTimerUtils countDownTimerUtils;
    private int DEFAULT_COUNT_DOWN_TIME = 61;
    private String default_text = "返回";
    /**
     * 计时器停止监听
     */
    private OnCountdownEndListener mOnCountdownEndListener;


    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
        init();
        //this(context, attrs, R.attr.buttonStyle);
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
        init();
    }

    private void init() {
        if (null == countDownTimerUtils) {
            initCount();
            startCount();
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != countDownTimerUtils) {
            countDownTimerUtils.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        if (null == countDownTimerUtils) {
            initCount();
            startCount();
        }
    }

    class CountDownTimerUtils extends CountDownTimer {


        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountDownTimerUtils(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }


        @Override
        public void onTick(long millisUntilFinished) {
            setText(default_text + "(" + (millisUntilFinished / 1000) + ")");
        }

        @Override
        public void onFinish() {
            stopCount();
            if (null != mOnCountdownEndListener) {
                mOnCountdownEndListener.onEnd(CountDownView.this);
            }
        }
    }

    /**
     * 设置初始化时间
     *
     * @param defaultCountDownTime
     */
    public void setCountDownTime(int defaultCountDownTime) {
        DEFAULT_COUNT_DOWN_TIME = defaultCountDownTime;
        if (null != countDownTimerUtils) {
            countDownTimerUtils.cancel();
            countDownTimerUtils = new CountDownTimerUtils(DEFAULT_COUNT_DOWN_TIME * 1000, 1000);
            startCount();
        }

    }

    public void setShowText(String text) {
        default_text = text;
    }


    private void initCount() {
        if (null == countDownTimerUtils)

            countDownTimerUtils = new CountDownTimerUtils(DEFAULT_COUNT_DOWN_TIME * 1000, 1000);
    }


    private void startCount() {
        if (null != countDownTimerUtils)
            countDownTimerUtils.start();
    }

    /**
     * 停止计时器并初始化
     */
    public void stopCount() {
        if (null != countDownTimerUtils) countDownTimerUtils.cancel();
        //setText(R.string.back);
        //setBackgroundResource(R.drawable.bg_identify_code_normal);
    }

    /**
     * 重置计时器
     */
    public void resetCount() {

        startCount();
    }

    public interface OnCountdownEndListener {
        void onEnd(CountDownView cv);
    }

    /**
     * set countdown end callback listener
     *
     * @param onCountdownEndListener OnCountdownEndListener
     */
    public void setOnCountdownEndListener(OnCountdownEndListener onCountdownEndListener) {
        mOnCountdownEndListener = onCountdownEndListener;
    }

}

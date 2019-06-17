package vmc.project.ui.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vmc.project.R;

/**
 * <b>Create Date:</b> 9/07/16<br>
 * <b>Author:</b> likun<br>
 * <b>Description:</b>
 * 自定义控制页码
 * <br>
 */
public class ControlToggleView extends RelativeLayout implements View.OnClickListener, CountDownView.OnCountdownEndListener {

    private static String TAG = ControlToggleView.class.getSimpleName();
    private static final int DEFAULT_CURRENT_PAGE = 0;

    /**
     * 返回按钮点击回调
     */
    public interface OnCountDownListener {
        void onBackListener(); //主动返回
        void onTimeOutEndListener(CountDownView cv);//超时返回
    }

    private TextView mPreviousText;
    private CountDownView mBackBtn;
    private TextView mNextText;
    private Context mContext;

    private int currentPage = DEFAULT_CURRENT_PAGE; //缺省
    private int totalPage = 0; //总页数

    private ViewPager mBindVPager;

    private OnCountDownListener mOnCountDownListener;

    public ControlToggleView(Context context) {
        this(context, null);
    }

    public ControlToggleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public ControlToggleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        inflate(context, R.layout.page_control_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPreviousText = (TextView) findViewById(R.id.page_control_previous_text);
        mBackBtn = (CountDownView) findViewById(R.id.page_control_back_btn);
        mBackBtn.setOnCountdownEndListener(this);
        mNextText = (TextView) findViewById(R.id.page_control_next_text);
        this.mPreviousText.setOnClickListener(this);
        this.mBackBtn.setOnClickListener(this);
        this.mNextText.setOnClickListener(this);
        initControlView();
    }


    /**
     * 初始化
     */
    private void initControlView() {
        setPreviousClick(false);
        setNextClick(true);
    }

    /**
     * 设置上一页是否可点击
     *
     * @param isFlag true 可点击 false 不可点击
     */
    private void setPreviousClick(boolean isFlag) {
        if (isFlag) {
            mPreviousText.setEnabled(true);
            mPreviousText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        } else {
            mPreviousText.setEnabled(false);
            mPreviousText.setTextColor(ContextCompat.getColor(mContext, R.color.base_black_3));
        }
    }

    /**
     * 设置下一页是否可点击
     *
     * @param isFlag true 可点击 false 不可点击
     */
    private void setNextClick(boolean isFlag) {
        if (isFlag) {
            mNextText.setEnabled(true);
            mNextText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        } else {
            mNextText.setEnabled(false);
            mNextText.setTextColor(ContextCompat.getColor(mContext, R.color.base_black_3));
        }
    }

    public void setOnCountDownListener(OnCountDownListener onCountDownListener) {
        this.mOnCountDownListener = onCountDownListener;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (R.id.page_control_previous_text == id) {
            //上一页
            if (currentPage > 0) {
                currentPage--;
                if (null != mBindVPager) mBindVPager.setCurrentItem(currentPage);
            }
            if (currentPage > 0) {
                setPreviousClick(true);
            } else {
                setPreviousClick(false);
            }
            if (currentPage == totalPage - 1) {
                setNextClick(false);
            } else {
                setNextClick(true);
            }
        } else if (R.id.page_control_back_btn == id) {
            //返回
            if (null != mOnCountDownListener) mOnCountDownListener.onBackListener();
        } else if (R.id.page_control_next_text == id) {
            //下一页
            if (currentPage < totalPage - 1) {
                currentPage++;
                //log.v(TAG , "开启当前页码:" + currentPage + ",共"+totalPage + "页");
                if (null != mBindVPager) mBindVPager.setCurrentItem(currentPage);
            }
            if (currentPage > 0) {
                setPreviousClick(true);
            } else {
                setPreviousClick(false);
            }
            if (currentPage == totalPage - 1) {
                setNextClick(false);
            } else {
                setNextClick(true);
            }

        }
    }

    @Override
    public void onEnd(CountDownView cv) {
        if (null != mOnCountDownListener) mOnCountDownListener.onTimeOutEndListener(cv);
    }

    /**
     * 绑定并统计页数
     *
     * @param vp
     */
    public void bindViewPager(ViewPager vp) {
        if (null != vp) {
            mBindVPager = vp;
            totalPage = vp.getAdapter().getCount();
            if (totalPage == 1) {
                //当总页数为0时禁用点击分页
                setNextClick(false);
                setPreviousClick(false);
            }
            //log.v(TAG, "绑定的总页码为:" + totalPage);
        }
    }






    /**
     * 重置计数
     */
    public void resetCount() {
        if (null != mBackBtn) mBackBtn.resetCount();
    }

    /**
     * 停止计数
     */
    public void stopCount() {
        if (null != mBackBtn) mBackBtn.stopCount();
    }


}

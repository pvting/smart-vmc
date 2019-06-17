package com.want.vendor.tips;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.vmc.core.BLLController;
import com.vmc.core.OdooAction;
import com.vmc.core.utils.ConfigUtils;
import com.want.base.sdk.framework.app.dialog.BaseDialogFragment;
import com.want.vendor.home.guide.problem.ProblemHelpFragment;
import com.want.vendor.tips.cancelorder.CancelOrderContract;
import com.want.vendor.tips.cancelorder.CancelOrderFragment;
import com.want.vendor.tips.cancelorder.CancelOrderPresenter;
import com.want.vmc.R;
import com.want.vmc.core.Constants;

import vmc.core.log;
import vmc.vendor.common.back.BackViewModel;
import vmc.vendor.utils.IntentHelper;

/**
 * <b>Create Date:</b> 2016/12/7<br>
 * <b>Author:</b> yuxin<br>
 * <b>Description:</b> <br>
 */
public class TipsDialogFragment extends BaseDialogFragment{
    
    private final static String TAG = "TipsDialogFragment";
    private static final String FRAGMENT_TIPS_TAG = "fragment_tips";
    private static final String FRAGMENT_CONTINUE_TAG = "fragment_back";
    
    
    private CountDownTimer countDownTimer;
    
    private OnDissmissLisener lisener;
    private Button btnWaitOutGoods;
    

    public static TipsDialogFragment newInstance( ){
        TipsDialogFragment fragment = new TipsDialogFragment( );
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater,
                              @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState ){
        View view = inflater.inflate(R.layout.fragment_tips_dialog, null);
        view.setOnTouchListener(new View.OnTouchListener( ){
            @Override
            public boolean onTouch( View v, MotionEvent event ){
                final int action = event.getAction( );
                if (MotionEvent.ACTION_DOWN == action || MotionEvent.ACTION_UP == action || MotionEvent.ACTION_CANCEL == action) {
                    cancelCountDownTimer( );
                    startCountDownTimer( );
                }
                return false;
            }
        });
        
        btnWaitOutGoods = (Button) view.findViewById(R.id.btn_wait_goods);
        btnWaitOutGoods.setOnClickListener(new View.OnClickListener( ){
            @Override
            public void onClick( View v ){
                log.d(TAG, "onClick: continue to pay");
                dismiss( );
            }
        });
        
        
        return view;
    }
    
    private void cancelCountDownTimer( ){
        if (countDownTimer != null) {
            countDownTimer.cancel( );
            countDownTimer = null;
        }
    }
    
    private void startCountDownTimer( ){
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(getCountDownTotalTime( ) * Constants.Time.SECOND_1 + 500, Constants.Time.SECOND_1){
                @Override
                public void onTick( long millisUntilFinished ){
                    String strLeftTime = String.valueOf(millisUntilFinished / 1000);
                    btnWaitOutGoods.setText("等待出货" + "(" + strLeftTime + ")");
                }
                
                @Override
                public void onFinish( ){
                    dismiss( );
                }
            };
        }
        countDownTimer.start( );
    }
    
    protected void onConfigDialog( Dialog dialog ){
        Window window = dialog.getWindow( );
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes( );
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ){
        Dialog dialog = new Dialog(getActivity( ));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        onConfigDialog(dialog);
        return dialog;
    }
    
    @Override
    public void onActivityCreated( Bundle savedInstanceState ){
        super.onActivityCreated(savedInstanceState);
        
        IntentFilter intentFilter = new IntentFilter(OdooAction.BLL_PAY_STATUS_TO_UI);
        LocalBroadcastManager.getInstance(getContext( )).registerReceiver(hiddenTipsDialogFragmentReceiver, intentFilter);
        
        
        FragmentManager fm = getChildFragmentManager( );
        FragmentTransaction ft = fm.beginTransaction( );
        // 提示信息
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TIPS_TAG);
        if (null == fragment) {
            if (null == ft) {
                ft = fm.beginTransaction( );
            }
            fragment = ProblemHelpFragment.newInstance( );
            ft.add(R.id.vendor_tips_problem_code, fragment, FRAGMENT_TIPS_TAG);
        }
        
        // 返回按钮
        fragment = fm.findFragmentByTag(FRAGMENT_CONTINUE_TAG);
        if (null == fragment) {
            if (null == ft) {
                ft = fm.beginTransaction( );
            }
            fragment = CancelOrderFragment.newInstance( );
            ft.add(R.id.vendor_cancel_order, fragment, FRAGMENT_CONTINUE_TAG);
        }
        if (null != fragment) {
          new CancelOrderPresenter((CancelOrderContract.View) fragment){
                @Override
                public void onBack( ){
                    super.onBack( );
                    dismiss( );
                    if (!BLLController.getInstance( ).canCancelOrder( )) {
                        log.i(TAG, "back:not arrow cancel the order");
                        return;
                    }
                    BLLController.getInstance( ).cancelSelectProduct( );
                    BLLController.getInstance( ).cancelOrder(TipsDialogFragment.this.getContext( ));
                    log.i(TAG, "back: cancel the order");
                    // 返回商品列表
                    IntentHelper.startProductList(getContext( ));
                }
            };
        }
        
        if (null != ft) {
            ft.commit( );
        }
        
        
    }
    
    @Override
    public void onViewCreated( View view, @Nullable Bundle savedInstanceState ){
        super.onViewCreated(view, savedInstanceState);
    }
    
    @Override
    public void onResume( ){
        super.onResume( );
        startCountDownTimer( );
    }
    
    
    /**
     * 得到总共倒计时时间
     *
     * @return
     */
    private int getCountDownTotalTime( ){
        if (ConfigUtils.getConfig(getContext( )) != null && ConfigUtils.getConfig(getContext( )).vmc_count_down_time_settings != null) {
            if (ConfigUtils.getConfig(getContext( )).vmc_count_down_time_settings.message_countdown_long != 0) {
                return ConfigUtils.getConfig(getContext( )).vmc_count_down_time_settings.message_countdown_long;
            }
        }
        return BackViewModel.DEFAULT_TIMELEFT;
    }
    
    
    public void dismiss( ){
        super.dismiss( );
        if (lisener != null) {
            lisener.onDissmiss( );
        }
    }
    
    public void setOnDissmissLisener( OnDissmissLisener lisener ){
        this.lisener = lisener;
    }
    
    @Override
    public void onPause( ){
        super.onPause( );
        cancelCountDownTimer( );
        LocalBroadcastManager.getInstance(getContext( )).unregisterReceiver(hiddenTipsDialogFragmentReceiver);
        
    }
    
    public interface OnDissmissLisener{
        void onDissmiss( );
    }
    
    /**
     * 接收BL层广播 接收到 支付状态
     */
    private BroadcastReceiver hiddenTipsDialogFragmentReceiver = new BroadcastReceiver( ){
        @Override
        public void onReceive( Context context, Intent intent ){
            log.d(FRAGMENT_TIPS_TAG, "hiddenTipsDialogFragmentReceiver:begin");
            final String action = intent.getAction( );
            if (TextUtils.equals(action, OdooAction.BLL_PAY_STATUS_TO_UI)) {
                dismiss( );
            }
            log.d(FRAGMENT_TIPS_TAG, "hiddenTipsDialogFragmentReceiver:end");
        }
    };
    
}

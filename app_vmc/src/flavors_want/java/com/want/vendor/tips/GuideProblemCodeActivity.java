package com.want.vendor.tips;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;

import com.vmc.core.OdooAction;
import com.vmc.core.utils.ConfigUtils;
import com.want.base.sdk.framework.eventbus.MEventBus;
import com.want.vendor.deliver.fai.ClearEditTextEventBus;
import com.want.vendor.tips.problem.ProblemContract;
import com.want.vendor.tips.problem.ProblemFragment;
import com.want.vendor.tips.problem.ProblemPresenter;
import com.want.vmc.R;

import vmc.core.log;
import vmc.vendor.VActivity;
import vmc.vendor.common.back.BackContract;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.common.back.BackPresenter;
import vmc.vendor.common.back.BackViewModel;
import vmc.vendor.utils.IntentHelper;

public class GuideProblemCodeActivity extends VActivity {


    protected  final String FRAGMENT_TAG_CODE = "f_t_code";

    protected  final String TAG = "GuideProblemCodeActivity";

    private BackPresenter mBackPresenter;

    private  int showType;



    public static void start(Context context) {
        Intent starter = new Intent(context, GuideProblemCodeActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putExtra("showType", 0);
        context.startActivity(starter);
    }

    /**
     * 有订单号的
     *
     * @param context
     * @param order
     */
    public static void start(Context context, String order) {
        Intent starter = new Intent(context, GuideProblemCodeActivity.class);
        starter.putExtra("order", order);
        starter.putExtra("showType", 4);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    /**
     *
     * @param context
     * @param showType
     */
    public static void start(Context context, int showType) {
        Intent starter = new Intent(context, GuideProblemCodeActivity.class);
        starter.putExtra("showType", showType);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestToolbar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_guide_problem_code_activity);

        registerReceiver();

        this.showType = (getIntent().getIntExtra("showType", 0));

        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // 错误提示和二维码页面
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_CODE);
        if (null == fragment && null != findViewById(R.id.vendor_tips_problem_code)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }
            fragment = ProblemFragment.newInstance();
            fragment.setArguments(getIntent().getExtras());
            ft.add(R.id.vendor_tips_problem_code, fragment, FRAGMENT_TAG_CODE);
        }
        if (null != fragment) {
            new ProblemPresenter((ProblemContract.View) fragment);
        }


        // 返回
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_BACK);
        if (null == fragment && null != findViewById(R.id.vendor_back)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }
            fragment = BackFragment.newInstance();
            ft.add(R.id.vendor_back, fragment, FRAGMENT_TAG_BACK);
        }

        if (null != fragment) {
            mBackPresenter = new BackPresenter((BackContract.View) fragment) {
                @Override
                public void onBack() {
                    super.onBack();
                    if (showType!= 1) {//返回首页
                        IntentHelper.startHome(GuideProblemCodeActivity.this);
                    } else {
                        MEventBus.getDefault().post(new ClearEditTextEventBus());
                        GuideProblemCodeActivity.this.finish();
                    }
                }

                @Override
                public void onTimerEnd() {
                    super.onTimerEnd();//超时返回首页
                    IntentHelper.startHome(GuideProblemCodeActivity.this);
                }
            };
        }

        if (null != ft) {
            ft.commit();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (ConfigUtils.getConfig(GuideProblemCodeActivity.this) != null &&
                ConfigUtils.getConfig(GuideProblemCodeActivity.this).vmc_count_down_time_settings != null) {
            if (ConfigUtils.getConfig(GuideProblemCodeActivity.this).vmc_count_down_time_settings.help_page_countdown != 0) {
                mBackPresenter.setTimeLeft(ConfigUtils.getConfig(GuideProblemCodeActivity.this).vmc_count_down_time_settings.help_page_countdown);
            } else {
                mBackPresenter.setTimeLeft(BackViewModel.DEFAULT_TIMELEFT);
            }
        }
    }

    /**
     * 注册取消窗口广播
     */
    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(OdooAction.UI_CANCEL_PROBLEM_TO_UI);
        LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(cancelProblemReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(cancelProblemReceiver);
        super.onDestroy();
        log.i(TAG,"onDestroy: 关闭问题帮助");
    }



    /**
     * 接受BLL层网络发生变化
     */
    private BroadcastReceiver cancelProblemReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra("showType",0)==showType){
                if (!GuideProblemCodeActivity.this.isFinishing()){
                    GuideProblemCodeActivity.this.finish();
                    log.i(TAG,"cancelProblemReceiver -->onReceive: 取消异常弹窗");
                }
            }
        }
    };
}

package com.want.vendor.common.seriporterror;

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
import com.want.vendor.common.upgrade.UpgradeFragment;
import com.want.vmc.R;
import com.want.vmc.serialporterror.SerialPortErrorFragment;

import vmc.vendor.VActivity;
import vmc.vendor.common.back.BackPresenter;


/*
        ************************************
         ** http://weibo.com/lixiaodaoaaa #
         ** create at 2017/10/24   19:08 ***
         ******** by:lixiaodaoaaa ***********
*/
public class SeriportErrorActivity extends VActivity {


    public static final String TAG = "SeriportErrorActivity";
    private BackPresenter mBackPresenter;

    public static void start(Context context) {
        Intent starter = new Intent(context, SeriportErrorActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestContentView();
        setContentView(R.layout.vendor_seriport_error_activity);

//        initBroadcast();

       final FragmentManager fm = getSupportFragmentManager();
       FragmentTransaction ft = null;
        Fragment fragment = fm.findFragmentByTag(UpgradeFragment.TAG);
        if (null == fragment) {
           ft = fm.beginTransaction();
            fragment = SeriportErrorFragment.newInstance();
           ft.add(R.id.vendor_seriport_error, fragment, SeriportErrorFragment.TAG);
        }
        if (null != ft) {
            ft.commit();
        }


    }


//       void initBroadcast(){
//
//
//        IntentFilter filter = new IntentFilter(OdooAction.BLL_SERIAL_ERROR_TO_UI);
//
//        LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(mUpgradeReceiver,filter);

   // }



    /**
     * 后台升级取消
     */
//    public BroadcastReceiver mUpgradeReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            if (intent.getAction().equals(OdooAction.BLL_CANCEL_UPGRADE_TO_UI)){
//               SeriportErrorActivity.this.finish();
//            }
//        }
//    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(mUpgradeReceiver);
    }
}

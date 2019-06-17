package com.want.vendor.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.vmc.core.OdooAction;
import com.vmc.core.utils.ConfigUtils;
import com.want.vendor.home.info.InfoViewModel;
import com.want.vmc.BR;
import com.want.vmc.R;
import com.want.vendor.home.guide.GuideContract;
import com.want.vendor.home.guide.GuideFragment;
import com.want.vendor.home.guide.GuidePresenter;
import com.want.vendor.home.imageads.ImageAdsFragment;
import com.want.vendor.home.info.InfoContract;
import com.want.vendor.home.info.InfoFragment;
import com.want.vendor.home.info.InfoPresenter;
import com.want.vendor.home.shopping.ShoppingContract;
import com.want.vendor.home.shopping.ShoppingFragment;
import com.want.vendor.home.shopping.ShoppingPresenter;
import com.want.vendor.home.surprise.SurpriseContract;
import com.want.vendor.home.surprise.SurpriseFragment;
import com.want.vendor.home.surprise.SurprisePresenter;


import vmc.core.log;
import vmc.vendor.VActivity;
import vmc.vendor.utils.IntentHelper;

import com.want.vmc.home.advert.AdvertFragment;


/**
 * <b>Create Date:</b> 2016/11/14<br>
 * <b>Author:</b> ZhongWenjie<br>
 * <b>Description:</b> <br>
 */
public class HomeActivity extends VActivity {
    private static final String TAG = "HomeActivity";
    private static final String FRAGMENT_TAG_INFO = "info";
    private static final String FRAGMENT_TAG_ADVERT = "advert";
    private static final String FRAGMENT_TAG_IMAGE_ADVERT = "image_advert";
    private static final String FRAGMENT_TAG_GUIDE = "guide";
    private static final String FRAGMENT_TAG_SHOPPING = "shopping";
    private static final String FRAGMENT_TAG_SURPRISE = "surprise";
    private TelephonyManager mTelephonyManager;
    private PhoneStateListener mPhoneStateListener;

    // 该方法的访问修饰符声明为private是为了让使用者通过Intent.startXXX的形式启动Activity
    private static void start(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.vendor_home_activity);

        sendBroadcastToLaunch();//发送广播给激活软件(agent)

        isWebServiceUpgrade();//后台是否正在升级中

        registBroadCast();//注册监听广播

        addFragment();//添加视图

    }

    /**
     * 启动后发送广播给launch app
     */
    private void sendBroadcastToLaunch() {
        Intent intent=new Intent("InstallApp_Success");
        intent.putExtra("package_name", HomeActivity.this.getPackageName());
        HomeActivity.this.sendBroadcast(intent);
    }

    private void isWebServiceUpgrade(){
        //如果当前是升级状态
       if (ConfigUtils.getConfig(this.getApplicationContext()).is_upgrade){
           IntentHelper.startUpgrade(this);
       }
    }

    /**
     * 注册广播接收器
     */
   private void registBroadCast(){

       //获取telephonyManager
       mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

       //监听信号
       mPhoneStateListener = new PhoneStateListener();

       IntentFilter intentFilter = new IntentFilter();
       intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
       intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
       intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
       intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);


       //过滤 电话刷新的广播
       IntentFilter intentFilter1 = new IntentFilter();
       intentFilter.addAction(OdooAction.VMC_NOTICE_PHONE_UPDATE);


       //注册广播
       registerReceiver(mNetWorkBroadCastReciver, intentFilter);

       LocalBroadcastManager.getInstance(this).registerReceiver(mUpdatePhoneReceiver, intentFilter1);
   }

    /**
     * 添加fragment到Activity
     */
    private void  addFragment(){
      FragmentManager fm = getSupportFragmentManager();

      FragmentTransaction ft = fm.beginTransaction();

      // 顶部信息展示区
      Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_INFO);
      if (null == fragment && null != findViewById(R.id.home_info)) {
          fragment = InfoFragment.newInstance();
          ft.add(R.id.home_info, fragment, FRAGMENT_TAG_INFO);
      }
      if (null != fragment) {
          new InfoPresenter((InfoContract.View) fragment);
      }

      // 视频广告
      fragment = fm.findFragmentByTag(FRAGMENT_TAG_ADVERT);

      if (null == fragment && null != findViewById(R.id.home_advert_video)) {
          fragment = AdvertFragment.newInstance();
          ft.add(R.id.home_advert_video, fragment, FRAGMENT_TAG_ADVERT);
      }

      // 图片广告
      fragment = fm.findFragmentByTag(FRAGMENT_TAG_IMAGE_ADVERT);
      if (null == fragment && null != findViewById(R.id.home_advert_image)) {
          fragment = ImageAdsFragment.newInstance();
          ft.add(R.id.home_advert_image, fragment, FRAGMENT_TAG_IMAGE_ADVERT);
      }

      // 提货码
      fragment = fm.findFragmentByTag(FRAGMENT_TAG_SURPRISE);
      if (null == fragment && null != findViewById(R.id.home_surprise)) {
          fragment = SurpriseFragment.newInstance();
          ft.add(R.id.home_surprise, fragment, FRAGMENT_TAG_SURPRISE);
      }

      if (null != fragment) {
          new SurprisePresenter((SurpriseContract.View) fragment);
      }

      // 购物引导
      fragment = fm.findFragmentByTag(FRAGMENT_TAG_GUIDE);
      if (null == fragment && null != findViewById(R.id.home_guide)) {
          fragment = GuideFragment.newInstance();
          ft.add(com.want.vmc.R.id.home_guide, fragment, FRAGMENT_TAG_GUIDE);
      }

      if (null != fragment) {
          new GuidePresenter((GuideContract.View) fragment);
      }

      // 快乐购
      fragment = fm.findFragmentByTag(FRAGMENT_TAG_SHOPPING);
      if (null == fragment && null != findViewById(R.id.home_shopping)) {
          fragment = ShoppingFragment.newInstance();
          ft.add(R.id.home_shopping, fragment, FRAGMENT_TAG_SHOPPING);
      }

      if (null != fragment) {
          new ShoppingPresenter((ShoppingContract.View) fragment);
      }

      if (null != ft) {
          ft.commit();
      }
   }


    /**
     * 监测网络信号的广播
     */
   private BroadcastReceiver mNetWorkBroadCastReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.d("mNetWorkBroadCastReciver", "监测到网络信号变化");
            InfoFragment fragment = (InfoFragment) HomeActivity.this.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_INFO);
            if (null != fragment && null != findViewById(R.id.vendor_netWork)) {
                if (fragment.getViewModel() != null) ;
                fragment.getViewModel().notifyPropertyChanged(BR.netWorkIcon);
            }
        }
    };



    /**
     * 刷新客服电话
     */
    private BroadcastReceiver mUpdatePhoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log.d("mUpdatePhoneReceiver", "再次获取客服电话: " + "刷新啦");
            InfoFragment fragment = (InfoFragment) HomeActivity.this.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_INFO);
            if (null != fragment && null != findViewById(R.id.home_info)) {
                if (fragment.getViewModel() != null) ;
                fragment.getViewModel().notifyPropertyChanged(BR.configNum);//刷新客服电话
            }
        }
    };



    @Override
    protected void onPause() {
        super.onPause();
        //离开此页面 停止监听
        mTelephonyManager.listen(mPhoneStateListener, InfoViewModel.PhoneStatListener.LISTEN_NONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTelephonyManager.listen(mPhoneStateListener,InfoViewModel.PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);
    }


    @Override
    public int getThrottleTime() {
        return 500;
    }


    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mUpdatePhoneReceiver);
        unregisterReceiver(mNetWorkBroadCastReciver);
        super.onDestroy();
    }
}

package com.want.vmc.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.want.vmc.MainActivity;
import com.want.vmc.home.advert.AdvertContract;
import com.want.vmc.home.advert.AdvertFragment;
import com.want.vmc.home.advert.AdvertPresenter;
import com.want.vmc.home.guide.GuideContract;
import com.want.vmc.home.guide.GuideFragment;
import com.want.vmc.home.guide.GuidePresenter;
import com.want.vmc.home.info.InfoContract;
import com.want.vmc.home.info.InfoFragment;
import com.want.vmc.home.info.InfoPresenter;
import com.want.vmc.home.shopping.ShoppingContract;
import com.want.vmc.home.shopping.ShoppingFragment;
import com.want.vmc.home.shopping.ShoppingPresenter;

import com.want.vmc.R;
import vmc.vendor.VActivity;

/**
 * <b>Create Date:</b> 10/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class HomeActivity extends VActivity {

    private static final String FRAGMENT_TAG_INFO = "info";
    private static final String FRAGMENT_TAG_ADVERT = "advert";
    private static final String FRAGMENT_TAG_GUIDE = "guide";
    private static final String FRAGMENT_TAG_SHOPPING = "shopping";

    // 该方法的访问修饰符声明为private是为了让使用者通过Intent.startXXX的形式启动Activity
    @SuppressWarnings("unused")
    private static void start(Context context) {
        Intent starter = new Intent(context, HomeActivity.class);
        context.startActivity(starter);
    }
    /**
     * 启动后发送广播给launch app
     */
    private void sendBroadcastToLaunch() {
        Intent intent=new Intent("InstallApp_Success");
        intent.putExtra("package_name",HomeActivity.this.getPackageName());
        HomeActivity.this.sendBroadcast(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestToolbar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_home_activity);
        sendBroadcastToLaunch();
        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = null;

        // TODO: 10/22/16 处理Activity内存重启

        // 信息展示
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_INFO);
        if (null == fragment && null != findViewById(R.id.home_info)) {
            ft = fm.beginTransaction();
            fragment = InfoFragment.newInstance();
            ft.add(R.id.home_info, fragment, FRAGMENT_TAG_INFO);
        }

        if (null != fragment) {
            new InfoPresenter((InfoContract.View) fragment);
        }

        // 广告展示
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_ADVERT);
        if (null == fragment && null != findViewById(R.id.home_advert_video)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }

            fragment = AdvertFragment.newInstance();

            ft.add(R.id.home_advert_video, fragment, FRAGMENT_TAG_ADVERT);
        }

        if (null != fragment) {
            new AdvertPresenter((AdvertContract.View) fragment);
        }

        // 购物引导
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_GUIDE);
        if (null == fragment && null != findViewById(R.id.home_guide)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }

            fragment = GuideFragment.newInstance();
            ft.add(R.id.home_guide, fragment, FRAGMENT_TAG_GUIDE);
        }

        if (null != fragment) {
            new GuidePresenter((GuideContract.View) fragment);
        }

        // 购物
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_SHOPPING);
        if (null == fragment && null != findViewById(R.id.home_shopping)) {
            if (null == ft) {
                ft = fm.beginTransaction();
            }

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
}

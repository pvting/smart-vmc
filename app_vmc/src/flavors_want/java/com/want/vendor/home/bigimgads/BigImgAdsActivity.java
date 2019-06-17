package com.want.vendor.home.bigimgads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.vmc.core.utils.ConfigUtils;
import com.want.vmc.R;

import vmc.vendor.VActivity;
import vmc.vendor.common.back.BackContract;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.common.back.BackPresenter;
import vmc.vendor.common.back.BackViewModel;


public class BigImgAdsActivity extends VActivity {
    private final String FRAGMENT_TAG_BIGIMG = "fragment_tag_bigimg";
    public final String URL_KEY = "extra_key";
    private BackPresenter mBackPresenter;


    public static void start(Context context,String url) {
        Intent intent = new Intent(context, BigImgAdsActivity.class);
        intent.putExtra("extra_key",url);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_home_bigimgads_activity);
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_BIGIMG);
        //大图展示区
        if (null == fragment && null != findViewById(R.id.home_bigimageads)) {
            fragment = BigImageFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("url", getIntent().getStringExtra(URL_KEY));
            fragment.setArguments(bundle);
            ft.add(R.id.home_bigimageads, fragment, FRAGMENT_TAG_BIGIMG);
        }

        // 返回
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_BACK);
        if (null == fragment && null != findViewById(R.id.vendor_back)) {

            fragment = BackFragment.newInstance();
            ft.add(R.id.vendor_back, fragment, FRAGMENT_TAG_BACK);
        }
        if (null != fragment) {
            mBackPresenter = new BackPresenter((BackContract.View) fragment) {

                @Override
                public void onBack() {
                    super.onBack();
                    BigImgAdsActivity.this.finish();
                }

                @Override
                public void onTimerEnd() {
                    super.onTimerEnd();
                    BigImgAdsActivity.this.finish();
                }
            };
        }




        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConfigUtils.getConfig(BigImgAdsActivity.this) != null &&
            ConfigUtils.getConfig(BigImgAdsActivity.this).vmc_count_down_time_settings != null) {
            if(ConfigUtils.getConfig(BigImgAdsActivity.this).vmc_count_down_time_settings.advertising_countdown != 0) {
                mBackPresenter.setTimeLeft(ConfigUtils.getConfig(BigImgAdsActivity.this).vmc_count_down_time_settings.advertising_countdown);
            }else{
                mBackPresenter.setTimeLeft(BackViewModel.DEFAULT_TIMELEFT);
            }
        }
    }
}

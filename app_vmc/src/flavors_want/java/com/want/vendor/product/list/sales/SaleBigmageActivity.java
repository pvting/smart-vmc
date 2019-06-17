package com.want.vendor.product.list.sales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.want.vmc.R;

import vmc.vendor.VActivity;
import vmc.vendor.common.back.BackContract;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.common.back.BackPresenter;

/**
 * Created by zhongwenjie on 2016/12/8.
 */

public class SaleBigmageActivity extends VActivity{

    private final String FRAGMENT_TAG_BIGIMG = "fragment_tag_bigimg";
    public final String URL_KEY = "extra_key";


    public static void start(Context context, String url) {
        Intent intent = new Intent(context, SaleBigmageActivity.class);
        intent.putExtra("extra_key",url);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_home_salebigmage_activity);
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_BIGIMG);
        //大图展示区
        if (null == fragment && null != findViewById(R.id.vendor_home_salebigmage)) {
            fragment = SaleBigmageFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("url", getIntent().getStringExtra(URL_KEY));
            fragment.setArguments(bundle);
            ft.add(R.id.vendor_home_salebigmage, fragment, FRAGMENT_TAG_BIGIMG);
        }

        // 返回
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_BACK);
        if (null == fragment && null != findViewById(R.id.vendor_back)) {

            fragment = BackFragment.newInstance();
            ft.add(R.id.vendor_back, fragment, FRAGMENT_TAG_BACK);
        }
        if (null != fragment) {
            new BackPresenter((BackContract.View) fragment) {

                @Override
                public void onBack() {
                    super.onBack();
                    SaleBigmageActivity.this.finish();
                }

                @Override
                public void onTimerEnd() {
                    super.onTimerEnd();
                    SaleBigmageActivity.this.finish();
                }
            };
        }

        ft.commit();
    }
}

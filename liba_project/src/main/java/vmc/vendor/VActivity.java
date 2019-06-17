package vmc.vendor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;

import com.vmc.core.OdooAction;
import com.want.vmc.core.ui.PActivity;

import org.apache.commons.collections4.functors.IfClosure;

import vmc.core.log;
import vmc.project.R;
import vmc.project.ui.view.CornerImageView;
import vmc.vendor.common.back.BackFragment;
import vmc.vendor.utils.SerialPortUtils;

import static android.R.attr.data;
import static vmc.vendor.Constants.AQI.WEATHER_CHANGE_BAD;
import static vmc.vendor.Constants.AQI.WEATHER_CHANGE_BEST;
import static vmc.vendor.Constants.AQI.WEATHER_CHANGE_FINE;
import static vmc.vendor.Constants.AQI.WEATHER_CHANGE_LIGHT;
import static vmc.vendor.Constants.AQI.WEATHER_CHANGE_MID;
import static vmc.vendor.Constants.Weather.WEATHER_CHANGE_CLOUDY;
import static vmc.vendor.Constants.Weather.WEATHER_CHANGE_FOG;
import static vmc.vendor.Constants.Weather.WEATHER_CHANGE_RAIN;
import static vmc.vendor.Constants.Weather.WEATHER_CHANGE_SNOW;
import static vmc.vendor.Constants.Weather.WEATHER_CHANGE_SUNSHINE;
import static vmc.vendor.Constants.Weather.WEATHER_CHANGE_THUNDER;
import static vmc.vendor.Constants.Weather.WEATHER_CHANGE_WIND;

/**
 * <b>Create Date:</b> 10/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class VActivity extends PActivity {
    protected static final String FRAGMENT_TAG_BACK = "f_t_back";
    public static final String WEATHER_CHANGE_TAG="weather_change";
    public static int weatherChangeTag = 0;
    public static int PM25ChangeTag = 11;

    private long mFirstTouch;



    private boolean enableToolbar = false;

    private BroadcastReceiver mAdsUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("mWeather");

            if ((findViewById(R.id.vendor_home_style_bg) != null)) {


                if (WEATHER_CHANGE_SUNSHINE.equals(data)) {
                    findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.sunshine_bg));
                    setFrameLayoutBg(R.color.sunshine_bg);
                } else if (WEATHER_CHANGE_THUNDER.equals(data)) {

                    findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.thunder_bg));
                    setFrameLayoutBg(R.color.thunder_bg);

                } else if (WEATHER_CHANGE_SNOW.equals(data)) {

//                    findViewById(R.id.vendor_home_weather_sunny_background).setBackgroundResource(R.drawable.vendor_home_wether_snow_background);
                    findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.snow_bg));
                    setFrameLayoutBg(R.color.snow_bg);

                } else if (WEATHER_CHANGE_CLOUDY.equals(data)) {

//                    findViewById(R.id.vendor_home_weather_sunny_background).setBackgroundResource(R.drawable.vendor_home_weather_cloudy_background);
                    findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.cloudy_bg));
                    setFrameLayoutBg(R.color.cloudy_bg);

                } else if (WEATHER_CHANGE_RAIN.equals(data)) {

//                    findViewById(R.id.vendor_home_weather_sunny_background).setBackgroundResource(R.drawable.vendor_home_weather_rain_background);
                    findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.rain_bg));
                    setFrameLayoutBg(R.color.rain_bg);

                } else if (WEATHER_CHANGE_WIND.equals(data)) {

                    findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.wind_bg));
                    setFrameLayoutBg(R.color.wind_bg);

                } else if (WEATHER_CHANGE_FOG.equals(data)) {

                    findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.fog_bg));
                    setFrameLayoutBg(R.color.fog_bg);

                }

//                PM25
                if ((findViewById(R.id.watergod_home_style_bg) != null)) {

                    if (WEATHER_CHANGE_BEST.equals(data)) {
                        findViewById(R.id.watergod_home_style_bg).setBackgroundColor(getResources().getColor(R.color.weather_best));
                        setFrameLayoutBg(R.color.weather_best);

                    } else if (WEATHER_CHANGE_FINE.equals(data)) {

                        findViewById(R.id.watergod_home_style_bg).setBackgroundColor(getResources().getColor(R.color.weather_fine));
                        setFrameLayoutBg(R.color.weather_fine);

                    } else if (WEATHER_CHANGE_LIGHT.equals(data)) {

//                    findViewById(R.id.vendor_home_weather_sunny_background).setBackgroundResource(R.drawable.vendor_home_wether_snow_background);
                        findViewById(R.id.watergod_home_style_bg).setBackgroundColor(getResources().getColor(R.color.weather_light));
                        setFrameLayoutBg(R.color.weather_light);

                    } else if (WEATHER_CHANGE_MID.equals(data)) {

//                    findViewById(R.id.vendor_home_weather_sunny_background).setBackgroundResource(R.drawable.vendor_home_weather_cloudy_background);
                        findViewById(R.id.watergod_home_style_bg).setBackgroundColor(getResources().getColor(R.color.weather_mid));
                        setFrameLayoutBg(R.color.weather_mid);

                    } else if (WEATHER_CHANGE_BAD.equals(data)) {

//                    findViewById(R.id.vendor_home_weather_sunny_background).setBackgroundResource(R.drawable.vendor_home_weather_rain_background);
                        findViewById(R.id.watergod_home_style_bg).setBackgroundColor(getResources().getColor(R.color.weather_bad));
                        setFrameLayoutBg(R.color.weather_bad);

                    }
                }


            }


        }
    };

    /**
     * 接受串口异常发送的广播，并进行响应的处理。
     */
    private class VmcErrorReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (OdooAction.BLL_SERIAL_ERROR_TO_UI.equals(action)) {
                SerialPortUtils.setSerialPortError(context);
            }
        }
    }


    private void setFrameLayoutBg(int colorBg){
        if((findViewById(R.id.cornerBottomRight))!=null&&
           (findViewById(R.id.cornerBottomLeft))!=null&&
           (findViewById(R.id.cornerBottomLeft))!=null&&
           (findViewById(R.id.cornerTopLeft))!=null&&
           (findViewById(R.id.cornerTopRight))!=null){
            ((CornerImageView)findViewById(R.id.cornerBottomRight)).setCBackground(getResources().getColor(colorBg));
            ((CornerImageView)findViewById(R.id.cornerBottomLeft)).setCBackground(getResources().getColor(colorBg));
            ((CornerImageView)findViewById(R.id.cornerTopLeft)).setCBackground(getResources().getColor(colorBg));
            ((CornerImageView)findViewById(R.id.cornerTopRight)).setCBackground(getResources().getColor(colorBg));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter(WEATHER_CHANGE_TAG);
        this.registerReceiver(mAdsUpdateReceiver,intentFilter);
        VApplication vApplication= VApplication.getInstance();
        if(vApplication!=null){
            vApplication.addActivity(this);
        }
        IntentFilter filter = new IntentFilter(OdooAction.BLL_SERIAL_ERROR_TO_UI);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new VmcErrorReceiver(),filter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if ((findViewById(R.id.vendor_home_style_bg) != null)) {
            switch (weatherChangeTag) {
                case 0:
                    findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.sunshine_bg));
                    setFrameLayoutBg(R.color.sunshine_bg);

                    break;
                case 1:
                    findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.thunder_bg));
                    setFrameLayoutBg(R.color.thunder_bg);

                    break;
                case 2:
                    findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.snow_bg));
                    setFrameLayoutBg(R.color.snow_bg);

                    break;
                case 3:
                    findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.cloudy_bg));
                    setFrameLayoutBg(R.color.cloudy_bg);

                    break;
                case 4:
                    findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.rain_bg));
                    setFrameLayoutBg(R.color.rain_bg);

                    break;
                case 5:
                    findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.wind_bg));
                    setFrameLayoutBg(R.color.wind_bg);

                    break;
                case 6:
                    findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.fog_bg));
                    setFrameLayoutBg(R.color.fog_bg);

                    break;
            }
        }
        //pm25

        if ((findViewById(R.id.watergod_home_style_bg) != null)) {
            switch (PM25ChangeTag) {
                case 11:
                    findViewById(R.id.watergod_home_style_bg).setBackgroundColor(getResources().getColor(R.color.weather_best));
                    setFrameLayoutBg(R.color.weather_best);

                    break;
                case 12:
                    findViewById(R.id.watergod_home_style_bg).setBackgroundColor(getResources().getColor(R.color.weather_fine));
                    setFrameLayoutBg(R.color.weather_fine);

                    break;
                case 13:
                    findViewById(R.id.watergod_home_style_bg).setBackgroundColor(getResources().getColor(R.color.weather_light));
                    setFrameLayoutBg(R.color.weather_light);

                    break;
                case 14:
                    findViewById(R.id.watergod_home_style_bg).setBackgroundColor(getResources().getColor(R.color.weather_mid));
                    setFrameLayoutBg(R.color.weather_mid);

                    break;
                case 15:
                    findViewById(R.id.watergod_home_style_bg).setBackgroundColor(getResources().getColor(R.color.weather_bad));
                    setFrameLayoutBg(R.color.weather_bad);

                    break;

            }
        }


        if (WEATHER_CHANGE_SUNSHINE.equals(data)) {
            findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.sunshine_bg));
            setFrameLayoutBg(R.color.sunshine_bg);

        } else if (WEATHER_CHANGE_THUNDER.equals(data)) {

            findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.thunder_bg));
            setFrameLayoutBg(R.color.thunder_bg);

        } else if (WEATHER_CHANGE_SNOW.equals(data)) {

            findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.snow_bg));
            setFrameLayoutBg(R.color.snow_bg);

        } else if (WEATHER_CHANGE_CLOUDY.equals(data)) {

            findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.cloudy_bg));
            setFrameLayoutBg(R.color.cloudy_bg);

        } else if (WEATHER_CHANGE_RAIN.equals(data)) {

            findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.rain_bg));
            setFrameLayoutBg(R.color.rain_bg);

        } else if (WEATHER_CHANGE_WIND.equals(data)) {

            findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.wind_bg));
            setFrameLayoutBg(R.color.wind_bg);

        } else if (WEATHER_CHANGE_FOG.equals(data)) {

            findViewById(R.id.vendor_home_style_bg).setBackgroundColor(getResources().getColor(R.color.fog_bg));
            setFrameLayoutBg(R.color.fog_bg);

        }

        //pm25

        if (WEATHER_CHANGE_BEST.equals(data)) {
            findViewById(R.id.watergod_home_style_bg).setBackgroundColor(getResources().getColor(R.color.weather_best));

        } else if (WEATHER_CHANGE_FINE.equals(data)) {

            findViewById(R.id.watergod_home_style_bg).setBackgroundColor(getResources().getColor(R.color.weather_fine));

        } else if (WEATHER_CHANGE_LIGHT.equals(data)) {

            findViewById(R.id.watergod_home_style_bg).setBackgroundColor(getResources().getColor(R.color.weather_light));

        } else if (WEATHER_CHANGE_MID.equals(data)) {

            findViewById(R.id.watergod_home_style_bg).setBackgroundColor(getResources().getColor(R.color.weather_mid));

        } else if (WEATHER_CHANGE_BAD.equals(data)) {

            findViewById(R.id.watergod_home_style_bg).setBackgroundColor(getResources().getColor(R.color.weather_bad));

        }

    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(mAdsUpdateReceiver);
        VApplication vApplication= VApplication.getInstance();
        if(vApplication!=null){
            vApplication.finishActivity(this);
        }
        super.onDestroy();

    }

    protected void requestToolbar(boolean enable) {
        this.enableToolbar = enable;

    }


    @Override
    public void setupToolbar(AppCompatActivity appCompatActivity, Toolbar toolbar) {
        super.setupToolbar(appCompatActivity, toolbar);
        if (!enableToolbar) {
            toolbar.setVisibility(View.GONE);
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        // FIXME: 04/11/2016 暂时每次点击都重置计时器，后续根据需要来决定是否修改
        if (MotionEvent.ACTION_UP == action || MotionEvent.ACTION_CANCEL == action) {
            final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_BACK);
            if (fragment instanceof BackFragment) {
                final BackFragment f = (BackFragment) fragment;
                f.resetTimer();
            }
        }
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (System.currentTimeMillis() - mFirstTouch < getThrottleTime()) {
                return true;
            } else {
                mFirstTouch = System.currentTimeMillis();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     *
     * 两次点击的延时时间间隔
     * @return
     */
    public  int getThrottleTime(){
        return 180;
    }
}

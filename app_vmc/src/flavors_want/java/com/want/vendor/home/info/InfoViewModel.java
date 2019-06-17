package com.want.vendor.home.info;

import android.content.Context;
import android.content.Intent;
import android.databinding.Bindable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.vmc.core.model.view.Weather;
import com.want.location.ILocation;
import com.want.location.ILocationClient;
import com.want.location.LocationListener;
import com.want.location.LocationManager;
import com.want.vmc.BR;
import com.want.vmc.R;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import vmc.core.log;
import vmc.vendor.VActivity;

import static android.telephony.TelephonyManager.NETWORK_TYPE_1xRTT;
import static android.telephony.TelephonyManager.NETWORK_TYPE_CDMA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EDGE;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EHRPD;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_0;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_A;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_B;
import static android.telephony.TelephonyManager.NETWORK_TYPE_GPRS;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSDPA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSPA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSPAP;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSUPA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_IDEN;
import static android.telephony.TelephonyManager.NETWORK_TYPE_LTE;
import static android.telephony.TelephonyManager.NETWORK_TYPE_UMTS;
import static vmc.vendor.Constants.Weather.WEATHER_CHANGE_CLOUDY;
import static vmc.vendor.Constants.Weather.WEATHER_CHANGE_FOG;
import static vmc.vendor.Constants.Weather.WEATHER_CHANGE_RAIN;
import static vmc.vendor.Constants.Weather.WEATHER_CHANGE_SNOW;
import static vmc.vendor.Constants.Weather.WEATHER_CHANGE_SUNSHINE;
import static vmc.vendor.Constants.Weather.WEATHER_CHANGE_THUNDER;
import static vmc.vendor.Constants.Weather.WEATHER_CHANGE_WIND;

/**
 * <b>Create Date:</b> 2016/11/14<br>
 * <b>Author:</b> ZhongWenjie<br>
 * <b>Description:</b> <br>
 */
public class InfoViewModel extends com.want.vmc.home.info.InfoViewModel {
    private static final String TAG = "InfoViewModel";
    private Drawable mWeatherIcon;
    public String mWeather;
    public String mTemperature;
    private Timer mWeatherTimer;
    private volatile ILocation location;
    private String mLocation;
    private int backGround = R.drawable.vendor_home_weather_sunny_background;
    private int mRssi;
    private int mNetworkType;
    private String currentCity = "";

    public InfoViewModel(Context context) {
        super(context);
        //获取地理位置
        location = LocationManager.getInstance().getLastKnownLocation();
        //获取经纬度
        if (location != null) {
            log.i(TAG,
                  "获取纬度坐标:" +
                  location.getLatitude() +
                  "、获取经度:" +
                  location.getLongitude() +
                  "、城市:" +
                  location.getCity());
            mLocation = location.getLongitude() + "," + location.getLatitude();
            currentCity = location.getCity();
            if (currentCity == null) {
                currentCity = "";
            }

        } else {
            currentCity = "";
            log.e(TAG, "未获取到当前地理位置");
        }

        //初始化天气
        initWeather();

        LocationManager.getInstance().addLocationListener(new LocationListener() {
            @Override
            public void onReceiveLocation(ILocation location, ILocationClient.RESULT result) {

                location = LocationManager.getInstance().getLastKnownLocation();
                if (location != null) {
                    mLocation = location.getLongitude() + "," + location.getLatitude();
                    if (!TextUtils.isEmpty(location.getCity())) {
                        if (!location.getCity().equals(currentCity)) {
                            log.i(TAG, "onReceiveLocation: 地理位置发生改变" + location.getCity());
                            initWeather();
                        } else {
                            log.i(TAG, "onReceiveLocation: 地理位置未发生变化" + location.getCity());
                        }
                    } else {
                        log.i(TAG, "onReceiveLocation: 地理更新错误" + location.getCity());
                    }
                }

            }
        });
    }

    @Bindable
    public Drawable getIconid() {
        return mWeatherIcon;
    }

    @Bindable
    public String getWeather() {
        return mWeather;
    }

    @Bindable
    public String getTemperature() {
        return mTemperature;
    }

    @Override
    @Bindable
    public String getTime() {
        return super.getTime();
    }

    @Bindable
    public String getCity() {
        return (null == location) ? "上海" : location.getCity();
    }

    @Override
    @Bindable
    public String getDate() {
        return super.getDate();
    }

    @Bindable
    public String getHello() {
        final Calendar calendar = Calendar.getInstance();
        final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay >= 12) {
            return "下午好";
        }
        return "上午好";
    }

    /**
     * 获取信号强度
     */
    public class PhoneStatListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            //网络信号改变时，获取网络信息
            log.i(TAG, "getNetWorkIcon: 网络信发生号改变");
            getNetWorkIcon();
            notifyPropertyChanged(BR.netWorkIcon);
        }

    }

    //绑定 网络信号的图标
    @Bindable
    public Drawable getNetWorkIcon() {
        //区分 链接的网络是wifi 还是 SIM卡
        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        //通过 NetworkInfo对象获取网络的类型
        if (info != null && info.isAvailable()) {
            mNetworkType = info.getSubtype();
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager manager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
                WifiInfo connectionInfo = manager.getConnectionInfo();
                mRssi = connectionInfo.getRssi();
                if (mRssi <= (-90)) {
                    return mContext.getResources().getDrawable(R.drawable.vendor_home_info_network0);
                } else if (mRssi <= (-80)) {
                    return mContext.getResources().getDrawable(R.drawable.vendor_home_info_network1);
                } else if (mRssi <= (-70)) {
                    return mContext.getResources().getDrawable(R.drawable.vendor_home_info_network2);
                } else if (mRssi <= (-60)) {
                    return mContext.getResources().getDrawable(R.drawable.vendor_home_info_network3);
                } else if (mRssi <= (-50)) {
                    return mContext.getResources().getDrawable(R.drawable.vendor_home_info_network4);
                } else if (mRssi <= 40) {
                    return mContext.getResources().getDrawable(R.drawable.vendor_home_info_network5);
                } else {
                    return mContext.getResources().getDrawable(R.drawable.vendor_home_info_network0);
                }
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (mNetworkType != 0)
                    switch (mNetworkType) {

                        case NETWORK_TYPE_GPRS:

                        case NETWORK_TYPE_EDGE:

                        case NETWORK_TYPE_CDMA:

                        case NETWORK_TYPE_1xRTT:

                        case NETWORK_TYPE_IDEN:
                            //2G网络
                            return mContext.getResources().getDrawable(R.drawable.vendor_home_info_network1);

                        case NETWORK_TYPE_UMTS:

                        case NETWORK_TYPE_EVDO_0:

                        case NETWORK_TYPE_EVDO_A:

                        case NETWORK_TYPE_HSDPA:

                        case NETWORK_TYPE_HSUPA:

                        case NETWORK_TYPE_HSPA:

                        case NETWORK_TYPE_EVDO_B:

                        case NETWORK_TYPE_EHRPD:

                        case NETWORK_TYPE_HSPAP:
                            //3G网络
                            return mContext.getResources().getDrawable(R.drawable.vendor_home_info_network3);

                        case NETWORK_TYPE_LTE:
                            //4G
                            return mContext.getResources().getDrawable(R.drawable.vendor_home_info_network4);

                        default:
                            return mContext.getResources().getDrawable(R.drawable.vendor_home_info_network0);
                    }
            }

        }
        //默认返回 没有网络
        return mContext.getResources().getDrawable(R.drawable.vendor_home_info_network0);
    }

    /**
     * 获取机器出厂编码
     *
     * @return
     */
    @Bindable
    public String getFactoryCode() {
        return super.getFactoryCode();
    }

    /**
     * 获取客服电话
     *
     * @return
     */
    @Bindable
    @Override
    public String getConfigNum() {
        return super.getConfigNum();
    }


    private void initWeather() {
        log.i(TAG, "initWeather:初始化天气");
        autoUpdateWeather();
    }

    Handler mHandler;

    /**
     * 自动更新天气信息
     */
    private void autoUpdateWeather() {

        mWeatherTimer = new Timer();

        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                Weather weather = requestWeather();
                if (weather == null) {
                    mWeatherTimer.cancel();
                    log.e(TAG, "获取天气失败,将于5分钟后再次获取");

                    if (mHandler == null) {
                        mHandler = new Handler(Looper.getMainLooper());
                    } else {
                        mHandler.removeCallbacksAndMessages(null);
                    }

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            autoUpdateWeather();
                        }
                    }, 1000*60*5);
                    return;
                }

                log.i(TAG, "获取天气成功,将于30分钟后再次获取");

                reflesh(weather);
            }
        };
        mWeatherTimer.schedule(task, 0, (1000 * 60 * 30));
    }


    void reflesh(Weather weather) {


        final double temperature = weather.result.temperature;

        final String skycon = weather.result.skycon;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                DecimalFormat decimalFormat = new DecimalFormat("#");
                mTemperature = decimalFormat.format(temperature) + "°";
                String weather;
                mWeatherIcon = null;
                if (TextUtils.equals(skycon, "CLEAR_DAY") ||
                    TextUtils.equals(skycon, "CLEAR_NIGHT")) {
                    weather = getString(R.string.vendor_sun_icon);
                    VActivity.weatherChangeTag = 0;
                    backGround = R.drawable.vendor_home_weather_sunny_background;
                    mWeatherIcon =
                            mContext.getResources().getDrawable(R.drawable.vendor_weather_sunny_icon);
                    Intent intent = new Intent(VActivity.WEATHER_CHANGE_TAG);
                    intent.putExtra("mWeather", WEATHER_CHANGE_SUNSHINE);
                    mContext.sendBroadcast(intent);
                } else if (TextUtils.equals(skycon, "CLOUDY") ||
                           TextUtils.equals(skycon, "PARTLY_CLOUDY_DAY") ||
                           TextUtils.equals(skycon, "PARTLY_CLOUDY_NIGHT")) {
                    VActivity.weatherChangeTag = 3;
                    weather = getString(R.string.vendor_cloudy_icon);

                    mWeatherIcon =
                            mContext.getResources()
                                    .getDrawable(R.drawable.vendor_weather_cloudy_icon);
                    backGround = R.drawable.vendor_home_weather_cloudy_background;
                    Intent intent = new Intent(VActivity.WEATHER_CHANGE_TAG);
                    intent.putExtra("mWeather", WEATHER_CHANGE_CLOUDY);
                    mContext.sendBroadcast(intent);
                } else if (TextUtils.equals(skycon, "RAIN")) {
                    VActivity.weatherChangeTag = 4;
                    weather = getString(R.string.vendor_rain_icon);
                    backGround = R.drawable.vendor_home_weather_rain_background;
                    mWeatherIcon =
                            mContext.getResources().getDrawable(R.drawable.vendor_weather_rain_icon);
                    Intent intent = new Intent(VActivity.WEATHER_CHANGE_TAG);
                    intent.putExtra("mWeather", WEATHER_CHANGE_RAIN);
                    mContext.sendBroadcast(intent);
                } else if (TextUtils.equals(skycon, "SNOW")) {
                    weather = getString(R.string.vendor_snow_icon);
                    VActivity.weatherChangeTag = 2;
                    backGround = R.drawable.vendor_home_weather_snow_background;
                    mWeatherIcon =
                            mContext.getResources().getDrawable(R.drawable.vendor_weather_snow_icon);
                    Intent intent = new Intent(VActivity.WEATHER_CHANGE_TAG);
                    intent.putExtra("mWeather", WEATHER_CHANGE_SNOW);
                    mContext.sendBroadcast(intent);
                } else if (TextUtils.equals(skycon, "THUNDER")) {
                    VActivity.weatherChangeTag = 1;
                    weather = getString(R.string.vendor_thunder_icon);
                    backGround = R.drawable.vendor_home_weather_thunder_background;
                    mWeatherIcon =
                            mContext.getResources()
                                    .getDrawable(R.drawable.vendor_weather_thunder_icon);
                    Intent intent = new Intent(VActivity.WEATHER_CHANGE_TAG);
                    intent.putExtra("mWeather", WEATHER_CHANGE_THUNDER);
                    mContext.sendBroadcast(intent);
                } else if (TextUtils.equals(skycon, "WIND")) {
                    VActivity.weatherChangeTag = 5;
                    weather = getString(R.string.vendor_wind_icon);
                    backGround = R.drawable.vendor_home_weather_wind_background;
                    mWeatherIcon =
                            mContext.getResources().getDrawable(R.drawable.vendor_weather_wind_icon);
                    Intent intent = new Intent(VActivity.WEATHER_CHANGE_TAG);
                    intent.putExtra("mWeather", WEATHER_CHANGE_WIND);
                    mContext.sendBroadcast(intent);
                } else if (TextUtils.equals(skycon, "FOG")) {
                    VActivity.weatherChangeTag = 6;
                    weather = getString(R.string.vendor_fog_icon);
                    backGround = R.drawable.vendor_home_weather_fog_background;
                    mWeatherIcon =
                            mContext.getResources().getDrawable(R.drawable.vendor_weather_fog_icon);
                    Intent intent = new Intent(VActivity.WEATHER_CHANGE_TAG);
                    intent.putExtra("mWeather", WEATHER_CHANGE_FOG);
                    mContext.sendBroadcast(intent);
                } else {
                    weather = getString(R.string.vendor_download_icon);
                }
                mWeather = weather;
                notifyPropertyChanged(BR.iconid);
                notifyPropertyChanged(BR.weather);
                notifyPropertyChanged(BR.temperature);
                notifyPropertyChanged(BR.backGround);
            }
        });
    }


    /**
     * 获取天气信息
     *
     * @return 天气对象
     */
    private Weather requestWeather() {
        OkHttpClient client = new OkHttpClient();
        String url;
        if (mLocation != null&&location!=null) {
            log.i(TAG, "当前位置已确定，将获取(" + location.getCity() + ")天气");
            url = "https://api.caiyunapp.com/v2/zPPIZnZ=eCFe=8=2/" + mLocation + "/realtime.json";
        } else {
            log.i(TAG, "当前位置不确定，将获取默认(上海)天气");
            url = "https://api.caiyunapp.com/v2/zPPIZnZ=eCFe=8=2/121.48,31.22/realtime.json";
        }
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String data = null;
                if(null!=response.body()){
                    data = response.body().string();
                }

                if (TextUtils.isEmpty(data)){
                    log.e(TAG, "getWeather:数据异常");
                    return null;
                }

                final Weather weather = new Gson().fromJson(data, Weather.class);
                if (null != weather && null != weather.result) {
                    log.i(TAG, "getWeather: 获取天气成功, 天气: " + weather.result.skycon + ", 温度: " + weather.result.temperature);
                    return weather;
                }
                log.e(TAG, "getWeather: 获取天气异常");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.e(TAG, "getWeather:解析异常"+e.getMessage());
        }
        return null;
    }

    @Bindable
    public Drawable getBackGround() {
        return mContext.getResources().getDrawable(backGround);
    }

}

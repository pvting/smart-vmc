package vmc.vendor;

/**
 * <b>Create Date:</b> 02/11/2016<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public interface Constants extends com.want.vmc.core.Constants {

    /**
     * 公共结果
     */
    public static class Result {
        /**
         * 成功
         **/
        public static final int OK = 1;
        /**
         * 失败
         **/
        public static final int FAIL = 0;
    }

    class Action {
        /**
         * 启动启动页面
         */
        public static final String MAIN = "vmc.vendor.ACTION_MAIN";
        /**
         * 启动首页
         */
        public static final String HOME = "vmc.vendor.ACTION_HOME";
        /**
         * 启动提货码页面
         */
        public static final String DELIVER = "vmc.vendor.ACTION_DELIVER";
        /**
         * 启动商品列表页面
         */
        public static final String PRODUCTLIST = "vmc.vendor.ACTION_PRODUCTLIST";
        /**
         * 产品信息
         */
        public static final String PRODUCT_INFO = "vmc.vendor.ACTION_PRODUCT_INFO";


        public static final String PRODUCT_RESULT = "vmc.vendor.ACTION_OUT_PRODUCT_RESULT";

        public static final String SERVICE_UPGRADE = "vmc.vendor.ACTION_UPGRADE";





        /**
         * 点击了支付页面
         */
        public static final String CLICK_PAYMENTH_METHOD = "vmc.vendor.ACTION_CLICK_PAYMENTHOD";
    }

    class Weather {
        public static String WEATHER_CHANGE_SUNSHINE = "sunshine_bg";//晴天
        public static String WEATHER_CHANGE_THUNDER = "thunder_bg";//雷电
        public static String WEATHER_CHANGE_SNOW = "snow_bg";//雪天
        public static String WEATHER_CHANGE_CLOUDY = "cloudy_bg";//阴天
        public static String WEATHER_CHANGE_RAIN = "rain_bg";//雨天
        public static String WEATHER_CHANGE_WIND = "wind_bg";//风
        public static String WEATHER_CHANGE_FOG = "rain_bg";//雾
    }

    class AQI {
        public static String WEATHER_CHANGE_BEST = "best_bg";//优
        public static String WEATHER_CHANGE_FINE = "fine_bg";//良
        public static String WEATHER_CHANGE_LIGHT = "light_bg";//轻度
        public static String WEATHER_CHANGE_MID = "mid_bg";//中度
        public static String WEATHER_CHANGE_BAD = "bad_bg";//重度
    }
}

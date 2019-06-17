package vmc.vendor.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * <b>Create Date:</b> 27/12/2016<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 串口工具类
 * <br>
 */
public class SerialPortUtils {

    private static final String SP_SERIALPORT_NAME = "actiondata";
    private static final String SERIALPORT_CONN_ERROR = "connError";

    public static final String SERIALPORT_INIT = "init";
    public static final String SERIALPORT_ERROR = "error";

    public static final String LIQUIDSTATE_ERROR = "liquidState";
    public static final String OUTPUT_ERROR = "outPut";



    private SerialPortUtils() {
        //no instance
    }



    public static void setLiquidstate(Context context,boolean liquid) {
        getSp(context)
                .edit()
                .putBoolean(LIQUIDSTATE_ERROR, liquid)
                .apply();
    }


    public static void initSensorState(Context context) {
        getSp(context)
                .edit()
                .putBoolean(LIQUIDSTATE_ERROR, true)
                .apply();

        getSp(context)
                .edit()
                .putBoolean(OUTPUT_ERROR, true)
                .apply();
    }




    public static void setOutPutstate(Context context,boolean outPut) {
        getSp(context)
                .edit()
                .putBoolean(OUTPUT_ERROR, outPut)
                .apply();
    }

    public static boolean getLiquidstate(Context context) {
        return getSp(context).getBoolean(LIQUIDSTATE_ERROR, true);
    }

    public static boolean getOutPutstate(Context context) {
        return getSp(context).getBoolean(OUTPUT_ERROR, true);
    }





    /**
     * 初始化串口状态
     *
     * @param context
     */
    public static void setSerialPortInit(Context context) {
        getSp(context).edit().putString(SERIALPORT_CONN_ERROR, SERIALPORT_INIT).apply();
    }

    /**
     * 是否初始化
     *
     * @param context
     *
     * @return
     */
    public static boolean isInit(Context context) {
        return getSp(context).getString(SERIALPORT_CONN_ERROR, "").equals(SERIALPORT_INIT);
    }

    public static void setSerialPortError(Context context) {
        getSp(context)
                .edit()
                .putString(SERIALPORT_CONN_ERROR, SERIALPORT_ERROR)
                .apply();
    }

    /**
     * 串口是否异常
     *
     * @param context
     *
     * @return
     */
    public static boolean isError(Context context) {
        return getSp(context).getString(SERIALPORT_CONN_ERROR, "").equals(SERIALPORT_ERROR);
    }

    private static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences(SP_SERIALPORT_NAME, Context.MODE_PRIVATE);
    }
}

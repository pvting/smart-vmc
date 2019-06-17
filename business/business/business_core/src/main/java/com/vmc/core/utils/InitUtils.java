package com.vmc.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;

/**
 * <b>Create Date:</b> 8/23/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class InitUtils {

    private static final String INIT_SP_NAME = "init";
    private static final String INIT_SP_CODE = "code";
    private static final String INIT_KEY_INIT = "isinit";
    private static final String INIT_KEY_CODE = "factory_code";
    private static final String INIT_KEY_MACHINE_ID = "machine_id";
    public static final int MODE_READ = Context.MODE_WORLD_READABLE;

    private InitUtils() {
        //no instance
    }

    /**
     * 是否已经初始化
     *
     * @param context
     *
     * @return
     */
    public static boolean isInit(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(INIT_SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(INIT_KEY_INIT, false);
    }


    /**
     * 设置位已初始化状态
     *
     * @param context
     */
    public static void setInit(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(INIT_SP_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(INIT_KEY_INIT, true);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    /**
     * 持久化机器ID
     *
     * @param context
     * @param machineId
     */
    public static void setInitMachineId(Context context, String machineId) {
        final SharedPreferences sp = context.getSharedPreferences(INIT_SP_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString(INIT_KEY_MACHINE_ID, machineId);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    /**
     * 获取机器ID
     *
     * @param context
     *
     * @return
     */
    public static String getInitMachineId(Context context) {
        return context.getSharedPreferences(INIT_SP_NAME, Context.MODE_PRIVATE)
                      .getString(INIT_KEY_MACHINE_ID, "");
    }

    public static void setFactoryCode(Context context,String factoryCode) {
        final SharedPreferences sp = context.getSharedPreferences(INIT_SP_CODE, MODE_READ);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString(INIT_KEY_CODE, factoryCode);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    public static String getFactoryCode(Context context) {
        return context.getSharedPreferences(INIT_SP_CODE, MODE_READ)
                      .getString(INIT_KEY_CODE, "");
    }

}

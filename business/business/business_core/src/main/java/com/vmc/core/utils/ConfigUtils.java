package com.vmc.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;

import com.vmc.core.model.config.ConfigInit;
import com.want.base.sdk.utils.JsonUtils;

/**
 * <b>Create Date:</b>2016/12/6,上午8:52<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class ConfigUtils {

    private static final String SP_NAME = "ConfigParams";

    public static volatile int[] sBLLLock = new int[0];

    private ConfigUtils() {
        throw new UnsupportedOperationException("not instants exception");
    }


    /**
     * 保存页面返回时间
     *
     * @param context
     * @param config
     */
    public static void setConfig(Context context, ConfigInit config) {

        synchronized (sBLLLock) {
            SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sp.edit();
            editor.putString("config", JsonUtils.toJson(config));
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        }
    }

    /**
     * 获取页面倒计时
     *
     * @param context
     */
    public static ConfigInit getConfig(Context context) {
        synchronized (sBLLLock) {
            SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            String config = sp.getString("config", JsonUtils.toJson(new ConfigInit()));
            return JsonUtils.fromJson(config, ConfigInit.class);
        }
    }

    /**
     * 设置脉冲数和排废水时间
     *
     * @param context
     * @param pulse
     * @param pumpTime
     */
    public static void setPulseAndPumpTimeConfig(Context context, int pulse, int pumpTime) {
        synchronized (sBLLLock) {
            SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sp.edit();
            editor.putInt("pulse", pulse);
            editor.putInt("pumpTime", pumpTime);
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        }
    }

    /**
     * 获取脉冲数和排废水时间
     *
     * @param context
     *
     * @return
     */
    public static int getPulseConfig(Context context) {
        synchronized (sBLLLock) {
            SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            int config = sp.getInt("pulse", 350);
            return config;
        }
    }

    public static int getPumpTimeConfig(Context context) {
        synchronized (sBLLLock) {
            SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            int config = sp.getInt("pumpTime", 4);
            return config;
        }
    }
}
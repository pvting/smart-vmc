package com.vmc.core.model.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;

/**
 * <b>Project:</b> android-hollywant<br>
 * <b>Create Date:</b> 2016/4/11<br>
 * <b>Author:</b> ldc <br>
 * <b>Description:</b> <br>
 * <p/>
 * 用户类型
 * <p/>
 * <p/>
 */

public class UserType {

    private static final String SP_NAME = "sp";
    private static final String STIRNG_KEY = "user_type";
    private static SharedPreferences sp;

    //业代
    public static String TYPE_SALER = "Dealer";

    //终端
    public static String TYPE_USER = "Terminal";

    //经销商
    public static String TYPE_ADMIN = "Admin";

    //未分类
    public static String TYPE_NULL = "Null";


    /**
     * 保存用户类型在本地
     *
     * @param context
     * @param type
     */
    public static void setUserType(Context context, String type) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        SharedPreferencesCompat.EditorCompat.getInstance().apply(sp.edit().putString(STIRNG_KEY, type));
    }

    /**
     * 获取用户类型
     *
     * @param context
     * @return
     */
    public static String getUserType(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(STIRNG_KEY, "");
    }

    /**
     * 判断终端
     *
     * @param context
     * @return
     */
    public static boolean isUser(Context context) {
        return getUserType(context).equals(TYPE_USER) ? true : false;
    }

    /**
     * 判断业代
     *
     * @param context
     * @return
     */
    public static boolean isSaler(Context context) {
        return getUserType(context).equals(TYPE_SALER) ? true : false;
    }

    /**
     * 判断经销商
     *
     * @param context
     * @return
     */
    public static boolean isAdmin(Context context) {
        return getUserType(context).equals(TYPE_ADMIN) ? true : false;
    }

    /**
     * 判断未分配
     *
     * @param context
     * @return
     */
    public static boolean isNull(Context context) {
        return getUserType(context).equals(TYPE_NULL) ? true : false;
    }

}

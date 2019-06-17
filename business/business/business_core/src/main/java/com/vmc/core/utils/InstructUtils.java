package com.vmc.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;

import com.vmc.core.model.instruct.Instruct;
import com.vmc.core.model.instruct.InstructList;
import com.want.base.sdk.utils.JsonUtils;

import java.util.HashSet;
import java.util.Set;

import vmc.core.log;

/**
 * <b>Create Date:</b>2016/12/6,上午8:52<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class InstructUtils {

    private static final String SP_NAME = "Instruct";

    private InstructUtils() {
        throw new UnsupportedOperationException("not instants exception");
    }

    /**
     * 缓存指令
     *
     * @param context
     */
    public static void setInstruct(Context context, InstructList instructList) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        Set<String> instructs = new HashSet<>();
        for (Instruct record : instructList.records) {
            instructs.add(JsonUtils.toJson(record));
        }
        editor.putStringSet("instruct", instructs);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    /**
     * 获取指令集合
     *
     * @param context
     */
    public static Set<String> getInstruct(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getStringSet("instruct", new HashSet<String>());
    }


    /**
     * 获取指令集合
     *
     * @param context
     */
    public static void removeInstruct(Context context, Instruct instruct) {
        Set<String> set = getInstruct(context);
        String item = null;
        for (String s : set) {
            Instruct ins = JsonUtils.fromJson(s, Instruct.class);
            if (instruct.id .equals(ins.id) ) {
                item = s;
            }
        }
        set.remove(item);
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet("instruct", set);
        log.i("InstructUtils", "指令：" + set.toString());
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }
}
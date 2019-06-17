package com.vmc.core.model;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * <b>Project:</b> Odoo<br>
 * <b>Create Date:</b> 15/12/31<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * Utils for JSONObject.
 * <br>
 */
public class OdooGsonHelper {


    private static class Holder {
        public static Gson gson = new Gson();
    }


    private JSONObject mJsonObject;

    private OdooGsonHelper(JSONObject jsonObject) {
        this.mJsonObject = jsonObject;
    }


    /**
     * Convert string json to json bean.
     * @param json string json
     * @param clazz json bean class
     * @param <T> json bean class type
     * @return json bean.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return Holder.gson.fromJson(json, clazz);
    }

    /**
     * Convert string json to json bean with {@link Type}
     * @param json string json
     * @param type json bean type
     * @param <T> json bean
     * @return json bean.
     */
    public static <T> T fromJson(String json, Type type) {
        return Holder.gson.fromJson(json, type);
    }

    public static String toJson(Object clazz) {
        return Holder.gson.toJson(clazz);
    }

    public static Gson getGson() {
        return Holder.gson;
    }
}

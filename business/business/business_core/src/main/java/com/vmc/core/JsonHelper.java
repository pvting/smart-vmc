package com.vmc.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * <b>Project:</b> Odoo<br>
 * <b>Create Date:</b> 15/12/31<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * Utils for JSONObject.
 * <br>
 */
class JsonHelper {

    private JSONObject mJsonObject;

    private JsonHelper(JSONObject jsonObject) {
        this.mJsonObject = jsonObject;
    }

    /**
     * Put value to JSONObject safely.
     * @param json {@link JSONObject}
     * @param name name
     * @param value value
     */
    static JsonHelper optput(JSONObject json, String name, Object value) {
        JsonHelper helper = new JsonHelper(json);
        helper.optput(name, value);
        return helper;
    }

    /**
     * Put value to JSONObject safely.
     * @param name name
     * @param value value
     */
    JsonHelper optput(String name, Object value) {
        try {
            mJsonObject.putOpt(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }


    /**
     * Put map value to JSONObject safely.
     *
     * @param params {@link Map}
     * @return {@link JsonHelper}
     */
    JsonHelper optput(Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            optput(entry.getKey(), entry.getValue());
        }
        return this;
    }

}

package com.vmc.core.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * <b>Project:</b> Odoo<br>
 * <b>Create Date:</b> 15/12/31<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * Base model class. All the models must be implements this model.
 * <br>
 */
public abstract class Model implements Parcelable, Serializable {
    private static final long serialVersionUID = 1L;

    public String msg;
    public String title;
    public String error;

    public Model() {

    }

    /**
     * Protected constructer method workded with {@link Parcelable}.
     *
     * @param in {@link Parcel}
     */
    protected Model(Parcel in) {
        msg = in.readString();
        title = in.readString();
        error = in.readString();
    }

    @Override
    public String toString() {
        return "msg='" + msg + '\'' +
               ", title='" + title + '\'' +
               ", error='" + error + '\'';
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msg);
        dest.writeString(title);
        dest.writeString(error);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }


    protected void putopt(JSONObject object, String key, Object value){
        try {
            object.putOpt(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

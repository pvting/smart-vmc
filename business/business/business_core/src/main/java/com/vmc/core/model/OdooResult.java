package com.vmc.core.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <b>Project:</b> Odoo<br>
 * <b>Create Date:</b> 15/12/31<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 *
 * <br>
 */
public class OdooResult<T extends Model> extends Model {

    public String jsonrpc;
    public String id;
    public T result;
//    public JSONObject error;

    protected OdooResult() {
        
    }

    protected OdooResult(Parcel in) {
        super(in);
        jsonrpc = in.readString();
        id = in.readString();
        result = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    public static final Parcelable.Creator<OdooResult> CREATOR = new Parcelable.Creator<OdooResult>() {
        public OdooResult createFromParcel(Parcel in) {
            return new OdooResult(in);
        }

        public OdooResult[] newArray(int size) {
            return new OdooResult[size];
        }
    };

    /**
     * Flatten this object in to a Parcel.
     *  @param dest The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     * May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(jsonrpc);
        dest.writeString(id);
        dest.writeParcelable(result, flags);
    }
}

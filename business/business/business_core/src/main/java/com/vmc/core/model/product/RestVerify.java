package com.vmc.core.model.product;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b> 2016/11/22<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b> <br>
 */
public class RestVerify extends Model {

  public   boolean success;


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(this.success ? (byte) 1 : (byte) 0);
    }

    public RestVerify() {}

    protected RestVerify(Parcel in) {
        super(in);
        this.success = in.readByte() != 0;
    }

    public static final Creator<RestVerify> CREATOR = new Creator<RestVerify>() {
        @Override
        public RestVerify createFromParcel(Parcel source) {return new RestVerify(source);}

        @Override
        public RestVerify[] newArray(int size) {return new RestVerify[size];}
    };
}

package com.vmc.core.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <b>Project:</b> Odoo<br>
 * <b>Create Date:</b> 16/1/11<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class OdooMessage extends Model {
    public String count;
    public String cart_qty;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.count);
        dest.writeString(this.cart_qty);
    }

    public OdooMessage() {
    }

    protected OdooMessage(Parcel in) {
        super(in);
        this.count = in.readString();
        this.cart_qty = in.readString();
    }

    public static final Parcelable.Creator<OdooMessage> CREATOR = new Parcelable.Creator<OdooMessage>() {
        @Override
        public OdooMessage createFromParcel(Parcel source) {
            return new OdooMessage(source);
        }

        @Override
        public OdooMessage[] newArray(int size) {
            return new OdooMessage[size];
        }
    };
}


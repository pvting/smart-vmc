package com.vmc.core.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <b>Project:</b> Odoo<br>
 * <b>Create Date:</b> 15/12/31<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * Odoo Context Info Data model.
 * <br>
 */
public class ContextInfo extends Model {

    /** 用户语言 */
    public String lang;
    /** 用户时区 */
    public String tz;
    /** 用户ID */
    public int uid;

    public ContextInfo() {

    }

    protected ContextInfo(Parcel in) {
        super(in);
        this.lang = in.readString();
        this.tz = in.readString();
        this.uid = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.lang);
        dest.writeString(this.tz);
        dest.writeInt(this.uid);
    }


    public static final Parcelable.Creator<ContextInfo> CREATOR = new Parcelable.Creator<ContextInfo>() {
        public ContextInfo createFromParcel(Parcel source) {return new ContextInfo(source);}

        public ContextInfo[] newArray(int size) {return new ContextInfo[size];}
    };
}

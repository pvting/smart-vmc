package com.vmc.core.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * <b>Project:</b> apps<br>
 * <b>Create Date:</b> 16/1/15<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * Base List Data model.
 * <br>
 */
public abstract class OdooList<T extends Parcelable> extends Model {

    /** 目标数据列表 */
    public ArrayList<T> records;
    /** 目标数据列表的总长度 */
    public int total;

    public OdooList() {}

    protected OdooList(Parcel in) {
        super(in);
        this.total = in.readInt();
    }

    @Override
    public String toString() {
        return "OdooList{" +
               "length=" + total +
               ", records=" + records +
               '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.total);
        dest.writeTypedList(this.records);
    }
}

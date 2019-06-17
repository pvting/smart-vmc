package com.vmc.core.model.product;

import android.os.Parcel;

import com.vmc.core.model.OdooList;

/**
 * <b>Create Date:</b>2017/2/7 14:25<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class OdooStockList extends OdooList<OdooStock>{

    public OdooStockList() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {super.writeToParcel(dest, flags);}

    protected OdooStockList(Parcel in) {super(in);}

    public static final Creator<OdooStockList> CREATOR = new Creator<OdooStockList>() {
        @Override
        public OdooStockList createFromParcel(Parcel source) {return new OdooStockList(source);}

        @Override
        public OdooStockList[] newArray(int size) {return new OdooStockList[size];}
    };
}
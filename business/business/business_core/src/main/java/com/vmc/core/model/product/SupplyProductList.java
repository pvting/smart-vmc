package com.vmc.core.model.product;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * <b>Create Date:</b>2017/6/5 09:04<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class SupplyProductList implements Parcelable {

    public String msg;
    public String result;
    public ArrayList<SupplyProduct> data;


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.msg);
        dest.writeString(this.result);
        dest.writeTypedList(this.data);
    }

    public SupplyProductList() {}

    protected SupplyProductList(Parcel in) {
        this.msg = in.readString();
        this.result = in.readString();
        this.data = in.createTypedArrayList(SupplyProduct.CREATOR);
    }

    public static final Parcelable.Creator<SupplyProductList>
            CREATOR =
            new Parcelable.Creator<SupplyProductList>() {
                @Override
                public SupplyProductList createFromParcel(Parcel source) {return new SupplyProductList(source);}

                @Override
                public SupplyProductList[] newArray(int size) {return new SupplyProductList[size];}
            };
}
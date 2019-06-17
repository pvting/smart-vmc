package com.vmc.core.model.product;

import android.os.Parcel;

import com.vmc.core.model.OdooList;

import java.util.ArrayList;

/**
 * <b>Create Date:</b> 8/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class OdooProductList extends OdooList<OdooProduct> {


    public ArrayList<String> product_type_list;

    public OdooProductList() {

    }

    @Override
    public String toString() {
        return "ProductList{" +
                "product_type_list=" + product_type_list +
                "} " + super.toString();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeStringList(this.product_type_list);
    }

    protected OdooProductList(Parcel in) {
        super(in);
        this.product_type_list = in.createStringArrayList();
    }

    public static final Creator<OdooProductList> CREATOR = new Creator<OdooProductList>() {
        @Override
        public OdooProductList createFromParcel(Parcel source) {return new OdooProductList(source);}

        @Override
        public OdooProductList[] newArray(int size) {return new OdooProductList[size];}
    };
}

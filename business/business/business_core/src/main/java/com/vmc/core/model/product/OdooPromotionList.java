package com.vmc.core.model.product;

import android.os.Parcel;

import com.vmc.core.model.OdooList;

/**
 * <b>Create Date:</b>2017/2/7 16:54<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class OdooPromotionList extends OdooList<OdooPromotion> {


    public OdooPromotionList() {}

    @Override
    public String toString() {
        return "PromotionList{} " + super.toString();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {super.writeToParcel(dest, flags);}

    protected OdooPromotionList(Parcel in) {super(in);}

    public static final Creator<OdooPromotionList> CREATOR = new Creator<OdooPromotionList>() {
        @Override
        public OdooPromotionList createFromParcel(Parcel source) {return new OdooPromotionList(source);}

        @Override
        public OdooPromotionList[] newArray(int size) {return new OdooPromotionList[size];}
    };
}
package com.vmc.core.model.ads;

import android.os.Parcel;

import com.vmc.core.model.OdooList;

/**
 * <b>Create Date:</b> 8/25/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class AdList extends OdooList<Ads> {


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public AdList() {}

    protected AdList(Parcel in) {
        super(in);
        this.records = in.createTypedArrayList(Ads.CREATOR);
    }

    public static final Creator<AdList> CREATOR = new Creator<AdList>() {
        @Override
        public AdList createFromParcel(Parcel source) {return new AdList(source);}

        @Override
        public AdList[] newArray(int size) {return new AdList[size];}
    };


}

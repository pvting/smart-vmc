package com.vmc.core.model.pickup;

import android.os.Parcel;

import com.vmc.core.model.OdooList;
import com.vmc.core.model.replenishment.ReplenishProduct;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/19<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 */

public class PickCreateList extends OdooList<ReplenishProduct> {

    @Override
    public void writeToParcel(Parcel dest, int flags) {super.writeToParcel(dest, flags);}

    public PickCreateList() {}

    protected PickCreateList(Parcel in) {
        super(in);
        this.records = in.createTypedArrayList(ReplenishProduct.CREATOR);
    }

    public static final Creator<PickCreateList> CREATOR = new Creator<PickCreateList>() {
        @Override
        public PickCreateList createFromParcel(Parcel source) {return new PickCreateList(source);}

        @Override
        public PickCreateList[] newArray(int size) {return new PickCreateList[size];}
    };
}

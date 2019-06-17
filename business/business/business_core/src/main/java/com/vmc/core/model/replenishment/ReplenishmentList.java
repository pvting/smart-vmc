package com.vmc.core.model.replenishment;

import android.os.Parcel;

import com.vmc.core.model.OdooList;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/12<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 */

public class ReplenishmentList extends OdooList<Replenishment> {

    public ReplenishmentList() {
    }

    protected ReplenishmentList(Parcel in) {
        super(in);
        this.records = in.createTypedArrayList(Replenishment.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public static final Creator<ReplenishmentList> CREATOR = new Creator<ReplenishmentList>() {
        @Override
        public ReplenishmentList createFromParcel(Parcel source) {
            return new ReplenishmentList(source);
        }

        @Override
        public ReplenishmentList[] newArray(int size) {
            return new ReplenishmentList[size];
        }
    };
}

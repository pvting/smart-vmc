package com.vmc.core.model.replenishment;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/20<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 */

public class Financial extends Model {

    public String take_amount;
    public String supply_amount;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.take_amount);
        dest.writeString(this.supply_amount);
    }

    public Financial() {}

    protected Financial(Parcel in) {
        super(in);
        this.take_amount = in.readString();
        this.supply_amount = in.readString();
    }

    public static final Creator<Financial> CREATOR = new Creator<Financial>() {
        @Override
        public Financial createFromParcel(Parcel source) {return new Financial(source);}

        @Override
        public Financial[] newArray(int size) {return new Financial[size];}
    };
}

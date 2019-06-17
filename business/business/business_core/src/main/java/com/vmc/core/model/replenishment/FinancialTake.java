package com.vmc.core.model.replenishment;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/20<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 */

public class FinancialTake extends Model {

    public String machine_coin;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.machine_coin);
    }

    public FinancialTake() {}

    protected FinancialTake(Parcel in) {
        super(in);
        this.machine_coin = in.readString();
    }

    public static final Creator<FinancialTake> CREATOR = new Creator<FinancialTake>() {
        @Override
        public FinancialTake createFromParcel(Parcel source) {return new FinancialTake(source);}

        @Override
        public FinancialTake[] newArray(int size) {return new FinancialTake[size];}
    };
}

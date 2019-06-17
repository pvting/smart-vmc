package com.vmc.core.model.replenishment;

import android.os.Parcel;

import com.vmc.core.model.OdooList;
import com.vmc.core.model.machine.Machine;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/12<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 */

public class Replenishment extends OdooList<Records> {

    public Machine machine;
    public int supply_id;
    public Financial financial;
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.machine, flags);
        dest.writeInt(supply_id);
        dest.writeParcelable(this.financial, flags);
    }

    public Replenishment() {
    }

    protected Replenishment(Parcel in) {
        super(in);
        this.machine = in.readParcelable(Machine.class.getClassLoader());
        this.supply_id = in.readInt();
        this.financial = in.readParcelable(Financial.class.getClassLoader());
    }

    public static final Creator<Replenishment> CREATOR = new Creator<Replenishment>() {
        @Override
        public Replenishment createFromParcel(Parcel source) {
            return new Replenishment(source);
        }

        @Override
        public Replenishment[] newArray(int size) {
            return new Replenishment[size];
        }
    };
}

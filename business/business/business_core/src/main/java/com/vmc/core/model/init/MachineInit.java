package com.vmc.core.model.init;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b> 8/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class MachineInit extends Model {

    /** 后台记录的机器ID */
    public String machine_id;
    /** 机器货道数量 */
    public int stack_qty;


    public MachineInit() {}

    protected MachineInit(Parcel in) {
        super(in);
        this.machine_id = in.readString();
        this.stack_qty = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.machine_id);
        dest.writeInt(this.stack_qty);
    }


    public static final Creator<MachineInit> CREATOR = new Creator<MachineInit>() {
        @Override
        public MachineInit createFromParcel(Parcel source) {return new MachineInit(source);}

        @Override
        public MachineInit[] newArray(int size) {return new MachineInit[size];}
    };
}

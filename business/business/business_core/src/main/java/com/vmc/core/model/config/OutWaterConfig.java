package com.vmc.core.model.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b>2016/12/5 15:18<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class OutWaterConfig extends Model {
    public int larger_water;
    public int larger_water_time;
    public int pulse_count;
    public int wastewater_time;

    public OutWaterConfig() {
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.larger_water);
        dest.writeInt(this.larger_water_time);
        dest.writeInt(this.pulse_count);
        dest.writeInt(this.wastewater_time);
    }

    protected OutWaterConfig(Parcel in) {
        super(in);
        this.larger_water = in.readInt();
        this.larger_water_time = in.readInt();
        this.pulse_count = in.readInt();
        this.wastewater_time = in.readInt();
    }

    public static final Parcelable.Creator<OutWaterConfig> CREATOR = new Creator<OutWaterConfig>() {
        @Override
        public OutWaterConfig createFromParcel(Parcel source) {return new OutWaterConfig(source);}

        @Override
        public OutWaterConfig[] newArray(int size) {return new OutWaterConfig[size];}
    };
}
package com.vmc.core.model.config;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b>2016/12/5 15:18<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class ReBootTime extends Model {
    /**重启时间 格式：（每天）*/
    public  String reset_time;
    /**重启时间：格式：（12：00：00）*/
    public  String time;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.reset_time);
        dest.writeString(this.time);
    }

    public ReBootTime() {
    }

    protected ReBootTime(Parcel in) {
        super(in);
        this.reset_time = in.readString();
        this.time = in.readString();
    }

    public static final Creator<ReBootTime> CREATOR = new Creator<ReBootTime>() {
        @Override
        public ReBootTime createFromParcel(Parcel source) {
            return new ReBootTime(source);
        }

        @Override
        public ReBootTime[] newArray(int size) {
            return new ReBootTime[size];
        }
    };
}
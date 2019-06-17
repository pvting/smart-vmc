package com.vmc.core.model.config;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b>2016/12/5 15:18<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class TimeSetting extends Model {
    /**一般页面倒计时*/
    public int general_page_countdown;
    /**提示信息倒计时*/
    public int message_countdown;

    /**提示信息倒计时(长)*/
    public int  message_countdown_long;

    /**提示信息倒计时（短）*/
    public int  message_countdown_short;


    /**帮助倒计时*/
    public int help_page_countdown;
    /**商品购买页倒计时*/
    public int purchase_page_countdown;
    /**广告页倒计时*/
    public int advertising_countdown;


    public TimeSetting() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.general_page_countdown);
        dest.writeInt(this.message_countdown);
        dest.writeInt(this.message_countdown_long);
        dest.writeInt(this.message_countdown_short);
        dest.writeInt(this.help_page_countdown);
        dest.writeInt(this.purchase_page_countdown);
        dest.writeInt(this.advertising_countdown);
    }

    protected TimeSetting(Parcel in) {
        super(in);
        this.general_page_countdown = in.readInt();
        this.message_countdown = in.readInt();
        this.message_countdown_long = in.readInt();
        this.message_countdown_short = in.readInt();
        this.help_page_countdown = in.readInt();
        this.purchase_page_countdown = in.readInt();
        this.advertising_countdown = in.readInt();
    }

    public static final Creator<TimeSetting> CREATOR = new Creator<TimeSetting>() {
        @Override
        public TimeSetting createFromParcel(Parcel source) {
            return new TimeSetting(source);
        }

        @Override
        public TimeSetting[] newArray(int size) {
            return new TimeSetting[size];
        }
    };
}
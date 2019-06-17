package com.vmc.core.model.config;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b>2016/12/5 15:18<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class PayMentWay extends Model {

    public int payment_weixin;
    public int payment_cash;
    public int payment_alipay;
    public int payment_wangbi;
    public int card_watergod;


    public PayMentWay() {
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.payment_weixin);
        dest.writeInt(this.payment_cash);
        dest.writeInt(this.payment_alipay);
        dest.writeInt(this.payment_wangbi);
        dest.writeInt(this.card_watergod);
    }

    protected PayMentWay(Parcel in) {
        super(in);
        this.payment_weixin = in.readInt();
        this.payment_cash = in.readInt();
        this.payment_alipay = in.readInt();
        this.payment_wangbi = in.readInt();
        this.card_watergod = in.readInt();
    }

    public static final Creator<PayMentWay> CREATOR = new Creator<PayMentWay>() {
        @Override
        public PayMentWay createFromParcel(Parcel source) {return new PayMentWay(source);}

        @Override
        public PayMentWay[] newArray(int size) {return new PayMentWay[size];}
    };
}
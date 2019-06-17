package com.vmc.core.request.pay;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b>2016/12/22 16:52<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class CardPayStatus extends Model {

    public  double remain_money;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(this.remain_money);
    }

    public CardPayStatus() {}

    protected CardPayStatus(Parcel in) {
        super(in);
        this.remain_money = in.readDouble();
    }

    public static final Creator<CardPayStatus> CREATOR = new Creator<CardPayStatus>() {
        @Override
        public CardPayStatus createFromParcel(Parcel source) {return new CardPayStatus(source);}

        @Override
        public CardPayStatus[] newArray(int size) {return new CardPayStatus[size];}
    };
}
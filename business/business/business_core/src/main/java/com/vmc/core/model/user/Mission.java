package com.vmc.core.model.user;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b> 2016/10/21<br>
 * <b>Author:</b> Wisn(吴贻顺)<br>
 * <b>Description:</b>
 * <p>
 * <br>
 */

public class Mission extends Model {
      public  int  total_count;
      public  int  to_delivery;
      public  int  to_supply;
      public  int  to_refund;
      public  int  supplied_count;


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.total_count);
        dest.writeInt(this.to_delivery);
        dest.writeInt(this.to_supply);
        dest.writeInt(this.to_refund);
        dest.writeInt(this.supplied_count);
    }

    public Mission() {}

    protected Mission(Parcel in) {
        super(in);
        this.total_count = in.readInt();
        this.to_delivery = in.readInt();
        this.to_supply = in.readInt();
        this.to_refund = in.readInt();
        this.supplied_count = in.readInt();
    }

    public static final Creator<Mission> CREATOR = new Creator<Mission>() {
        @Override
        public Mission createFromParcel(Parcel source) {return new Mission(source);}

        @Override
        public Mission[] newArray(int size) {return new Mission[size];}
    };

    @Override
    public String toString() {
        return "Mission{" +
               "total_count=" + total_count +
               ", to_delivery=" + to_delivery +
               ", to_supply=" + to_supply +
               ", to_refund=" + to_refund +
               ", supplied_count=" + supplied_count +
               '}';
    }
}

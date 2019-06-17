package com.vmc.core.model.product;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b> 8/22/16<br>
 * <b>Author:</b> Wisn<br>
 * <b>Description:</b> <br>
 */
public class FreeBie extends Model {
    public int id;
    public int quantity;
    public int stock;
    public String name;
    public String stack_no;
    public String box_no;
    public String seq_no;
    public String net_weight;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.id);
        dest.writeInt(this.quantity);
        dest.writeInt(this.stock);
        dest.writeString(this.name);
        dest.writeString(this.stack_no);
        dest.writeString(this.box_no);
        dest.writeString(this.seq_no);
        dest.writeString(this.net_weight);
    }

    public FreeBie() {}

    protected FreeBie(Parcel in) {
        super(in);
        this.id = in.readInt();
        this.quantity = in.readInt();
        this.stock = in.readInt();
        this.name = in.readString();
        this.stack_no = in.readString();
        this.box_no = in.readString();
        this.seq_no = in.readString();
        this.net_weight = in.readString();
    }

    public static final Creator<FreeBie> CREATOR = new Creator<FreeBie>() {
        @Override
        public FreeBie createFromParcel(Parcel source) {return new FreeBie(source);}

        @Override
        public FreeBie[] newArray(int size) {return new FreeBie[size];}
    };
}

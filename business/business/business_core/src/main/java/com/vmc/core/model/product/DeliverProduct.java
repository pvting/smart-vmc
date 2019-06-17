package com.vmc.core.model.product;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b> 2016/11/22<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b> <br>
 */
public class DeliverProduct extends Model {
    public String name;
    public String stack_no;
    public String box_no;
    public String net_weight;
    public String image_url;
    public int product_quantity;
    public int id;
    public String unit;


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.name);
        dest.writeString(this.stack_no);
        dest.writeString(this.box_no);
        dest.writeString(this.net_weight);
        dest.writeString(this.image_url);
        dest.writeInt(this.product_quantity);
        dest.writeInt(this.id);
        dest.writeString(this.unit);
    }

    public DeliverProduct() {}

    protected DeliverProduct(Parcel in) {
        super(in);
        this.name = in.readString();
        this.stack_no = in.readString();
        this.box_no = in.readString();
        this.net_weight = in.readString();
        this.image_url = in.readString();
        this.product_quantity = in.readInt();
        this.id = in.readInt();
        this.unit = in.readString();
    }

    public static final Creator<DeliverProduct> CREATOR = new Creator<DeliverProduct>() {
        @Override
        public DeliverProduct createFromParcel(Parcel source) {return new DeliverProduct(source);}

        @Override
        public DeliverProduct[] newArray(int size) {return new DeliverProduct[size];}
    };
}

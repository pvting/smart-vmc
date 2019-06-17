package com.vmc.core.model.stock;

import android.os.Parcel;

import com.vmc.core.model.Model;
import com.vmc.core.model.product.Product;

/**
 * <b>Create Date:</b> 8/29/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class Stock extends Model {
    /** 库存对应的商品信息 */
    public String  box_no;
    /** 库存的总量 */
    public String stack_no;
    /** 库存余量 */
    public int stock;


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.box_no);
        dest.writeString(this.stack_no);
        dest.writeInt(this.stock);
    }

    public Stock() {}

    protected Stock(Parcel in) {
        super(in);
        this.box_no = in.readString();
        this.stack_no = in.readString();
        this.stock = in.readInt();
    }

    public static final Creator<Stock> CREATOR = new Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel source) {return new Stock(source);}

        @Override
        public Stock[] newArray(int size) {return new Stock[size];}
    };
}

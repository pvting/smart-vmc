package com.vmc.core.model.product;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b> 2016/11/22<br>
 * <b>Author:</b> Stone <br>
 * <b>Description:</b> <br>
 */
public class PickUpProduct extends Model {

    public int product_id;


    public PickUpProduct() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.product_id);
    }

    protected PickUpProduct(Parcel in) {
        super(in);
        this.product_id = in.readInt();
    }

    public static final Creator<PickUpProduct> CREATOR = new Creator<PickUpProduct>() {
        @Override
        public PickUpProduct createFromParcel(Parcel source) {return new PickUpProduct(source);}

        @Override
        public PickUpProduct[] newArray(int size) {return new PickUpProduct[size];}
    };
}

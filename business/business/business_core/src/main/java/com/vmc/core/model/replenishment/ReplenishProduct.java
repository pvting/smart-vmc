package com.vmc.core.model.replenishment;

import android.os.Parcel;
import android.os.Parcelable;

import com.vmc.core.model.product.Product;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/11<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 * 补货商品
 */
public class ReplenishProduct extends Product {

    public int supply_count;
    public int remaining_count;
    public int supplied_count;
    public int actual_count;
    public int stack_volume;
    public int quantity;
    public String unit;
    public String flavor;
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.supply_count);
        dest.writeInt(this.remaining_count);
        dest.writeInt(this.supplied_count);
        dest.writeInt(this.actual_count);
        dest.writeInt(this.stack_volume);
        dest.writeString(this.unit);
        dest.writeString(this.flavor);
        dest.writeInt(this.quantity);
    }

    public ReplenishProduct() {
    }

    protected ReplenishProduct(Parcel in) {
        super(in);
        this.supply_count = in.readInt();
        this.remaining_count = in.readInt();
        this.supplied_count = in.readInt();
        this.actual_count = in.readInt();
        this.stack_volume = in.readInt();
        this.unit = in.readString();
        this.flavor = in.readString();
        this.quantity = in.readInt();
    }

    public static final Parcelable.Creator<ReplenishProduct> CREATOR = new Parcelable.Creator<ReplenishProduct>() {
        @Override
        public ReplenishProduct createFromParcel(Parcel source) {
            return new ReplenishProduct(source);
        }

        @Override
        public ReplenishProduct[] newArray(int size) {
            return new ReplenishProduct[size];
        }
    };

    @Override
    public String toString() {
        return "ReplenishProduct{" +
               "supply_count=" + supply_count +
               ", remaining_count=" + remaining_count +
               ", supplied_count=" + supplied_count +
               ", actual_count=" + actual_count +
               ", stack_volume=" + stack_volume +
               ", quantity=" + quantity +
               ", unit='" + unit + '\'' +
               ", flavor='" + flavor + '\'' +
               "} " + super.toString();
    }
}

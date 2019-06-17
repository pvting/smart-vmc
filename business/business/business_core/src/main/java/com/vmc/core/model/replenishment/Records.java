package com.vmc.core.model.replenishment;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/10<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 */

public class Records extends Model {
    /**
     * 当前货道货品
     **/
    public ReplenishProduct current_product;
    /**
     * 补货商品
     **/
    public ReplenishProduct supply_product;
    /**
     * 货道
     **/
    public Stack stack;
    /**
     * 是否换货
     **/

    public int product_change;
    /**
     * 标记是否补货完成
     */
    public boolean isfinish=false;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.current_product, flags);
        dest.writeParcelable(this.supply_product, flags);
        dest.writeParcelable(this.stack, flags);
        dest.writeInt(this.product_change);
    }

    public Records() {
    }

    protected Records(Parcel in) {
        super(in);
        this.current_product = in.readParcelable(ReplenishProduct.class.getClassLoader());
        this.supply_product = in.readParcelable(ReplenishProduct.class.getClassLoader());
        this.stack = in.readParcelable(Stack.class.getClassLoader());
        this.product_change = in.readInt();
    }

    public static final Creator<Records> CREATOR = new Creator<Records>() {
        @Override
        public Records createFromParcel(Parcel source) {
            return new Records(source);
        }

        @Override
        public Records[] newArray(int size) {
            return new Records[size];
        }
    };
}

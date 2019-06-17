package com.vmc.core.model.replenishment;

import android.os.Parcel;

import com.vmc.core.model.Model;
import com.vmc.core.model.product.OdooStockProduct;
import com.vmc.core.model.product.Product;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/13<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 */

public class OdooStackSync extends Model {

    public Stack stack;
    public OdooStockProduct product;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.stack, flags);
        dest.writeParcelable(this.product, flags);
    }

    public OdooStackSync() {
    }

    protected OdooStackSync(Parcel in) {
        super(in);
        this.stack = in.readParcelable(Stack.class.getClassLoader());
        this.product = in.readParcelable(Product.class.getClassLoader());
    }

    public static final Creator<OdooStackSync> CREATOR = new Creator<OdooStackSync>() {
        @Override
        public OdooStackSync createFromParcel(Parcel source) {
            return new OdooStackSync(source);
        }

        @Override
        public OdooStackSync[] newArray(int size) {
            return new OdooStackSync[size];
        }
    };
}

package com.vmc.core.model.product;


import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 8/30/16<br>
 * <b>Author:</b> Peiweiwei<br>
 * <b>Description:</b> <br>
 */
public class OdooStock extends Model {
    /**
     * id : 34190
     * name : 180g珍珠梅
     * stock : 10
     * stack_no : 6
     * box_no : 9
     */

    public int id;
    public String name;
    public int stock;
    public String stack_no;
    public String box_no;


    public OdooStock() {}

    @Override
    public String toString() {
        return "Stock{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", stock=" + stock +
               ", stack_no='" + stack_no + '\'' +
               ", box_no='" + box_no + '\'' +
               "} " + super.toString();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.stock);
        dest.writeString(this.stack_no);
        dest.writeString(this.box_no);
    }

    protected OdooStock(Parcel in) {
        super(in);
        this.id = in.readInt();
        this.name = in.readString();
        this.stock = in.readInt();
        this.stack_no = in.readString();
        this.box_no = in.readString();
    }

    public static final Creator<OdooStock> CREATOR = new Creator<OdooStock>() {
        @Override
        public OdooStock createFromParcel(Parcel source) {return new OdooStock(source);}

        @Override
        public OdooStock[] newArray(int size) {return new OdooStock[size];}
    };
}

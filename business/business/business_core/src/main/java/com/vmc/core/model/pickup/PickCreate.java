package com.vmc.core.model.pickup;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/19<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 */

public class PickCreate extends Model {

    public int id;
    public int quantity;

    public PickCreate() {}

    public PickCreate(int id) {
        this.id = id;
    }

    public PickCreate(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.id);
        dest.writeInt(this.quantity);
    }

    protected PickCreate(Parcel in) {
        super(in);
        this.id = in.readInt();
        this.quantity = in.readInt();
    }

    public static final Creator<PickCreate> CREATOR = new Creator<PickCreate>() {
        @Override
        public PickCreate createFromParcel(Parcel source) {return new PickCreate(source);}

        @Override
        public PickCreate[] newArray(int size) {return new PickCreate[size];}
    };

    @Override
    public String toString() {
        return "{id=" + id + "}";
    }
}

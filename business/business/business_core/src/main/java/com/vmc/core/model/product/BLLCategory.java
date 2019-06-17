package com.vmc.core.model.product;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * <b>Create Date:</b>2017/2/8 14:01<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class BLLCategory implements Parcelable {
    public String category_name;
    public HashMap<Integer,BLLProduct> mBLLProductHashMap = new HashMap<>();

    public BLLCategory() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.category_name);
        dest.writeSerializable(this.mBLLProductHashMap);
    }

    protected BLLCategory(Parcel in) {
        this.category_name = in.readString();
        this.mBLLProductHashMap = (HashMap<Integer, BLLProduct>) in.readSerializable();
    }

    public static final Creator<BLLCategory> CREATOR = new Creator<BLLCategory>() {
        @Override
        public BLLCategory createFromParcel(Parcel source) {return new BLLCategory(source);}

        @Override
        public BLLCategory[] newArray(int size) {return new BLLCategory[size];}
    };

    @Override
    public String toString() {
        return "BLLCategory{" +
               "category_name='" + category_name + '\'' +
               ", mBLLProductHashMap=" + mBLLProductHashMap +
               '}';
    }
}
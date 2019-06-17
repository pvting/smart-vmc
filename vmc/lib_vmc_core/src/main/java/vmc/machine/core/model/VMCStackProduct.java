package vmc.machine.core.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <b>Create Date:</b>2017/2/20 09:40<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class VMCStackProduct implements Parcelable {
    public int boxId;
    public int roadId;
    public int stock;
    public int price;
    public String seqNo;


    @Override
    public String toString() {
        return "VMCStackProduct{" +
               "boxId=" + boxId +
               ", roadId=" + roadId +
               ", stock=" + stock +
               ", price=" + price +
               ", seqNo='" + seqNo + '\'' +
               '}';
    }

    public VMCStackProduct() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.boxId);
        dest.writeInt(this.roadId);
        dest.writeInt(this.stock);
        dest.writeInt(this.price);
        dest.writeString(this.seqNo);
    }

    protected VMCStackProduct(Parcel in) {
        this.boxId = in.readInt();
        this.roadId = in.readInt();
        this.stock = in.readInt();
        this.price = in.readInt();
        this.seqNo = in.readString();
    }

    public static final Creator<VMCStackProduct> CREATOR = new Creator<VMCStackProduct>() {
        @Override
        public VMCStackProduct createFromParcel(Parcel source) {return new VMCStackProduct(source);}

        @Override
        public VMCStackProduct[] newArray(int size) {return new VMCStackProduct[size];}
    };


}
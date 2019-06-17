package vmc.project.content.bean;

import android.os.Parcel;
import android.os.Parcelable;



/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2017/5/12<br>
 * <b>Author:</b> Peiweiwei<br>
 * <b>Description:</b> <br>
 */

public class VmcState implements Parcelable {

    public boolean isLackOf50Cent;
    public boolean isLackOf100Cent;
    public boolean isSoldOut;
    public boolean isVMCDisconnected;
    public boolean isDoorOpened;


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isDoorOpened ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLackOf50Cent ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLackOf100Cent ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isVMCDisconnected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSoldOut ? (byte) 1 : (byte) 0);


    }


    public VmcState(Parcel in) {
        this.isDoorOpened = in.readByte() != 0;
        this.isLackOf50Cent = in.readByte() != 0;
        this.isLackOf100Cent = in.readByte() != 0;
        this.isVMCDisconnected = in.readByte() != 0;
        this.isSoldOut = in.readByte() != 0;

    }

    public static final Creator<VmcState> CREATOR = new Creator<VmcState>() {
        @Override
        public VmcState createFromParcel(Parcel source) {return new VmcState(source);}

        @Override
        public VmcState[] newArray(int size) {return new VmcState[size];}
    };
}

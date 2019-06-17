package vmc.machine.impl.boueki;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/25<br>
 * <b>Author:</b> Peiweiwei<br>
 * <b>Description:</b> <br>
 */

public class VmcRunStates implements Parcelable {
    public String faultCode;
    public List<Integer> temperature;
    public String lightState;
    public boolean isDoorOpened;
    public boolean isLackOf50Cent;
    public boolean isLackOf100Cent;
    public boolean isSaleStop;
    public boolean isVMCDisconnected;
    public boolean isSoldOut;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.faultCode);
        dest.writeList(this.temperature);
        dest.writeString(this.lightState);
        dest.writeByte(this.isDoorOpened ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLackOf50Cent ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLackOf100Cent ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSaleStop ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isVMCDisconnected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSoldOut ? (byte) 1 : (byte) 0);
    }

    public VmcRunStates() {}

    protected VmcRunStates(Parcel in) {
        this.faultCode = in.readString();
        this.temperature = new ArrayList<Integer>();
        in.readList(this.temperature, Integer.class.getClassLoader());
        this.lightState = in.readString();
        this.isDoorOpened = in.readByte() != 0;
        this.isLackOf50Cent = in.readByte() != 0;
        this.isLackOf100Cent = in.readByte() != 0;
        this.isSaleStop = in.readByte() != 0;
        this.isVMCDisconnected = in.readByte() != 0;
        this.isSoldOut = in.readByte() != 0;
    }

    public static final Creator<VmcRunStates> CREATOR = new Creator<VmcRunStates>() {
        @Override
        public VmcRunStates createFromParcel(Parcel source) {return new VmcRunStates(source);}

        @Override
        public VmcRunStates[] newArray(int size) {return new VmcRunStates[size];}
    };
}

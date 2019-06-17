package vmc.machine.impl.watergod;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2017/3/8<br>
 * <b>Author:</b> kevin_zhuang<br>
 * <b>Description:</b> <br>
 */
public class VmcRunStatesWg implements Parcelable {

    //原液
    public boolean liquidState;

    //水压
    public boolean waterPressureState;

    //固件版本
    public String machineVersionState;

    //app版本
    public String appVersionState;

    //ph状态
    public String phState;

    //Acc状态
    public String accState;

    //水压状态
    public String waterDegreeState;

    //门状态
    public String doorState;

    //网络
    public String networkState;

    //机器号
    public String machineIdState;






    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.liquidState ? (byte) 1 : (byte) 0);
        dest.writeByte(this.waterPressureState ? (byte) 1 : (byte) 0);
        dest.writeString(this.machineVersionState);
        dest.writeString(this.appVersionState);
        dest.writeString(this.phState);
        dest.writeString(this.accState);
        dest.writeString(this.waterDegreeState);
        dest.writeString(this.doorState);
        dest.writeString(this.networkState);
        dest.writeString(this.machineIdState);
    }


    protected VmcRunStatesWg(Parcel in) {

        this.liquidState = in.readByte() != 0;
        this.waterPressureState = in.readByte() != 0;
        this.machineVersionState = in.readString();
        this.appVersionState = in.readString();
        this.phState = in.readString();
        this.accState = in.readString();
        this.waterDegreeState = in.readString();
        this.doorState = in.readString();
        this.networkState = in.readString();
        this.machineIdState = in.readString();

    }

    public VmcRunStatesWg(){}

    public static final Creator<VmcRunStatesWg> CREATOR = new Creator<VmcRunStatesWg>() {
        @Override
        public VmcRunStatesWg createFromParcel(Parcel in) {
            return new VmcRunStatesWg(in);
        }

        @Override
        public VmcRunStatesWg[] newArray(int size) {
            return new VmcRunStatesWg[size];
        }
    };




}

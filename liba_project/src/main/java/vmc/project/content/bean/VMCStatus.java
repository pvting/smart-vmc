package vmc.project.content.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * <b>Create Date:</b> 10/9/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class VMCStatus implements Parcelable, Serializable {
    /** 机器运行状态 */
    public static final String KEY_RUNNING_STATUS = "vmc_running_status";

    public String key;
    public boolean value;

    public VMCStatus(String key,boolean value) {
        this.key = key;
        this.value = value;
    }

    protected VMCStatus(Parcel in) {
        this.key = in.readString();
        this.value = (in.readByte() != 0);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeByte(this.value ? (byte) 1 : (byte) 0);
    }

    @Override
    public String toString() {
        return "VMCStatus{" +
               "key='" + key + '\'' +
               ", value='" + value + '\'' +
               '}';
    }

    public static final Creator<VMCStatus> CREATOR = new Creator<VMCStatus>() {
        @Override
        public VMCStatus createFromParcel(Parcel source) {return new VMCStatus(source);}

        @Override
        public VMCStatus[] newArray(int size) {return new VMCStatus[size];}
    };
}

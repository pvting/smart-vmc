package com.vmc.core.model.instruct;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b>2016/12/5 15:18<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class InstructStatus extends Model {
    public String id;
    public String status;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.id);
        dest.writeString(this.status);
    }

    public InstructStatus() {
    }

    protected InstructStatus(Parcel in) {
        super(in);
        this.id = in.readString();
        this.status = in.readString();
    }

    public static final Creator<InstructStatus> CREATOR = new Creator<InstructStatus>() {
        @Override
        public InstructStatus createFromParcel(Parcel source) {
            return new InstructStatus(source);
        }

        @Override
        public InstructStatus[] newArray(int size) {
            return new InstructStatus[size];
        }
    };
}
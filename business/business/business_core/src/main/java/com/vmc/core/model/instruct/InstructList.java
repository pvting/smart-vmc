package com.vmc.core.model.instruct;

import android.os.Parcel;

import com.vmc.core.model.OdooList;

/**
 * <b>Create Date:</b>2016/12/5 15:18<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class InstructList extends OdooList<Instruct> {
    public InstructList() {

    }
    @Override
    public String toString() {
        return "InstructList{} " + super.toString();
    }

    protected InstructList(Parcel in) {
        super(in);
        this.records = in.createTypedArrayList(Instruct.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public static final Creator<InstructList> CREATOR = new Creator<InstructList>() {
        @Override
        public InstructList createFromParcel(Parcel source) {
            return new InstructList(source);
        }

        @Override
        public InstructList[] newArray(int size) {
            return new InstructList[size];
        }
    };


}
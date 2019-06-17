package com.vmc.core.model.replenishment;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 2016/10/11<br>
 * <b>Author:</b> NewOrin<br>
 * <b>Description:</b>
 * 货道
 */
public class Stack extends Model {
    public String box_no;
    public String stack_no;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.box_no);
        dest.writeString(this.stack_no);
    }

    public Stack() {
    }

    protected Stack(Parcel in) {
        super(in);
        this.box_no = in.readString();
        this.stack_no = in.readString();
    }

    public static final Creator<Stack> CREATOR = new Creator<Stack>() {
        @Override
        public Stack createFromParcel(Parcel source) {
            return new Stack(source);
        }

        @Override
        public Stack[] newArray(int size) {
            return new Stack[size];
        }
    };
}
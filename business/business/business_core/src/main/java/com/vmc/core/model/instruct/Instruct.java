package com.vmc.core.model.instruct;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b>2016/12/5 15:18<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class Instruct extends Model {
    public String id;
    public String show_name;
    public String industrial_type;
    public String important_level;
    public String issued_time;
    public String run_time;
    public String special_data;



    public Instruct() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.id);
        dest.writeString(this.show_name);
        dest.writeString(this.industrial_type);
        dest.writeString(this.important_level);
        dest.writeString(this.issued_time);
        dest.writeString(this.run_time);
        dest.writeString(this.special_data);
    }

    protected Instruct(Parcel in) {
        super(in);
        this.id = in.readString();
        this.show_name = in.readString();
        this.industrial_type = in.readString();
        this.important_level = in.readString();
        this.issued_time = in.readString();
        this.run_time = in.readString();
        this.special_data = in.readString();
    }

    public static final Creator<Instruct> CREATOR = new Creator<Instruct>() {
        @Override
        public Instruct createFromParcel(Parcel source) {
            return new Instruct(source);
        }

        @Override
        public Instruct[] newArray(int size) {
            return new Instruct[size];
        }
    };
}
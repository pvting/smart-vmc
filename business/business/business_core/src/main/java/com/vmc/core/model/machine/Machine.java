package com.vmc.core.model.machine;

import android.os.Parcel;
import android.text.TextUtils;

import com.vmc.core.model.Model;

import org.json.JSONObject;

/**
 * <b>Create Date:</b> 9/5/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class Machine extends Model {

    public int id;
    public String status;
    public String name;
    public String address;
    public String coordinate;
    public String factory_code;
    public int selected;
    public String location;


    public Machine() {}

    protected Machine(Parcel in) {
        super(in);
        this.id = in.readInt();
        this.status = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.factory_code = in.readString();
        this.coordinate = in.readString();
        this.selected = in.readInt();
        this.location = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.id);
        dest.writeString(this.status);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeString(this.coordinate);
        dest.writeString(this.factory_code);
        dest.writeInt(selected);
        dest.writeString(this.location);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        putopt(object, "id", this.id);
        putopt(object, "status", this.status);
        putopt(object, "name", this.name);
        putopt(object, "address", this.address);
        putopt(object, "selected", this.selected);
        putopt(object, "coordinate", this.coordinate);
        putopt(object, "factory_code", this.factory_code);
        if (TextUtils.isEmpty(location)) {
            location = "";
        }
        putopt(object, "location", this.location);
        return object;
    }

    public static final Creator<Machine> CREATOR = new Creator<Machine>() {
        @Override
        public Machine createFromParcel(Parcel source) {return new Machine(source);}

        @Override
        public Machine[] newArray(int size) {return new Machine[size];}
    };
}

package com.vmc.core.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * <b>Project:</b> Odoo<br>
 * <b>Create Date:</b> 16/1/4<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * Odoo system error data model.
 * <br>
 */
public class OdooError extends Model {

    /** 错误信息 */
    public String message;
    /** 错误代码*/
    public int code;
    /** 错误数据*/
    public ErrorData data;

    public OdooError() {}

    protected OdooError(Parcel in) {
        super(in);
        this.message = in.readString();
        this.code = in.readInt();
        this.data = in.readParcelable(ErrorData.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.message);
        dest.writeInt(this.code);
        dest.writeParcelable(this.data, 0);
    }


    public static final Parcelable.Creator<OdooError> CREATOR = new Parcelable.Creator<OdooError>() {
        public OdooError createFromParcel(Parcel source) {return new OdooError(source);}

        public OdooError[] newArray(int size) {return new OdooError[size];}
    };

    public static class ErrorData extends Model {
        /** debug info*/
        public String debug;
        /** debug message*/
        public String message;
        /** name*/
        public String name;
        /** debug arguments*/
        public List<String> arguments;


        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(this.debug);
            dest.writeString(this.message);
            dest.writeString(this.name);
            dest.writeStringList(this.arguments);
        }

        public ErrorData() {}

        protected ErrorData(Parcel in) {
            super(in);
            this.debug = in.readString();
            this.message = in.readString();
            this.name = in.readString();
            this.arguments = in.createStringArrayList();
        }

        public static final Parcelable.Creator<ErrorData> CREATOR = new Parcelable.Creator<ErrorData>() {
            public ErrorData createFromParcel(Parcel source) {return new ErrorData(source);}

            public ErrorData[] newArray(int size) {return new ErrorData[size];}
        };
    }
}

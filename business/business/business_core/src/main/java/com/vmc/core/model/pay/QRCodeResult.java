package com.vmc.core.model.pay;

import android.os.Parcel;

import com.vmc.core.model.Model;

/**
 * <b>Create Date:</b>2016/12/5 15:18<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class QRCodeResult extends Model {
  public   String result;
    public String code_url;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.result);
        dest.writeString(this.code_url);
    }

    public QRCodeResult() {
    }

    protected QRCodeResult(Parcel in) {
        super(in);
        this.result = in.readString();
        this.code_url = in.readString();
    }

    public static final Creator<QRCodeResult> CREATOR = new Creator<QRCodeResult>() {
        @Override
        public QRCodeResult createFromParcel(Parcel source) {
            return new QRCodeResult(source);
        }

        @Override
        public QRCodeResult[] newArray(int size) {
            return new QRCodeResult[size];
        }
    };
}